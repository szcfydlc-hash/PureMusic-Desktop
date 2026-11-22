package com.puremusic.desktop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image as SkiaImage
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

object ArtworkMemoryCache {
    private val map = object : LinkedHashMap<String, ImageBitmap?>(96, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, ImageBitmap?>?): Boolean = size > 140
    }
    private val colorMap = object : LinkedHashMap<String, Color>(96, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Color>?): Boolean = size > 140
    }

    @Synchronized fun get(key: String): ImageBitmap? = map[key]
    @Synchronized fun contains(key: String): Boolean = map.containsKey(key)
    @Synchronized fun put(key: String, bmp: ImageBitmap?) { map[key] = bmp }

    @Synchronized fun getColor(key: String): Color? = colorMap[key]
    @Synchronized fun putColor(key: String, c: Color) { colorMap[key] = c }
}

@Composable
fun rememberSongArtwork(filePath: String?): ImageBitmap? {
    var bitmap by remember(filePath) {
        mutableStateOf(if (filePath != null && ArtworkMemoryCache.contains(filePath)) ArtworkMemoryCache.get(filePath) else null)
    }
    LaunchedEffect(filePath) {
        if (filePath == null) {
            bitmap = null
            return@LaunchedEffect
        }
        if (ArtworkMemoryCache.contains(filePath)) {
            bitmap = ArtworkMemoryCache.get(filePath)
            return@LaunchedEffect
        }
        val loaded = withContext(Dispatchers.IO) {
            runCatching {
                val bytes = DesktopLibraryScanner.loadArtwork(filePath) ?: return@runCatching null
                SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
            }.getOrNull()
        }
        ArtworkMemoryCache.put(filePath, loaded)
        bitmap = loaded
    }
    return bitmap
}

/**
 * Real-time dominant mood color extractor from album cover so Vinyl Lightfield & Spatial Gallery
 * dynamically breathe with every song's true cover palette.
 */
@Composable
fun rememberSongDominantColor(filePath: String?, fallback: Color = Color(0xFF4A2520)): Color {
    val bmp = rememberSongArtwork(filePath)
    var dominant by remember(filePath) {
        mutableStateOf(filePath?.let { ArtworkMemoryCache.getColor(it) } ?: fallback)
    }
    LaunchedEffect(filePath, bmp) {
        if (filePath == null || bmp == null) {
            dominant = fallback
            return@LaunchedEffect
        }
        ArtworkMemoryCache.getColor(filePath)?.let {
            dominant = it
            return@LaunchedEffect
        }
        val computed = withContext(Dispatchers.Default) {
            runCatching {
                val pm = bmp.toPixelMap()
                val stepX = (pm.width / 18).coerceAtLeast(1)
                val stepY = (pm.height / 18).coerceAtLeast(1)
                var rSum = 0f; var gSum = 0f; var bSum = 0f; var wSum = 0f
                var y = 0
                while (y < pm.height) {
                    var x = 0
                    while (x < pm.width) {
                        val c = pm[x, y]
                        val maxC = max(c.red, max(c.green, c.blue))
                        val minC = minOf(c.red, minOf(c.green, c.blue))
                        val sat = if (maxC > 0.01f) (maxC - minC) / maxC else 0f
                        val luma = 0.299f * c.red + 0.587f * c.green + 0.114f * c.blue
                        // Prefer rich mid-tone colors over pure black/white
                        val weight = (0.25f + sat * 2.2f) * if (luma in 0.12f..0.86f) 1.4f else 0.35f
                        rSum += c.red * weight
                        gSum += c.green * weight
                        bSum += c.blue * weight
                        wSum += weight
                        x += stepX
                    }
                    y += stepY
                }
                if (wSum > 0.01f) {
                    Color(
                        red = (rSum / wSum).coerceIn(0.08f, 0.72f),
                        green = (gSum / wSum).coerceIn(0.08f, 0.72f),
                        blue = (bSum / wSum).coerceIn(0.08f, 0.72f),
                        alpha = 1f,
                    )
                } else fallback
            }.getOrDefault(fallback)
        }
        ArtworkMemoryCache.putColor(filePath, computed)
        dominant = computed
    }
    return dominant
}

/**
 * Ultra-refined 2.5dp Desktop Sleek Slider (replaces chunky mobile Material3 Slider everywhere on Desktop).
 */
@Composable
fun DesktopSleekSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f),
    thumbColor: Color = activeColor,
    modifier: Modifier = Modifier,
) {
    val span = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)
    val frac = ((value - valueRange.start) / span).coerceIn(0f, 1f)
    var widthPx by remember { mutableFloatStateOf(1f) }
    var dragging by remember { mutableStateOf(false) }

    fun updateFromX(x: Float) {
        val f = (x / widthPx).coerceIn(0f, 1f)
        onValueChange(valueRange.start + f * span)
    }

    Box(
        modifier = modifier
            .height(22.dp)
            .onSizeChanged { widthPx = it.width.toFloat().coerceAtLeast(1f) }
            .pointerInput(valueRange) {
                detectTapGestures { off -> updateFromX(off.x) }
            }
            .pointerInput(valueRange) {
                detectHorizontalDragGestures(
                    onDragStart = { off -> dragging = true; updateFromX(off.x) },
                    onHorizontalDrag = { change, _ -> updateFromX(change.position.x) },
                    onDragEnd = { dragging = false },
                    onDragCancel = { dragging = false },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(16.dp)) {
            val midY = size.height / 2f
            val thumbR = if (dragging) 6.2f else 4.8f
            val activeX = (size.width * frac).coerceIn(thumbR, (size.width - thumbR).coerceAtLeast(thumbR))
            drawLine(trackColor, Offset(thumbR, midY), Offset(size.width - thumbR, midY), strokeWidth = 2.6f, cap = StrokeCap.Round)
            drawLine(activeColor, Offset(thumbR, midY), Offset(activeX, midY), strokeWidth = 2.8f, cap = StrokeCap.Round)
            if (dragging) {
                drawCircle(thumbColor.copy(alpha = 0.22f), radius = 10.5f, center = Offset(activeX, midY))
            }
            drawCircle(thumbColor, radius = thumbR, center = Offset(activeX, midY))
        }
    }
}

@Composable
fun DesktopAlbumCover(
    filePath: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
    iconSize: Dp = 24.dp,
) {
    val bmp = rememberSongArtwork(filePath)
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (bmp != null) {
            Image(
                bitmap = bmp,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.52f),
                modifier = Modifier.size(iconSize),
            )
        }
    }
}

