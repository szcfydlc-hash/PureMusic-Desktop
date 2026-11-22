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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Beat-reactive 48-band live PCM spectrum bus (identical DSP quality to PureMusic Android v1.8).
 */
object DesktopSpectrumBus {
    const val BAND_COUNT = 48

    private val _bands = MutableStateFlow(FloatArray(BAND_COUNT))
    val bands: StateFlow<FloatArray> = _bands.asStateFlow()

    private val _beatEnergy = MutableStateFlow(0f)
    val beatEnergy: StateFlow<Float> = _beatEnergy.asStateFlow()

    private val smoothed = FloatArray(BAND_COUNT)
    private var runningPeak = 0.06f
    private var runningBassAvg = 0.04f
    private var smoothedBeat = 0f
    private var lastEmitMs = 0L

    fun resetToIdle() {
        smoothed.fill(0f)
        runningPeak = 0.06f
        runningBassAvg = 0.04f
        smoothedBeat = 0f
        _beatEnergy.value = 0f
        _bands.value = FloatArray(BAND_COUNT)
    }

    fun feedPcm16Le(bytes: ByteArray, length: Int, channels: Int) {
        val now = System.currentTimeMillis()
        if (now - lastEmitMs < 18L) return
        lastEmitMs = now

        val ch = channels.coerceAtLeast(1)
        val frameBytes = ch * 2
        val totalFrames = length / frameBytes
        if (totalFrames < 16) return

        val sampleCount = totalFrames.coerceAtMost(256)
        val stride = (totalFrames / sampleCount).coerceAtLeast(1)
        val mono = FloatArray(sampleCount)
        for (i in 0 until sampleCount) {
            val idx = i * stride * frameBytes
            if (idx + 1 >= length) break
            val b0 = bytes[idx].toInt() and 0xFF
            val b1 = bytes[idx + 1].toInt()
            val sample = ((b1 shl 8) or b0).toShort()
            val hann = 0.5f * (1f - cos((2.0 * Math.PI * i) / (sampleCount - 1)).toFloat())
            mono[i] = (sample / 32768f) * hann
        }

        val halfBands = BAND_COUNT / 2
        val rawHalf = FloatArray(halfBands)
        var frameMax = 0.001f
        var bassSum = 0f
        val twoPiOverN = (2.0 * Math.PI) / sampleCount

        for (b in 0 until halfBands) {
            val t = b.toDouble() / (halfBands - 1)
            val freqBin = 1.0 + t.pow(1.75) * 44.0
            var re = 0.0
            var im = 0.0
            var idx = 0
            while (idx < sampleCount) {
                val angle = twoPiOverN * freqBin * idx
                val v = mono[idx].toDouble()
                re += v * cos(angle)
                im += v * sin(angle)
                idx += 2
            }
            val mag = (sqrt(re * re + im * im) / (sampleCount / 2.0)).toFloat()
            val weighted = mag * (1.0f + t.toFloat() * 1.15f)
            rawHalf[b] = weighted
            if (b < 5) bassSum += weighted
            if (weighted > frameMax) frameMax = weighted
        }

        val bassNow = bassSum / 5f
        runningBassAvg = runningBassAvg * 0.90f + bassNow * 0.10f
        val rawBeat = ((bassNow - runningBassAvg * 0.78f) / runningBassAvg.coerceAtLeast(0.008f)).coerceIn(0f, 1.4f)
        smoothedBeat = if (rawBeat > smoothedBeat) {
            smoothedBeat + (rawBeat - smoothedBeat) * 0.72f
        } else {
            smoothedBeat * 0.82f
        }
        _beatEnergy.value = smoothedBeat.coerceIn(0f, 1f)

        runningPeak = if (frameMax > runningPeak) {
            runningPeak + (frameMax - runningPeak) * 0.38f
        } else {
            (runningPeak * 0.985f).coerceAtLeast(0.02f)
        }
        val normScale = 1f / runningPeak.coerceAtLeast(0.02f)

        val rawFull = FloatArray(BAND_COUNT)
        for (i in 0 until BAND_COUNT) {
            val distFromCenter = abs(i - (BAND_COUNT - 1) / 2f) / ((BAND_COUNT - 1) / 2f)
            val binIdx = (distFromCenter * (halfBands - 1)).toInt().coerceIn(0, halfBands - 1)
            val norm = (rawHalf[binIdx] * normScale).coerceIn(0f, 1.25f)
            val beatBoost = (1f - distFromCenter * 0.55f) * smoothedBeat * 0.34f
            rawFull[i] = (norm.pow(1.35f) * 0.88f + beatBoost).coerceIn(0f, 1f)
        }

        val next = FloatArray(BAND_COUNT)
        for (i in 0 until BAND_COUNT) {
            val m2 = rawFull[(i - 2).coerceAtLeast(0)]
            val m1 = rawFull[(i - 1).coerceAtLeast(0)]
            val c0 = rawFull[i]
            val p1 = rawFull[(i + 1).coerceAtMost(BAND_COUNT - 1)]
            val p2 = rawFull[(i + 2).coerceAtMost(BAND_COUNT - 1)]
            val graceful = m2 * 0.10f + m1 * 0.22f + c0 * 0.36f + p1 * 0.22f + p2 * 0.10f

            val prev = smoothed[i]
            val damped = if (graceful > prev) {
                prev + (graceful - prev) * 0.72f
            } else {
                prev + (graceful - prev) * 0.20f
            }
            smoothed[i] = if (abs(damped) < 0.004f) 0f else damped
            next[i] = smoothed[i]
        }
        _bands.value = next
    }
}

