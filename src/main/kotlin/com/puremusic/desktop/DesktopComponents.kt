package com.puremusic.desktop

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun rememberSongArtwork(songPath: String?): ImageBitmap? {
    var bitmap by remember(songPath) { mutableStateOf(songPath?.let { ArtworkCache.get(it) }) }
    LaunchedEffect(songPath) {
        if (songPath == null) {
            bitmap = null
            return@LaunchedEffect
        }
        if (ArtworkCache.contains(songPath)) {
            bitmap = ArtworkCache.get(songPath)
            return@LaunchedEffect
        }
        bitmap = withContext(Dispatchers.IO) { LibraryScanner.loadArtwork(songPath) }
    }
    return bitmap
}

@Composable
fun DesktopAlbumCover(
    bitmap: ImageBitmap?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 0.dp,
) {
    val shape = if (cornerRadius == 0.dp) RoundedCornerShape(0.dp) else RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(0xFF211F1D)),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = null,
                tint = Color(0xFFC83E2B).copy(alpha = 0.72f),
                modifier = Modifier.size(36.dp),
            )
        }
    }
}

/**
 * 96-groove audiophile brushed-silver black vinyl LP (direct port of PureMusic Android).
 */
@Composable
fun DesktopSpinningVinylDisc(
    artwork: ImageBitmap?,
    rotationProvider: () -> Float,
    discSize: Dp,
) {
    Box(
        modifier = Modifier
            .size(discSize)
            .shadow(24.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.55f))
            .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            drawCircle(
                brush = Brush.radialGradient(
                    0.00f to Color(0xFF070709),
                    0.42f to Color(0xFF0B0B0D),
                    0.72f to Color(0xFF141417),
                    0.95f to Color(0xFF0C0C0E),
                    1.00f to Color(0xFF17171A),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )

            drawCircle(
                brush = Brush.sweepGradient(
                    0.00f to Color.Transparent,
                    0.26f to Color.Transparent,
                    0.375f to Color(0xFFEAECEF).copy(alpha = 0.34f),
                    0.50f to Color.Transparent,
                    0.76f to Color.Transparent,
                    0.875f to Color(0xFFEAECEF).copy(alpha = 0.36f),
                    0.99f to Color.Transparent,
                    center = center,
                ),
                radius = radius * 0.96f,
                center = center,
            )

            for (i in 0..95) {
                val factor = 0.435f + i * 0.00545f
                val isGap = i == 26 || i == 54 || i == 80
                when {
                    isGap -> drawCircle(
                        color = Color.Black.copy(alpha = 0.48f),
                        radius = radius * factor,
                        center = center,
                        style = Stroke(width = 2.0f),
                    )
                    i % 2 == 0 -> drawCircle(
                        color = Color.White.copy(alpha = if (i % 6 == 0) 0.11f else 0.055f),
                        radius = radius * factor,
                        center = center,
                        style = Stroke(width = 0.55f),
                    )
                    else -> drawCircle(
                        color = Color.Black.copy(alpha = 0.28f),
                        radius = radius * factor,
                        center = center,
                        style = Stroke(width = 0.75f),
                    )
                }
            }

            drawCircle(
                color = Color.White.copy(alpha = 0.12f),
                radius = radius - 1.2f,
                center = center,
                style = Stroke(width = 1.1f),
            )
        }

        Box(
            modifier = Modifier
                .size(discSize * 0.37f)
                .graphicsLayer { rotationZ = rotationProvider() }
                .clip(CircleShape)
                .border(1.5.dp, Color(0xFF0A0A0B), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            DesktopAlbumCover(bitmap = artwork, modifier = Modifier.fillMaxSize(), cornerRadius = discSize)
            Box(
                modifier = Modifier
                    .size(discSize * 0.045f)
                    .clip(CircleShape)
                    .background(Color(0xFF141416))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
            )
        }
    }
}

