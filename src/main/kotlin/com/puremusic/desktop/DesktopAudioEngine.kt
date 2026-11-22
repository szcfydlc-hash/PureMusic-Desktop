package com.puremusic.desktop

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.FloatControl
import javax.sound.sampled.SourceDataLine
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tanh

/**
 * True 256-point Radix-2 Cooley-Tukey FFT spectrum engine with soft-knee tanh dB scaling.
 * Matches PureMusic Android v1.8.5+ DSP quality: silky, musical, zero flat-top clipping.
 */
object DesktopSpectrumBus {
    const val BAND_COUNT = 48
    private const val FFT_SIZE = 256

    private val _bands = MutableStateFlow(FloatArray(BAND_COUNT))
    val bands: StateFlow<FloatArray> = _bands.asStateFlow()

    private val _beatEnergy = MutableStateFlow(0f)
    val beatEnergy: StateFlow<Float> = _beatEnergy.asStateFlow()

    private val _stereoVu = MutableStateFlow(0f to 0f)
    val stereoVu: StateFlow<Pair<Float, Float>> = _stereoVu.asStateFlow()

    private var smoothVuL = 0f
    private var smoothVuR = 0f

    private val smoothed = FloatArray(BAND_COUNT)
    private val real = DoubleArray(FFT_SIZE)
    private val imag = DoubleArray(FFT_SIZE)
    private val hannWindow = DoubleArray(FFT_SIZE) { i ->
        0.5 * (1.0 - cos(2.0 * PI * i / (FFT_SIZE - 1)))
    }

    private var runningFloorDb = -52.0
    private var runningCeilDb = -14.0
    private var runningBassAvg = 0.08f
    private var smoothedBeat = 0f
    private var lastEmitMs = 0L

    fun resetToIdle() {
        smoothed.fill(0f)
        runningFloorDb = -52.0
        runningCeilDb = -14.0
        runningBassAvg = 0.08f
        smoothedBeat = 0f
        smoothVuL = 0f
        smoothVuR = 0f
        _stereoVu.value = 0f to 0f
        _beatEnergy.value = 0f
        _bands.value = FloatArray(BAND_COUNT)
    }