@Composable
fun DesktopAudioSpecStamp(
    song: DesktopSong,
    isDarkSurface: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    val hiResBg = if (isDarkSurface) Color(0xFFE6C896).copy(alpha = 0.18f) else accentColor.copy(alpha = 0.12f)
    val hiResInk = if (isDarkSurface) Color(0xFFE6C896) else accentColor
    val specBorder = if (isDarkSurface) Color.White.copy(alpha = 0.22f) else MaterialTheme.colorScheme.outline
    val specInk = if (isDarkSurface) Color(0xFFD5CFC6) else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (song.isHiRes || song.isLossless) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(hiResBg)
                    .border(0.7.dp, hiResInk.copy(alpha = 0.55f), RoundedCornerShape(5.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = if (song.isHiRes) "Hi-Res" else "SQ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = hiResInk,
                    letterSpacing = 0.4.sp,
                )
            }
        }
        if (song.audioSpecText.isNotBlank()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .border(0.7.dp, specBorder, RoundedCornerShape(5.dp))
                    .padding(horizontal = 7.dp, vertical = 2.dp),
            ) {
                Text(
                    text = song.audioSpecText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = specInk,
                    letterSpacing = 0.3.sp,
                )
            }
        }
    }
}

/**
 * Photorealistic Anisotropic-Specular Vinyl Disc with 42 micro-grooves & multi-band light reflection.
 */
@Composable
fun DesktopSpinningVinylDisc(
    filePath: String?,
    rotationDegrees: Float,
    discSize: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(discSize)
            .shadow(28.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.65f), spotColor = Color.Black)
            .graphicsLayer { rotationZ = rotationDegrees },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f
            val c = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                brush = Brush.radialGradient(
                    0.00f to Color(0xFF141416),
                    0.56f to Color(0xFF0A0A0C),
                    0.82f to Color(0xFF19191D),
                    0.95f to Color(0xFF0E0E11),
                    1.00f to Color(0xFF050506),
                    center = c,
                    radius = r,
                ),
                radius = r,
                center = c,
            )
            // 42 fine vinyl micro-grooves with realistic track-gap bands
            for (g in 1..42) {
                val frac = 0.37f + g * 0.0143f
                val isTrackGap = g == 14 || g == 28
                drawCircle(
                    color = Color.White.copy(alpha = if (isTrackGap) 0.11f else if (g % 4 == 0) 0.055f else 0.025f),
                    radius = r * frac,
                    center = c,
                    style = Stroke(width = if (isTrackGap) 1.1f else 0.65f),
                )
            }
            // Crisp anisotropic studio light wedges (classic vinyl sheen)
            drawCircle(
                brush = Brush.sweepGradient(
                    0.00f to Color.Transparent,
                    0.07f to Color.White.copy(alpha = 0.05f),
                    0.12f to Color.White.copy(alpha = 0.22f),
                    0.17f to Color.White.copy(alpha = 0.05f),
                    0.24f to Color.Transparent,
                    0.50f to Color.Transparent,
                    0.57f to Color.White.copy(alpha = 0.05f),
                    0.62f to Color.White.copy(alpha = 0.19f),
                    0.67f to Color.White.copy(alpha = 0.04f),
                    0.74f to Color.Transparent,
                    1.00f to Color.Transparent,
                    center = c,
                ),
                radius = r * 0.97f,
                center = c,
            )
            drawCircle(color = Color.White.copy(alpha = 0.16f), radius = r - 0.8f, center = c, style = Stroke(1.3f))
        }

        DesktopAlbumCover(
            filePath = filePath,
            modifier = Modifier
                .size(discSize * 0.40f)
                .border(2.dp, Color(0xFF23211F), CircleShape),
            cornerRadius = discSize * 0.20f,
            iconSize = discSize * 0.14f,
        )

        Box(
            modifier = Modifier
                .size(discSize * 0.062f)
                .clip(CircleShape)
                .background(Color(0xFF141312))
                .border(1.3.dp, Color(0xFFE4DCCB), CircleShape),
        )
    }
}

@Composable
fun DesktopTonearm(
    isPlaying: Boolean,
    length: Dp = 200.dp,
    modifier: Modifier = Modifier,
) {
    val angle by animateFloatAsState(
        targetValue = if (isPlaying) 18.5f else -10f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessLow),
        label = "desktopTonearm",
    )
    Canvas(
        modifier = modifier
            .width(length * 0.56f)
            .height(length)
            .graphicsLayer {
                transformOrigin = TransformOrigin(0.55f, 0.12f)
                rotationZ = angle
            },
    ) {
        val pivot = Offset(size.width * 0.55f, size.height * 0.12f)
        val elbow = Offset(size.width * 0.48f, size.height * 0.72f)
        val head = Offset(size.width * 0.30f, size.height * 0.89f)

        // Soft drop shadow
        drawCircle(Color.Black.copy(alpha = 0.35f), radius = 26f, center = Offset(pivot.x + 4f, pivot.y + 6f))
        drawCircle(Color(0xFF1D1C1A), radius = 24f, center = pivot)
        drawCircle(Color(0xFF2E2C29), radius = 19f, center = pivot)
        drawCircle(Color(0xFFE0C89B), radius = 11f, center = pivot)
        drawCircle(Color(0xFF242220), radius = 4.5f, center = pivot)

        // Arm shadow + brushed metallic arm tube
        drawLine(Color.Black.copy(alpha = 0.32f), Offset(pivot.x + 4f, pivot.y + 6f), Offset(elbow.x + 4f, elbow.y + 6f), strokeWidth = 5.5f, cap = StrokeCap.Round)
        drawLine(Color(0xFFE6E2DA), pivot, elbow, strokeWidth = 5.2f, cap = StrokeCap.Round)
        drawLine(Color(0xFFB3AEA5), elbow, head, strokeWidth = 4.4f, cap = StrokeCap.Round)
        // Headshell cartridge
        drawLine(Color(0xFF191817), head, Offset(head.x - 11f, head.y + 15f), strokeWidth = 12.5f, cap = StrokeCap.Round)
        drawCircle(Color(0xFFE0C89B), radius = 2.8f, center = Offset(head.x - 10f, head.y + 14f))
    }
}

/**
 * Flagship 3D Spatial Record Gallery (Desktop Widescreen 1:1 port of Android v2.0.3).
 */