@Composable
fun DesktopTonearm(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val angle by animateFloatAsState(
        targetValue = if (isPlaying) 24f else 4f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessLow),
        label = "desktopTonearm",
    )
    val champagneLight = Color(0xFFE8D8BE)
    val champagneMid = Color(0xFFCBB696)

    Canvas(modifier = modifier) {
        val pivot = Offset(size.width * 0.72f, size.height * 0.14f)
        drawCircle(color = Color(0xFF1E1D1C), radius = size.width * 0.24f, center = pivot)
        drawCircle(
            color = champagneLight.copy(alpha = 0.30f),
            radius = size.width * 0.24f,
            center = pivot,
            style = Stroke(width = 1.6f),
        )
        drawCircle(
            brush = Brush.linearGradient(listOf(champagneLight, Color(0xFF8F7B5E))),
            radius = size.width * 0.09f,
            center = pivot,
        )

        rotate(degrees = angle, pivot = pivot) {
            val armEnd = Offset(pivot.x - size.width * 0.05f, pivot.y + size.height * 0.66f)
            drawLine(
                color = Color.Black.copy(alpha = 0.50f),
                start = Offset(pivot.x + 5f, pivot.y + 6f),
                end = Offset(armEnd.x + 5f, armEnd.y + 6f),
                strokeWidth = 10f,
                cap = StrokeCap.Round,
            )
            drawLine(
                brush = Brush.horizontalGradient(listOf(Color(0xFF9C9890), Color(0xFFF0EAE0), Color(0xFF77726B))),
                start = pivot,
                end = armEnd,
                strokeWidth = 8.5f,
                cap = StrokeCap.Round,
            )
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(Color(0xFF2B2825), Color(0xFF171514))),
                topLeft = Offset(armEnd.x - 12f, armEnd.y - 4f),
                size = Size(24f, 34f),
                cornerRadius = CornerRadius(4f, 4f),
            )
            drawRoundRect(
                color = champagneMid.copy(alpha = 0.45f),
                topLeft = Offset(armEnd.x - 12f, armEnd.y - 4f),
                size = Size(24f, 34f),
                cornerRadius = CornerRadius(4f, 4f),
                style = Stroke(width = 1.1f),
            )
            drawLine(
                color = champagneLight.copy(alpha = 0.85f),
                start = Offset(armEnd.x + 11f, armEnd.y + 8f),
                end = Offset(armEnd.x + 22f, armEnd.y + 16f),
                strokeWidth = 2.4f,
                cap = StrokeCap.Round,
            )
        }
    }
}

/**
 * Fixed 3-Slot Auto-Fit Bilingual Lyrics Stage (direct port of Android v1.8).
 */