/**
 * Pure-Java low-latency PCM audio player (MP3 / FLAC / WAV) feeding DesktopSpectrumBus directly.
 */
class DesktopAudioPlayer(
    private val onPositionUpdate: (Long) -> Unit,
    private val onPlayingChanged: (Boolean) -> Unit,
    private val onTrackCompleted: () -> Unit,
) {
    private var playThread: Thread? = null
    private val stopFlag = AtomicBoolean(false)
    private val pausedFlag = AtomicBoolean(false)
    @Volatile private var currentLine: SourceDataLine? = null
    @Volatile private var currentFile: File? = null
    @Volatile private var currentDurationMs: Long = 0L
    @Volatile private var volumeLevel: Float = 0.85f
    @Volatile private var pendingSeekMs: Long = -1L

    fun playFile(file: File, durationMs: Long, startMs: Long = 0L) {
        stopInternal()
        currentFile = file
        currentDurationMs = durationMs
        stopFlag.set(false)
        pausedFlag.set(false)
        pendingSeekMs = startMs.coerceAtLeast(0L)

        val thread = Thread({
            runPlaybackLoop(file, durationMs)
        }, "PureMusic-AudioThread").apply {
            isDaemon = true
        }
        playThread = thread
        thread.start()
    }

    fun togglePlayPause() {
        val nowPaused = !pausedFlag.get()
        pausedFlag.set(nowPaused)
        if (nowPaused) {
            DesktopSpectrumBus.resetToIdle()
            onPlayingChanged(false)
        } else {
            onPlayingChanged(true)
        }
    }

    fun setVolume(vol: Float) {
        volumeLevel = vol.coerceIn(0f, 1f)
        applyGain(currentLine, volumeLevel)
    }

    fun seekTo(targetMs: Long) {
        val file = currentFile ?: return
        val clamped = targetMs.coerceIn(0L, currentDurationMs.coerceAtLeast(1L))
        val wasPaused = pausedFlag.get()
        playFile(file, currentDurationMs, clamped)
        if (wasPaused) {
            pausedFlag.set(true)
            onPlayingChanged(false)
        }
    }

    fun stop() {
        stopInternal()
        onPlayingChanged(false)
        onPositionUpdate(0L)
    }

    private fun stopInternal() {
        stopFlag.set(true)
        pausedFlag.set(false)
        runCatching { currentLine?.stop() }
        runCatching { currentLine?.close() }
        currentLine = null
        playThread?.interrupt()
        runCatching { playThread?.join(250L) }
        playThread = null
        DesktopSpectrumBus.resetToIdle()
    }

    private fun applyGain(line: SourceDataLine?, vol: Float) {
        if (line == null || !line.isOpen) return
        runCatching {
            if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                val ctrl = line.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
                val dB = if (vol <= 0.001f) {
                    ctrl.minimum
                } else {
                    (20.0 * log10(vol.toDouble())).toFloat().coerceIn(ctrl.minimum, ctrl.maximum)
                }
                ctrl.value = dB
            }
        }
    }

    private fun runPlaybackLoop(file: File, durationMs: Long) {
        var finishedNaturally = false
        try {
            val rawStream = AudioSystem.getAudioInputStream(file)
            val baseFormat = rawStream.format
            val sampleRate = if (baseFormat.sampleRate > 0f) baseFormat.sampleRate else 44100f
            val channels = if (baseFormat.channels > 0) baseFormat.channels else 2
            val decodedFormat = AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sampleRate,
                16,
                channels,
                channels * 2,
                sampleRate,
                false,
            )
            val decodedStream = AudioSystem.getAudioInputStream(decodedFormat, rawStream)
            val info = DataLine.Info(SourceDataLine::class.java, decodedFormat)
            val line = AudioSystem.getLine(info) as SourceDataLine
            line.open(decodedFormat, 16384)
            applyGain(line, volumeLevel)
            currentLine = line

            val bytesPerSecond = (sampleRate * channels * 2).toLong().coerceAtLeast(1L)
            var bytesPlayed = 0L

            // Fast-forward skip to seek target if requested
            val seekTarget = pendingSeekMs
            pendingSeekMs = -1L
            if (seekTarget > 0L) {
                val skipBytes = (seekTarget * bytesPerSecond) / 1000L
                val alignedSkip = skipBytes - (skipBytes % (channels * 2))
                val skipBuf = ByteArray(8192)
                var remaining = alignedSkip
                while (remaining > 0 && !stopFlag.get()) {
                    val toRead = minOf(skipBuf.size.toLong(), remaining).toInt()
                    val r = decodedStream.read(skipBuf, 0, toRead)
                    if (r <= 0) break
                    remaining -= r
                    bytesPlayed += r
                }
            }

            line.start()
            onPlayingChanged(!pausedFlag.get())

            val buffer = ByteArray(4096)
            var lastPosEmit = 0L

            while (!stopFlag.get()) {
                if (pausedFlag.get()) {
                    if (line.isRunning) line.stop()
                    Thread.sleep(25L)
                    continue
                }
                if (!line.isRunning) line.start()

                val bytesRead = decodedStream.read(buffer, 0, buffer.size)
                if (bytesRead == -1) {
                    finishedNaturally = true
                    break
                }
                if (bytesRead > 0) {
                    DesktopSpectrumBus.feedPcm16Le(buffer, bytesRead, channels)
                    line.write(buffer, 0, bytesRead)
                    bytesPlayed += bytesRead

                    val now = System.currentTimeMillis()
                    if (now - lastPosEmit >= 80L) {
                        lastPosEmit = now
                        val posMs = (bytesPlayed * 1000L) / bytesPerSecond
                        onPositionUpdate(if (durationMs > 0L) posMs.coerceAtMost(durationMs) else posMs)
                    }
                }
            }

            if (finishedNaturally && !stopFlag.get()) {
                line.drain()
            }
            line.stop()
            line.close()
            decodedStream.close()
            rawStream.close()
        } catch (_: InterruptedException) {
            // Normal stop/skip interruption
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            currentLine = null
            DesktopSpectrumBus.resetToIdle()
            if (finishedNaturally && !stopFlag.get()) {
                onTrackCompleted()
            }
        }
    }
}