@Composable
fun DesktopSpatialRecordGallery(
    currentSong: DesktopSong,
    allSongs: List<DesktopSong>,
    onTogglePlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stackSongs = remember(currentSong.id, allSongs) {
        if (allSongs.isEmpty()) listOf(currentSong)
        else {
            val idx = allSongs.indexOfFirst { it.id == currentSong.id }.coerceAtLeast(0)
            List(minOf(6, allSongs.size)) { step -> allSongs[(idx + step) % allSongs.size] }
        }
    }

    val scope = rememberCoroutineScope()
    val dragOffsetX = remember { Animatable(0f) }
    val tapScale = remember { Animatable(1f) }
    val density = LocalDensity.current.density

    BoxWithConstraints(
        modifier = modifier
            .pointerInput(currentSong.id) {
                detectTapGestures(
                    onTap = {
                        scope.launch {
                            tapScale.animateTo(0.97f, tween(70))
                            tapScale.animateTo(1f, spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium))
                        }
                        onTogglePlayPause()
                    },
                )
            }
            .pointerInput(currentSong.id) {
                var totalDrag = 0f
                detectHorizontalDragGestures(
                    onDragStart = { totalDrag = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                        scope.launch { dragOffsetX.snapTo((totalDrag * 0.45f).coerceIn(-120f, 120f)) }
                    },
                    onDragEnd = {
                        if (abs(totalDrag) > 70f) {
                            if (totalDrag < 0f) onSkipNext() else onSkipPrevious()
                        }
                        scope.launch { dragOffsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow)) }
                    },
                    onDragCancel = {
                        scope.launch { dragOffsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow)) }
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        val frontSize = min(maxWidth * 0.38f, maxHeight * 0.68f).coerceIn(220.dp, 350.dp)
        val corridorW = frontSize * 1.72f
        val corridorH = frontSize * 1.38f
        val baseY = 56.dp
        val dynamicRotY = 16f + (dragOffsetX.value * 0.06f).coerceIn(-7f, 7f)
        val camDist = 6.2f * density

        val scaleSteps = floatArrayOf(1.00f, 0.89f, 0.79f, 0.71f, 0.64f, 0.58f)
        val xFractions = floatArrayOf(0.00f, 0.24f, 0.43f, 0.58f, 0.70f, 0.80f)
        val yLiftDp = floatArrayOf(0f, 16f, 30f, 41f, 49f, 55f)

        Box(
            modifier = Modifier
                .width(corridorW)
                .height(corridorH)
                .offset { IntOffset(dragOffsetX.value.roundToInt(), 0) }
                .graphicsLayer {
                    scaleX = tapScale.value
                    scaleY = tapScale.value
                },
            contentAlignment = Alignment.TopStart,
        ) {
            for (depth in (stackSongs.lastIndex downTo 0)) {
                val itemSong = stackSongs[depth]
                val isFront = depth == 0
                val dIdx = depth.coerceAtMost(scaleSteps.lastIndex)
                val p = scaleSteps[dIdx]
                val xShift = frontSize * xFractions[dIdx]
                val yShift = (baseY - yLiftDp[dIdx].dp).coerceAtLeast(2.dp)
                val rotY = if (isFront) dynamicRotY else 15f
                val sleeveAlpha = (1f - depth * 0.07f).coerceAtLeast(0.48f)
                val spineW = 9.dp
                val topRimH = 3.5.dp

                Column(
                    modifier = Modifier
                        .offset(x = xShift, y = yShift)
                        .width(frontSize + 24.dp)
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(0.0f, 0.42f)
                            scaleX = p
                            scaleY = p
                            rotationY = rotY
                            cameraDistance = camDist
                            alpha = sleeveAlpha
                        },
                ) {
                    Box(modifier = Modifier.width(frontSize + 24.dp).height(frontSize)) {
                        if (depth in 1..4) {
                            Canvas(modifier = Modifier.align(Alignment.CenterEnd).size(frontSize * 0.86f)) {
                                val r = size.minDimension * 0.5f
                                val c = Offset(size.width * 0.5f, size.height * 0.5f)
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        0.0f to Color(0xFF0A0A0B),
                                        0.72f to Color(0xFF171719),
                                        0.90f to Color(0xFF28282B),
                                        1.0f to Color(0xFF0D0D0F),
                                        center = c,
                                        radius = r,
                                    ),
                                    radius = r,
                                    center = c,
                                )
                                for (g in 1..6) {
                                    drawCircle(Color.White.copy(alpha = 0.06f), radius = r * (0.62f + g * 0.06f), center = c, style = Stroke(0.8f))
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .size(frontSize)
                                .shadow(if (isFront) 34.dp else 18.dp, RoundedCornerShape(3.dp), ambientColor = Color.Black, spotColor = Color.Black)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF1E1D1C)),
                        ) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                Box(
                                    modifier = Modifier
                                        .width(spineW)
                                        .fillMaxSize()
                                        .background(
                                            Brush.horizontalGradient(
                                                0.00f to Color(0xFF6B6A67),
                                                0.18f to Color(0xFFF0EEEA),
                                                0.46f to Color(0xFFC5C3BE),
                                                0.78f to Color(0xFF7D7B77),
                                                0.94f to Color(0xFF3E3D3B),
                                                1.00f to Color(0xFF1A1918),
                                            ),
                                        ),
                                )
                                Column(modifier = Modifier.weight(1f).fillMaxSize()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(topRimH)
                                            .background(Brush.verticalGradient(0.0f to Color(0xFFF0EEEA), 0.55f to Color(0xFFB5B3AE), 1.0f to Color(0xFF4A4946))),
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .background(Color(0xFF161514))
                                            .padding(1.5.dp)
                                            .border(0.6.dp, Color.White.copy(alpha = 0.22f), RectangleShape),
                                    ) {
                                        DesktopAlbumCover(
                                            filePath = itemSong.filePath,
                                            modifier = Modifier.fillMaxSize(),
                                            cornerRadius = 0.dp,
                                            iconSize = if (isFront) 54.dp else 32.dp,
                                        )
                                        if (isFront) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.linearGradient(
                                                            0.00f to Color.White.copy(alpha = 0.14f),
                                                            0.34f to Color.White.copy(alpha = 0.03f),
                                                            0.54f to Color.Transparent,
                                                            1.00f to Color.Black.copy(alpha = 0.20f),
                                                        ),
                                                    ),
                                            )
                                        } else {
                                            val depthDark = (0.16f + depth * 0.11f).coerceAtMost(0.68f)
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.horizontalGradient(
                                                            0.0f to Color.Black.copy(alpha = (depthDark + 0.34f).coerceAtMost(0.84f)),
                                                            0.55f to Color.Black.copy(alpha = depthDark + 0.12f),
                                                            1.0f to Color.Black.copy(alpha = depthDark),
                                                        ),
                                                    ),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(frontSize)
                            .height(2.5.dp)
                            .background(Brush.verticalGradient(0.0f to Color.Black.copy(alpha = if (isFront) 0.88f else 0.65f), 1.0f to Color.Transparent)),
                    )

                    if (depth <= 3) {
                        val reflStrength = when (depth) { 0 -> 0.40f; 1 -> 0.20f; 2 -> 0.11f; else -> 0.06f }
                        Row(
                            modifier = Modifier
                                .width(frontSize)
                                .height(frontSize * 0.30f)
                                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                                .drawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = Brush.verticalGradient(0.0f to Color.Black.copy(alpha = reflStrength), 0.52f to Color.Black.copy(alpha = reflStrength * 0.26f), 1.0f to Color.Transparent),
                                        blendMode = BlendMode.DstIn,
                                    )
                                }
                                .clip(RectangleShape),
                        ) {
                            Box(modifier = Modifier.width(spineW).fillMaxSize().background(Brush.horizontalGradient(0.0f to Color(0xFF4F4E4B), 0.35f to Color(0xFFA8A6A1), 1.0f to Color(0xFF2B2A28))))
                            DesktopAlbumCover(
                                filePath = itemSong.filePath,
                                modifier = Modifier.weight(1f).height(frontSize).graphicsLayer { scaleY = -1f },
                                cornerRadius = 0.dp,
                                iconSize = 28.dp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DesktopThreeSlotLyrics(
    lyrics: List<LyricLine>,
    activeIdx: Int,
    activeColor: Color,
    secondaryColor: Color,
    mutedColor: Color,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    textAlign: TextAlign = TextAlign.Center,
    modifier: Modifier = Modifier,
) {
    val activeLine = if (activeIdx in lyrics.indices) lyrics[activeIdx] else null
    val prevLine = if (activeIdx > 0 && activeIdx - 1 in lyrics.indices) lyrics[activeIdx - 1] else null
    val nextLine = if (activeIdx >= 0 && activeIdx + 1 in lyrics.indices) lyrics[activeIdx + 1] else null

    val (activePrimary, activeSecondary) = remember(activeLine) {
        if (activeLine == null) "" to null else splitBilingualLyric(activeLine.primaryText, activeLine.secondaryText)
    }
    val line1Text = remember(prevLine) {
        if (prevLine == null) "" else splitBilingualLyric(prevLine.primaryText, prevLine.secondaryText).first
    }
    val line3Text = remember(activeSecondary, nextLine) {
        when {
            !activeSecondary.isNullOrBlank() -> activeSecondary
            nextLine != null -> splitBilingualLyric(nextLine.primaryText, nextLine.secondaryText).first
            else -> ""
        }
    }

    val dynamicFontSize = remember(activePrimary) {
        when {
            activePrimary.length <= 20 -> 26.sp
            activePrimary.length <= 34 -> 22.sp
            activePrimary.length <= 52 -> 18.sp
            else -> 15.sp
        }
    }

    Column(modifier = modifier, horizontalAlignment = horizontalAlignment, verticalArrangement = Arrangement.Center) {
        if (lyrics.isEmpty()) {
            Text("纯粹聆听 · 静享此刻", fontSize = 16.sp, color = mutedColor, textAlign = textAlign)
            return@Column
        }
        Text(line1Text, fontSize = 14.sp, color = mutedColor.copy(alpha = 0.72f), maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = textAlign, modifier = Modifier.fillMaxWidth().height(22.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth().height(64.dp), contentAlignment = if (horizontalAlignment == Alignment.Start) Alignment.CenterStart else Alignment.Center) {
            Text(activePrimary, fontSize = dynamicFontSize, lineHeight = dynamicFontSize * 1.32f, fontWeight = FontWeight.Bold, color = activeColor, textAlign = textAlign, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(line3Text, fontSize = 14.5.sp, color = if (!activeSecondary.isNullOrBlank()) secondaryColor.copy(alpha = 0.90f) else mutedColor.copy(alpha = 0.72f), maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = textAlign, modifier = Modifier.fillMaxWidth().height(22.dp))
    }
}

/**
 * Oversized Widescreen Magazine Editorial Lyrics (38–52sp serif/editorial bold active line).
 */
@Composable
fun DesktopEditorialLyrics(
    lyrics: List<LyricLine>,
    activeIdx: Int,
    activeColor: Color,
    accentColor: Color,
    secondaryColor: Color,
    mutedColor: Color,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    textAlign: TextAlign = TextAlign.Start,
    highlightAccent: Boolean = false,
    useEditorialSerif: Boolean = true,
    maxActiveSp: Float = 48f,
    modifier: Modifier = Modifier,
) {
    val activeLine = if (activeIdx in lyrics.indices) lyrics[activeIdx] else null
    val prevLine = if (activeIdx > 0 && activeIdx - 1 in lyrics.indices) lyrics[activeIdx - 1] else null
    val nextLine = if (activeIdx >= 0 && activeIdx + 1 in lyrics.indices) lyrics[activeIdx + 1] else null

    val (activePrimary, activeSecondary) = remember(activeLine) {
        if (activeLine == null) "" to null else splitBilingualLyric(activeLine.primaryText, activeLine.secondaryText)
    }
    val prevText = remember(prevLine) {
        if (prevLine == null) "" else splitBilingualLyric(prevLine.primaryText, prevLine.secondaryText).first
    }
    val subText = remember(activeSecondary, nextLine) {
        when {
            !activeSecondary.isNullOrBlank() -> activeSecondary
            nextLine != null -> splitBilingualLyric(nextLine.primaryText, nextLine.secondaryText).first
            else -> ""
        }
    }

    val dynamicSp = remember(activePrimary, maxActiveSp) {
        when {
            activePrimary.length <= 16 -> maxActiveSp
            activePrimary.length <= 28 -> (maxActiveSp * 0.84f).coerceAtLeast(30f)
            activePrimary.length <= 44 -> (maxActiveSp * 0.68f).coerceAtLeast(25f)
            else -> (maxActiveSp * 0.56f).coerceAtLeast(21f)
        }
    }
    val family = if (useEditorialSerif) FontFamily.Serif else FontFamily.SansSerif

    Column(modifier = modifier, horizontalAlignment = horizontalAlignment, verticalArrangement = Arrangement.Center) {
        if (lyrics.isEmpty()) {
            Text("纯粹聆听 · 静享此刻", fontSize = 22.sp, fontFamily = family, fontWeight = FontWeight.Medium, color = mutedColor, textAlign = textAlign)
            return@Column
        }
        if (prevText.isNotBlank()) {
            Text(prevText, fontSize = 15.sp, fontFamily = family, color = mutedColor.copy(alpha = 0.72f), maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = textAlign, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
        }
        Text(
            text = activePrimary,
            fontSize = dynamicSp.sp,
            lineHeight = (dynamicSp * 1.22f).sp,
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            color = if (highlightAccent) accentColor else activeColor,
            textAlign = textAlign,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
        if (subText.isNotBlank()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(subText, fontSize = 18.sp, lineHeight = 26.sp, fontFamily = family, color = secondaryColor.copy(alpha = 0.86f), maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = textAlign, modifier = Modifier.fillMaxWidth())
        }
    }
}

/**
 * Distortion-Free 1:1 Square Clickable Local Album Poster Wall + Giant Centered Spinning Vinyl LP.
 */
@Composable
fun DesktopPosterWallStage(
    currentSong: DesktopSong,
    allSongs: List<DesktopSong>,
    vinylRotation: Float,
    lyrics: List<LyricLine>,
    activeLyricIdx: Int,
    accentColor: Color,
    onSelectSong: (DesktopSong) -> Unit,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeLine = if (activeLyricIdx in lyrics.indices) lyrics[activeLyricIdx] else null
    val activeText = remember(activeLine) {
        if (activeLine == null) "" else splitBilingualLyric(activeLine.primaryText, activeLine.secondaryText).first
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize().clip(RectangleShape)) {
        val discSize = min(maxHeight * 0.64f, maxWidth * 0.34f).coerceIn(240.dp, 380.dp)
        // Compute strict 1:1 square tile grid so album covers NEVER stretch or squash!
        val rows = 4
        val gap = 8.dp
        val tileSize = ((maxHeight - gap * (rows + 1)) / rows).coerceAtLeast(80.dp)
        val cols = ((maxWidth / (tileSize + gap)).toInt() + 1).coerceIn(6, 12)
        val uniqueAlbums = remember(allSongs, currentSong.id) {
            allSongs.distinctBy { it.album }.ifEmpty { listOf(currentSong) }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(gap, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            for (r in 0 until rows) {
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    for (c in 0 until cols) {
                        val tileSong = uniqueAlbums[(r * cols + c) % uniqueAlbums.size]
                        Box(
                            modifier = Modifier
                                .size(tileSize)
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onSelectSong(tileSong) },
                        ) {
                            DesktopAlbumCover(filePath = tileSong.filePath, modifier = Modifier.fillMaxSize(), cornerRadius = 6.dp, iconSize = 24.dp)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = if (tileSong.id == currentSong.id) 0.14f else 0.44f)),
                            )
                        }
                    }
                }
            }
        }

        // Studio radial + bottom dark vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.00f to Color.Black.copy(alpha = 0.22f),
                        0.45f to Color.Black.copy(alpha = 0.16f),
                        0.76f to Color.Black.copy(alpha = 0.74f),
                        1.00f to Color.Black.copy(alpha = 0.94f),
                    ),
                ),
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            DesktopSpinningVinylDisc(
                filePath = currentSong.filePath,
                rotationDegrees = vinylRotation,
                discSize = discSize,
                modifier = Modifier.clickable(onClick = onTogglePlayPause),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(currentSong.title, fontSize = 25.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = Color(0xFFF6F2EA), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(2.dp))
            Text(currentSong.artist, fontSize = 14.sp, color = Color(0xFFBDB6AC), maxLines = 1)
            if (activeText.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = activeText,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFAF6EE),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.76f),
                )
            }
        }
    }
}