    fun feedPcm16Le(bytes: ByteArray, length: Int, channels: Int) {
        val now = System.currentTimeMillis()
        if (now - lastEmitMs < 14L) return // ~65-70Hz crisp update ceiling
        lastEmitMs = now

        val ch = channels.coerceAtLeast(1)
        val frameBytes = ch * 2
        val totalFrames = length / frameBytes
        if (totalFrames < 32) return

        // Fill 256-point Hann-windowed mono buffer + real stereo L/R RMS for analog VU meters
        val step = (totalFrames.toDouble() / FFT_SIZE).coerceAtLeast(1.0)
        var sumSqL = 0.0
        var sumSqR = 0.0
        for (i in 0 until FFT_SIZE) {
            val frameIdx = (i * step).toInt().coerceAtMost(totalFrames - 1)
            val byteIdx = frameIdx * frameBytes
            var sL = 0.0
            var sR = 0.0
            if (byteIdx + 1 < length) {
                val b0 = bytes[byteIdx].toInt() and 0xFF
                val b1 = bytes[byteIdx + 1].toInt()
                sL = ((b1 shl 8) or b0).toShort() / 32768.0
                sR = sL
            }
            if (ch >= 2 && byteIdx + 3 < length) {
                val b2 = bytes[byteIdx + 2].toInt() and 0xFF
                val b3 = bytes[byteIdx + 3].toInt()
                sR = ((b3 shl 8) or b2).toShort() / 32768.0
            }
            sumSqL += sL * sL
            sumSqR += sR * sR
            real[i] = ((sL + sR) * 0.5) * hannWindow[i]
            imag[i] = 0.0
        }
        val rmsL = sqrt(sumSqL / FFT_SIZE).toFloat()
        val rmsR = sqrt(sumSqR / FFT_SIZE).toFloat()
        val vuTargetL = tanh((rmsL * 3.8).toDouble()).toFloat().coerceIn(0f, 1f)
        val vuTargetR = tanh((rmsR * 3.8).toDouble()).toFloat().coerceIn(0f, 1f)
        smoothVuL = if (vuTargetL > smoothVuL) smoothVuL + (vuTargetL - smoothVuL) * 0.45f else smoothVuL * 0.86f
        smoothVuR = if (vuTargetR > smoothVuR) smoothVuR + (vuTargetR - smoothVuR) * 0.45f else smoothVuR * 0.86f
        _stereoVu.value = smoothVuL to smoothVuR

        // In-place Radix-2 Cooley-Tukey FFT
        fftRadix2(real, imag)

        // Compute log-spaced 24 half-bands (mirrored gracefully across 48 bands)
        val halfBands = BAND_COUNT / 2
        val maxBin = FFT_SIZE / 2 - 1 // 127
        val rawDb = DoubleArray(halfBands)
        var frameMaxDb = -70.0
        var bassLinear = 0f

        for (b in 0 until halfBands) {
            val t0 = b.toDouble() / halfBands
            val t1 = (b + 1).toDouble() / halfBands
            val binStart = (1.0 + t0.pow(1.85) * (maxBin - 1)).toInt().coerceIn(1, maxBin)
            val binEnd = (1.0 + t1.pow(1.85) * (maxBin - 1)).toInt().coerceIn(binStart, maxBin)
            var energy = 0.0
            for (k in binStart..binEnd) {
                val mag = sqrt(real[k] * real[k] + imag[k] * imag[k]) / (FFT_SIZE / 2.0)
                if (mag > energy) energy = mag
            }
            // Gentle psychoacoustic high-frequency lift so treble shimmers naturally
            val lifted = energy * (1.0 + t0 * 1.45)
            val db = 20.0 * log10(lifted.coerceAtLeast(1e-5))
            rawDb[b] = db
            if (db > frameMaxDb) frameMaxDb = db
            if (b < 4) bassLinear += lifted.toFloat()
        }

        // Beat detection on sub-bass / kick bins
        val bassNow = bassLinear / 4f
        runningBassAvg = runningBassAvg * 0.90f + bassNow * 0.10f
        val rawBeat = ((bassNow - runningBassAvg * 0.80f) / runningBassAvg.coerceAtLeast(0.01f)).coerceIn(0f, 1.4f)
        smoothedBeat = if (rawBeat > smoothedBeat) {
            smoothedBeat + (rawBeat - smoothedBeat) * 0.68f
        } else {
            smoothedBeat * 0.84f
        }
        _beatEnergy.value = smoothedBeat.coerceIn(0f, 1f)

        // Adaptive dynamic range with soft-knee tanh compression (zero flat-top!)
        runningCeilDb = if (frameMaxDb > runningCeilDb) {
            runningCeilDb + (frameMaxDb - runningCeilDb) * 0.25
        } else {
            (runningCeilDb - 0.18).coerceAtLeast(-26.0)
        }
        val floorDb = (runningCeilDb - 42.0).coerceAtLeast(-66.0)
        runningFloorDb = runningFloorDb * 0.92 + floorDb * 0.08
        val span = (runningCeilDb - runningFloorDb).coerceAtLeast(18.0)

        val normHalf = FloatArray(halfBands) { b ->
            val linear = ((rawDb[b] - runningFloorDb) / span).coerceIn(0.0, 1.35)
            // Soft-knee tanh curve keeps peaks organic and musical
            tanh(linear * 1.35).toFloat().coerceIn(0f, 1f)
        }

        // Mirror symmetrically with center bass richness + spatial smoothing kernel
        val rawFull = FloatArray(BAND_COUNT)
        for (i in 0 until BAND_COUNT) {
            val distFromCenter = abs(i - (BAND_COUNT - 1) / 2f) / ((BAND_COUNT - 1) / 2f)
            val binIdx = (distFromCenter * (halfBands - 1)).toInt().coerceIn(0, halfBands - 1)
            val beatBoost = (1f - distFromCenter * 0.58f) * smoothedBeat * 0.22f
            rawFull[i] = (normHalf[binIdx] * 0.92f + beatBoost).coerceIn(0f, 1f)
        }

        val next = FloatArray(BAND_COUNT)
        for (i in 0 until BAND_COUNT) {
            val m1 = rawFull[(i - 1).coerceAtLeast(0)]
            val c0 = rawFull[i]
            val p1 = rawFull[(i + 1).coerceAtMost(BAND_COUNT - 1)]
            val target = m1 * 0.22f + c0 * 0.56f + p1 * 0.22f
            val prev = smoothed[i]
            val updated = if (target > prev) {
                prev + (target - prev) * 0.62f // crisp musical attack
            } else {
                prev * 0.86f + target * 0.14f // silky natural release
            }
            smoothed[i] = updated.coerceIn(0f, 1f)
            next[i] = smoothed[i]
        }
        _bands.value = next
    }

