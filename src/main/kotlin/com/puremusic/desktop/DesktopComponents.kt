package com.puremusic.desktop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.material.icons.rounded.MusicNote
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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
import kotlin.math.roundToInt
import kotlin.math.sin

object ArtworkMemoryCache {
    private val map = object : LinkedHashMap<String, ImageBitmap?>(96, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, ImageBitmap?>?): Boolean = size > 140
    }

    @Synchronized fun get(key: String): ImageBitmap? = map[key]
    @Synchronized fun contains(key: String): Boolean = map.containsKey(key)
    @Synchronized fun put(key: String, bmp: ImageBitmap?) { map[key] = bmp }
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
    val specBorder = if (isDarkSurface) Color.White.copy(alpha = 0.24f) else MaterialTheme.colorScheme.outline
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
            .shadow(22.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.55f))
            .graphicsLayer { rotationZ = rotationDegrees },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f
            val c = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                brush = Brush.radialGradient(
                    0.0f to Color(0xFF151517),
                    0.66f to Color(0xFF0A0A0C),
                    0.92f to Color(0xFF1C1C20),
                    1.0f to Color(0xFF070709),
                    center = c,
                    radius = r,
                ),
                radius = r,
                center = c,
            )
            // Fine micro-grooves
            for (g in 1..28) {
                val frac = 0.38f + g * 0.0205f
                drawCircle(
                    color = Color.White.copy(alpha = if (g % 5 == 0) 0.075f else 0.032f),
                    radius = r * frac,
                    center = c,
                    style = Stroke(width = 0.7f),
                )
            }
            // Dual specular light sweeps
            drawCircle(
                brush = Brush.sweepGradient(
                    0.00f to Color.Transparent,
                    0.10f to Color.White.copy(alpha = 0.14f),
                    0.22f to Color.Transparent,
                    0.52f to Color.Transparent,
                    0.62f to Color.White.copy(alpha = 0.12f),
                    0.74f to Color.Transparent,
                    1.00f to Color.Transparent,
                    center = c,
                ),
                radius = r * 0.97f,
                center = c,
            )
            drawCircle(color = Color.White.copy(alpha = 0.14f), radius = r - 0.6f, center = c, style = Stroke(1.1f))
        }

        DesktopAlbumCover(
            filePath = filePath,
            modifier = Modifier
                .size(discSize * 0.42f)
                .border(1.5.dp, Color(0xFF282624), CircleShape),
            cornerRadius = discSize * 0.21f,
            iconSize = discSize * 0.14f,
        )

        Box(
            modifier = Modifier
                .size(discSize * 0.068f)
                .clip(CircleShape)
                .background(Color(0xFF141312))
                .border(1.2.dp, Color(0xFFDFD7C8), CircleShape),
        )
    }
}

@Composable
fun DesktopTonearm(
    isPlaying: Boolean,
    length: Dp = 156.dp,
    modifier: Modifier = Modifier,
) {
    val angle by animateFloatAsState(
        targetValue = if (isPlaying) 19f else -10f,
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

        drawCircle(Color(0xFF1F1D1B), radius = 18f, center = pivot)
        drawCircle(Color(0xFFE0C89B), radius = 11f, center = pivot)
        drawCircle(Color(0xFF2B2825), radius = 4.5f, center = pivot)

        drawLine(Color(0xFFDCD8D0), pivot, elbow, strokeWidth = 4.4f, cap = StrokeCap.Round)
        drawLine(Color(0xFF9C978E), elbow, head, strokeWidth = 3.8f, cap = StrokeCap.Round)
        drawLine(Color(0xFF1D1C1A), head, Offset(head.x - 10f, head.y + 13f), strokeWidth = 10f, cap = StrokeCap.Round)
        drawCircle(Color(0xFFE0C89B), radius = 2.6f, center = Offset(head.x - 9f, head.y + 12f))
    }
}