/**
 * Vintage Hi-Fi Walnut-Framed Brushed-Aluminum Stereo Receiver Deck (deeply refined material craft):
 * - Procedural walnut wood grain + inner chamfer shadow + twin machined metal feet
 * - Procedural horizontal brushed-aluminum hairlines + 52-slot precision ventilation grille
 * - Calibrated warm-amber dual analog VU meters (-20..+3dB scale, red overload arc, needle shadow)
 * - Tactile brushed-metal transport buttons, physical loop mode keys, and rotatable volume knob
 */
@Composable
fun DesktopVintageReceiverDeck(
    playerState: DesktopPlayerState,
    onTogglePlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleShuffle: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var vuL by remember { mutableFloatStateOf(0f) }
    var vuR by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(playerState.isPlaying) {
        while (true) {
            withFrameNanos {
                val (targetL, targetR) = if (playerState.isPlaying) DesktopSpectrumBus.stereoVu.value else (0f to 0f)
                vuL += (targetL - vuL) * 0.24f
                vuR += (targetR - vuR) * 0.24f
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        // Outer Walnut Wood Cabinet Frame with procedural wood grain
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(228.dp)
                .shadow(28.dp, RoundedCornerShape(6.dp), ambientColor = Color.Black, spotColor = Color.Black)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    Brush.verticalGradient(
                        0.00f to Color(0xFF774525),
                        0.08f to Color(0xFF5A3118),
                        0.88f to Color(0xFF3E1F0E),
                        1.00f to Color(0xFF241005),
                    ),
                ),
        ) {
            // Subtle walnut wood grain lines
            Canvas(modifier = Modifier.fillMaxSize()) {
                for (i in 0..22) {
                    val y = size.height * (i / 22f)
                    drawLine(Color.Black.copy(alpha = if (i % 3 == 0) 0.14f else 0.06f), Offset(0f, y), Offset(size.width, y), strokeWidth = 1.1f)
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 12.dp),
            ) {
                // Embedded warm-gold seek strip on walnut top rail
                val prog = if (playerState.durationMs > 0L) (playerState.positionMs.toFloat() / playerState.durationMs).coerceIn(0f, 1f) else 0f
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(formatDuration(playerState.positionMs), fontSize = 10.sp, color = Color(0xFFE7D3B0))
                    DesktopSleekSlider(
                        value = prog,
                        onValueChange = { f -> onSeekTo((f * playerState.durationMs).toLong()) },
                        activeColor = Color(0xFFE5C487),
                        trackColor = Color.Black.copy(alpha = 0.50f),
                        thumbColor = Color(0xFFF6E7C8),
                        modifier = Modifier.weight(1f),
                    )
                    Text(formatDuration(playerState.durationMs), fontSize = 10.sp, color = Color(0xFFE7D3B0))
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Brushed-Aluminum Faceplate with procedural horizontal hairlines & ventilation grille
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.verticalGradient(
                                0.00f to Color(0xFFD9D3C8),
                                0.35f to Color(0xFFC2BBB0),
                                0.72f to Color(0xFFB3ACA0),
                                1.00f to Color(0xFF9C9589),
                            ),
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.42f), RoundedCornerShape(3.dp)),
                ) {
                    // Procedural brushed aluminum hairlines + horizontal seam line
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        for (i in 0..46) {
                            val y = size.height * (i / 46f)
                            drawLine(Color.White.copy(alpha = if (i % 4 == 0) 0.09f else 0.03f), Offset(0f, y), Offset(size.width, y), strokeWidth = 0.7f)
                        }
                        val seamY = size.height * 0.71f
                        drawLine(Color.Black.copy(alpha = 0.16f), Offset(0f, seamY), Offset(size.width, seamY), strokeWidth = 1f)
                        drawLine(Color.White.copy(alpha = 0.25f), Offset(0f, seamY + 1f), Offset(size.width, seamY + 1f), strokeWidth = 0.8f)

                        // Precision 48-slot ventilation grille in lower-left faceplate
                        val grilleStartX = 28f
                        val grilleEndX = size.width * 0.46f
                        val slotCount = 48
                        val slotStep = (grilleEndX - grilleStartX) / slotCount
                        val slotTop = size.height * 0.78f
                        val slotBottom = size.height * 0.91f
                        for (s in 0 until slotCount) {
                            val x = grilleStartX + s * slotStep
                            drawLine(Color(0xFF2B2723).copy(alpha = 0.78f), Offset(x, slotTop), Offset(x, slotBottom), strokeWidth = 2.1f, cap = StrokeCap.Round)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        // Left: Engraved Label + Dual Warm-Amber Analog VU Meters
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                            modifier = Modifier.padding(top = 6.dp),
                        ) {
                            Column {
                                Text("立体声接收机", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF3A3530))
                                Text("STEREO RECEIVER", fontSize = 8.5.sp, color = Color(0xFF5D574F), letterSpacing = 1.2.sp)
                            }
                            VintageVuMeterBox(label = "左声道", level = vuL)
                            VintageVuMeterBox(label = "右声道", level = vuR)
                        }

                        // Center: Machined Brushed-Metal Transport Buttons + Physical Mode Keys
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(top = 10.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    VintageMetalButton(size = 46.dp, onClick = onSkipPrevious) {
                                        Icon(Icons.Rounded.SkipPrevious, contentDescription = "上一首", tint = Color(0xFF282522), modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("上一首", fontSize = 9.5.sp, color = Color(0xFF423D37))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    VintageMetalButton(size = 62.dp, onClick = onTogglePlayPause) {
                                        Icon(
                                            imageVector = if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                            contentDescription = "播放/暂停",
                                            tint = Color(0xFF1F1C19),
                                            modifier = Modifier.size(30.dp),
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(if (playerState.isPlaying) "暂停" else "播放", fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF322E29))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    VintageMetalButton(size = 46.dp, onClick = onSkipNext) {
                                        Icon(Icons.Rounded.SkipNext, contentDescription = "下一首", tint = Color(0xFF282522), modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("下一首", fontSize = 9.5.sp, color = Color(0xFF423D37))
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(
                                    Triple("顺序播放", playerState.repeatMode == RepeatMode.OFF && !playerState.shuffleEnabled, onCycleRepeat),
                                    Triple("单曲循环", playerState.repeatMode == RepeatMode.ONE && !playerState.shuffleEnabled, onCycleRepeat),
                                    Triple("随机播放", playerState.shuffleEnabled, onToggleShuffle),
                                    Triple("列表循环", playerState.repeatMode == RepeatMode.ALL && !playerState.shuffleEnabled, onCycleRepeat),
                                ).forEach { (lbl, active, action) ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(if (active) Color(0xFFE0B870) else Color(0xFFACA498))
                                            .border(0.8.dp, Color(0xFF544E46), RoundedCornerShape(3.dp))
                                            .clickable(onClick = action)
                                            .padding(horizontal = 10.dp, vertical = 4.dp),
                                    ) {
                                        Text(lbl, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF24201C))
                                    }
                                }
                            }
                        }

                        // Right: Large Tactile Draggable Brushed-Metal Volume Knob
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 8.dp),
                        ) {
                            Text("音量", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A3530))
                            Spacer(modifier = Modifier.height(8.dp))
                            val knobAngle = -135f + playerState.volume * 270f
                            Box(
                                modifier = Modifier
                                    .size(82.dp)
                                    .shadow(12.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.sweepGradient(
                                            0.00f to Color(0xFFE4DED4),
                                            0.22f to Color(0xFF9C9589),
                                            0.48f to Color(0xFFEAE4DA),
                                            0.76f to Color(0xFF8C857A),
                                            1.00f to Color(0xFFE4DED4),
                                        ),
                                    )
                                    .border(2.2.dp, Color(0xFF47423B), CircleShape)
                                    .pointerInput(playerState.volume) {
                                        detectVerticalDragGestures { _, dragAmount ->
                                            onVolumeChange((playerState.volume - dragAmount * 0.0045f).coerceIn(0f, 1f))
                                        }
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { rotationZ = knobAngle }) {
                                    drawCircle(Color.White.copy(alpha = 0.25f), radius = size.minDimension * 0.42f, style = Stroke(1f))
                                    drawLine(Color(0xFF2B2723), Offset(size.width / 2f, size.height * 0.14f), Offset(size.width / 2f, size.height * 0.34f), strokeWidth = 3.2f, cap = StrokeCap.Round)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("${(playerState.volume * 100).toInt()}%", fontSize = 9.5.sp, color = Color(0xFF4D473F))
                        }
                    }
                }
            }
        }

        // Twin Machined Metal Receiver Feet grounding the cabinet
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .width(46.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                        .background(Brush.horizontalGradient(0.0f to Color(0xFF6B655D), 0.5f to Color(0xFFC8C1B5), 1.0f to Color(0xFF504B44))),
                )
            }
        }
    }
}