    private fun fftRadix2(re: DoubleArray, im: DoubleArray) {
        val n = re.size
        var j = 0
        for (i in 1 until n) {
            var bit = n shr 1
            while ((j and bit) != 0) {
                j = j xor bit
                bit = bit shr 1
            }
            j = j xor bit
            if (i < j) {
                val tr = re[i]; re[i] = re[j]; re[j] = tr
                val ti = im[i]; im[i] = im[j]; im[j] = ti
            }
        }
        var len = 2
        while (len <= n) {
            val half = len shr 1
            val ang = -2.0 * PI / len
            val wLenRe = cos(ang)
            val wLenIm = sin(ang)
            var i = 0
            while (i < n) {
                var wRe = 1.0
                var wIm = 0.0
                for (k in 0 until half) {
                    val uRe = re[i + k]
                    val uIm = im[i + k]
                    val vRe = re[i + k + half] * wRe - im[i + k + half] * wIm
                    val vIm = re[i + k + half] * wIm + im[i + k + half] * wRe
                    re[i + k] = uRe + vRe
                    im[i + k] = uIm + vIm
                    re[i + k + half] = uRe - vRe
                    im[i + k + half] = uIm - vIm
                    val nextWRe = wRe * wLenRe - wIm * wLenIm
                    wIm = wRe * wLenIm + wIm * wLenRe
                    wRe = nextWRe
                }
                i += len
            }
            len = len shl 1
        }
    }
}

/**
 * 5-Band Pure-Java Biquad IIR Peaking Equalizer with Automatic Anti-Clipping Pre-amp Headroom.
 * 100% bit-transparent fast path when disabled; zero static or clipping even at +10dB boost.
 */
class DesktopEqualizerDsp {
    private val freqs = doubleArrayOf(60.0, 230.0, 910.0, 3600.0, 14000.0)
    private val qVal = 1.15

    @Volatile var enabled: Boolean = false
    @Volatile private var gainsDb: FloatArray = FloatArray(5)
    @Volatile private var dirty: Boolean = true

    private var currentSampleRate = 44100.0
    private var preampGain = 1.0
    private var smoothedPreamp = 1.0

    // Biquad coefficients [band]
    private val b0 = DoubleArray(5) { 1.0 }
    private val b1 = DoubleArray(5)
    private val b2 = DoubleArray(5)
    private val a1 = DoubleArray(5)
    private val a2 = DoubleArray(5)

    // Stereo filter state [band * 2 + ch]
    private val z1 = DoubleArray(10)
    private val z2 = DoubleArray(10)

    fun update(isEnabled: Boolean, bandsDb: List<Float>) {
        enabled = isEnabled
        val next = FloatArray(5) { idx -> (bandsDb.getOrNull(idx) ?: 0f).coerceIn(-10f, 10f) }
        gainsDb = next
        dirty = true
    }

    fun resetState() {
        z1.fill(0.0)
        z2.fill(0.0)
    }

    private fun recomputeCoefficients(sampleRate: Double) {
        currentSampleRate = sampleRate.coerceAtLeast(8000.0)
        val g = gainsDb
        var maxBoostDb = 0.0
        for (i in 0 until 5) {
            val db = g[i].toDouble()
            if (db > maxBoostDb) maxBoostDb = db
            val a = 10.0.pow(db / 40.0)
            val w0 = 2.0 * PI * freqs[i].coerceAtMost(currentSampleRate * 0.45) / currentSampleRate
            val cosW0 = cos(w0)
            val alpha = sin(w0) / (2.0 * qVal)
            val a0 = 1.0 + alpha / a
            b0[i] = (1.0 + alpha * a) / a0
            b1[i] = (-2.0 * cosW0) / a0
            b2[i] = (1.0 - alpha * a) / a0
            a1[i] = (-2.0 * cosW0) / a0
            a2[i] = (1.0 - alpha / a) / a0
        }
        // Automatic anti-clipping headroom (leaves clean margin when any band is boosted)
        preampGain = if (maxBoostDb > 0.5) 10.0.pow(-(maxBoostDb * 0.85) / 20.0) else 1.0
        dirty = false
    }