@Composable
fun DesktopThreeSlotLyrics(
    lyrics: List<LyricLine>,
    activeIdx: Int,
    activeColor: Color,
    secondaryColor: Color,
    mutedColor: Color,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()

    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        if (lyrics.isEmpty() || activeIdx < 0) {
            Text(
                text = "暂无歌词 · 将同名 .lrc 放入音频目录即可自动加载",
                style = MaterialTheme.typography.bodyMedium,
                color = secondaryColor,
            )
            return@Box
        }

        val prevPair = lyrics.getOrNull(activeIdx - 1)?.let { splitBilingualLyric(it) }
        val curPair = splitBilingualLyric(lyrics[activeIdx])
        val nextPair = lyrics.getOrNull(activeIdx + 1)?.let { splitBilingualLyric(it) }

        val line1Text = prevPair?.first.orEmpty()
        val line2Text = curPair.first
        val hasTranslation = !curPair.second.isNullOrBlank()
        val line3Text = if (hasTranslation) curPair.second.orEmpty() else nextPair?.first.orEmpty()

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(24.dp), contentAlignment = Alignment.CenterStart) {
                if (line1Text.isNotBlank()) {
                    Text(
                        text = line1Text,
                        style = TextStyle(fontSize = 14.sp, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic),
                        color = mutedColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(84.dp), contentAlignment = Alignment.CenterStart) {
                val maxWpx = constraints.maxWidth.coerceAtLeast(1)
                val maxHpx = constraints.maxHeight.coerceAtLeast(1)
                val fittedSp = remember(line2Text, maxWpx, maxHpx) {
                    var chosen = 14
                    for (cand in 28 downTo 13) {
                        val res = textMeasurer.measure(
                            text = line2Text,
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontSize = cand.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = (cand * 1.32f).sp,
                            ),
                            constraints = Constraints(maxWidth = maxWpx),
                        )
                        if (res.size.height <= maxHpx && !res.didOverflowWidth) {
                            chosen = cand
                            break
                        }
                    }
                    chosen
                }
                Text(
                    text = line2Text,
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = fittedSp.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = (fittedSp * 1.32f).sp,
                    ),
                    color = activeColor,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(40.dp), contentAlignment = Alignment.TopStart) {
                if (line3Text.isNotBlank()) {
                    Text(
                        text = line3Text,
                        style = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
                        color = if (hasTranslation) secondaryColor else mutedColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/**
 * 4-Mode 60fps Watchmaker-Refined Live PCM Audio Spectrum & Waveform Scrubber.
 */
@Composable
fun DesktopLiveSpectrumBar(
    songId: Long,
    positionMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    spectrumStyle: DesktopSpectrumStyle,
    playedColor: Color,
    unplayedColor: Color,
    needleColor: Color,
    timestampColor: Color,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val barCount = 72
    val spectrumBands by DesktopSpectrumBus.bands.collectAsState()
    val beatEnergy by DesktopSpectrumBus.beatEnergy.collectAsState()

    val restingContour = remember(songId) {
        FloatArray(barCount) { idx ->
            val t = idx.toFloat() / (barCount - 1)
            val arch = 0.22f + 0.22f * sin(t * Math.PI).toFloat()
            val detail = 0.08f * sin(idx * 0.48f + (songId % 9)).toFloat()
            (arch + detail).coerceIn(0.14f, 0.48f)
        }
    }

    val animatedLevels = remember { FloatArray(barCount) { 0.16f } }
    val peakHoldLevels = remember { FloatArray(barCount) { 0.16f } }
    var frameTick by remember { mutableIntStateOf(0) }
    var wavePhase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isPlaying) {
        var lastNs = 0L
        while (isActive) {
            withFrameNanos { nowNs ->
                val dt = if (lastNs == 0L) 0.016f else ((nowNs - lastNs) / 1_000_000_000f).coerceIn(0.004f, 0.05f)
                lastNs = nowNs
                if (isPlaying) wavePhase += dt * (3.6f + beatEnergy * 3.2f)

                val bandCount = spectrumBands.size.coerceAtLeast(1)
                var energySum = 0f
                for (b in spectrumBands) energySum += b
                val hasLivePcm = energySum > 0.03f

                var changed = false
                for (i in 0 until barCount) {
                    val t = i.toFloat() / (barCount - 1)
                    val ribbonTaper = 0.42f + 0.58f * sin(t * Math.PI).toFloat()
                    val target = if (isPlaying) {
                        val live = spectrumBands[(i * bandCount) / barCount]
                        val travelWave = 0.5f + 0.5f * sin(wavePhase - i * 0.20f)
                        if (hasLivePcm) {
                            (0.10f + (live * 1.12f + beatEnergy * 0.22f * travelWave) * ribbonTaper * 0.90f).coerceIn(0.10f, 0.98f)
                        } else {
                            ((0.28f + 0.52f * travelWave) * ribbonTaper).coerceIn(0.12f, 0.88f)
                        }
                    } else {
                        restingContour[i] * 0.58f
                    }
                    val cur = animatedLevels[i]
                    val speed = if (target > cur) 24f else 6.8f
                    val next = cur + (target - cur) * (dt * speed).coerceAtMost(1f)
                    if (abs(next - cur) > 0.001f) changed = true
                    animatedLevels[i] = next

                    val prevPeak = peakHoldLevels[i]
                    peakHoldLevels[i] = if (next >= prevPeak) next else (prevPeak - dt * 0.38f).coerceAtLeast(next)
                }
                if (changed || isPlaying) frameTick++
            }
            if (!isPlaying) delay(16L)
        }
    }

    var isDragging by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableFloatStateOf(0f) }
    var widthPx by remember { mutableFloatStateOf(1f) }

    val progress = if (isDragging) dragFraction else if (durationMs > 0L) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
    val displayPosMs = if (isDragging) (dragFraction * durationMs).toLong() else positionMs

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .onSizeChanged { widthPx = it.width.toFloat().coerceAtLeast(1f) }
                .pointerInput(durationMs) {
                    detectTapGestures { offset ->
                        val frac = (offset.x / widthPx).coerceIn(0f, 1f)
                        onSeekTo((frac * durationMs).toLong())
                    }
                }
                .pointerInput(durationMs) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            dragFraction = (offset.x / widthPx).coerceIn(0f, 1f)
                        },
                        onHorizontalDrag = { change, _ ->
                            dragFraction = (change.position.x / widthPx).coerceIn(0f, 1f)
                        },
                        onDragEnd = {
                            isDragging = false
                            onSeekTo((dragFraction * durationMs).toLong())
                        },
                        onDragCancel = { isDragging = false },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(48.dp)) {
                val _tick = frameTick
                val totalW = size.width
                val totalH = size.height
                val playheadX = (totalW * progress).coerceIn(2f, totalW - 2f)

                when (spectrumStyle) {
                    DesktopSpectrumStyle.SILK_RIBBON -> {
                        val step = totalW / barCount
                        val barW = (step * 0.46f).coerceIn(2.2f, 4.2f)
                        val baseY = totalH - 2f
                        val crestPath = Path()
                        var prevX = 0f
                        var prevY = baseY

                        for (i in 0 until barCount) {
                            val cx = i * step + step / 2f
                            val isPlayed = cx <= playheadX
                            val rawLevel = animatedLevels[i]
                            val level = if (isPlayed) rawLevel else (restingContour[i] * 0.28f + rawLevel * 0.62f).coerceIn(0.10f, 0.78f)
                            val barH = ((totalH - 6f) * level).coerceAtLeast(4f)
                            val topY = baseY - barH

                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    0.0f to (if (isPlayed) playedColor else unplayedColor),
                                    0.72f to (if (isPlayed) playedColor.copy(alpha = 0.72f) else unplayedColor.copy(alpha = 0.55f)),
                                    1.0f to Color.Transparent,
                                    startY = topY,
                                    endY = baseY,
                                ),
                                topLeft = Offset(cx - barW / 2f, topY),
                                size = Size(barW, barH),
                                cornerRadius = CornerRadius(barW / 2f, barW / 2f),
                            )

                            val crestY = (topY - 2f).coerceAtLeast(2f)
                            if (i == 0) crestPath.moveTo(cx, crestY) else crestPath.quadraticTo(prevX, prevY, (prevX + cx) / 2f, (prevY + crestY) / 2f)
                            prevX = cx; prevY = crestY
                        }
                        crestPath.lineTo(prevX, prevY)

                        clipRect(0f, 0f, playheadX, totalH) {
                            drawPath(crestPath, color = playedColor.copy(alpha = 0.22f), style = Stroke(width = 5.5f, cap = StrokeCap.Round))
                            drawPath(crestPath, color = playedColor.copy(alpha = 0.90f), style = Stroke(width = 2.0f, cap = StrokeCap.Round))
                        }
                        clipRect(playheadX, 0f, totalW, totalH) {
                            drawPath(crestPath, color = unplayedColor.copy(alpha = 0.80f), style = Stroke(width = 1.6f, cap = StrokeCap.Round))
                        }
                        drawLine(needleColor.copy(alpha = 0.28f + beatEnergy * 0.22f), Offset(playheadX, 3f), Offset(playheadX, baseY), strokeWidth = 6f, cap = StrokeCap.Round)
                        drawLine(needleColor, Offset(playheadX, 3f), Offset(playheadX, baseY), strokeWidth = 2.2f, cap = StrokeCap.Round)
                        drawCircle(needleColor, radius = 2.8f, center = Offset(playheadX, 3.5f))
                    }

                    DesktopSpectrumStyle.HARMONIC_STRINGS -> {
                        val midY = totalH / 2f
                        val pts = 88
                        drawLine(unplayedColor.copy(alpha = 0.35f), Offset(0f, midY), Offset(totalW, midY), strokeWidth = 0.8f)

                        val amps = listOf(
                            (0.22f + animatedLevels[barCount / 2] * 0.78f) * totalH * 0.42f,
                            (0.18f + animatedLevels[barCount / 3] * 0.62f) * totalH * 0.42f,
                            (0.14f + animatedLevels[(barCount * 3) / 4] * 0.46f) * totalH * 0.42f,
                        )
                        val colors = listOf(playedColor, Color(0xFFD8B888), needleColor.copy(alpha = 0.85f))
                        val strokes = listOf(2.2f, 1.5f, 1.2f)
                        val freqs = listOf(2.4f, 3.2f, 4.1f)
                        var primaryY = midY

                        for (sIdx in 0..2) {
                            val path = Path()
                            val amp = if (isPlaying) amps[sIdx] else totalH * 0.42f * (0.16f - sIdx * 0.04f)
                            for (p in 0..pts) {
                                val t = p.toFloat() / pts
                                val x = t * totalW
                                val y = midY + sin(t * freqs[sIdx] * Math.PI.toFloat() * 2f + wavePhase * (1f + sIdx * 0.25f) + sIdx * 1.3f) * amp * sin(t * Math.PI).toFloat()
                                if (p == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                if (sIdx == 0 && abs(x - playheadX) <= totalW / pts) primaryY = y
                            }
                            clipRect(0f, 0f, playheadX, totalH) {
                                drawPath(path, color = colors[sIdx].copy(alpha = if (sIdx == 0) 0.95f else 0.72f), style = Stroke(width = strokes[sIdx], cap = StrokeCap.Round))
                            }
                            clipRect(playheadX, 0f, totalW, totalH) {
                                drawPath(path, color = colors[sIdx].copy(alpha = 0.25f), style = Stroke(width = strokes[sIdx] * 0.85f, cap = StrokeCap.Round))
                            }
                        }
                        drawLine(playedColor.copy(alpha = 0.45f), Offset(playheadX, 2f), Offset(playheadX, totalH - 2f), strokeWidth = 1.2f)
                        drawCircle(playedColor.copy(alpha = 0.28f), radius = 7.5f, center = Offset(playheadX, primaryY))
                        drawCircle(playedColor, radius = 3.4f, center = Offset(playheadX, primaryY))
                    }

                    DesktopSpectrumStyle.DOT_MATRIX -> {
                        val cols = 48
                        val rows = 8
                        val colStep = totalW / cols
                        val matrixBottom = totalH - 7f
                        val rowStep = (matrixBottom - 3f) / rows
                        val dotR = (minOf(colStep, rowStep) * 0.30f).coerceIn(1.5f, 3.0f)

                        for (c in 0 until cols) {
                            val cx = c * colStep + colStep / 2f
                            val isPlayed = cx <= playheadX
                            val mIdx = (c * barCount) / cols
                            val level = if (isPlayed) animatedLevels[mIdx] else (restingContour[mIdx] * 0.25f + animatedLevels[mIdx] * 0.55f).coerceIn(0.08f, 0.75f)
                            val litRows = (level * rows).roundToInt().coerceIn(1, rows)
                            val peakRow = (peakHoldLevels[mIdx] * rows).roundToInt().coerceIn(litRows, rows)

                            for (r in 0 until rows) {
                                val cy = matrixBottom - r * rowStep - rowStep / 2f
                                val fade = 0.58f + (r.toFloat() / rows) * 0.42f
                                val col = when {
                                    r < litRows && isPlayed -> playedColor.copy(alpha = fade)
                                    r < litRows -> unplayedColor.copy(alpha = fade * 0.85f)
                                    else -> unplayedColor.copy(alpha = 0.14f)
                                }
                                drawCircle(col, radius = dotR, center = Offset(cx, cy))
                            }
                            if (isPlayed && peakRow in 1..rows) {
                                val py = matrixBottom - (peakRow - 1) * rowStep - rowStep / 2f
                                drawCircle(needleColor.copy(alpha = 0.30f), radius = dotR * 2f, center = Offset(cx, py))
                                drawCircle(needleColor, radius = dotR * 1.05f, center = Offset(cx, py))
                            }
                        }
                        for (tIdx in 0..50) {
                            val tx = (tIdx / 50f) * totalW
                            val h = if (tIdx % 5 == 0) 4.5f else 2.2f
                            drawLine(unplayedColor.copy(alpha = 0.45f), Offset(tx, totalH - h), Offset(tx, totalH), strokeWidth = 0.9f)
                        }
                        drawLine(playedColor.copy(alpha = 0.45f), Offset(playheadX, 2f), Offset(playheadX, matrixBottom), strokeWidth = 1.4f)
                    }

                    DesktopSpectrumStyle.INK_MOUNTAIN -> {
                        val step = totalW / (barCount - 1)
                        val baseY = totalH - 2f
                        val frontRidge = Path()
                        val frontFill = Path()
                        val backRidge = Path()
                        val backFill = Path()
                        var pFx = 0f; var pFy = baseY; var pBx = 0f; var pBy = baseY
                        var ridgeAtHead = baseY * 0.5f

                        for (i in 0 until barCount) {
                            val cx = i * step
                            val fy = (baseY - (totalH - 8f) * animatedLevels[i]).coerceIn(4f, baseY - 3f)
                            val bLvl = (animatedLevels[(i + 9) % barCount] * 0.72f + restingContour[i] * 0.25f).coerceIn(0.12f, 0.86f)
                            val by = (baseY - (totalH - 10f) * bLvl).coerceIn(6f, baseY - 4f)
                            if (abs(cx - playheadX) <= step) ridgeAtHead = fy

                            if (i == 0) {
                                frontRidge.moveTo(cx, fy); frontFill.moveTo(cx, baseY); frontFill.lineTo(cx, fy)
                                backRidge.moveTo(cx, by); backFill.moveTo(cx, baseY); backFill.lineTo(cx, by)
                            } else {
                                val mx = (pFx + cx) / 2f
                                frontRidge.quadraticTo(pFx, pFy, mx, (pFy + fy) / 2f)
                                frontFill.quadraticTo(pFx, pFy, mx, (pFy + fy) / 2f)
                                backRidge.quadraticTo(pBx, pBy, mx, (pBy + by) / 2f)
                                backFill.quadraticTo(pBx, pBy, mx, (pBy + by) / 2f)
                            }
                            pFx = cx; pFy = fy; pBx = cx; pBy = by
                        }
                        frontRidge.lineTo(pFx, pFy); frontFill.lineTo(pFx, pFy); frontFill.lineTo(totalW, baseY); frontFill.close()
                        backRidge.lineTo(pBx, pBy); backFill.lineTo(pBx, pBy); backFill.lineTo(totalW, baseY); backFill.close()

                        drawPath(backFill, brush = Brush.verticalGradient(0f to unplayedColor.copy(alpha = 0.24f), 1f to Color.Transparent, startY = 4f, endY = baseY))
                        drawPath(backRidge, color = unplayedColor.copy(alpha = 0.35f), style = Stroke(width = 1.1f))

                        clipRect(0f, 0f, playheadX, totalH) {
                            drawPath(frontFill, brush = Brush.verticalGradient(0f to playedColor.copy(alpha = 0.56f), 0.78f to playedColor.copy(alpha = 0.14f), 1f to Color.Transparent, startY = 2f, endY = baseY))
                            drawPath(frontRidge, color = playedColor.copy(alpha = 0.92f), style = Stroke(width = 2.0f, cap = StrokeCap.Round))
                        }
                        clipRect(playheadX, 0f, totalW, totalH) {
                            drawPath(frontRidge, color = unplayedColor.copy(alpha = 0.72f), style = Stroke(width = 1.3f, cap = StrokeCap.Round))
                        }

                        val d = if (isDragging) 6.5f else 5.0f
                        val diamond = Path().apply {
                            moveTo(playheadX, ridgeAtHead - d)
                            lineTo(playheadX + d, ridgeAtHead)
                            lineTo(playheadX, ridgeAtHead + d)
                            lineTo(playheadX - d, ridgeAtHead)
                            close()
                        }
                        drawCircle(needleColor.copy(alpha = 0.30f), radius = d * 1.9f, center = Offset(playheadX, ridgeAtHead))
                        drawPath(diamond, color = needleColor)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(formatDuration(displayPosMs), style = TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace), color = timestampColor)
            Text(formatDuration(durationMs), style = TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace), color = timestampColor)
        }
    }
}