@Composable
private fun VintageVuMeterBox(label: String, level: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(136.dp)
                .height(80.dp)
                .shadow(4.dp, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .background(
                    Brush.radialGradient(
                        0.0f to Color(0xFFFCE08E),
                        0.68f to Color(0xFFE7A948),
                        1.0f to Color(0xFFC07622),
                    ),
                )
                .border(2.2.dp, Color(0xFF241E18), RoundedCornerShape(4.dp)),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val pivot = Offset(size.width * 0.5f, size.height * 1.18f)
                val arcR = size.height * 0.86f
                // Warm glass inner shadow along top edge
                drawRect(
                    brush = Brush.verticalGradient(0.0f to Color.Black.copy(alpha = 0.28f), 0.24f to Color.Transparent),
                    size = Size(size.width, size.height * 0.35f),
                )
                // Scale arc ticks (-20dB to +3dB)
                for (i in 0..12) {
                    val deg = -132.0 + i * 7.0
                    val rad = Math.toRadians(deg)
                    val isHot = i >= 9
                    val len = if (i % 2 == 0) 9f else 5.5f
                    val p1 = Offset(pivot.x + (cos(rad) * (arcR - len)).toFloat(), pivot.y + (sin(rad) * (arcR - len)).toFloat())
                    val p2 = Offset(pivot.x + (cos(rad) * arcR).toFloat(), pivot.y + (sin(rad) * arcR).toFloat())
                    drawLine(if (isHot) Color(0xFFB5281C) else Color(0xFF3A2714), p1, p2, strokeWidth = if (i % 2 == 0) 2.2f else 1.2f)
                }
                // Needle shadow + crisp dark needle
                val needleDeg = -132.0 + level.coerceIn(0f, 1f) * 84.0
                val nRad = Math.toRadians(needleDeg)
                val tip = Offset(pivot.x + (cos(nRad) * (arcR + 5f)).toFloat(), pivot.y + (sin(nRad) * (arcR + 5f)).toFloat())
                drawLine(Color.Black.copy(alpha = 0.22f), Offset(pivot.x + 2.5f, pivot.y + 2f), Offset(tip.x + 2.5f, tip.y + 2f), strokeWidth = 2.6f, cap = StrokeCap.Round)
                drawLine(Color(0xFF21170E), pivot, tip, strokeWidth = 2.2f, cap = StrokeCap.Round)
            }
            Text(
                text = "dB",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4A3219),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp),
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 9.5.sp, color = Color(0xFF3D3832))
    }
}