/**
 * Flagship 3D Spatial Record Gallery (Desktop Widescreen 1:1 port of Android v2.0.3).
 * Features commanding hero sleeve, 8.5dp brushed-aluminum left spine, 3.5dp top bevel,
 * diagonal satin sheen, 5 stepped receding sleeves with peeking black vinyl LPs,
 * inter-sleeve cast shadow, dark AO contact line, and seamless parallel mirror reflection.
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
        if (allSongs.isEmpty()) {
            listOf(currentSong)
        } else {
            val idx = allSongs.indexOfFirst { it.id == currentSong.id }.coerceAtLeast(0)
            List(minOf(6, allSongs.size)) { step ->
                allSongs[(idx + step) % allSongs.size]
            }
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
        val frontSize = min(maxWidth * 0.38f, maxHeight * 0.68f).coerceIn(210.dp, 340.dp)
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
                val spineW = 8.5.dp
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
                    Box(
                        modifier = Modifier
                            .width(frontSize + 24.dp)
                            .height(frontSize),
                    ) {
                        // Sleek glossy black vinyl LP peeking out on receding sleeves
                        if (depth in 1..4) {
                            Canvas(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(frontSize * 0.86f),
                            ) {
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
                                for (g in 1..5) {
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.06f),
                                        radius = r * (0.64f + g * 0.065f),
                                        center = c,
                                        style = Stroke(width = 0.8f),
                                    )
                                }
                            }
                        }

                        // Physical brushed-aluminum-spined vinyl jacket
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .size(frontSize)
                                .shadow(
                                    elevation = if (isFront) 32.dp else 18.dp,
                                    shape = RoundedCornerShape(3.dp),
                                    ambientColor = Color.Black,
                                    spotColor = Color.Black,
                                )
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF1E1D1C)),
                        ) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                // 8.5dp Machined Brushed-Aluminum 3D Left Spine
                                Box(
                                    modifier = Modifier
                                        .width(spineW)
                                        .fillMaxSize()
                                        .background(
                                            Brush.horizontalGradient(
                                                0.00f to Color(0xFF6B6A67),
                                                0.18f to Color(0xFFEBE9E5),
                                                0.46f to Color(0xFFC5C3BE),
                                                0.78f to Color(0xFF7D7B77),
                                                0.94f to Color(0xFF3E3D3B),
                                                1.00f to Color(0xFF1A1918),
                                            ),
                                        ),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    0.0f to Color.White.copy(alpha = 0.24f),
                                                    0.25f to Color.Transparent,
                                                    0.78f to Color.Black.copy(alpha = 0.18f),
                                                    1.0f to Color.Black.copy(alpha = 0.36f),
                                                ),
                                            ),
                                    )
                                }

                                Column(modifier = Modifier.weight(1f).fillMaxSize()) {
                                    // 3.5dp Beveled Metallic Top Rim
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(topRimH)
                                            .background(
                                                Brush.verticalGradient(
                                                    0.0f to Color(0xFFF0EEEA),
                                                    0.55f to Color(0xFFB5B3AE),
                                                    1.0f to Color(0xFF4A4946),
                                                ),
                                            ),
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
                                                            0.32f to Color.White.copy(alpha = 0.03f),
                                                            0.52f to Color.Transparent,
                                                            0.82f to Color.Black.copy(alpha = 0.06f),
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

                    // Contact Ambient Occlusion (AO) floor shadow anchoring sleeve firmly
                    Box(
                        modifier = Modifier
                            .width(frontSize)
                            .height(2.5.dp)
                            .background(
                                Brush.verticalGradient(
                                    0.0f to Color.Black.copy(alpha = if (isFront) 0.88f else 0.65f),
                                    1.0f to Color.Black.copy(alpha = 0.0f),
                                ),
                            ),
                    )

                    // Seamless parallel floor mirror reflection (zero V-gap)
                    if (depth <= 3) {
                        val reflStrength = when (depth) {
                            0 -> 0.40f
                            1 -> 0.20f
                            2 -> 0.11f
                            else -> 0.06f
                        }
                        Row(
                            modifier = Modifier
                                .width(frontSize)
                                .height(frontSize * 0.30f)
                                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                                .drawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = Brush.verticalGradient(
                                            0.00f to Color.Black.copy(alpha = reflStrength),
                                            0.52f to Color.Black.copy(alpha = reflStrength * 0.26f),
                                            1.00f to Color.Transparent,
                                        ),
                                        blendMode = BlendMode.DstIn,
                                    )
                                }
                                .clip(RectangleShape),
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(spineW)
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            0.0f to Color(0xFF4F4E4B),
                                            0.35f to Color(0xFFA8A6A1),
                                            1.0f to Color(0xFF2B2A28),
                                        ),
                                    ),
                            )
                            DesktopAlbumCover(
                                filePath = itemSong.filePath,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(frontSize)
                                    .graphicsLayer { scaleY = -1f },
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

/**
 * Fixed 3-Slot Bilingual Lyrics Stage (prev / bold active / translation or next).
 */
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
            activePrimary.length <= 20 -> 24.sp
            activePrimary.length <= 34 -> 20.sp
            activePrimary.length <= 52 -> 17.sp
            else -> 14.5.sp
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.Center,
    ) {
        if (lyrics.isEmpty()) {
            Text(
                text = "纯粹聆听 · 静享此刻",
                fontSize = 15.sp,
                color = mutedColor,
                textAlign = textAlign,
            )
            return@Column
        }

        Text(
            text = line1Text,
            fontSize = 13.5.sp,
            color = mutedColor.copy(alpha = 0.72f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign,
            modifier = Modifier.fillMaxWidth().height(22.dp),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(64.dp),
            contentAlignment = if (horizontalAlignment == Alignment.Start) Alignment.CenterStart else Alignment.Center,
        ) {
            Text(
                text = activePrimary,
                fontSize = dynamicFontSize,
                lineHeight = dynamicFontSize * 1.32f,
                fontWeight = FontWeight.Bold,
                color = activeColor,
                textAlign = textAlign,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = line3Text,
            fontSize = 14.sp,
            color = if (!activeSecondary.isNullOrBlank()) secondaryColor.copy(alpha = 0.90f) else mutedColor.copy(alpha = 0.72f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign,
            modifier = Modifier.fillMaxWidth().height(22.dp),
        )
    }
}

/**
 * Silky 60fps Live 256-Point FFT Audio Spectrum SeekBar (Zero vertical playhead sticks!).
 * Reads DesktopSpectrumBus.bands.value live every frame via withFrameNanos (zero stale closure).
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

                // Live read from DesktopSpectrumBus every single frame (no stale Compose closure!)
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

                    // Physics gravity for DOT_MATRIX peak dots
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
                .height(64.dp)
                .onSizeChanged { widthPx = it.width.toFloat().coerceAtLeast(1f) }
                .pointerInput(durationMs) {
                    detectTapGestures { offset ->
                        onSeekTo(((offset.x / widthPx).coerceIn(0f, 1f) * durationMs).toLong())
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
            Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
                val _tick = frameTick
                val totalW = size.width
                val totalH = size.height
                val playheadX = (totalW * progress).coerceIn(0f, totalW)

                when (spectrumStyle) {
                    DesktopSpectrumStyle.SILK_RIBBON -> {
                        val step = totalW / barCount
                        val barW = (step * 0.44f).coerceIn(2.2f, 5.2f)
                        val baseY = totalH - 3f
                        val crestPath = Path()
                        var prevX = 0f
                        var prevY = baseY

                        for (i in 0 until barCount) {
                            val cx = i * step + step * 0.5f
                            val isPlayed = cx <= playheadX
                            val lvl = animatedLevels[i]
                            val barH = ((totalH - 8f) * lvl).coerceAtLeast(3.5f)
                            val topY = baseY - barH
                            val col = if (isPlayed) playedColor else unplayedColor

                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    0.0f to col,
                                    0.72f to col.copy(alpha = if (isPlayed) 0.65f else 0.35f),
                                    1.0f to Color.Transparent,
                                    startY = topY,
                                    endY = baseY,
                                ),
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
                            drawPath(crestPath, color = playedColor.copy(alpha = 0.22f), style = Stroke(width = 6f, cap = StrokeCap.Round))
                            drawPath(crestPath, color = playedColor.copy(alpha = 0.92f), style = Stroke(width = 1.9f, cap = StrokeCap.Round))
                        }
                        clipRect(playheadX, 0f, totalW, totalH) {
                            drawPath(crestPath, color = unplayedColor.copy(alpha = 0.72f), style = Stroke(width = 1.4f, cap = StrokeCap.Round))
                        }
                    }

                    DesktopSpectrumStyle.HARMONIC_STRINGS -> {
                        val midY = totalH * 0.52f
                        val step = totalW / (barCount - 1)
                        for (layer in 0..2) {
                            val ampScale = when (layer) {
                                0 -> 0.44f
                                1 -> 0.30f
                                else -> 0.19f
                            }
                            val phaseShift = layer * 1.45f
                            val path = Path()
                            var px = 0f
                            var py = midY
                            for (i in 0 until barCount) {
                                val x = i * step
                                val u = i.toFloat() / (barCount - 1)
                                val edgeTap = sin(u * PI).toFloat().coerceAtLeast(0.08f)
                                val osc = sin(phaseTime * (1.1f + layer * 0.25f) + i * 0.32f + phaseShift)
                                val y = midY + osc * animatedLevels[i] * totalH * ampScale * edgeTap
                                if (i == 0) path.moveTo(x, y)
                                else path.quadraticTo(px, py, (px + x) * 0.5f, (py + y) * 0.5f)
                                px = x; py = y
                            }
                            path.lineTo(px, py)
                            val strokeW = when (layer) {
                                0 -> 2.4f
                                1 -> 1.6f
                                else -> 1.1f
                            }
                            val alphaPlayed = when (layer) {
                                0 -> 0.94f
                                1 -> 0.56f
                                else -> 0.32f
                            }
                            clipRect(0f, 0f, playheadX, totalH) {
                                if (layer == 0) {
                                    drawPath(path, color = playedColor.copy(alpha = 0.20f), style = Stroke(7f, cap = StrokeCap.Round))
                                }
                                drawPath(path, color = playedColor.copy(alpha = alphaPlayed), style = Stroke(strokeW, cap = StrokeCap.Round))
                            }
                            clipRect(playheadX, 0f, totalW, totalH) {
                                drawPath(path, color = unplayedColor.copy(alpha = alphaPlayed * 0.72f), style = Stroke(strokeW * 0.85f, cap = StrokeCap.Round))
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
                                val fade = 0.45f + 0.55f * (r.toFloat() / rows)
                                drawCircle(col.copy(alpha = fade), radius = dotR, center = Offset(cx, cy))
                            }
                            // Physics gravity falling peak dot
                            val peakRow = (peakDots[i] * (rows - 1)).coerceIn(0f, (rows - 1).toFloat())
                            val peakY = baseY - peakRow * rowH - rowH * 0.5f
                            drawCircle(
                                color = if (isPlayed) accentColor else unplayedColor.copy(alpha = 0.85f),
                                radius = dotR * 1.18f,
                                center = Offset(cx, peakY),
                            )
                        }
                    }

                    DesktopSpectrumStyle.INK_MOUNTAIN -> {
                        val baseY = totalH - 2f
                        val step = totalW / (barCount - 1)
                        // Distant secondary ink mountain
                        val backPath = Path().apply {
                            moveTo(0f, baseY)
                            var px = 0f
                            var py = baseY
                            for (i in 0 until barCount) {
                                val x = i * step
                                val shifted = animatedLevels[(i + 4) % barCount]
                                val y = baseY - (totalH - 10f) * (shifted * 0.72f + 0.08f)
                                if (i == 0) lineTo(x, y) else quadraticTo(px, py, (px + x) * 0.5f, (py + y) * 0.5f)
                                px = x; py = y
                            }
                            lineTo(totalW, baseY)
                            close()
                        }
                        drawPath(
                            path = backPath,
                            brush = Brush.verticalGradient(
                                0.0f to unplayedColor.copy(alpha = 0.36f),
                                1.0f to Color.Transparent,
                            ),
                        )

                        // Foreground main ink mountain
                        val frontPath = Path().apply {
                            moveTo(0f, baseY)
                            var px = 0f
                            var py = baseY
                            for (i in 0 until barCount) {
                                val x = i * step
                                val y = baseY - (totalH - 6f) * (animatedLevels[i] * 0.94f + 0.05f)
                                if (i == 0) lineTo(x, y) else quadraticTo(px, py, (px + x) * 0.5f, (py + y) * 0.5f)
                                px = x; py = y
                            }
                            lineTo(totalW, baseY)
                            close()
                        }
                        clipRect(0f, 0f, playheadX, totalH) {
                            drawPath(
                                path = frontPath,
                                brush = Brush.verticalGradient(
                                    0.0f to playedColor.copy(alpha = 0.72f),
                                    0.85f to playedColor.copy(alpha = 0.16f),
                                    1.0f to Color.Transparent,
                                ),
                            )
                        }
                        clipRect(playheadX, 0f, totalW, totalH) {
                            drawPath(
                                path = frontPath,
                                brush = Brush.verticalGradient(
                                    0.0f to unplayedColor.copy(alpha = 0.45f),
                                    1.0f to Color.Transparent,
                                ),
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = formatDuration(displayPosMs), fontSize = 11.sp, color = timestampColor)
            Text(text = formatDuration(durationMs), fontSize = 11.sp, color = timestampColor)
        }
    }
}

/**
 * Compact 248dp Top-Right 2x2 Floating Control Menu (1:1 port of Android v2.0 ImmersiveControlSheet).
 * Keeps bottom lyrics and 60fps spectrum 100% unobstructed while live-previewing!
 */
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
            .width(248.dp)
            .background(menuBg, RoundedCornerShape(16.dp))
            .border(0.75.dp, borderColor, RoundedCornerShape(16.dp)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
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
                                    .border(
                                        0.75.dp,
                                        if (selected) activeAccent else Color.Transparent,
                                        RoundedCornerShape(10.dp),
                                    )
                                    .clickable { onSelectScene(st) }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = st.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (selected) activeAccent else ink,
                                    maxLines = 1,
                                )
                            }
                        }
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
                                    .border(
                                        0.75.dp,
                                        if (selected) activeAccent else Color.Transparent,
                                        RoundedCornerShape(10.dp),
                                    )
                                    .clickable { onSelectSpectrum(sp) }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = sp.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (selected) activeAccent else ink,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(thickness = 0.6.dp, color = borderColor)

            // Lyric Time Offset Micro-Adjuster
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "歌词微调 ${if (lyricOffsetMs >= 0) "+" else ""}${String.format("%.1fs", lyricOffsetMs / 1000f)}",
                    fontSize = 11.sp,
                    color = muted,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    listOf("-0.5s" to -500L, "归零" to 0L, "+0.5s" to 500L).forEach { (lbl, delta) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(pillBg)
                                .clickable {
                                    if (delta == 0L) onResetLyricOffset() else onAdjustLyricOffset(delta)
                                }
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

/**
 * Quiet-luxury Cinnabar Archival Sleeve Cold-Start Splash Overlay (matches Android v2.0).
 */
@Composable
fun DesktopSplashOverlay(
    visible: Boolean,
    darkMode: Boolean,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(100)),
        exit = fadeOut(tween(420)),
        modifier = Modifier.fillMaxSize(),
    ) {
        val bg = if (darkMode) Color(0xFF141312) else Color(0xFFF5F0E6)
        val ink = if (darkMode) Color(0xFFF2ECE1) else Color(0xFF1E1B18)
        val muted = if (darkMode) Color(0xFF9C9388) else Color(0xFF8C7E6D)
        Box(
            modifier = Modifier.fillMaxSize().background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(116.dp), contentAlignment = Alignment.BottomCenter) {
                    // Rising glossy black vinyl disc
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0E0E10))
                            .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFA9321E)),
                        )
                    }
                    // Cinnabar archival sleeve
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .shadow(14.dp, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFA9321E)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .border(2.2.dp, Color(0xFFF4E8D4), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF4E8D4)),
                            )
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