    fun processPcm16LeInPlace(bytes: ByteArray, length: Int, channels: Int, sampleRate: Float) {
        if (!enabled) return
        if (dirty || abs(sampleRate.toDouble() - currentSampleRate) > 1.0) {
            recomputeCoefficients(sampleRate.toDouble())
        }
        val ch = channels.coerceIn(1, 2)
        val frameBytes = ch * 2
        val frames = length / frameBytes

        for (f in 0 until frames) {
            // Click-free preamp ramp
            smoothedPreamp += (preampGain - smoothedPreamp) * 0.004
            val base = f * frameBytes
            for (c in 0 until ch) {
                val off = base + c * 2
                val lo = bytes[off].toInt() and 0xFF
                val hi = bytes[off + 1].toInt()
                var x = (((hi shl 8) or lo).toShort() / 32768.0) * smoothedPreamp

                for (band in 0 until 5) {
                    val st = band * 2 + c
                    val y = b0[band] * x + z1[st]
                    z1[st] = b1[band] * x - a1[band] * y + z2[st]
                    z2[st] = b2[band] * x - a2[band] * y
                    x = y
                }
                // Soft-clip safety limiter at ±0.985
                if (x > 0.985 || x < -0.985) x = tanh(x)
                val out = (x * 32767.0).toInt().coerceIn(-32768, 32767)
                bytes[off] = (out and 0xFF).toByte()
                bytes[off + 1] = ((out shr 8) and 0xFF).toByte()
            }
        }
    }
}

/**
 * Pure-JVM offline PCM playback engine supporting MP3 (via mp3spi), FLAC (via jflac), WAV & AIFF.
 * Includes live 256-pt FFT spectrum feeding, 5-band anti-clipping EQ DSP, smooth seeking & volume.
 */