@Composable
private fun VintageMetalButton(
    size: Dp,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    0.0f to Color(0xFFEAE4DA),
                    0.5f to Color(0xFFB8B1A5),
                    1.0f to Color(0xFF8A8377),
                ),
            )
            .border(1.3.dp, Color(0xFF4D473F), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/**
 * Silky 60fps Live 256-Point FFT Audio Spectrum SeekBar (Zero vertical playhead sticks!).
 */
@Composable
fun DesktopLiveSpectrumBar(
    songId: String,
    positionMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    spectrumStyle: DesktopSpectrumStyle,
    playedColor: Color,
    unplayedColor: Color,
    accentColor: Color,
    timestampColor: Color,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val barCount = DesktopSpectrumBus.BAND_COUNT
    val animatedLevels = remember { FloatArray(barCount) { 0.08f } }
    val peakDots = remember { FloatArray(barCount) { 0.08f } }
    val peakVel = remember { FloatArray(barCount) { 0f } }
    var frameTick by remember { mutableLongStateOf(0L) }
    var phaseTime by remember { mutableFloatStateOf(0f) }

    val restingContour = remember(songId) {
        val seed = songId.hashCode()
        FloatArray(barCount) { i ->
            val u = i.toFloat() / (barCount - 1)
            val env = sin(u * PI).toFloat().coerceAtLeast(0.18f)
            val wave = 0.5f + 0.5f * sin(i * 0.45f + (seed and 0xFF) * 0.1f)
            (0.08f + env * wave * 0.24f).coerceIn(0.06f, 0.34f)
        }
    }

    LaunchedEffect(isPlaying, songId) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { nanos ->
                val dt = if (lastNanos == 0L) 0.016f else ((nanos - lastNanos) / 1_000_000_000f).coerceIn(0.004f, 0.05f)
                lastNanos = nanos
                if (isPlaying) phaseTime += dt * 3.2f

                val liveBands = DesktopSpectrumBus.bands.value
                val attackRate = 1f - kotlin.math.exp((-22f * dt).toDouble()).toFloat()
                val releaseRate = 1f - kotlin.math.exp((-7.5f * dt).toDouble()).toFloat()

                for (i in 0 until barCount) {
                    val target = if (isPlaying) {
                        val raw = liveBands.getOrElse(i) { 0f }
                        val subtleBreath = 0.045f + 0.025f * sin(phaseTime + i * 0.28f)
                        (raw * 0.94f + subtleBreath).coerceIn(0.04f, 1.0f)
                    } else {
                        restingContour[i] * 0.42f
                    }
                    val cur = animatedLevels[i]
                    val nextVal = cur + (target - cur) * if (target > cur) attackRate else releaseRate
                    animatedLevels[i] = nextVal

                    if (nextVal >= peakDots[i]) {
                        peakDots[i] = nextVal
                        peakVel[i] = 0f
                    } else {
                        peakVel[i] += 1.45f * dt
                        peakDots[i] = (peakDots[i] - peakVel[i] * dt).coerceAtLeast(nextVal)
                    }
                }
                frameTick = nanos
            }
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
                .height(62.dp)
                .onSizeChanged { widthPx = it.width.toFloat().coerceAtLeast(1f) }
                .pointerInput(durationMs) {
                    detectTapGestures { offset -> onSeekTo(((offset.x / widthPx).coerceIn(0f, 1f) * durationMs).toLong()) }
                }
                .pointerInput(durationMs) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset -> isDragging = true; dragFraction = (offset.x / widthPx).coerceIn(0f, 1f) },
                        onHorizontalDrag = { change, _ -> dragFraction = (change.position.x / widthPx).coerceIn(0f, 1f) },
                        onDragEnd = { isDragging = false; onSeekTo((dragFraction * durationMs).toLong()) },
                        onDragCancel = { isDragging = false },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(58.dp)) {
                val _tick = frameTick
                val totalW = size.width
                val totalH = size.height
                val playheadX = (totalW * progress).coerceIn(0f, totalW)

                when (spectrumStyle) {
                    DesktopSpectrumStyle.SILK_RIBBON -> {
                        val step = totalW / barCount
                        val barW = (step * 0.42f).coerceIn(2.2f, 5.0f)
                        val baseY = totalH - 3f
                        val crestPath = Path()
                        var prevX = 0f; var prevY = baseY
                        for (i in 0 until barCount) {
                            val cx = i * step + step * 0.5f
                            val isPlayed = cx <= playheadX
                            val barH = ((totalH - 8f) * animatedLevels[i]).coerceAtLeast(3.5f)
                            val topY = baseY - barH
                            val col = if (isPlayed) playedColor else unplayedColor
                            drawRoundRect(
                                brush = Brush.verticalGradient(0.0f to col, 0.72f to col.copy(alpha = if (isPlayed) 0.65f else 0.35f), 1.0f to Color.Transparent, startY = topY, endY = baseY),
                                topLeft = Offset(cx - barW * 0.5f, topY),
                                size = Size(barW, barH),
                                cornerRadius = CornerRadius(barW * 0.5f, barW * 0.5f),
                            )
                            val crestY = (topY - 2f).coerceAtLeast(2f)
                            if (i == 0) crestPath.moveTo(cx, crestY)
                            else crestPath.quadraticTo(prevX, prevY, (prevX + cx) * 0.5f, (prevY + crestY) * 0.5f)
                            prevX = cx; prevY = crestY
                        }
                        crestPath.lineTo(prevX, prevY)
                        clipRect(0f, 0f, playheadX, totalH) {
                            drawPath(crestPath, color = playedColor.copy(alpha = 0.22f), style = Stroke(6f, cap = StrokeCap.Round))
                            drawPath(crestPath, color = playedColor.copy(alpha = 0.92f), style = Stroke(1.9f, cap = StrokeCap.Round))
                        }
                        clipRect(playheadX, 0f, totalW, totalH) {
                            drawPath(crestPath, color = unplayedColor.copy(alpha = 0.72f), style = Stroke(1.4f, cap = StrokeCap.Round))
                        }
                    }

                    DesktopSpectrumStyle.HARMONIC_STRINGS -> {
                        val midY = totalH * 0.52f
                        val step = totalW / (barCount - 1)
                        for (layer in 0..2) {
                            val ampScale = when (layer) { 0 -> 0.44f; 1 -> 0.30f; else -> 0.19f }
                            val path = Path()
                            var px = 0f; var py = midY
                            for (i in 0 until barCount) {
                                val x = i * step
                                val u = i.toFloat() / (barCount - 1)
                                val edgeTap = sin(u * PI).toFloat().coerceAtLeast(0.08f)
                                val osc = sin(phaseTime * (1.1f + layer * 0.25f) + i * 0.32f + layer * 1.45f)
                                val y = midY + osc * animatedLevels[i] * totalH * ampScale * edgeTap
                                if (i == 0) path.moveTo(x, y) else path.quadraticTo(px, py, (px + x) * 0.5f, (py + y) * 0.5f)
                                px = x; py = y
                            }
                            path.lineTo(px, py)
                            val strokeW = when (layer) { 0 -> 2.4f; 1 -> 1.6f; else -> 1.1f }
                            val alphaP = when (layer) { 0 -> 0.94f; 1 -> 0.56f; else -> 0.32f }
                            clipRect(0f, 0f, playheadX, totalH) {
                                if (layer == 0) drawPath(path, color = playedColor.copy(alpha = 0.20f), style = Stroke(7f, cap = StrokeCap.Round))
                                drawPath(path, color = playedColor.copy(alpha = alphaP), style = Stroke(strokeW, cap = StrokeCap.Round))
                            }
                            clipRect(playheadX, 0f, totalW, totalH) {
                                drawPath(path, color = unplayedColor.copy(alpha = alphaP * 0.72f), style = Stroke(strokeW * 0.85f, cap = StrokeCap.Round))
                            }
                        }
                    }

                    DesktopSpectrumStyle.DOT_MATRIX -> {
                        val step = totalW / barCount
                        val rows = 8
                        val rowH = (totalH - 6f) / rows
                        val dotR = (minOf(step, rowH) * 0.30f).coerceIn(1.5f, 2.8f)
                        val baseY = totalH - 3f
                        for (i in 0 until barCount) {
                            val cx = i * step + step * 0.5f
                            val isPlayed = cx <= playheadX
                            val activeRows = (animatedLevels[i] * rows).roundToInt().coerceIn(1, rows)
                            val col = if (isPlayed) playedColor else unplayedColor
                            for (r in 0 until activeRows) {
                                val cy = baseY - r * rowH - rowH * 0.5f
                                drawCircle(col.copy(alpha = 0.45f + 0.55f * (r.toFloat() / rows)), radius = dotR, center = Offset(cx, cy))
                            }
                            val peakRow = (peakDots[i] * (rows - 1)).coerceIn(0f, (rows - 1).toFloat())
                            val peakY = baseY - peakRow * rowH - rowH * 0.5f
                            drawCircle(if (isPlayed) accentColor else unplayedColor.copy(alpha = 0.85f), radius = dotR * 1.18f, center = Offset(cx, peakY))
                        }
                    }

                    DesktopSpectrumStyle.INK_MOUNTAIN -> {
                        val baseY = totalH - 2f
                        val step = totalW / (barCount - 1)
                        val backPath = Path().apply {
                            moveTo(0f, baseY)
                            var px = 0f; var py = baseY
                            for (i in 0 until barCount) {
                                val x = i * step
                                val y = baseY - (totalH - 10f) * (animatedLevels[(i + 4) % barCount] * 0.72f + 0.08f)
                                if (i == 0) lineTo(x, y) else quadraticTo(px, py, (px + x) * 0.5f, (py + y) * 0.5f)
                                px = x; py = y
                            }
                            lineTo(totalW, baseY); close()
                        }
                        drawPath(backPath, Brush.verticalGradient(0.0f to unplayedColor.copy(alpha = 0.36f), 1.0f to Color.Transparent))
                        val frontPath = Path().apply {
                            moveTo(0f, baseY)
                            var px = 0f; var py = baseY
                            for (i in 0 until barCount) {
                                val x = i * step
                                val y = baseY - (totalH - 6f) * (animatedLevels[i] * 0.94f + 0.05f)
                                if (i == 0) lineTo(x, y) else quadraticTo(px, py, (px + x) * 0.5f, (py + y) * 0.5f)
                                px = x; py = y
                            }
                            lineTo(totalW, baseY); close()
                        }
                        clipRect(0f, 0f, playheadX, totalH) {
                            drawPath(frontPath, Brush.verticalGradient(0.0f to playedColor.copy(alpha = 0.72f), 0.85f to playedColor.copy(alpha = 0.16f), 1.0f to Color.Transparent))
                        }
                        clipRect(playheadX, 0f, totalW, totalH) {
                            drawPath(frontPath, Brush.verticalGradient(0.0f to unplayedColor.copy(alpha = 0.45f), 1.0f to Color.Transparent))
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatDuration(displayPosMs), fontSize = 11.sp, color = timestampColor)
            Text(formatDuration(durationMs), fontSize = 11.sp, color = timestampColor)
        }
    }
}