/**
 * Oversized Widescreen Magazine Editorial Lyrics (36–46sp bold active line, matching reference proportions).
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
    maxActiveSp: Float = 44f,
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
            activePrimary.length <= 28 -> (maxActiveSp * 0.84f).coerceAtLeast(28f)
            activePrimary.length <= 44 -> (maxActiveSp * 0.68f).coerceAtLeast(24f)
            else -> (maxActiveSp * 0.56f).coerceAtLeast(20f)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.Center,
    ) {
        if (lyrics.isEmpty()) {
            Text(
                text = "纯粹聆听 · 静享此刻",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = mutedColor,
                textAlign = textAlign,
            )
            return@Column
        }

        if (prevText.isNotBlank()) {
            Text(
                text = prevText,
                fontSize = 15.sp,
                color = mutedColor.copy(alpha = 0.75f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = textAlign,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Text(
            text = activePrimary,
            fontSize = dynamicSp.sp,
            lineHeight = (dynamicSp * 1.24f).sp,
            fontWeight = FontWeight.Bold,
            color = if (highlightAccent) accentColor else activeColor,
            textAlign = textAlign,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )

        if (subText.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = subText,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                color = secondaryColor.copy(alpha = 0.88f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = textAlign,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * Full-bleed Clickable Local Album Poster Wall + Giant Centered Spinning Vinyl LP (matching reference #3).
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
    val wallSongs = remember(allSongs, currentSong.id) {
        val uniqueAlbums = allSongs.distinctBy { it.album }.ifEmpty { listOf(currentSong) }
        List(32) { idx -> uniqueAlbums[idx % uniqueAlbums.size] }
    }
    val activeLine = if (activeLyricIdx in lyrics.indices) lyrics[activeLyricIdx] else null
    val activeText = remember(activeLine) {
        if (activeLine == null) "" else splitBilingualLyric(activeLine.primaryText, activeLine.secondaryText).first
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val discSize = min(maxHeight * 0.62f, maxWidth * 0.34f).coerceIn(220.dp, 360.dp)

        // 8x4 Full-bleed clickable album cover mosaic backdrop
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            wallSongs.chunked(8).forEach { rowSongs ->
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowSongs.forEach { tileSong ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onSelectSong(tileSong) },
                        ) {
                            DesktopAlbumCover(
                                filePath = tileSong.filePath,
                                modifier = Modifier.fillMaxSize(),
                                cornerRadius = 6.dp,
                                iconSize = 24.dp,
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = if (tileSong.id == currentSong.id) 0.18f else 0.48f)),
                            )
                        }
                    }
                }
            }
        }

        // Dramatic radial & bottom dark vignette so vinyl & lyrics pop cleanly
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Black.copy(alpha = 0.25f),
                        0.45f to Color.Black.copy(alpha = 0.20f),
                        0.76f to Color.Black.copy(alpha = 0.72f),
                        1.0f to Color.Black.copy(alpha = 0.92f),
                    ),
                ),
        )

        // Giant Centered Spinning Vinyl LP + Song Info + Bold Single-Line Lyric
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            DesktopSpinningVinylDisc(
                filePath = currentSong.filePath,
                rotationDegrees = vinylRotation,
                discSize = discSize,
                modifier = Modifier.clickable(onClick = onTogglePlayPause),
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = currentSong.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF6F2EA),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = currentSong.artist,
                fontSize = 14.sp,
                color = Color(0xFFBDB6AC),
                maxLines = 1,
            )
            if (activeText.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = activeText,
                    fontSize = 24.sp,
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
 * Vintage Hi-Fi Walnut-Framed Brushed-Aluminum Stereo Receiver Deck (matching reference #4).
 * Features REAL 60fps stereo L/R analog VU meters driven by DesktopSpectrumBus.stereoVu,
 * machined brushed-metal buttons, physical repeat/shuffle keys, and draggable rotary volume knob.
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

    // Outer Walnut Wood Cabinet Frame
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(214.dp)
            .shadow(24.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    0.0f to Color(0xFF6B3E22),
                    0.14f to Color(0xFF4A2814),
                    0.86f to Color(0xFF3A1E0E),
                    1.0f to Color(0xFF241106),
                ),
            )
            .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 10.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top thin warm-gold seek strip embedded into walnut bevel
            val prog = if (playerState.durationMs > 0L) (playerState.positionMs.toFloat() / playerState.durationMs).coerceIn(0f, 1f) else 0f
            var barW by remember { mutableFloatStateOf(1f) }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(formatDuration(playerState.positionMs), fontSize = 10.sp, color = Color(0xFFE0CCA8))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .onSizeChanged { barW = it.width.toFloat().coerceAtLeast(1f) }
                        .pointerInput(playerState.durationMs) {
                            detectTapGestures { off ->
                                onSeekTo(((off.x / barW).coerceIn(0f, 1f) * playerState.durationMs).toLong())
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(2.5.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.55f))) {
                        Box(modifier = Modifier.fillMaxWidth(prog).fillMaxHeight().background(Color(0xFFE5C487)))
                    }
                }
                Text(formatDuration(playerState.durationMs), fontSize = 10.sp, color = Color(0xFFE0CCA8))
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Brushed-Aluminum Faceplate
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color(0xFFCFC8BC),
                            0.45f to Color(0xFFB5AEA2),
                            1.0f to Color(0xFF9C9589),
                        ),
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Left: Brand Engraving + Dual Glowing Warm-Amber Analog VU Meters
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    Column {
                        Text("立体声接收机", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF3E3933))
                        Text("PURE MUSIC HI-FI", fontSize = 9.sp, color = Color(0xFF5D574F), letterSpacing = 1.sp)
                    }
                    VintageVuMeterBox(label = "左声道 L", level = vuL)
                    VintageVuMeterBox(label = "右声道 R", level = vuR)
                }

                // Center: Machined Brushed-Metal Transport Buttons + Mode Keys
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        VintageMetalButton(size = 44.dp, onClick = onSkipPrevious) {
                            Icon(Icons.Rounded.SkipPrevious, contentDescription = "上一首", tint = Color(0xFF282522), modifier = Modifier.size(20.dp))
                        }
                        VintageMetalButton(size = 58.dp, onClick = onTogglePlayPause) {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = "播放/暂停",
                                tint = Color(0xFF1F1C19),
                                modifier = Modifier.size(28.dp),
                            )
                        }
                        VintageMetalButton(size = 44.dp, onClick = onSkipNext) {
                            Icon(Icons.Rounded.SkipNext, contentDescription = "下一首", tint = Color(0xFF282522), modifier = Modifier.size(20.dp))
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            Triple("列表循环", playerState.repeatMode == RepeatMode.ALL && !playerState.shuffleEnabled, onCycleRepeat),
                            Triple("单曲循环", playerState.repeatMode == RepeatMode.ONE && !playerState.shuffleEnabled, onCycleRepeat),
                            Triple("随机播放", playerState.shuffleEnabled, onToggleShuffle),
                        ).forEach { (lbl, active, action) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (active) Color(0xFFD9AE64) else Color(0xFFABA397))
                                    .border(0.8.dp, Color(0xFF544E46), RoundedCornerShape(4.dp))
                                    .clickable(onClick = action)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            ) {
                                Text(lbl, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF24201C))
                            }
                        }
                    }
                }

                // Right: Tactile Draggable Rotary Volume Knob
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("音量 ${(playerState.volume * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A3530))
                    Spacer(modifier = Modifier.height(6.dp))
                    val knobAngle = -135f + playerState.volume * 270f
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(10.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    0.00f to Color(0xFFDAD3C7),
                                    0.25f to Color(0xFF9C9589),
                                    0.50f to Color(0xFFE2DCD1),
                                    0.75f to Color(0xFF8C857A),
                                    1.00f to Color(0xFFDAD3C7),
                                ),
                            )
                            .border(2.dp, Color(0xFF47423B), CircleShape)
                            .pointerInput(playerState.volume) {
                                detectVerticalDragGestures { _, dragAmount ->
                                    onVolumeChange((playerState.volume - dragAmount * 0.0045f).coerceIn(0f, 1f))
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { rotationZ = knobAngle }) {
                            drawLine(
                                color = Color(0xFF2B2723),
                                start = Offset(size.width / 2f, size.height * 0.14f),
                                end = Offset(size.width / 2f, size.height * 0.34f),
                                strokeWidth = 3.2f,
                                cap = StrokeCap.Round,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VintageVuMeterBox(label: String, level: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(126.dp)
                .height(74.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0xFFE7A94B),
                        0.55f to Color(0xFFF3C66F),
                        1.0f to Color(0xFFD59034),
                    ),
                )
                .border(2.dp, Color(0xFF29241E), RoundedCornerShape(4.dp)),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val pivot = Offset(size.width * 0.5f, size.height * 1.18f)
                val arcR = size.height * 0.88f
                // Scale tick marks (-20dB to +3dB)
                for (i in 0..10) {
                    val deg = -132.0 + i * 8.4
                    val rad = Math.toRadians(deg)
                    val p1 = Offset(pivot.x + (cos(rad) * (arcR - 8f)).toFloat(), pivot.y + (sin(rad) * (arcR - 8f)).toFloat())
                    val p2 = Offset(pivot.x + (cos(rad) * arcR).toFloat(), pivot.y + (sin(rad) * arcR).toFloat())
                    drawLine(
                        color = if (i >= 8) Color(0xFFB52B1E) else Color(0xFF3A2714),
                        start = p1,
                        end = p2,
                        strokeWidth = if (i % 2 == 0) 2.2f else 1.3f,
                    )
                }
                // Live swinging needle
                val needleDeg = -132.0 + level.coerceIn(0f, 1f) * 84.0
                val nRad = Math.toRadians(needleDeg)
                val tip = Offset(pivot.x + (cos(nRad) * (arcR + 4f)).toFloat(), pivot.y + (sin(nRad) * (arcR + 4f)).toFloat())
                drawLine(Color(0xFF21170E), pivot, tip, strokeWidth = 2.4f, cap = StrokeCap.Round)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, color = Color(0xFF3D3832))
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
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    0.0f to Color(0xFFE3DDD2),
                    0.5f to Color(0xFFB5AEA2),
                    1.0f to Color(0xFF8A8377),
                ),
            )
            .border(1.2.dp, Color(0xFF4D473F), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