class DesktopAudioEngine(
    private val onStateChanged: (isPlaying: Boolean, positionMs: Long, durationMs: Long) -> Unit,
    private val onTrackCompleted: () -> Unit,
) {
    val equalizer = DesktopEqualizerDsp()

    @Volatile private var currentFile: File? = null
    @Volatile private var totalDurationMs: Long = 0L
    @Volatile private var isPaused = false
    @Volatile private var volumeLevel = 0.85f
    @Volatile private var currentLine: SourceDataLine? = null
    @Volatile private var playbackThread: Thread? = null
    @Volatile private var currentPositionMs: Long = 0L

    private val stopFlag = AtomicBoolean(false)

    fun playFile(file: File, durationHintMs: Long, startMs: Long = 0L) {
        stopInternal()
        currentFile = file
        totalDurationMs = durationHintMs.coerceAtLeast(1L)
        isPaused = false
        stopFlag.set(false)
        currentPositionMs = startMs.coerceAtLeast(0L)
        equalizer.resetState()

        val t = Thread({ runPlaybackLoop(file, startMs) }, "PureMusic-AudioThread")
        t.isDaemon = true
        playbackThread = t
        t.start()
    }

    fun togglePlayPause(): Boolean {
        val line = currentLine ?: return false
        isPaused = !isPaused
        if (isPaused) {
            runCatching { line.stop() }
            DesktopSpectrumBus.resetToIdle()
        } else {
            runCatching { line.start() }
        }
        onStateChanged(!isPaused, currentPositionMs, totalDurationMs)
        return !isPaused
    }

    fun pauseIfPlaying() {
        val line = currentLine ?: return
        if (!isPaused) {
            isPaused = true
            runCatching { line.stop() }
            DesktopSpectrumBus.resetToIdle()
            onStateChanged(false, currentPositionMs, totalDurationMs)
        }
    }

    fun setVolume(vol: Float) {
        volumeLevel = vol.coerceIn(0f, 1f)
        currentLine?.let { applyGain(it, volumeLevel) }
    }

    fun seekTo(targetMs: Long) {
        val f = currentFile ?: return
        val clamped = targetMs.coerceIn(0L, totalDurationMs)
        val wasPaused = isPaused
        playFile(f, totalDurationMs, clamped)
        if (wasPaused) {
            Thread.sleep(25)
            togglePlayPause()
        }
    }

    fun stop() {
        stopInternal()
        DesktopSpectrumBus.resetToIdle()
        onStateChanged(false, 0L, totalDurationMs)
    }

    private fun stopInternal() {
        stopFlag.set(true)
        isPaused = false
        runCatching {
            currentLine?.stop()
            currentLine?.flush()
            currentLine?.close()
        }
        currentLine = null
        val t = playbackThread
        playbackThread = null
        if (t != null && t !== Thread.currentThread()) {
            runCatching { t.join(250) }
        }
    }

    private fun applyGain(line: SourceDataLine, vol: Float) {
        runCatching {
            if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                val ctrl = line.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
                val db = if (vol <= 0.001f) {
                    ctrl.minimum
                } else {
                    (20.0 * log10(vol.toDouble())).toFloat().coerceIn(ctrl.minimum, ctrl.maximum)
                }
                ctrl.value = db
            }
        }
    }

    private fun runPlaybackLoop(file: File, startMs: Long) {
        var completedNaturally = false
        try {
            AudioSystem.getAudioInputStream(file).use { rawStream ->
                val baseFormat = rawStream.format
                val targetRate = if (baseFormat.sampleRate > 0) baseFormat.sampleRate else 44100f
                val targetChannels = if (baseFormat.channels > 0) baseFormat.channels else 2
                val decodedFormat = AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    targetRate,
                    16,
                    targetChannels,
                    targetChannels * 2,
                    targetRate,
                    false,
                )

                AudioSystem.getAudioInputStream(decodedFormat, rawStream).use { pcmStream ->
                    val info = DataLine.Info(SourceDataLine::class.java, decodedFormat)
                    val line = AudioSystem.getLine(info) as SourceDataLine
                    line.open(decodedFormat, 16384)
                    applyGain(line, volumeLevel)
                    line.start()
                    currentLine = line

                    val bytesPerSecond = (targetRate * targetChannels * 2).toLong().coerceAtLeast(1L)
                    var bytesPlayed = (startMs * bytesPerSecond) / 1000L

                    if (startMs > 0L) {
                        var toSkip = bytesPlayed
                        val skipBuf = ByteArray(8192)
                        while (toSkip > 0L && !stopFlag.get()) {
                            val chunk = minOf(skipBuf.size.toLong(), toSkip).toInt()
                            val n = pcmStream.read(skipBuf, 0, chunk)
                            if (n <= 0) break
                            toSkip -= n
                        }
                    }

                    val buffer = ByteArray(4096)
                    var lastUiEmit = 0L
                    onStateChanged(true, startMs, totalDurationMs)

                    while (!stopFlag.get()) {
                        if (isPaused) {
                            Thread.sleep(25)
                            continue
                        }
                        val read = pcmStream.read(buffer, 0, buffer.size)
                        if (read == -1) {
                            completedNaturally = true
                            break
                        }
                        if (read > 0) {
                            equalizer.processPcm16LeInPlace(buffer, read, targetChannels, targetRate)
                            DesktopSpectrumBus.feedPcm16Le(buffer, read, targetChannels)
                            line.write(buffer, 0, read)
                            bytesPlayed += read
                            val posMs = ((bytesPlayed * 1000L) / bytesPerSecond).coerceAtMost(totalDurationMs)
                            currentPositionMs = posMs

                            val now = System.currentTimeMillis()
                            if (now - lastUiEmit >= 80L) {
                                lastUiEmit = now
                                onStateChanged(true, posMs, totalDurationMs)
                            }
                        }
                    }

                    if (completedNaturally && !stopFlag.get()) {
                        line.drain()
                    }
                    runCatching {
                        line.stop()
                        line.close()
                    }
                    if (currentLine === line) currentLine = null
                }
            }
        } catch (_: Throwable) {
            // Graceful recovery for unsupported codecs or moved files
        } finally {
            DesktopSpectrumBus.resetToIdle()
            if (completedNaturally && !stopFlag.get()) {
                onStateChanged(false, totalDurationMs, totalDurationMs)
                onTrackCompleted()
            }
        }
    }
}