@Composable
fun DesktopImmersiveControlMenu(
    expanded: Boolean,
    isDarkSurface: Boolean,
    currentScene: DesktopSceneStyle,
    currentSpectrum: DesktopSpectrumStyle,
    lyricOffsetMs: Long,
    accentColor: Color,
    onDismiss: () -> Unit,
    onSelectScene: (DesktopSceneStyle) -> Unit,
    onSelectSpectrum: (DesktopSpectrumStyle) -> Unit,
    onAdjustLyricOffset: (Long) -> Unit,
    onResetLyricOffset: () -> Unit,
) {
    val menuBg = if (isDarkSurface) Color(0xF21A1918) else Color(0xF5FBF9F4)
    val borderColor = if (isDarkSurface) Color.White.copy(alpha = 0.14f) else Color(0xFFDDD6C8)
    val ink = if (isDarkSurface) Color(0xFFF2ECE1) else Color(0xFF1E1B18)
    val muted = if (isDarkSurface) Color(0xFF9C948A) else Color(0xFF6B655D)
    val activeAccent = if (isDarkSurface) Color(0xFFE0C08E) else accentColor
    val pillBg = if (isDarkSurface) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.045f)

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier
            .width(264.dp)
            .background(menuBg, RoundedCornerShape(16.dp))
            .border(0.75.dp, borderColor, RoundedCornerShape(16.dp)),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("沉浸场景", fontSize = 11.sp, color = muted, fontWeight = FontWeight.Medium)
                DesktopSceneStyle.entries.chunked(2).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        rowItems.forEach { st ->
                            val selected = st == currentScene
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) activeAccent.copy(alpha = 0.18f) else pillBg)
                                    .border(0.75.dp, if (selected) activeAccent else Color.Transparent, RoundedCornerShape(10.dp))
                                    .clickable { onSelectScene(st) }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(st.label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, color = if (selected) activeAccent else ink, maxLines = 1)
                            }
                        }
                        if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            HorizontalDivider(thickness = 0.6.dp, color = borderColor)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("实时频谱", fontSize = 11.sp, color = muted, fontWeight = FontWeight.Medium)
                DesktopSpectrumStyle.entries.chunked(2).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        rowItems.forEach { sp ->
                            val selected = sp == currentSpectrum
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) activeAccent.copy(alpha = 0.18f) else pillBg)
                                    .border(0.75.dp, if (selected) activeAccent else Color.Transparent, RoundedCornerShape(10.dp))
                                    .clickable { onSelectSpectrum(sp) }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(sp.label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, color = if (selected) activeAccent else ink, maxLines = 1)
                            }
                        }
                    }
                }
            }

            HorizontalDivider(thickness = 0.6.dp, color = borderColor)

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("歌词微调 ${if (lyricOffsetMs >= 0) "+" else ""}${String.format("%.1fs", lyricOffsetMs / 1000f)}", fontSize = 11.sp, color = muted)
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    listOf("-0.5s" to -500L, "归零" to 0L, "+0.5s" to 500L).forEach { (lbl, delta) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(pillBg)
                                .clickable { if (delta == 0L) onResetLyricOffset() else onAdjustLyricOffset(delta) }
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                        ) {
                            Text(lbl, fontSize = 11.sp, color = ink)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DesktopSplashOverlay(visible: Boolean, darkMode: Boolean) {
    AnimatedVisibility(visible = visible, enter = fadeIn(tween(100)), exit = fadeOut(tween(420)), modifier = Modifier.fillMaxSize()) {
        val bg = if (darkMode) Color(0xFF141312) else Color(0xFFF5F0E6)
        val ink = if (darkMode) Color(0xFFF2ECE1) else Color(0xFF1E1B18)
        val muted = if (darkMode) Color(0xFF9C9388) else Color(0xFF8C7E6D)
        Box(modifier = Modifier.fillMaxSize().background(bg), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(116.dp), contentAlignment = Alignment.BottomCenter) {
                    Box(modifier = Modifier.align(Alignment.TopCenter).size(84.dp).clip(CircleShape).background(Color(0xFF0E0E10)).border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFA9321E)))
                    }
                    Box(modifier = Modifier.size(88.dp).shadow(14.dp, RoundedCornerShape(10.dp)).clip(RoundedCornerShape(10.dp)).background(Color(0xFFA9321E)), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.size(34.dp).border(2.2.dp, Color(0xFFF4E8D4), CircleShape), contentAlignment = Alignment.Center) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFF4E8D4)))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text("简约音乐", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ink, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("PURE MUSIC · HI-RES AUDIO", fontSize = 10.sp, color = muted, letterSpacing = 2.4.sp)
            }
        }
    }
}
