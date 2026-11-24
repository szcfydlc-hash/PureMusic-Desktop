package com.puremusic.desktop

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Album
import androidx.compose.foundation.Image
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.OpenInFull
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PictureInPictureAlt
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.UIManager
import kotlin.math.abs
import kotlin.math.roundToInt

private sealed interface DesktopDetailView {
    data class AlbumView(val album: DesktopAlbum) : DesktopDetailView
    data class ArtistView(val artist: DesktopArtist) : DesktopDetailView
    data class FolderView(val folder: DesktopFolder) : DesktopDetailView
    data class PlaylistView(val playlistId: String) : DesktopDetailView
    data object FavoritesView : DesktopDetailView
    data object RecentView : DesktopDetailView
    data object ListeningReportView : DesktopDetailView
}

fun main() = application {
    runCatching { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) }

    val initialConfig = remember {
        DesktopConfigStore.load().also {
            DesktopLibraryScanner.setCustomOverrides(it.customCoverPaths, it.customLyricPaths)
        }
    }
    var config by remember { mutableStateOf(initialConfig) }

    var allSongs by remember { mutableStateOf<List<DesktopSong>>(emptyList()) }
    var activeQueue by remember { mutableStateOf<List<DesktopSong>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(-1) }
    var isScanning by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf(SortOrder.TITLE) }
    var activeTab by remember { mutableStateOf(DesktopTab.LIBRARY) }
    var librarySubTab by remember { mutableStateOf(LibrarySubTab.SONGS) }
    var activeDetail by remember { mutableStateOf<DesktopDetailView?>(null) }

    var showImmersiveStage by remember { mutableStateOf(false) }
    var showSpectrumInImmersive by remember { mutableStateOf(true) }
    var showMiniVinylWindow by remember { mutableStateOf(false) }
    var showSplash by remember { mutableStateOf(true) }
    var showQueueDrawer by remember { mutableStateOf(false) }
    var showEqDrawer by remember { mutableStateOf(false) }

    var playerState by remember {
        mutableStateOf(
            DesktopPlayerState(
                volume = initialConfig.volume,
                repeatMode = initialConfig.repeatMode,
                shuffleEnabled = initialConfig.shuffleEnabled,
            ),
        )
    }
    var currentLyrics by remember { mutableStateOf<List<LyricLine>>(emptyList()) }
    var isTranslatingLyrics by remember { mutableStateOf(false) }
    var translateStatusTip by remember { mutableStateOf<String?>(null) }
    var sleepTimerRemainingSec by remember { mutableStateOf<Int?>(null) }

    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var renamingPlaylist by remember { mutableStateOf<DesktopPlaylist?>(null) }
    var addingSongToPlaylist by remember { mutableStateOf<DesktopSong?>(null) }

    val scope = rememberCoroutineScope()

    fun updateConfig(transform: (DesktopConfig) -> DesktopConfig) {
        val updated = transform(config)
        config = updated
        DesktopLibraryScanner.setCustomOverrides(updated.customCoverPaths, updated.customLyricPaths)
        scope.launch(Dispatchers.IO) { DesktopConfigStore.save(updated) }
    }

    fun pickSingleFile(dialogTitle: String, filterDesc: String, vararg exts: String): String? {
        return runCatching {
            val chooser = JFileChooser().apply {
                this.dialogTitle = dialogTitle
                fileSelectionMode = JFileChooser.FILES_ONLY
                isAcceptAllFileFilterUsed = true
                if (exts.isNotEmpty()) {
                    fileFilter = FileNameExtensionFilter(filterDesc, *exts)
                }
            }
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                chooser.selectedFile?.absolutePath
            } else null
        }.getOrNull()
    }

    lateinit var engine: DesktopAudioEngine

    fun playFromQueue(queue: List<DesktopSong>, index: Int) {
        if (queue.isEmpty() || index !in queue.indices) return
        val song = queue[index]
        activeQueue = queue
        currentIndex = index
        playerState = playerState.copy(
            currentSong = song,
            isPlaying = true,
            positionMs = 0L,
            durationMs = song.durationMs,
        )
        engine.playFile(File(song.filePath), song.durationMs)
        scope.launch(Dispatchers.IO) {
            currentLyrics = DesktopLibraryScanner.loadLyricsForSong(song)
        }
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).coerceIn(0, 23)
        updateConfig { cfg ->
            val nextRecent = (listOf(song.filePath) + cfg.recentPaths.filterNot { it == song.filePath }).take(50)
            val nextCounts = cfg.playCounts.toMutableMap().apply {
                this[song.filePath] = (this[song.filePath] ?: 0) + 1
            }
            val nextHourly = cfg.hourlyPlays.toMutableMap().apply {
                this[hour] = (this[hour] ?: 0) + 1
            }
            cfg.copy(
                recentPaths = nextRecent,
                playCounts = nextCounts,
                hourlyPlays = nextHourly,
                lastSongPath = song.filePath,
            )
        }
    }

    fun skipNext() {
        val q = activeQueue.ifEmpty { allSongs }
        if (q.isEmpty()) return
        val nextIdx = when {
            playerState.shuffleEnabled && q.size > 1 -> q.indices.filter { it != currentIndex }.random()
            currentIndex < 0 -> 0
            currentIndex + 1 < q.size -> currentIndex + 1
            playerState.repeatMode == RepeatMode.ALL -> 0
            else -> return
        }
        playFromQueue(q, nextIdx)
    }

    fun skipPrevious() {
        val q = activeQueue.ifEmpty { allSongs }
        if (q.isEmpty()) return
        val prevIdx = if (currentIndex <= 0) q.lastIndex else currentIndex - 1
        playFromQueue(q, prevIdx)
    }

    fun togglePlayOrResume() {
        val cur = playerState.currentSong
        if (cur != null) {
            if (playerState.positionMs == 0L && !playerState.isPlaying && currentIndex in activeQueue.indices) {
                playFromQueue(activeQueue, currentIndex)
            } else {
                engine.togglePlayPause()
            }
        } else if (allSongs.isNotEmpty()) {
            playFromQueue(allSongs, 0)
        }
    }

    engine = remember {
        DesktopAudioEngine(
            onStateChanged = { playing, pos, dur ->
                playerState = playerState.copy(isPlaying = playing, positionMs = pos, durationMs = dur)
            },
            onTrackCompleted = {
                scope.launch {
                    val q = activeQueue.ifEmpty { allSongs }
                    when {
                        playerState.repeatMode == RepeatMode.ONE && currentIndex in q.indices -> playFromQueue(q, currentIndex)
                        else -> skipNext()
                    }
                }
            },
        )
    }

    LaunchedEffect(Unit) {
        engine.setVolume(initialConfig.volume)
    }
    LaunchedEffect(config.eqEnabled, config.eqBandsDb) {
        engine.equalizer.update(config.eqEnabled, config.eqBandsDb)
    }
    LaunchedEffect(Unit) {
        delay(780)
        showSplash = false
    }
    LaunchedEffect(sleepTimerRemainingSec != null) {
        while (sleepTimerRemainingSec != null) {
            delay(1000)
            val rem = sleepTimerRemainingSec ?: break
            if (rem <= 1) {
                sleepTimerRemainingSec = null
                engine.pauseIfPlaying()
            } else {
                sleepTimerRemainingSec = rem - 1
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose { engine.stop() }
    }

    fun rescanLibrary(folders: List<String>) {
        scope.launch {
            isScanning = true
            val scanned = withContext(Dispatchers.IO) { DesktopLibraryScanner.scanFolders(folders) }
            allSongs = scanned
            if (activeQueue.isEmpty()) activeQueue = scanned
            if (playerState.currentSong == null && config.lastSongPath != null) {
                val lastIdx = scanned.indexOfFirst { it.filePath == config.lastSongPath }
                if (lastIdx >= 0) {
                    val restored = scanned[lastIdx]
                    currentIndex = lastIdx
                    playerState = playerState.copy(currentSong = restored, durationMs = restored.durationMs)
                    scope.launch(Dispatchers.IO) {
                        currentLyrics = DesktopLibraryScanner.loadLyricsForSong(restored)
                    }
                }
            }
            isScanning = false
        }
    }

    fun pickAndAddFolder() {
        val chooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialogTitle = "选择本地音乐文件夹"
        }
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            val path = chooser.selectedFile?.absolutePath ?: return
            if (path !in config.folders) {
                val updated = config.folders + path
                updateConfig { it.copy(folders = updated) }
                rescanLibrary(updated)
            }
        }
    }

    LaunchedEffect(Unit) {
        val defaultFolders = config.folders.ifEmpty {
            val userMusic = File(System.getProperty("user.home"), "Music")
            if (userMusic.exists() && userMusic.isDirectory) listOf(userMusic.absolutePath) else emptyList()
        }
        if (defaultFolders != config.folders) {
            updateConfig { it.copy(folders = defaultFolders) }
        }
        if (defaultFolders.isNotEmpty()) rescanLibrary(defaultFolders)
    }

    // Derived collections
    val songsByPath = remember(allSongs) { allSongs.associateBy { it.filePath } }
    val filteredSongs = remember(allSongs, searchQuery, sortOrder, config.shortAudioFilterSec) {
        val minDurMs = config.shortAudioFilterSec * 1000L
        val durFiltered = if (minDurMs > 0L) allSongs.filter { it.durationMs >= minDurMs } else allSongs
        val q = searchQuery.trim().lowercase(Locale.getDefault())
        val base = if (q.isEmpty()) durFiltered else durFiltered.filter {
            it.title.lowercase(Locale.getDefault()).contains(q) ||
                it.artist.lowercase(Locale.getDefault()).contains(q) ||
                it.album.lowercase(Locale.getDefault()).contains(q)
        }
        when (sortOrder) {
            SortOrder.TITLE -> base.sortedWith(
                compareBy<DesktopSong> { DesktopAlphabetIndex.sortRank(DesktopAlphabetIndex.initialLetter(it.title)) }
                    .thenBy { it.title.lowercase(Locale.getDefault()) },
            )
            SortOrder.ARTIST -> base.sortedWith(
                compareBy<DesktopSong> { DesktopAlphabetIndex.sortRank(DesktopAlphabetIndex.initialLetter(it.artist)) }
                    .thenBy { it.artist.lowercase(Locale.getDefault()) },
            )
            SortOrder.ALBUM -> base.sortedWith(
                compareBy<DesktopSong> { DesktopAlphabetIndex.sortRank(DesktopAlphabetIndex.initialLetter(it.album)) }
                    .thenBy { it.album.lowercase(Locale.getDefault()) },
            )
            SortOrder.DURATION -> base.sortedByDescending { it.durationMs }
        }
    }
    val albums = remember(allSongs) {
        allSongs.groupBy { it.album }
            .map { (name, list) -> DesktopAlbum(name, list.first().artist, list) }
            .sortedBy { it.name.lowercase(Locale.getDefault()) }
    }
    val artists = remember(allSongs) {
        allSongs.groupBy { it.artist }
            .map { (name, list) -> DesktopArtist(name, list) }
            .sortedWith(
                compareBy<DesktopArtist> { DesktopAlphabetIndex.sortRank(DesktopAlphabetIndex.initialLetter(it.name)) }
                    .thenBy { it.name.lowercase(Locale.getDefault()) },
            )
    }
    val folders = remember(allSongs) {
        allSongs.groupBy { File(it.filePath).parent ?: it.folderName }
            .map { (path, list) -> DesktopFolder(File(path).name.ifBlank { path }, path, list) }
            .sortedBy { it.name.lowercase(Locale.getDefault()) }
    }
    val favoriteSongs = remember(allSongs, config.favoritePaths) {
        allSongs.filter { it.filePath in config.favoritePaths }
    }
    val recentSongs = remember(songsByPath, config.recentPaths) {
        config.recentPaths.mapNotNull { songsByPath[it] }
    }
    val mostPlayedSongs = remember(allSongs, config.playCounts) {
        allSongs.filter { (config.playCounts[it.filePath] ?: 0) > 0 }
            .sortedByDescending { config.playCounts[it.filePath] ?: 0 }
            .take(50)
    }

    var vinylAngleDeg by remember { mutableFloatStateOf(0f) }
    var vinylVelocity by remember { mutableFloatStateOf(0f) }
    val isPlayingForVinyl = playerState.isPlaying
    LaunchedEffect(isPlayingForVinyl) {
        var lastNanos = 0L
        val targetVel = if (isPlayingForVinyl) 14f else 0f // Serene 14 deg/s (~25.7s per rotation)
        while (isActive && (isPlayingForVinyl || vinylVelocity > 0.10f)) {
            withFrameNanos { nanos ->
                val dt = if (lastNanos == 0L) 0f else ((nanos - lastNanos) / 1_000_000_000f).coerceIn(0f, 0.05f)
                lastNanos = nanos
                val accel = if (isPlayingForVinyl) 3.6f else 2.8f
                vinylVelocity += (targetVel - vinylVelocity) * (accel * dt).coerceAtMost(1f)
                if (!isPlayingForVinyl && vinylVelocity < 0.15f) vinylVelocity = 0f
                vinylAngleDeg = (vinylAngleDeg + vinylVelocity * dt) % 360f
            }
        }
    }

    // Android-Parity 6 Archival Bookbinding Paper Palettes (linked to selected Spine Accent)
    val rawAccent = Color(config.accentColor.hex)
    val (pBg, pSide, pSurf, pVar, pOut) = if (config.darkMode) {
        when (config.accentColor) {
            DesktopAccentColor.CINNABAR -> arrayOf(Color(0xFF161413), Color(0xFF110F0E), Color(0xFF1D1B19), Color(0xFF262320), Color(0xFF302C28))
            DesktopAccentColor.PINE_GREEN -> arrayOf(Color(0xFF121715), Color(0xFF0E1211), Color(0xFF181F1D), Color(0xFF212A27), Color(0xFF2B3531))
            DesktopAccentColor.PRUSSIAN_BLUE -> arrayOf(Color(0xFF12151A), Color(0xFF0E1014), Color(0xFF181C22), Color(0xFF20262E), Color(0xFF2A313C))
            DesktopAccentColor.WALNUT -> arrayOf(Color(0xFF161411), Color(0xFF12100D), Color(0xFF1E1B17), Color(0xFF28241E), Color(0xFF332D26))
            DesktopAccentColor.OCHRE_GOLD -> arrayOf(Color(0xFF171511), Color(0xFF12100D), Color(0xFF1F1C17), Color(0xFF29251E), Color(0xFF342F26))
            DesktopAccentColor.PURE_INK -> arrayOf(Color(0xFF141414), Color(0xFF0F0F0F), Color(0xFF1C1C1C), Color(0xFF252525), Color(0xFF2F2F2F))
        }
    } else {
        when (config.accentColor) {
            DesktopAccentColor.CINNABAR -> arrayOf(Color(0xFFF5F0E6), Color(0xFFEDE6D9), Color(0xFFFBF8F2), Color(0xFFECE5D8), Color(0xFFDFD7C8))
            DesktopAccentColor.PINE_GREEN -> arrayOf(Color(0xFFEFF3F0), Color(0xFFE6ECE8), Color(0xFFF7FAF8), Color(0xFFE3EAE5), Color(0xFFD3DDD6))
            DesktopAccentColor.PRUSSIAN_BLUE -> arrayOf(Color(0xFFEEF1F5), Color(0xFFE5E9F0), Color(0xFFF7F9FC), Color(0xFFE2E7EF), Color(0xFFD2D9E4))
            DesktopAccentColor.WALNUT -> arrayOf(Color(0xFFF4EFE7), Color(0xFFECE5DA), Color(0xFFFAF7F2), Color(0xFFEAE2D6), Color(0xFFDCD3C5))
            DesktopAccentColor.OCHRE_GOLD -> arrayOf(Color(0xFFF6F1E5), Color(0xFFEEE8D8), Color(0xFFFBF9F2), Color(0xFFECE5D5), Color(0xFFDFD7C3))
            DesktopAccentColor.PURE_INK -> arrayOf(Color(0xFFF3F2EF), Color(0xFFEAE9E4), Color(0xFFFAF9F7), Color(0xFFE7E5E0), Color(0xFFD9D7D1))
        }
    }
    val sidebarBg = pSide
    val colorScheme = if (config.darkMode) {
        darkColorScheme(
            primary = if (config.accentColor == DesktopAccentColor.PURE_INK) Color(0xFFE0C08E) else rawAccent,
            onPrimary = Color(0xFFF6F2EA),
            background = pBg,
            onBackground = Color(0xFFF2ECE1),
            surface = pSurf,
            onSurface = Color(0xFFF2ECE1),
            surfaceVariant = pVar,
            onSurfaceVariant = Color(0xFF9C968E),
            outline = pOut,
        )
    } else {
        lightColorScheme(
            primary = rawAccent,
            onPrimary = Color(0xFFFDFBF7),
            background = pBg,
            onBackground = Color(0xFF1F1C18),
            surface = pSurf,
            onSurface = Color(0xFF1F1C18),
            surfaceVariant = pVar,
            onSurfaceVariant = Color(0xFF7A7267),
            outline = pOut,
        )
    }

    val scale = config.fontScale.coerceIn(0.85f, 1.30f)
    val appFont = remember(config.appFontStyle, config.customFontPath) {
        DesktopTypographyFamilies.resolveAppFont(config.appFontStyle, config.customFontPath)
    }
    val lyricFont = remember(config.lyricFontStyle, config.appFontStyle, config.customFontPath) {
        DesktopTypographyFamilies.resolveLyricFont(config.lyricFontStyle, config.appFontStyle, config.customFontPath)
    }
    val bodyWeight = FontWeight(config.fontWeightValue.coerceIn(200, 900))
    val titleWeight = FontWeight((config.fontWeightValue + 200).coerceIn(300, 900))
    val baseTypo = Typography()
    val scaledTypo = Typography(
        headlineMedium = baseTypo.headlineMedium.copy(fontFamily = appFont, fontWeight = titleWeight),
        headlineSmall = baseTypo.headlineSmall.copy(fontFamily = appFont, fontWeight = titleWeight),
        titleLarge = baseTypo.titleLarge.copy(fontFamily = appFont, fontWeight = titleWeight),
        titleMedium = baseTypo.titleMedium.copy(fontFamily = appFont, fontWeight = titleWeight),
        titleSmall = baseTypo.titleSmall.copy(fontFamily = appFont, fontWeight = titleWeight),
        bodyLarge = baseTypo.bodyLarge.copy(fontFamily = appFont, fontWeight = bodyWeight),
        bodyMedium = baseTypo.bodyMedium.copy(fontFamily = appFont, fontWeight = bodyWeight),
        bodySmall = baseTypo.bodySmall.copy(fontFamily = appFont, fontWeight = bodyWeight),
        labelLarge = baseTypo.labelLarge.copy(fontFamily = appFont, fontWeight = bodyWeight),
        labelMedium = baseTypo.labelMedium.copy(fontFamily = appFont, fontWeight = bodyWeight),
        labelSmall = baseTypo.labelSmall.copy(fontFamily = appFont, fontWeight = bodyWeight),
    )

    val mainWindowState = rememberWindowState(
        size = DpSize(initialConfig.windowWidthDp.dp, initialConfig.windowHeightDp.dp),
        position = WindowPosition(Alignment.Center),
    )

    fun saveAndExit() {
        if (mainWindowState.placement == WindowPlacement.Floating) {
            val w = mainWindowState.size.width.value.toInt().coerceIn(900, 3840)
            val h = mainWindowState.size.height.value.toInt().coerceIn(600, 2160)
            DesktopConfigStore.save(config.copy(windowWidthDp = w, windowHeightDp = h))
        }
        engine.stop()
        exitApplication()
    }

    val windowAppIcon = remember {
        runCatching {
            Thread.currentThread().contextClassLoader.getResourceAsStream("app_icon.png")?.use {
                BitmapPainter(loadImageBitmap(it))
            }
        }.getOrNull()
    }
    LaunchedEffect(config.crossfadeSec, config.volumeNormalizationEnabled) {
        engine.setCrossfadeSeconds(config.crossfadeSec)
        engine.setVolumeNormalizationEnabled(config.volumeNormalizationEnabled)
    }

    Window(
        onCloseRequest = { saveAndExit() },
        state = mainWindowState,
        title = "简约音乐 · PureMusic",
        icon = windowAppIcon,
        undecorated = true,
        onPreviewKeyEvent = { event ->
            if (event.type == KeyEventType.KeyDown) {
                when (event.key) {
                    Key.Spacebar -> {
                        if (searchQuery.isEmpty() && !showCreatePlaylistDialog && renamingPlaylist == null) {
                            togglePlayOrResume()
                            true
                        } else false
                    }
                    Key.F11 -> {
                        mainWindowState.placement = if (mainWindowState.placement == WindowPlacement.Fullscreen) {
                            WindowPlacement.Floating
                        } else {
                            WindowPlacement.Fullscreen
                        }
                        true
                    }
                    Key.Escape -> {
                        when {
                            mainWindowState.placement == WindowPlacement.Fullscreen -> { mainWindowState.placement = WindowPlacement.Floating; true }
                            showEqDrawer -> { showEqDrawer = false; true }
                            showQueueDrawer -> { showQueueDrawer = false; true }
                            showImmersiveStage -> { showImmersiveStage = false; true }
                            activeDetail != null -> { activeDetail = null; true }
                            else -> false
                        }
                    }
                    Key.DirectionRight -> {
                        if (showImmersiveStage && playerState.currentSong != null) {
                            engine.seekTo((playerState.positionMs + 5_000L).coerceAtMost(playerState.durationMs))
                            true
                        } else false
                    }
                    Key.DirectionLeft -> {
                        if (showImmersiveStage && playerState.currentSong != null) {
                            engine.seekTo((playerState.positionMs - 5_000L).coerceAtLeast(0L))
                            true
                        } else false
                    }
                    else -> false
                }
            } else false
        },
    ) {
        val baseDensity = LocalDensity.current
        val winScale = if (config.autoWindowScale) {
            minOf(mainWindowState.size.width.value / 1280f, mainWindowState.size.height.value / 760f).coerceIn(0.78f, 1.42f)
        } else 1.0f
        CompositionLocalProvider(
            LocalDensity provides Density(baseDensity.density, fontScale = (baseDensity.fontScale * scale * winScale).coerceIn(0.75f, 1.65f)),
            LocalLyricFontFamily provides lyricFont,
            LocalLyricSlantDegrees provides config.lyricSlantDegrees,
        ) {
        MaterialTheme(colorScheme = colorScheme, typography = scaledTypo) {
            val currentLyricOffset = playerState.currentSong?.filePath?.let { config.lyricOffsetsMs[it] } ?: 0L
            val effectivePosMs = (playerState.positionMs + currentLyricOffset).coerceAtLeast(0L)
            val activeLyricIdx = remember(currentLyrics, effectivePosMs) {
                val found = currentLyrics.indexOfLast { it.timeMs <= effectivePosMs }
                if (found >= 0) found else if (currentLyrics.isNotEmpty()) 0 else -1
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .border(0.75.dp, MaterialTheme.colorScheme.outline),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Main Book Spread v4.0: Full-Height Integrated Collector Sidebar (224dp) + Seamless Right Canvas
                    Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .width(224.dp)
                                .fillMaxHeight()
                                .background(sidebarBg)
                                .padding(bottom = 18.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Draggable Top Brand Lockup with Official Icon #1
                                WindowDraggableArea(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onDoubleTap = {
                                                    mainWindowState.placement = if (mainWindowState.placement == WindowPlacement.Maximized) {
                                                        WindowPlacement.Floating
                                                    } else {
                                                        WindowPlacement.Maximized
                                                    }
                                                },
                                            )
                                        },
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 20.dp, end = 16.dp, top = 22.dp, bottom = 22.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        if (windowAppIcon != null) {
                                            Image(
                                                painter = windowAppIcon,
                                                contentDescription = "简约音乐",
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .shadow(6.dp, RoundedCornerShape(10.dp), ambientColor = Color.Black.copy(alpha = 0.18f))
                                                    .clip(RoundedCornerShape(10.dp)),
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "简约音乐",
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground,
                                            )
                                            Text(
                                                text = "PureMusic v4.2",
                                                fontSize = 10.5.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f),
                                            )
                                        }
                                    }
                                }

                                // 4 Crisp Navigation Chapters with Elastic Glide Card + Cinnabar Spine
                                val selectedTabIdx = DesktopTab.entries.indexOf(activeTab).coerceAtLeast(0)
                                val indicatorOffsetY by animateDpAsState(
                                    targetValue = (selectedTabIdx * 48).dp,
                                    animationSpec = spring(dampingRatio = 0.74f, stiffness = 360f),
                                    label = "sidebarGlideY",
                                )
                                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
                                    if (activeDetail == null) {
                                        val glassPillShape = RoundedCornerShape(12.dp)
                                        Box(
                                            modifier = Modifier
                                                .offset(y = indicatorOffsetY)
                                                .fillMaxWidth()
                                                .height(44.dp)
                                                .shadow(4.dp, glassPillShape, ambientColor = Color.Black.copy(alpha = 0.10f))
                                                .clip(glassPillShape)
                                                .background(MaterialTheme.colorScheme.surface)
                                                .border(0.6.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.75f), glassPillShape),
                                            contentAlignment = Alignment.CenterStart,
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(start = 3.dp)
                                                    .width(3.5.dp)
                                                    .height(22.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary),
                                            )
                                        }
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        DesktopTab.entries.forEach { tab ->
                                            val selected = tab == activeTab && activeDetail == null
                                            val hoverSrc = remember { MutableInteractionSource() }
                                            val hovered by hoverSrc.collectIsHoveredAsState()
                                            val tabIcon = when (tab) {
                                                DesktopTab.LIBRARY -> Icons.Rounded.LibraryMusic
                                                DesktopTab.ALBUMS -> Icons.Rounded.Album
                                                DesktopTab.PLAYLISTS -> Icons.Rounded.QueueMusic
                                                DesktopTab.SETTINGS -> Icons.Rounded.Settings
                                            }
                                            val textCol by animateColorAsState(
                                                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                tween(180),
                                                label = "sideTxt",
                                            )
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(44.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (!selected && hovered) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f) else Color.Transparent)
                                                    .hoverable(hoverSrc)
                                                    .pointerHoverIcon(PointerIcon.Hand)
                                                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                                        activeTab = tab
                                                        activeDetail = null
                                                    },
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Icon(
                                                    imageVector = tabIcon,
                                                    contentDescription = null,
                                                    tint = textCol,
                                                    modifier = Modifier.size(18.dp),
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = tab.label,
                                                    fontSize = 14.5.sp,
                                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                                    color = if (selected) MaterialTheme.colorScheme.onBackground else textCol,
                                                )
                                            }
                                        }
                                    }
                                }

                                // Quick-Access User Playlists in Lower Sidebar
                                if (config.playlists.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 18.dp),
                                        thickness = 0.6.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "自建歌单",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        modifier = Modifier.padding(horizontal = 20.dp),
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                        verticalArrangement = Arrangement.spacedBy(2.dp),
                                    ) {
                                        config.playlists.take(6).forEach { pl ->
                                            val plCover = pl.songPaths.firstOrNull { it in songsByPath }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .desktopHoverClickable(shape = RoundedCornerShape(8.dp)) {
                                                        activeDetail = DesktopDetailView.PlaylistView(pl.id)
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            ) {
                                                DesktopAlbumCover(filePath = plCover, modifier = Modifier.size(26.dp), cornerRadius = 5.dp, iconSize = 12.dp)
                                                Text(
                                                    text = pl.name,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.88f),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Quiet bottom library import & count
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                if (sleepTimerRemainingSec != null) {
                                    val mins = (sleepTimerRemainingSec ?: 0) / 60
                                    val secs = (sleepTimerRemainingSec ?: 0) % 60
                                    Text(
                                        text = String.format(Locale.US, "睡眠定时 %02d:%02d", mins, secs),
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).clickable { sleepTimerRemainingSec = null },
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                                        .border(0.6.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                        .pointerHoverIcon(PointerIcon.Hand)
                                        .clickable { pickAndAddFolder() }
                                        .padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Icon(Icons.Rounded.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text("导入音乐目录", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
                                }
                                Text(
                                    text = if (isScanning) "正在扫描曲库..." else "${allSongs.size} 首本地音乐 · ${albums.size} 张专辑",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                                    modifier = Modifier.padding(start = 2.dp, top = 2.dp),
                                )
                            }
                        }

                        VerticalDivider(thickness = 0.6.dp, color = MaterialTheme.colorScheme.outline)

                        // Right Monograph Content Area with Floating Minimalist Top-Right Window Controls
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            WindowDraggableArea(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onDoubleTap = {
                                                mainWindowState.placement = if (mainWindowState.placement == WindowPlacement.Maximized) {
                                                    WindowPlacement.Floating
                                                } else {
                                                    WindowPlacement.Maximized
                                                }
                                            },
                                        )
                                    },
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End,
                                ) {
                                    IconButton(
                                        onClick = { showMiniVinylWindow = !showMiniVinylWindow },
                                        modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.PictureInPictureAlt,
                                            contentDescription = "迷你黑胶悬浮窗",
                                            tint = if (showMiniVinylWindow) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(15.dp),
                                        )
                                    }
                                    IconButton(
                                        onClick = { mainWindowState.isMinimized = true },
                                        modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand),
                                    ) {
                                        Icon(Icons.Rounded.Remove, contentDescription = "最小化", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            mainWindowState.placement = if (mainWindowState.placement == WindowPlacement.Maximized) {
                                                WindowPlacement.Floating
                                            } else {
                                                WindowPlacement.Maximized
                                            }
                                        },
                                        modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand),
                                    ) {
                                        Icon(Icons.Rounded.CropSquare, contentDescription = "最大化", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.5.dp))
                                    }
                                    IconButton(
                                        onClick = { saveAndExit() },
                                        modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand),
                                    ) {
                                        Icon(Icons.Rounded.Close, contentDescription = "关闭", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
                                    }
                                }
                            }
                        AnimatedContent(
                            targetState = Pair(activeTab, activeDetail),
                            transitionSpec = {
                                (fadeIn(tween(220, easing = FastOutSlowInEasing)) + slideInVertically(tween(240, easing = FastOutSlowInEasing)) { 14 }) togetherWith
                                    fadeOut(tween(130))
                            },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            label = "mainPageTransition",
                        ) { (currentTab, detail) ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (detail is DesktopDetailView.ListeningReportView) {
                                DesktopListeningReportPage(
                                    allSongs = allSongs,
                                    mostPlayedSongs = mostPlayedSongs,
                                    artists = artists,
                                    playCounts = config.playCounts,
                                    hourlyPlays = config.hourlyPlays,
                                    currentSongId = playerState.currentSong?.id,
                                    onBack = { activeDetail = null },
                                    onPlayAll = { if (mostPlayedSongs.isNotEmpty()) playFromQueue(mostPlayedSongs, 0) },
                                    onPlayAt = { idx -> playFromQueue(mostPlayedSongs, idx) },
                                    onSelectArtist = { activeDetail = DesktopDetailView.ArtistView(it) },
                                )
                            } else if (detail != null) {
                                val (dTitle, dSubtitle, dSongs) = when (detail) {
                                    is DesktopDetailView.AlbumView -> Triple(detail.album.name, detail.album.artist, detail.album.songs)
                                    is DesktopDetailView.ArtistView -> Triple(detail.artist.name, "${detail.artist.songs.size} 首作品", detail.artist.songs)
                                    is DesktopDetailView.FolderView -> Triple(detail.folder.name, detail.folder.path, detail.folder.songs)
                                    is DesktopDetailView.PlaylistView -> {
                                        val pl = config.playlists.firstOrNull { it.id == detail.playlistId }
                                        val plSongs = pl?.songPaths?.mapNotNull { songsByPath[it] }.orEmpty()
                                        Triple(pl?.name ?: "歌单", "${plSongs.size} 首曲目", plSongs)
                                    }
                                    DesktopDetailView.FavoritesView -> Triple("我喜欢的音乐", "${favoriteSongs.size} 首珍藏曲目", favoriteSongs)
                                    DesktopDetailView.RecentView -> Triple("最近播放", "${recentSongs.size} 首播放记录", recentSongs)
                                    DesktopDetailView.ListeningReportView -> Triple("", "", emptyList())
                                }
                                DesktopCollectionDetailPage(
                                    title = dTitle,
                                    subtitle = dSubtitle,
                                    songs = dSongs,
                                    currentSongId = playerState.currentSong?.id,
                                    favoritePaths = config.favoritePaths,
                                    density = config.layoutDensity,
                                    onBack = { activeDetail = null },
                                    onPlayAt = { idx -> playFromQueue(dSongs, idx) },
                                    onToggleFavorite = { path ->
                                        updateConfig { cfg ->
                                            val next = if (path in cfg.favoritePaths) cfg.favoritePaths - path else cfg.favoritePaths + path
                                            cfg.copy(favoritePaths = next)
                                        }
                                    },
                                    onAddToPlaylist = { addingSongToPlaylist = it },
                                )
                            } else {
                                when (currentTab) {
                                    DesktopTab.LIBRARY -> DesktopLibraryPage(
                                        subTab = librarySubTab,
                                        onSelectSubTab = { librarySubTab = it },
                                        songs = filteredSongs,
                                        artists = artists,
                                        folders = folders,
                                        searchQuery = searchQuery,
                                        onSearchQueryChange = { searchQuery = it },
                                        sortOrder = sortOrder,
                                        onSelectSortOrder = { sortOrder = it },
                                        currentSongId = playerState.currentSong?.id,
                                        favoritePaths = config.favoritePaths,
                                        density = config.layoutDensity,
                                        onPlayAt = { idx -> playFromQueue(filteredSongs, idx) },
                                        onToggleFavorite = { path ->
                                            updateConfig { cfg ->
                                                val next = if (path in cfg.favoritePaths) cfg.favoritePaths - path else cfg.favoritePaths + path
                                                cfg.copy(favoritePaths = next)
                                            }
                                        },
                                        onAddToPlaylist = { addingSongToPlaylist = it },
                                        onSelectArtist = { activeDetail = DesktopDetailView.ArtistView(it) },
                                        onSelectFolder = { activeDetail = DesktopDetailView.FolderView(it) },
                                        onPickFolder = { pickAndAddFolder() },
                                    )

                                    DesktopTab.ALBUMS -> DesktopAlbumsPage(
                                        albums = albums,
                                        density = config.layoutDensity,
                                        viewMode = config.albumViewMode,
                                        onSelectViewMode = { vm -> updateConfig { it.copy(albumViewMode = vm) } },
                                        onSelectAlbum = { activeDetail = DesktopDetailView.AlbumView(it) },
                                    )

                                    DesktopTab.PLAYLISTS -> DesktopPlaylistsPage(
                                        favoriteSongs = favoriteSongs,
                                        recentSongs = recentSongs,
                                        allSongs = allSongs,
                                        playCounts = config.playCounts,
                                        hourlyPlays = config.hourlyPlays,
                                        playlists = config.playlists,
                                        songsByPath = songsByPath,
                                        density = config.layoutDensity,
                                        onOpenFavorites = { activeDetail = DesktopDetailView.FavoritesView },
                                        onPlayFavorites = { if (favoriteSongs.isNotEmpty()) playFromQueue(favoriteSongs, 0) },
                                        onOpenRecent = { activeDetail = DesktopDetailView.RecentView },
                                        onOpenReport = { activeDetail = DesktopDetailView.ListeningReportView },
                                        onCreatePlaylist = { showCreatePlaylistDialog = true },
                                        onOpenPlaylist = { activeDetail = DesktopDetailView.PlaylistView(it.id) },
                                        onRenamePlaylist = { renamingPlaylist = it },
                                        onDeletePlaylist = { pl ->
                                            updateConfig { cfg -> cfg.copy(playlists = cfg.playlists.filterNot { it.id == pl.id }) }
                                        },
                                    )

                                    DesktopTab.SETTINGS -> DesktopSettingsPage(
                                        config = config,
                                        onUpdateConfig = { cfg -> updateConfig { cfg } },
                                        sleepTimerRemainingSec = sleepTimerRemainingSec,
                                        onToggleDarkMode = { dm -> updateConfig { it.copy(darkMode = dm) } },
                                        onSelectAccent = { ac -> updateConfig { it.copy(accentColor = ac) } },
                                        onSelectDensity = { dn -> updateConfig { it.copy(layoutDensity = dn) } },
                                        onFontScaleChange = { sc -> updateConfig { it.copy(fontScale = sc) } },
                                        onSelectAppFont = { f -> updateConfig { it.copy(appFontStyle = f) } },
                                        onSelectLyricFont = { lf -> updateConfig { it.copy(lyricFontStyle = lf) } },
                                        onSelectFontWeight = { fw -> updateConfig { it.copy(fontWeightLevel = fw) } },
                                        onFontWeightValueChange = { wv -> updateConfig { it.copy(fontWeightValue = wv.coerceIn(200, 900)) } },
                                        onLyricSlantChange = { sl -> updateConfig { it.copy(lyricSlantDegrees = sl.coerceIn(0f, 16f)) } },
                                        onToggleFloatingBar = { fl -> updateConfig { it.copy(floatingControlBar = fl) } },
                                        onToggleLiquidGlass = { lg -> updateConfig { it.copy(liquidGlassEnabled = lg) } },
                                        onSelectLiquidGlassLevel = { lv -> updateConfig { it.copy(liquidGlassLevel = lv) } },
                                        onControlBarHeightChange = { h -> updateConfig { it.copy(controlBarHeightDp = h.coerceIn(54, 76)) } },
                                        onSelectShortAudioFilter = { sec -> updateConfig { it.copy(shortAudioFilterSec = sec) } },
                                        onToggleEqEnabled = { en -> updateConfig { it.copy(eqEnabled = en) } },
                                        onEqBandChange = { idx, db ->
                                            updateConfig { cfg ->
                                                val next = cfg.eqBandsDb.toMutableList()
                                                if (idx in next.indices) next[idx] = db
                                                cfg.copy(eqEnabled = true, eqBandsDb = next)
                                            }
                                        },
                                        onApplyEqPreset = { preset -> updateConfig { it.copy(eqEnabled = true, eqBandsDb = preset.gainsDb) } },
                                        onSelectScene = { sc -> updateConfig { it.copy(sceneStyle = sc) } },
                                        onSelectSpectrum = { sp -> updateConfig { it.copy(spectrumStyle = sp) } },
                                        onPickCustomFont = {
                                            val chosen = pickSingleFile("选择本地字体文件", "字体文件 (*.ttf, *.otf, *.ttc)", "ttf", "otf", "ttc")
                                            if (chosen != null) {
                                                updateConfig { it.copy(appFontStyle = DesktopFontStyle.CUSTOM, customFontPath = chosen) }
                                            }
                                        },
                                        onSetSleepTimerMin = { mins -> sleepTimerRemainingSec = if (mins <= 0) null else mins * 60 },
                                        onAddFolder = { pickAndAddFolder() },
                                        onRemoveFolder = { path ->
                                            val updated = config.folders.filterNot { it == path }
                                            updateConfig { it.copy(folders = updated) }
                                            rescanLibrary(updated)
                                        },
                                        onRescan = { rescanLibrary(config.folders) },
                                        totalSongsCount = allSongs.size,
                                        visibleSongsCount = filteredSongs.size,
                                    )
                                }
                            }
                        }
                        }
                        }
                        }
                    }

                    // 100% Unified Bookbinding Bottom Control Rail (razor-thin top progress line + calm controls)
                    UnifiedBottomControlRail(
                        playerState = playerState,
                        isFavorite = playerState.currentSong?.filePath in config.favoritePaths,
                        isImmersive = false,
                        isDarkSurface = config.darkMode,
                        floatingCapsule = config.floatingControlBar,
                        liquidGlassEnabled = config.liquidGlassEnabled,
                        liquidGlassLevel = config.liquidGlassLevel,
                        barHeightDp = config.controlBarHeightDp,
                        accentColor = MaterialTheme.colorScheme.primary,
                        eqActive = config.eqEnabled || showEqDrawer,
                        queueOpen = showQueueDrawer,
                        spectrumVisible = false,
                        onOpenImmersive = { if (playerState.currentSong != null) showImmersiveStage = true },
                        onToggleFavorite = {
                            val path = playerState.currentSong?.filePath ?: return@UnifiedBottomControlRail
                            updateConfig { cfg ->
                                val next = if (path in cfg.favoritePaths) cfg.favoritePaths - path else cfg.favoritePaths + path
                                cfg.copy(favoritePaths = next)
                            }
                        },
                        onToggleShuffle = {
                            val next = !playerState.shuffleEnabled
                            playerState = playerState.copy(shuffleEnabled = next)
                            updateConfig { it.copy(shuffleEnabled = next) }
                        },
                        onCycleRepeat = {
                            val next = when (playerState.repeatMode) {
                                RepeatMode.OFF -> RepeatMode.ALL
                                RepeatMode.ALL -> RepeatMode.ONE
                                RepeatMode.ONE -> RepeatMode.OFF
                            }
                            playerState = playerState.copy(repeatMode = next)
                            updateConfig { it.copy(repeatMode = next) }
                        },
                        onSkipPrevious = { skipPrevious() },
                        onTogglePlayPause = { togglePlayOrResume() },
                        onSkipNext = { skipNext() },
                        onSeekTo = { engine.seekTo(it) },
                        onVolumeChange = { vol ->
                            playerState = playerState.copy(volume = vol)
                            engine.setVolume(vol)
                            updateConfig { it.copy(volume = vol) }
                        },
                        onToggleEq = {
                            showEqDrawer = !showEqDrawer
                            if (showEqDrawer) showQueueDrawer = false
                        },
                        onToggleQueue = {
                            showQueueDrawer = !showQueueDrawer
                            if (showQueueDrawer) showEqDrawer = false
                        },
                        onToggleSpectrum = {},
                    )
                }

                // Unified Full-Screen Immersive Stage (4 cohesive scenes sharing exact same top/bottom control language)
                AnimatedVisibility(
                    visible = showImmersiveStage && playerState.currentSong != null,
                    enter = fadeIn(tween(260)) + slideInVertically(spring(dampingRatio = 0.86f, stiffness = 320f)) { it / 10 } + scaleIn(initialScale = 0.965f, animationSpec = tween(260, easing = FastOutSlowInEasing)),
                    exit = fadeOut(tween(190)) + slideOutVertically(tween(200)) { it / 12 } + scaleOut(targetScale = 0.98f, animationSpec = tween(190)),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val song = playerState.currentSong
                    if (song != null) {
                        UnifiedImmersiveStage(
                        karaokeLyricMode = config.karaokeLyricMode,
                        isTranslatingLyrics = isTranslatingLyrics,
                        translateStatusTip = translateStatusTip,
                        onTranslateLyrics = {
                            if (!isTranslatingLyrics && currentLyrics.isNotEmpty()) {
                                val targetSong = song
                                val linesSnapshot = currentLyrics
                                isTranslatingLyrics = true
                                translateStatusTip = "正在翻译歌词..."
                                scope.launch(Dispatchers.IO) {
                                    val res = DesktopLyricTranslator.translateAndCacheBilingualLrc(
                                        song = targetSong,
                                        lines = linesSnapshot,
                                        customBaseUrl = config.translateBaseUrl,
                                        customApiKey = config.translateApiKey,
                                        customModel = config.translateModel,
                                    )
                                    withContext(Dispatchers.Main) {
                                        res.onSuccess { lrcPath ->
                                            DesktopLibraryScanner.setCustomLyricOverride(targetSong.filePath, lrcPath)
                                            updateConfig { cfg -> cfg.copy(customLyricPaths = cfg.customLyricPaths + (targetSong.filePath to lrcPath)) }
                                            currentLyrics = DesktopLibraryScanner.loadLyricsForSong(targetSong)
                                            translateStatusTip = "翻译完成，已缓存双语"
                                        }.onFailure { err ->
                                            translateStatusTip = err.message ?: "翻译失败"
                                        }
                                        isTranslatingLyrics = false
                                    }
                                }
                            }
                        },
                            song = song,
                            allSongs = allSongs,
                            playerState = playerState,
                            isFavorite = song.filePath in config.favoritePaths,
                            lyrics = currentLyrics,
                            activeLyricIdx = activeLyricIdx,
                            vinylRotation = vinylAngleDeg,
                            sceneStyle = config.sceneStyle,
                            spectrumStyle = config.spectrumStyle,
                            showSpectrum = showSpectrumInImmersive,
                            lyricOffsetMs = currentLyricOffset,
                            lyricFontScale = config.lyricFontScale,
                            isDarkTheme = config.darkMode,
                            accentColor = MaterialTheme.colorScheme.primary,
                            isFullscreen = mainWindowState.placement == WindowPlacement.Fullscreen,
                            eqActive = config.eqEnabled || showEqDrawer,
                            queueOpen = showQueueDrawer,
                            onSelectScene = { st -> updateConfig { it.copy(sceneStyle = st) } },
                            onSelectSpectrum = { sp -> updateConfig { it.copy(spectrumStyle = sp) } },
                            onToggleSpectrum = { showSpectrumInImmersive = !showSpectrumInImmersive },
                            onAdjustLyricOffset = { delta ->
                                updateConfig { cfg ->
                                    val next = cfg.lyricOffsetsMs.toMutableMap()
                                    val updated = ((next[song.filePath] ?: 0L) + delta).coerceIn(-15_000L, 15_000L)
                                    if (updated == 0L) next.remove(song.filePath) else next[song.filePath] = updated
                                    cfg.copy(lyricOffsetsMs = next)
                                }
                            },
                            onResetLyricOffset = {
                                updateConfig { cfg ->
                                    val next = cfg.lyricOffsetsMs.toMutableMap().apply { remove(song.filePath) }
                                    cfg.copy(lyricOffsetsMs = next)
                                }
                            },
                            onAdjustLyricScale = { delta ->
                                updateConfig { cfg ->
                                    cfg.copy(lyricFontScale = (cfg.lyricFontScale + delta).coerceIn(0.75f, 1.45f))
                                }
                            },
                            onResetLyricScale = {
                                updateConfig { it.copy(lyricFontScale = 1.0f) }
                            },
                            onImportLyrics = {
                                val chosen = pickSingleFile("导入本地歌词文件", "歌词文件 (*.lrc, *.txt)", "lrc", "txt")
                                if (chosen != null) {
                                    updateConfig { cfg ->
                                        val next = cfg.customLyricPaths.toMutableMap().apply { put(song.filePath, chosen) }
                                        cfg.copy(customLyricPaths = next)
                                    }
                                    scope.launch(Dispatchers.IO) {
                                        currentLyrics = DesktopLibraryScanner.loadLyricsForSong(song)
                                    }
                                }
                            },
                            onReplaceCover = {
                                val chosen = pickSingleFile("替换当前歌曲专辑封面", "图片文件 (*.jpg, *.jpeg, *.png, *.webp)", "jpg", "jpeg", "png", "webp")
                                if (chosen != null) {
                                    updateConfig { cfg ->
                                        val next = cfg.customCoverPaths.toMutableMap().apply { put(song.filePath, chosen) }
                                        cfg.copy(customCoverPaths = next)
                                    }
                                    ArtworkMemoryCache.invalidate(song.filePath)
                                }
                            },
                            onClose = { showImmersiveStage = false },
                            onToggleFullscreen = {
                                mainWindowState.placement = if (mainWindowState.placement == WindowPlacement.Fullscreen) {
                                    WindowPlacement.Floating
                                } else {
                                    WindowPlacement.Fullscreen
                                }
                            },
                            onSelectSong = { chosen ->
                                val idx = allSongs.indexOfFirst { it.id == chosen.id }
                                if (idx >= 0) playFromQueue(allSongs, idx)
                            },
                            onToggleFavorite = {
                                updateConfig { cfg ->
                                    val next = if (song.filePath in cfg.favoritePaths) cfg.favoritePaths - song.filePath else cfg.favoritePaths + song.filePath
                                    cfg.copy(favoritePaths = next)
                                }
                            },
                            onToggleShuffle = {
                                val next = !playerState.shuffleEnabled
                                playerState = playerState.copy(shuffleEnabled = next)
                                updateConfig { it.copy(shuffleEnabled = next) }
                            },
                            onCycleRepeat = {
                                val next = when (playerState.repeatMode) {
                                    RepeatMode.OFF -> RepeatMode.ALL
                                    RepeatMode.ALL -> RepeatMode.ONE
                                    RepeatMode.ONE -> RepeatMode.OFF
                                }
                                playerState = playerState.copy(repeatMode = next)
                                updateConfig { it.copy(repeatMode = next) }
                            },
                            onSkipPrevious = { skipPrevious() },
                            onTogglePlayPause = { togglePlayOrResume() },
                            onSkipNext = { skipNext() },
                            onSeekTo = { engine.seekTo(it) },
                            onVolumeChange = { vol ->
                                playerState = playerState.copy(volume = vol)
                                engine.setVolume(vol)
                                updateConfig { it.copy(volume = vol) }
                            },
                            onToggleEq = {
                                showEqDrawer = !showEqDrawer
                                if (showEqDrawer) showQueueDrawer = false
                            },
                            onToggleQueue = {
                                showQueueDrawer = !showQueueDrawer
                                if (showQueueDrawer) showEqDrawer = false
                            },
                        )
                    }
                }

                // Right-Side Sliding EQ Drawer (available anywhere via bottom rail EQ button)
                AnimatedVisibility(
                    visible = showEqDrawer,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(tween(180)),
                    exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(tween(150)),
                    modifier = Modifier.align(Alignment.CenterEnd).padding(top = 40.dp, bottom = 66.dp),
                ) {
                    DesktopEqualizerDrawer(
                        enabled = config.eqEnabled,
                        bandsDb = config.eqBandsDb,
                        onClose = { showEqDrawer = false },
                        onToggleEnabled = { en -> updateConfig { it.copy(eqEnabled = en) } },
                        onBandChange = { idx, db ->
                            updateConfig { cfg ->
                                val next = cfg.eqBandsDb.toMutableList()
                                if (idx in next.indices) next[idx] = db
                                cfg.copy(eqEnabled = true, eqBandsDb = next)
                            }
                        },
                        onApplyPreset = { preset ->
                            updateConfig { it.copy(eqEnabled = true, eqBandsDb = preset.gainsDb) }
                        },
                    )
                }

                // Right-Side Sliding Play Queue Drawer
                AnimatedVisibility(
                    visible = showQueueDrawer,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(tween(180)),
                    exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(tween(150)),
                    modifier = Modifier.align(Alignment.CenterEnd).padding(top = 40.dp, bottom = 66.dp),
                ) {
                    val queueListState = rememberLazyListState()
                    Box(
                        modifier = Modifier
                            .width(340.dp)
                            .fillMaxHeight()
                            .shadow(18.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(0.6.dp, MaterialTheme.colorScheme.outline),
                    ) {
                        Column(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("播放队列 · ${activeQueue.size}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                IconButton(onClick = { showQueueDrawer = false }, modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                    Icon(Icons.Rounded.Close, contentDescription = "关闭队列", modifier = Modifier.size(16.dp))
                                }
                            }
                            HorizontalDivider(thickness = 0.6.dp, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 10.dp))
                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                LazyColumn(state = queueListState, modifier = Modifier.fillMaxSize().padding(end = 6.dp)) {
                                    itemsIndexed(activeQueue, key = { i, s -> "${s.id}_$i" }) { idx, s ->
                                        val isCur = idx == currentIndex
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .desktopHoverClickable(shape = RoundedCornerShape(6.dp)) { playFromQueue(activeQueue, idx) }
                                                .padding(horizontal = 8.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            DesktopAlbumCover(filePath = s.filePath, modifier = Modifier.size(34.dp), cornerRadius = 4.dp, iconSize = 14.dp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(s.title, style = MaterialTheme.typography.bodySmall, fontWeight = if (isCur) FontWeight.SemiBold else FontWeight.Normal, color = if (isCur) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                Text(s.artist, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            }
                                        }
                                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                                    }
                                }
                                DesktopThinScrollbar(listState = queueListState, modifier = Modifier.align(Alignment.CenterEnd))
                            }
                        }
                    }
                }

                // Cold-Start Cinnabar Archival Splash
                DesktopSplashOverlay(visible = showSplash, darkMode = config.darkMode)
            }

            // Quiet Bookbinding Dialogs
            if (showCreatePlaylistDialog) {
                var newName by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showCreatePlaylistDialog = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    title = { Text("新建歌单", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) },
                    text = {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            singleLine = true,
                            placeholder = { Text("输入歌单名称") },
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val trimmed = newName.trim()
                                if (trimmed.isNotEmpty()) {
                                    val pl = DesktopPlaylist(UUID.randomUUID().toString(), trimmed, emptyList())
                                    updateConfig { it.copy(playlists = it.playlists + pl) }
                                }
                                showCreatePlaylistDialog = false
                            },
                        ) { Text("创建") }
                    },
                    dismissButton = { TextButton(onClick = { showCreatePlaylistDialog = false }) { Text("取消") } },
                )
            }

            renamingPlaylist?.let { targetPl ->
                var editName by remember(targetPl.id) { mutableStateOf(targetPl.name) }
                AlertDialog(
                    onDismissRequest = { renamingPlaylist = null },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    title = { Text("重命名歌单", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) },
                    text = { OutlinedTextField(value = editName, onValueChange = { editName = it }, singleLine = true) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val trimmed = editName.trim()
                                if (trimmed.isNotEmpty()) {
                                    updateConfig { cfg -> cfg.copy(playlists = cfg.playlists.map { if (it.id == targetPl.id) it.copy(name = trimmed) else it }) }
                                }
                                renamingPlaylist = null
                            },
                        ) { Text("保存") }
                    },
                    dismissButton = { TextButton(onClick = { renamingPlaylist = null }) { Text("取消") } },
                )
            }

            addingSongToPlaylist?.let { targetSong ->
                AlertDialog(
                    onDismissRequest = { addingSongToPlaylist = null },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    title = { Text("添加到歌单", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) },
                    text = {
                        if (config.playlists.isEmpty()) {
                            Text("还没有自建歌单，请先在「歌单」页新建。", style = MaterialTheme.typography.bodySmall)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                config.playlists.forEach { pl ->
                                    val checked = targetSong.filePath in pl.songPaths
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .desktopHoverClickable(shape = RoundedCornerShape(6.dp)) {
                                                updateConfig { cfg ->
                                                    cfg.copy(
                                                        playlists = cfg.playlists.map { item ->
                                                            if (item.id != pl.id) item
                                                            else item.copy(songPaths = if (checked) item.songPaths - targetSong.filePath else item.songPaths + targetSong.filePath)
                                                        },
                                                    )
                                                }
                                            }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Checkbox(checked = checked, onCheckedChange = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(pl.name, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = { TextButton(onClick = { addingSongToPlaylist = null }) { Text("完成") } },
                )
            }
        }
        }
    }

    // Collector Floating Mini Player Window (Light Washi + Dark Dynamic Obsidian, Pin Toggle, Live Lyric & Seek Filament)
    if (showMiniVinylWindow) {
        var miniAlwaysOnTop by remember { mutableStateOf(true) }
        val miniState = rememberWindowState(size = DpSize(416.dp, 126.dp), position = WindowPosition(Alignment.BottomEnd))
        Window(
            onCloseRequest = { showMiniVinylWindow = false },
            state = miniState,
            title = "简约音乐 · Mini",
            icon = windowAppIcon,
            undecorated = true,
            transparent = true,
            alwaysOnTop = miniAlwaysOnTop,
            resizable = false,
        ) {
            MaterialTheme(colorScheme = colorScheme, typography = scaledTypo) {
                val miniSong = playerState.currentSong
                val miniDark = config.darkMode
                val miniMood = rememberSongDominantColor(miniSong?.filePath)
                val miniBg = if (miniDark) lerp(Color(0xFF141E26), miniMood, 0.34f) else colorScheme.surface
                val miniInk = if (miniDark) Color(0xFFF4F7F9) else colorScheme.onBackground
                val miniMuted = if (miniDark) Color(0xFFA2B0BA) else colorScheme.onSurfaceVariant
                val miniAccent = if (miniDark) lerp(Color(0xFFA9DAE8), miniMood, 0.35f) else colorScheme.primary
                val miniBorder = if (miniDark) Color.White.copy(alpha = 0.14f) else colorScheme.outline
                val miniActiveIdx = remember(currentLyrics, playerState.positionMs) {
                    val f = currentLyrics.indexOfLast { it.timeMs <= playerState.positionMs }
                    if (f >= 0) f else -1
                }
                val miniLyricText = remember(currentLyrics, miniActiveIdx) {
                    currentLyrics.getOrNull(miniActiveIdx)?.let { splitBilingualLyric(it.primaryText, it.secondaryText).first }.orEmpty()
                }
                val miniProg = if (playerState.durationMs > 0L) (playerState.positionMs.toFloat() / playerState.durationMs).coerceIn(0f, 1f) else 0f
                val miniIsFav = miniSong?.filePath in config.favoritePaths

                WindowDraggableArea(modifier = Modifier.fillMaxSize()) {
                    val cardShape = RoundedCornerShape(18.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .shadow(14.dp, cardShape, ambientColor = Color.Black.copy(alpha = 0.28f))
                            .clip(cardShape)
                            .background(miniBg.copy(alpha = 0.97f))
                            .border(0.75.dp, miniBorder, cardShape),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(start = 12.dp, end = 14.dp, top = 10.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Left: 76dp Square Cover Sleeve + Peeking Grooved 180g Vinyl LP
                            Box(modifier = Modifier.width(102.dp).height(76.dp), contentAlignment = Alignment.CenterStart) {
                                DesktopSpinningVinylDisc(
                                    filePath = miniSong?.filePath,
                                    rotationDegrees = vinylAngleDeg,
                                    discSize = 68.dp,
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                )
                                DesktopAlbumCover(
                                    filePath = miniSong?.filePath,
                                    modifier = Modifier.size(76.dp).shadow(6.dp, RoundedCornerShape(6.dp)),
                                    cornerRadius = 6.dp,
                                    iconSize = 24.dp,
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                                // Top row: Title + Artist + Pin / Restore / Close
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(miniSong?.title ?: "暂未播放", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = miniInk, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                                        Text(miniSong?.artist ?: "简约音乐", fontSize = 11.5.sp, color = miniMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { miniAlwaysOnTop = !miniAlwaysOnTop }, modifier = Modifier.size(22.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                            Icon(Icons.Rounded.PushPin, contentDescription = "置顶", tint = if (miniAlwaysOnTop) miniAccent else miniMuted, modifier = Modifier.size(13.dp))
                                        }
                                        IconButton(onClick = { mainWindowState.isMinimized = false; showMiniVinylWindow = false }, modifier = Modifier.size(22.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                            Icon(Icons.Rounded.OpenInFull, contentDescription = "还原主窗口", tint = miniMuted, modifier = Modifier.size(13.dp))
                                        }
                                        IconButton(onClick = { showMiniVinylWindow = false }, modifier = Modifier.size(22.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                            Icon(Icons.Rounded.Close, contentDescription = "关闭", tint = miniMuted, modifier = Modifier.size(13.dp))
                                        }
                                    }
                                }

                                // Middle row: Live single-line synced lyric
                                Text(
                                    text = miniLyricText.ifBlank { "纯粹聆听 · 静享此刻" },
                                    fontSize = 11.5.sp,
                                    color = if (miniDark) miniAccent.copy(alpha = 0.88f) else miniMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )

                                // Bottom row: Transport Controls + Favorite + Time
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(onClick = { skipPrevious() }, modifier = Modifier.size(24.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                            Icon(Icons.Rounded.SkipPrevious, contentDescription = "上一首", tint = miniInk, modifier = Modifier.size(18.dp))
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(CircleShape)
                                                .background(miniAccent)
                                                .pointerHoverIcon(PointerIcon.Hand)
                                                .clickable { togglePlayOrResume() },
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(
                                                imageVector = if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                                contentDescription = "播放/暂停",
                                                tint = if (miniDark) Color(0xFF0E1820) else Color(0xFFFDFBF7),
                                                modifier = Modifier.size(18.dp),
                                            )
                                        }
                                        IconButton(onClick = { skipNext() }, modifier = Modifier.size(24.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                            Icon(Icons.Rounded.SkipNext, contentDescription = "下一首", tint = miniInk, modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(
                                            onClick = {
                                                val p = miniSong?.filePath ?: return@IconButton
                                                updateConfig { cfg -> cfg.copy(favoritePaths = if (p in cfg.favoritePaths) cfg.favoritePaths - p else cfg.favoritePaths + p) }
                                            },
                                            modifier = Modifier.size(24.dp).pointerHoverIcon(PointerIcon.Hand),
                                        ) {
                                            Icon(
                                                imageVector = if (miniIsFav) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                                contentDescription = "收藏",
                                                tint = if (miniIsFav) miniAccent else miniMuted,
                                                modifier = Modifier.size(15.dp),
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${formatDuration(playerState.positionMs)} / ${formatDuration(playerState.durationMs)}",
                                        fontSize = 10.5.sp,
                                        color = miniMuted,
                                    )
                                }
                            }
                        }
                        // Bottom-edge razor-thin clickable progress filament
                        var seekW by remember { mutableFloatStateOf(1f) }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(6.dp)
                                .onSizeChanged { seekW = it.width.toFloat().coerceAtLeast(1f) }
                                .pointerHoverIcon(PointerIcon.Hand)
                                .pointerInput(playerState.durationMs) {
                                    detectTapGestures { off ->
                                        if (playerState.durationMs > 0L) {
                                            engine.seekTo(((off.x / seekW).coerceIn(0f, 1f) * playerState.durationMs).toLong())
                                        }
                                    }
                                },
                            contentAlignment = Alignment.BottomStart,
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().height(2.5.dp).background(miniMuted.copy(alpha = 0.16f)))
                            Box(modifier = Modifier.fillMaxWidth(miniProg).height(2.5.dp).background(miniAccent))
                        }
                    }
                }
            }
        }
    }
}

// =================================================================================================
// 100% UNIFIED BOOKBINDING BOTTOM CONTROL RAIL (Identical geometry in Main Window & Immersive Mode!)
// =================================================================================================
@Composable
private fun UnifiedBottomControlRail(
    playerState: DesktopPlayerState,
    isFavorite: Boolean,
    isImmersive: Boolean,
    isDarkSurface: Boolean,
    floatingCapsule: Boolean = false,
    liquidGlassEnabled: Boolean = false,
    liquidGlassLevel: DesktopLiquidGlassLevel = DesktopLiquidGlassLevel.MEDIUM,
    barHeightDp: Int = 62,
    accentColor: Color,
    eqActive: Boolean,
    queueOpen: Boolean,
    spectrumVisible: Boolean,
    onOpenImmersive: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onSkipPrevious: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleEq: () -> Unit,
    onToggleQueue: () -> Unit,
    onToggleSpectrum: () -> Unit,
) {
    val song = playerState.currentSong
    val useFloating = floatingCapsule && !isImmersive
    val capsuleShape = RoundedCornerShape(20.dp)
    val barBg = when {
        isImmersive -> Color.Transparent
        liquidGlassEnabled -> MaterialTheme.colorScheme.surface.copy(alpha = liquidGlassLevel.surfaceAlpha)
        else -> MaterialTheme.colorScheme.surface
    }
    val ink = if (isDarkSurface) Color(0xFFF3EEE4) else MaterialTheme.colorScheme.onBackground
    val muted = if (isDarkSurface) Color(0xFFA69F95) else MaterialTheme.colorScheme.onSurfaceVariant
    val hairline = if (isDarkSurface) Color.White.copy(alpha = 0.14f) else MaterialTheme.colorScheme.outline
    val progress = if (playerState.durationMs > 0L) (playerState.positionMs.toFloat() / playerState.durationMs).coerceIn(0f, 1f) else 0f

    var railWidthPx by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (useFloating) Modifier.padding(horizontal = 24.dp, vertical = 10.dp) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (useFloating) {
                    Modifier
                        .shadow(if (liquidGlassEnabled) 14.dp else 8.dp, capsuleShape, ambientColor = Color.Black.copy(alpha = 0.16f))
                        .clip(capsuleShape)
                        .background(barBg)
                        .border(
                            width = 0.8.dp,
                            brush = Brush.verticalGradient(
                                0.0f to Color.White.copy(alpha = if (liquidGlassEnabled) liquidGlassLevel.rimAlpha + 0.14f else 0.18f),
                                1.0f to hairline,
                            ),
                            shape = capsuleShape,
                        )
                } else {
                    Modifier.background(barBg)
                },
            ),
    ) {
        // Razor-thin full-width bookmark progress line (click or drag anywhere along the edge to seek!)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .onSizeChanged { railWidthPx = it.width.toFloat().coerceAtLeast(1f) }
                .pointerHoverIcon(PointerIcon.Hand)
                .pointerInput(playerState.durationMs) {
                    detectTapGestures { off ->
                        if (playerState.durationMs > 0L) onSeekTo(((off.x / railWidthPx).coerceIn(0f, 1f) * playerState.durationMs).toLong())
                    }
                }
                .pointerInput(playerState.durationMs) {
                    detectHorizontalDragGestures { change, _ ->
                        if (playerState.durationMs > 0L) onSeekTo(((change.position.x / railWidthPx).coerceIn(0f, 1f) * playerState.durationMs).toLong())
                    }
                },
            contentAlignment = Alignment.TopStart,
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(hairline))
            Box(modifier = Modifier.fillMaxWidth(progress).height(2.5.dp).background(accentColor))
        }

        Row(
            modifier = Modifier.fillMaxWidth().height(barHeightDp.coerceIn(54, 76).dp).padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left: Cover + Title in Main Mode, or quiet timestamps in Immersive Mode
            Row(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (!isImmersive && song != null) {
                            Modifier.desktopHoverClickable(shape = RoundedCornerShape(6.dp), onClick = onOpenImmersive).padding(vertical = 4.dp, horizontal = 4.dp)
                        } else Modifier,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!isImmersive) {
                    DesktopAlbumCover(filePath = song?.filePath, modifier = Modifier.size(40.dp), cornerRadius = 4.dp, iconSize = 18.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(song?.title ?: "暂未播放", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            text = if (song != null) "${song.artist} · ${formatDuration(playerState.positionMs)} / ${formatDuration(playerState.durationMs)}" else "选择一首本地唱片开始聆听",
                            fontSize = 11.sp,
                            color = muted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (song != null) {
                        IconButton(onClick = onToggleFavorite, modifier = Modifier.size(30.dp).pointerHoverIcon(PointerIcon.Hand)) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = "喜欢",
                                tint = if (isFavorite) accentColor else muted,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                } else {
                    Text(
                        text = "${formatDuration(playerState.positionMs)}  /  ${formatDuration(playerState.durationMs)}",
                        fontSize = 12.sp,
                        color = muted,
                    )
                    if (song != null) {
                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(onClick = onToggleFavorite, modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand)) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = "喜欢",
                                tint = if (isFavorite) accentColor else muted,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }

            // Center: Pure Minimal Transport Controls (IDENTICAL across every view & scene!)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                IconButton(onClick = onToggleShuffle, modifier = Modifier.size(30.dp).pointerHoverIcon(PointerIcon.Hand)) {
                    Icon(Icons.Rounded.Shuffle, contentDescription = "随机", tint = if (playerState.shuffleEnabled) accentColor else muted, modifier = Modifier.size(16.dp))
                }
                Box(modifier = Modifier.size(34.dp).clip(CircleShape).desktopSpringPress(onClick = onSkipPrevious), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.SkipPrevious, contentDescription = "上一首", tint = ink, modifier = Modifier.size(22.dp))
                }
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).desktopSpringPress(pressedScale = 0.85f, onClick = onTogglePlayPause), contentAlignment = Alignment.Center) {
                    AnimatedContent(targetState = playerState.isPlaying, transitionSpec = { (fadeIn(tween(140)) + scaleIn(initialScale = 0.82f)) togetherWith (fadeOut(tween(110)) + scaleOut(targetScale = 0.82f)) }, label = "playPauseAnim") { playing ->
                        Icon(
                            imageVector = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = "播放/暂停",
                            tint = ink,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }
                Box(modifier = Modifier.size(34.dp).clip(CircleShape).desktopSpringPress(onClick = onSkipNext), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.SkipNext, contentDescription = "下一首", tint = ink, modifier = Modifier.size(22.dp))
                }
                IconButton(onClick = onCycleRepeat, modifier = Modifier.size(30.dp).pointerHoverIcon(PointerIcon.Hand)) {
                    Icon(
                        imageVector = if (playerState.repeatMode == RepeatMode.ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                        contentDescription = "循环模式",
                        tint = if (playerState.repeatMode != RepeatMode.OFF) accentColor else muted,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            // Right: Sleek Volume + EQ + Queue + Spectrum/Immersive
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = "音量", tint = muted, modifier = Modifier.size(16.dp))
                DesktopSleekSlider(
                    value = playerState.volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..1f,
                    activeColor = accentColor,
                    trackColor = hairline,
                    modifier = Modifier.width(88.dp).padding(horizontal = 6.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable(onClick = onToggleEq),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "EQ",
                        fontSize = 11.5.sp,
                        lineHeight = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (eqActive) accentColor else muted,
                    )
                }
                IconButton(onClick = onToggleQueue, modifier = Modifier.size(32.dp).pointerHoverIcon(PointerIcon.Hand)) {
                    Icon(Icons.Rounded.QueueMusic, contentDescription = "播放队列", tint = if (queueOpen) accentColor else muted, modifier = Modifier.size(18.dp))
                }
                if (isImmersive) {
                    IconButton(onClick = onToggleSpectrum, modifier = Modifier.size(32.dp).pointerHoverIcon(PointerIcon.Hand)) {
                        Icon(Icons.Rounded.GraphicEq, contentDescription = "实时频谱开关", tint = if (spectrumVisible) accentColor else muted, modifier = Modifier.size(18.dp))
                    }
                } else if (song != null) {
                    IconButton(onClick = onOpenImmersive, modifier = Modifier.size(32.dp).pointerHoverIcon(PointerIcon.Hand)) {
                        Icon(Icons.Rounded.OpenInFull, contentDescription = "沉浸聆听", tint = ink, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
    }
}

// =================================================================================================
// 1. LIBRARY PAGE (Book Monograph Header + Borderless Hairline Track Rows)
// =================================================================================================
@Composable
private fun DesktopLibraryPage(
    subTab: LibrarySubTab,
    onSelectSubTab: (LibrarySubTab) -> Unit,
    songs: List<DesktopSong>,
    artists: List<DesktopArtist>,
    folders: List<DesktopFolder>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sortOrder: SortOrder,
    onSelectSortOrder: (SortOrder) -> Unit,
    currentSongId: String?,
    favoritePaths: Set<String>,
    density: DesktopLayoutDensity,
    onPlayAt: (Int) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddToPlaylist: (DesktopSong) -> Unit,
    onSelectArtist: (DesktopArtist) -> Unit,
    onSelectFolder: (DesktopFolder) -> Unit,
    onPickFolder: () -> Unit,
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 1180.dp)
                .fillMaxWidth()
                .padding(horizontal = 36.dp, vertical = 26.dp),
        ) {
            // Editorial Chapter Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(26.dp)) {
                    Column {
                        Text("曲库", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.width(34.dp).height(2.dp).background(MaterialTheme.colorScheme.onBackground))
                    }

                    Row(
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .height(34.dp)
                            .clip(RoundedCornerShape(17.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f))
                            .border(0.6.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(17.dp))
                            .padding(2.5.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LibrarySubTab.entries.forEach { tab ->
                            val sel = tab == subTab
                            val pillShape = RoundedCornerShape(14.5.dp)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .then(
                                        if (sel) {
                                            Modifier.clip(pillShape).background(MaterialTheme.colorScheme.surface).border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f), pillShape)
                                        } else {
                                            Modifier.clip(pillShape)
                                        },
                                    )
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .clickable { onSelectSubTab(tab) }
                                    .padding(horizontal = 14.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = tab.label,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                // Quiet Hairline Search + Sort
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 4.dp)) {
                    Row(
                        modifier = Modifier
                            .width(210.dp)
                            .height(32.dp)
                            .border(0.6.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (searchQuery.isEmpty()) Text("检索歌曲、歌手", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        if (searchQuery.isNotEmpty()) {
                            Icon(Icons.Rounded.Close, contentDescription = "清除", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp).pointerHoverIcon(PointerIcon.Hand).clickable { onSearchQueryChange("") })
                        }
                    }

                    if (subTab == LibrarySubTab.SONGS) {
                        Box {
                            Row(
                                modifier = Modifier
                                    .height(32.dp)
                                    .border(0.6.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .clickable { sortMenuExpanded = true }
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(sortOrder.label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)).border(0.6.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                            ) {
                                SortOrder.entries.forEach { order ->
                                    DropdownMenuItem(
                                        text = { Text(if (order == sortOrder) "✓ ${order.label}" else order.label, fontSize = 13.sp) },
                                        onClick = { sortMenuExpanded = false; onSelectSortOrder(order) },
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(thickness = 0.6.dp, color = MaterialTheme.colorScheme.outline)
            var selectedGenre by remember { mutableStateOf<String?>(null) }
            val availableGenres = remember(songs) {
                val minCount = if (songs.size >= 20) 2 else 1
                songs.asSequence().map { it.genre.trim() }.filter { it.isNotEmpty() }
                    .groupingBy { it }.eachCount().entries.filter { it.value >= minCount }
                    .sortedByDescending { it.value }.map { it.key }
            }
            val effectiveGenre = selectedGenre?.takeIf { it in availableGenres }
            val genreFilteredSongs = remember(songs, effectiveGenre) {
                if (effectiveGenre == null) songs else songs.filter { it.genre == effectiveGenre }
            }
            if (subTab == LibrarySubTab.SONGS && availableGenres.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    item(key = "desk_genre_all") {
                        val isAll = effectiveGenre == null
                        val chipShape = RoundedCornerShape(14.dp)
                        Box(
                            modifier = Modifier
                                .height(28.dp)
                                .clip(chipShape)
                                .background(if (isAll) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                .then(if (isAll) Modifier else Modifier.border(0.6.dp, MaterialTheme.colorScheme.outline, chipShape))
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable { selectedGenre = null }
                                .padding(horizontal = 13.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("全部", fontSize = 12.sp, fontWeight = if (isAll) FontWeight.SemiBold else FontWeight.Medium, color = if (isAll) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                    items(availableGenres, key = { "desk_genre_$it" }) { g ->
                        val isSel = effectiveGenre == g
                        val chipShape = RoundedCornerShape(14.dp)
                        Box(
                            modifier = Modifier
                                .height(28.dp)
                                .clip(chipShape)
                                .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                .then(if (isSel) Modifier else Modifier.border(0.6.dp, MaterialTheme.colorScheme.outline, chipShape))
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable { selectedGenre = if (isSel) null else g }
                                .padding(horizontal = 13.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(g, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Medium, color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            when (subTab) {
                LibrarySubTab.SONGS -> {
                    val activeSongList = genreFilteredSongs
                    if (activeSongList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("本地曲库暂无曲目", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "+ 选择本地音乐文件夹",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.border(0.75.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(8.dp)).pointerHoverIcon(PointerIcon.Hand).clickable { onPickFolder() }.padding(horizontal = 16.dp, vertical = 8.dp),
                                )
                            }
                        }
                    } else {
                        val listState = rememberLazyListState()
                        val listScope = rememberCoroutineScope()
                        val showAlphaBar = sortOrder != SortOrder.DURATION
                        val letterIndexMap = remember(activeSongList, sortOrder) {
                            val map = LinkedHashMap<String, Int>()
                            activeSongList.forEachIndexed { idx, s ->
                                val keyText = when (sortOrder) {
                                    SortOrder.TITLE -> s.title
                                    SortOrder.ARTIST -> s.artist
                                    SortOrder.ALBUM -> s.album
                                    SortOrder.DURATION -> s.title
                                }
                                val letter = DesktopAlphabetIndex.initialLetter(keyText)
                                map.putIfAbsent(letter, idx)
                            }
                            map
                        }
                        val firstVisibleIdx = listState.firstVisibleItemIndex
                        val activeLetter = remember(firstVisibleIdx, activeSongList, sortOrder) {
                            val s = activeSongList.getOrNull(firstVisibleIdx) ?: return@remember null
                            val keyText = when (sortOrder) {
                                SortOrder.TITLE -> s.title
                                SortOrder.ARTIST -> s.artist
                                SortOrder.ALBUM -> s.album
                                SortOrder.DURATION -> s.title
                            }
                            DesktopAlphabetIndex.initialLetter(keyText)
                        }
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Crisp Desktop Multi-Column Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 14.dp, end = if (showAlphaBar) 44.dp else 22.dp, top = 4.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Spacer(modifier = Modifier.width(54.dp))
                                Text("歌曲", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f), modifier = Modifier.weight(1.35f))
                                Text("歌手", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f), modifier = Modifier.weight(0.75f))
                                Text("专辑", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f), modifier = Modifier.weight(0.85f))
                                Text("音质", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f), modifier = Modifier.width(72.dp))
                                Text("时长", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f), textAlign = TextAlign.End, modifier = Modifier.width(78.dp))
                            }
                            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize().padding(end = if (showAlphaBar) 32.dp else 10.dp),
                                contentPadding = PaddingValues(top = 6.dp, bottom = 92.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                itemsIndexed(activeSongList, key = { _, s -> s.id }) { idx, song ->
                                    BookTrackRow(
                                        index = idx + 1,
                                        song = song,
                                        isCurrent = song.id == currentSongId,
                                        isFavorite = song.filePath in favoritePaths,
                                        density = density,
                                        onClick = { onPlayAt(idx) },
                                        onToggleFavorite = { onToggleFavorite(song.filePath) },
                                        onAddToPlaylist = { onAddToPlaylist(song) },
                                    )
                                }
                            }
                            if (showAlphaBar) {
                                DesktopAlphabetIndexBar(
                                    availableLetters = letterIndexMap.keys,
                                    activeLetter = activeLetter,
                                    onSelectLetter = { picked ->
                                        val target = letterIndexMap[picked] ?: run {
                                            val rank = DesktopAlphabetIndex.sortRank(picked)
                                            letterIndexMap.entries.firstOrNull { DesktopAlphabetIndex.sortRank(it.key) >= rank }?.value
                                        }
                                        if (target != null) {
                                            listScope.launch { listState.scrollToItem(target) }
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 10.dp),
                                )
                            }
                            DesktopThinScrollbar(listState = listState, modifier = Modifier.align(Alignment.CenterEnd))
                        }
                        }
                    }
                }

                LibrarySubTab.ARTISTS -> {
                    val listState = rememberLazyListState()
                    val listScope = rememberCoroutineScope()
                    val artistLetterMap = remember(artists) {
                        val map = LinkedHashMap<String, Int>()
                        artists.forEachIndexed { idx, a ->
                            map.putIfAbsent(DesktopAlphabetIndex.initialLetter(a.name), idx)
                        }
                        map
                    }
                    val firstArtistIdx = listState.firstVisibleItemIndex
                    val activeArtistLetter = remember(firstArtistIdx, artists) {
                        artists.getOrNull(firstArtistIdx)?.let { DesktopAlphabetIndex.initialLetter(it.name) }
                    }
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(end = 32.dp)) {
                            items(artists, key = { it.name }) { artist ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .desktopHoverClickable(shape = RoundedCornerShape(6.dp)) { onSelectArtist(artist) }
                                        .padding(horizontal = 8.dp, vertical = (density.rowPaddingDp + 2).dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(Icons.Rounded.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(artist.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("${artist.songs.size} 首", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.65f))
                            }
                        }
                        DesktopAlphabetIndexBar(
                            availableLetters = artistLetterMap.keys,
                            activeLetter = activeArtistLetter,
                            onSelectLetter = { picked ->
                                val target = artistLetterMap[picked] ?: run {
                                    val rank = DesktopAlphabetIndex.sortRank(picked)
                                    artistLetterMap.entries.firstOrNull { DesktopAlphabetIndex.sortRank(it.key) >= rank }?.value
                                }
                                if (target != null) {
                                    listScope.launch { listState.scrollToItem(target) }
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 10.dp),
                        )
                        DesktopThinScrollbar(listState = listState, modifier = Modifier.align(Alignment.CenterEnd))
                    }
                }

                LibrarySubTab.FOLDERS -> {
                    val listState = rememberLazyListState()
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(end = 10.dp)) {
                            items(folders, key = { it.path }) { folder ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .desktopHoverClickable(shape = RoundedCornerShape(6.dp)) { onSelectFolder(folder) }
                                        .padding(horizontal = 8.dp, vertical = (density.rowPaddingDp + 2).dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(Icons.Rounded.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(folder.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(folder.path, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Text("${folder.songs.size} 首", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.65f))
                            }
                        }
                        DesktopThinScrollbar(listState = listState, modifier = Modifier.align(Alignment.CenterEnd))
                    }
                }
            }
        }
    }
}

/**
 * Serene Monograph Track Row (borderless, whisper-thin hairline divider, hover reveals actions).
 */
@Composable
private fun BookTrackRow(
    index: Int,
    song: DesktopSong,
    isCurrent: Boolean,
    isFavorite: Boolean,
    density: DesktopLayoutDensity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
) {
    val hoverSrc = remember { MutableInteractionSource() }
    val hovered by hoverSrc.collectIsHoveredAsState()
    val rowShape = RoundedCornerShape(10.dp)
    val outline = MaterialTheme.colorScheme.outline

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                when {
                    isCurrent -> Modifier
                        .clip(rowShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        .border(0.6.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), rowShape)
                    hovered -> Modifier
                        .shadow(3.dp, rowShape, ambientColor = Color.Black.copy(alpha = 0.08f))
                        .clip(rowShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(0.6.dp, outline.copy(alpha = 0.85f), rowShape)
                    else -> Modifier.clip(rowShape)
                },
            )
            .hoverable(hoverSrc)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = density.rowPaddingDp.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DesktopAlbumCover(
            filePath = song.filePath,
            modifier = Modifier.size(density.artSizeDp.dp).shadow(2.dp, RoundedCornerShape(6.dp)),
            cornerRadius = 6.dp,
            iconSize = 16.dp,
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = song.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.35f).padding(end = 12.dp),
        )
        Text(
            text = song.artist,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.75f).padding(end = 12.dp),
        )
        Text(
            text = song.album,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.82f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.85f).padding(end = 10.dp),
        )
        Box(modifier = Modifier.width(72.dp), contentAlignment = Alignment.CenterStart) {
            val badgeLabel = if (song.isHiRes) "Hi-Res" else song.formatLabel
            val badgeColor = if (song.isHiRes) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .border(0.6.dp, badgeColor.copy(alpha = 0.45f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(badgeLabel, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = badgeColor, maxLines = 1)
            }
        }
        Row(
            modifier = Modifier.width(78.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            if (hovered || isFavorite) {
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(24.dp).pointerHoverIcon(PointerIcon.Hand)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "收藏",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            if (hovered) {
                IconButton(onClick = onAddToPlaylist, modifier = Modifier.size(24.dp).pointerHoverIcon(PointerIcon.Hand)) {
                    Icon(Icons.Rounded.Add, contentDescription = "加入歌单", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formatDuration(song.durationMs),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
            )
        }
    }
}

// =================================================================================================
// 2. ALBUMS PAGE (Clean Gallery Monograph Grid)
// =================================================================================================
@Composable
private fun DesktopAlbumsPage(
    albums: List<DesktopAlbum>,
    density: DesktopLayoutDensity,
    viewMode: DesktopAlbumViewMode = DesktopAlbumViewMode.GRID_6,
    onSelectViewMode: (DesktopAlbumViewMode) -> Unit = {},
    onSelectAlbum: (DesktopAlbum) -> Unit,
) {
    var albumQuery by remember { mutableStateOf("") }
    val filteredAlbums = remember(albums, albumQuery) {
        val q = albumQuery.trim()
        if (q.isEmpty()) albums
        else albums.filter { it.name.contains(q, ignoreCase = true) || it.artist.contains(q, ignoreCase = true) }
    }
    val outline = MaterialTheme.colorScheme.outline
    val surface = MaterialTheme.colorScheme.surface
    val ink = MaterialTheme.colorScheme.onBackground
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    val accent = MaterialTheme.colorScheme.primary

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 1220.dp)
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("专辑", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ink)
                    Text("${filteredAlbums.size} 张专辑", fontSize = 12.5.sp, color = muted, modifier = Modifier.padding(bottom = 4.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Sleek album search pill
                    Row(
                        modifier = Modifier
                            .width(190.dp)
                            .height(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(surface)
                            .border(0.6.dp, outline, RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.Search, contentDescription = null, tint = muted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (albumQuery.isEmpty()) Text("筛选专辑或歌手", fontSize = 11.5.sp, color = muted.copy(alpha = 0.65f))
                            BasicTextField(
                                value = albumQuery,
                                onValueChange = { albumQuery = it },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall.copy(color = ink),
                                cursorBrush = SolidColor(accent),
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        if (albumQuery.isNotEmpty()) {
                            Icon(Icons.Rounded.Close, contentDescription = "清除", tint = muted, modifier = Modifier.size(13.dp).pointerHoverIcon(PointerIcon.Hand).clickable { albumQuery = "" })
                        }
                    }

                    // 3-Mode Album View Switcher (4列精装 / 6列高效 / 书目列表)
                    Row(
                        modifier = Modifier
                            .height(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f))
                            .border(0.6.dp, outline, RoundedCornerShape(16.dp))
                            .padding(2.5.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DesktopAlbumViewMode.entries.forEach { mode ->
                            val sel = mode == viewMode
                            val pillShape = RoundedCornerShape(13.5.dp)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .then(
                                        if (sel) {
                                            Modifier.clip(pillShape).background(surface).border(0.5.dp, outline.copy(alpha = 0.75f), pillShape)
                                        } else {
                                            Modifier.clip(pillShape)
                                        },
                                    )
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .clickable { onSelectViewMode(mode) }
                                    .padding(horizontal = 11.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = mode.label,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (sel) accent else muted,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 0.6.dp, color = outline)
            Spacer(modifier = Modifier.height(density.gridSpacingDp.dp))

            val listState = rememberLazyListState()
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                when (viewMode) {
                    DesktopAlbumViewMode.LIST -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize().padding(end = 10.dp),
                            contentPadding = PaddingValues(bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            items(filteredAlbums, key = { "${it.name}_${it.artist}" }) { album ->
                                val hoverSrc = remember { MutableInteractionSource() }
                                val hovered by hoverSrc.collectIsHoveredAsState()
                                val totalMs = remember(album) { album.songs.sumOf { it.durationMs.coerceAtLeast(0L) } }
                                val firstSong = album.songs.firstOrNull()
                                val rowShape = RoundedCornerShape(10.dp)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(
                                            if (hovered) Modifier.shadow(2.dp, rowShape, ambientColor = Color.Black.copy(alpha = 0.07f)).clip(rowShape).background(surface).border(0.6.dp, outline, rowShape)
                                            else Modifier.clip(rowShape),
                                        )
                                        .hoverable(hoverSrc)
                                        .pointerHoverIcon(PointerIcon.Hand)
                                        .clickable { onSelectAlbum(album) }
                                        .padding(horizontal = 12.dp, vertical = density.rowPaddingDp.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    DesktopAlbumCover(filePath = firstSong?.filePath, modifier = Modifier.size(density.artSizeDp.dp).shadow(2.dp, RoundedCornerShape(5.dp)), cornerRadius = 5.dp, iconSize = 18.dp)
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(album.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = ink, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1.3f).padding(end = 12.dp))
                                    Text(album.artist, style = MaterialTheme.typography.bodySmall, color = muted, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(0.9f).padding(end = 12.dp))
                                    Text("${album.songs.size} 首", fontSize = 12.sp, color = muted, modifier = Modifier.width(56.dp))
                                    Text(firstSong?.formatLabel ?: "AUDIO", fontSize = 10.5.sp, color = if (firstSong?.isHiRes == true) accent else muted, modifier = Modifier.width(58.dp))
                                    Text(formatDuration(totalMs), fontSize = 12.sp, color = muted, textAlign = TextAlign.End, modifier = Modifier.width(58.dp))
                                }
                            }
                        }
                    }

                    DesktopAlbumViewMode.GRID_6 -> {
                        val cols = ((maxWidth.value / 158f).toInt()).coerceIn(5, 7)
                        val rows = remember(filteredAlbums, cols) { filteredAlbums.chunked(cols) }
                        val gapDp = density.gridSpacingDp.dp
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize().padding(end = 10.dp),
                            contentPadding = PaddingValues(bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(gapDp + 4.dp),
                        ) {
                            items(rows, key = { row -> row.joinToString("|") { "${it.name}_${it.artist}" } }) { rowAlbums ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gapDp)) {
                                    rowAlbums.forEach { album ->
                                        val hoverSrc = remember { MutableInteractionSource() }
                                        val hovered by hoverSrc.collectIsHoveredAsState()
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .hoverable(hoverSrc)
                                                .pointerHoverIcon(PointerIcon.Hand)
                                                .clickable { onSelectAlbum(album) },
                                        ) {
                                            DesktopAlbumCover(
                                                filePath = album.songs.firstOrNull()?.filePath,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(1f)
                                                    .shadow(if (hovered) 10.dp else 3.dp, RoundedCornerShape(6.dp), ambientColor = Color.Black.copy(alpha = 0.14f))
                                                    .border(0.5.dp, outline.copy(alpha = if (hovered) 0.9f else 0.55f), RoundedCornerShape(6.dp)),
                                                cornerRadius = 6.dp,
                                                iconSize = 28.dp,
                                            )
                                            Spacer(modifier = Modifier.height(7.dp))
                                            Text(album.name, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = if (hovered) accent else ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Spacer(modifier = Modifier.height(1.dp))
                                            Text("${album.artist} · ${album.songs.size}首", fontSize = 11.sp, color = muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                    repeat(cols - rowAlbums.size) { Spacer(modifier = Modifier.weight(1f)) }
                                }
                            }
                        }
                    }

                    DesktopAlbumViewMode.GRID_4 -> {
                        val cols = ((maxWidth.value / 230f).toInt()).coerceIn(3, 5)
                        val rows = remember(filteredAlbums, cols) { filteredAlbums.chunked(cols) }
                        val gapDp = (density.gridSpacingDp + 6).dp
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize().padding(end = 10.dp),
                            contentPadding = PaddingValues(bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(gapDp),
                        ) {
                            items(rows, key = { row -> row.joinToString("|") { "${it.name}_${it.artist}" } }) { rowAlbums ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gapDp)) {
                                    rowAlbums.forEach { album ->
                                        val hoverSrc = remember { MutableInteractionSource() }
                                        val hovered by hoverSrc.collectIsHoveredAsState()
                                        val peekOffset by animateDpAsState(if (hovered) 14.dp else 0.dp, spring(dampingRatio = 0.72f, stiffness = 340f), label = "albPeek")
                                        val matShape = RoundedCornerShape(10.dp)
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .hoverable(hoverSrc)
                                                .pointerHoverIcon(PointerIcon.Hand)
                                                .clickable { onSelectAlbum(album) },
                                        ) {
                                            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f), contentAlignment = Alignment.CenterStart) {
                                                if (peekOffset > 1.dp) {
                                                    Canvas(
                                                        modifier = Modifier
                                                            .align(Alignment.CenterEnd)
                                                            .offset(x = peekOffset)
                                                            .fillMaxHeight(0.84f)
                                                            .aspectRatio(1f),
                                                    ) {
                                                        val r = size.minDimension / 2f
                                                        val c = Offset(r, r)
                                                        drawCircle(
                                                            brush = Brush.radialGradient(
                                                                0.0f to Color(0xFF0A0A0C),
                                                                0.70f to Color(0xFF17171B),
                                                                0.90f to Color(0xFF25252A),
                                                                1.0f to Color(0xFF0A0A0C),
                                                                center = c,
                                                                radius = r,
                                                            ),
                                                            radius = r,
                                                            center = c,
                                                        )
                                                        for (g in 1..6) {
                                                            drawCircle(Color.White.copy(alpha = 0.07f), radius = r * (0.55f + g * 0.06f), center = c, style = Stroke(0.7f))
                                                        }
                                                    }
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .shadow(if (hovered) 10.dp else 4.dp, matShape, ambientColor = Color.Black.copy(alpha = 0.14f))
                                                        .clip(matShape)
                                                        .background(surface)
                                                        .border(0.6.dp, outline.copy(alpha = 0.8f), matShape)
                                                        .padding(7.dp),
                                                ) {
                                                    DesktopAlbumCover(
                                                        filePath = album.songs.firstOrNull()?.filePath,
                                                        modifier = Modifier.fillMaxSize(),
                                                        cornerRadius = 5.dp,
                                                        iconSize = 36.dp,
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(9.dp))
                                            Text(album.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text("${album.artist} · ${album.songs.size} 首", fontSize = 11.5.sp, color = muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                    repeat(cols - rowAlbums.size) { Spacer(modifier = Modifier.weight(1f)) }
                                }
                            }
                        }
                    }
                }
                DesktopThinScrollbar(listState = listState, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
    }
}

// =================================================================================================
// 3. PLAYLISTS PAGE (Quiet Paper Hero Favorites + Bento Recent/Report + User Playlists)
// =================================================================================================
@Composable
private fun DesktopPlaylistsPage(
    favoriteSongs: List<DesktopSong>,
    recentSongs: List<DesktopSong>,
    allSongs: List<DesktopSong>,
    playCounts: Map<String, Int>,
    hourlyPlays: Map<Int, Int>,
    playlists: List<DesktopPlaylist>,
    songsByPath: Map<String, DesktopSong>,
    density: DesktopLayoutDensity,
    onOpenFavorites: () -> Unit,
    onPlayFavorites: () -> Unit,
    onOpenRecent: () -> Unit,
    onOpenReport: () -> Unit,
    onCreatePlaylist: () -> Unit,
    onOpenPlaylist: (DesktopPlaylist) -> Unit,
    onRenamePlaylist: (DesktopPlaylist) -> Unit,
    onDeletePlaylist: (DesktopPlaylist) -> Unit,
) {
    val cardShape = RoundedCornerShape(12.dp)
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outline
    val accent = MaterialTheme.colorScheme.primary
    val ink = MaterialTheme.colorScheme.onBackground
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    val favDurationMs = remember(favoriteSongs) { favoriteSongs.sumOf { it.durationMs.coerceAtLeast(0L) } }
    val favSub = remember(favoriteSongs.size, favDurationMs) {
        if (favoriteSongs.isEmpty()) "暂无珍藏 · 点击心形收藏喜爱曲目"
        else {
            val mins = (favDurationMs / 60_000L).toInt()
            val h = mins / 60; val m = mins % 60
            "${favoriteSongs.size} 首珍藏 · " + if (h > 0) "${h}小时${m}分" else "${mins.coerceAtLeast(1)}分钟"
        }
    }
    val totalPlays = remember(playCounts) { playCounts.values.sum() }
    val hoursText = remember(allSongs, playCounts) {
        val byPath = allSongs.associateBy { it.filePath }
        val ms = playCounts.entries.sumOf { (p, c) -> (byPath[p]?.durationMs ?: 210_000L) * c }
        val h = ms / 3_600_000.0
        when {
            ms <= 0L -> "0 分钟"
            h < 1.0 -> "${(ms / 60_000L).coerceAtLeast(1L)} 分钟"
            else -> String.format(Locale.US, "%.1f 小时", h)
        }
    }

    val listState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxHeight().widthIn(max = 1180.dp).fillMaxWidth().padding(horizontal = 36.dp),
            contentPadding = PaddingValues(vertical = 26.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "pl_hdr") {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        Column {
                            Text("歌单", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ink)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.width(34.dp).height(2.dp).background(ink))
                        }
                        Text(
                            text = "+ 新建歌单",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = accent,
                            modifier = Modifier
                                .padding(bottom = 6.dp)
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable(onClick = onCreatePlaylist),
                        )
                    }
                    HorizontalDivider(thickness = 0.6.dp, color = outline)
                }
            }

            // Top Widescreen Collector Row: Left 62% Light Washi Vinyl Hero Card + Right 38% Stacked Slim Pills
            item(key = "pl_bento_v4") {
                val heroShape = RoundedCornerShape(16.dp)
                val pillShape = RoundedCornerShape(14.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Left: Light Washi Vinyl Hero Card ("我喜欢的音乐")
                    Row(
                        modifier = Modifier
                            .weight(1.55f)
                            .height(132.dp)
                            .shadow(5.dp, heroShape, ambientColor = Color.Black.copy(alpha = 0.08f))
                            .clip(heroShape)
                            .background(surface)
                            .border(0.7.dp, outline, heroShape)
                            .desktopHoverClickable(shape = heroShape, onClick = onOpenFavorites)
                            .padding(horizontal = 22.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Mini Cinnabar Vinyl Sleeve + Peeking 180g Black LP
                        Box(modifier = Modifier.width(114.dp).height(88.dp), contentAlignment = Alignment.CenterStart) {
                            Canvas(modifier = Modifier.align(Alignment.CenterEnd).size(80.dp)) {
                                val r = size.minDimension / 2f
                                val c = Offset(r, r)
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        0.0f to Color(0xFF0A0A0B),
                                        0.68f to Color(0xFF17171A),
                                        0.90f to Color(0xFF242428),
                                        1.0f to Color(0xFF0A0A0B),
                                        center = c,
                                        radius = r,
                                    ),
                                    radius = r,
                                    center = c,
                                )
                                for (g in 1..6) {
                                    drawCircle(Color.White.copy(alpha = 0.07f), radius = r * (0.52f + g * 0.065f), center = c, style = Stroke(0.7f))
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(84.dp)
                                    .shadow(8.dp, RoundedCornerShape(8.dp), ambientColor = Color.Black.copy(alpha = 0.22f))
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(accent),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Rounded.Favorite, contentDescription = null, tint = Color(0xFFF6F2EA), modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("我喜欢的音乐", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = ink)
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(favSub, fontSize = 12.5.sp, color = muted)
                        }
                        if (favoriteSongs.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .border(1.2.dp, accent.copy(alpha = 0.78f), CircleShape)
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .clickable(onClick = onPlayFavorites),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = "播放我喜欢的音乐", tint = accent, modifier = Modifier.size(22.dp))
                            }
                        }
                    }

                    // Right: Two Slim Stacked Shortcut Pills (最近播放 + 听歌报告)
                    Column(
                        modifier = Modifier.weight(1f).height(132.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(pillShape)
                                .background(surface)
                                .border(0.7.dp, outline, pillShape)
                                .desktopHoverClickable(shape = pillShape, onClick = onOpenRecent)
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            DesktopAlbumCover(
                                filePath = recentSongs.firstOrNull()?.filePath,
                                modifier = Modifier.size(36.dp),
                                cornerRadius = 8.dp,
                                iconSize = 16.dp,
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text("最近播放", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = ink)
                                Text(
                                    text = if (recentSongs.isNotEmpty()) "${recentSongs.size} 首 · ${recentSongs.first().title}" else "暂无播放记录",
                                    fontSize = 11.5.sp,
                                    color = muted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(pillShape)
                                .background(surface)
                                .border(0.7.dp, outline, pillShape)
                                .desktopHoverClickable(shape = pillShape, onClick = onOpenReport)
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(accent.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(2.5.dp), verticalAlignment = Alignment.CenterVertically) {
                                    listOf(8.dp, 16.dp, 12.dp, 18.dp, 9.dp).forEach { bh ->
                                        Box(modifier = Modifier.width(2.5.dp).height(bh).clip(CircleShape).background(accent))
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("听歌报告", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = ink)
                                Text("$hoursText · $totalPlays 次播放", fontSize = 11.5.sp, color = muted, maxLines = 1)
                            }
                        }
                    }
                }
            }

            item(key = "user_pl_hdr") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("自建歌单", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ink)
                    Text("${playlists.size} 个", fontSize = 12.sp, color = muted)
                }
            }

            if (playlists.isEmpty()) {
                item(key = "empty_pl") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(surface)
                            .border(0.7.dp, outline, RoundedCornerShape(12.dp))
                            .desktopHoverClickable(shape = RoundedCornerShape(12.dp), onClick = onCreatePlaylist)
                            .padding(vertical = 22.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("新建你的第一个自建歌单", style = MaterialTheme.typography.bodyMedium, color = muted)
                    }
                }
            } else {
                val rows = playlists.chunked(2)
                items(rows, key = { row -> row.joinToString("_") { it.id } }) { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        rowItems.forEach { pl ->
                            val coverPath = pl.songPaths.firstOrNull { it in songsByPath }
                            var menuOpen by remember { mutableStateOf(false) }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .desktopHoverClickable(shape = RoundedCornerShape(10.dp)) { onOpenPlaylist(pl) }
                                        .padding(vertical = 8.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    // Compact 58dp Square Cover + Subtle Peeking Vinyl Edge
                                    Box(modifier = Modifier.width(72.dp).height(58.dp), contentAlignment = Alignment.CenterStart) {
                                        Canvas(modifier = Modifier.align(Alignment.CenterEnd).size(52.dp)) {
                                            val r = size.minDimension / 2f
                                            drawCircle(Color(0xFF141416), radius = r, center = Offset(r, r))
                                            drawCircle(Color.White.copy(alpha = 0.08f), radius = r * 0.72f, center = Offset(r, r), style = Stroke(0.6f))
                                        }
                                        DesktopAlbumCover(
                                            filePath = coverPath,
                                            modifier = Modifier.size(58.dp).shadow(4.dp, RoundedCornerShape(6.dp)),
                                            cornerRadius = 6.dp,
                                            iconSize = 22.dp,
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(pl.name, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("${pl.songPaths.size} 首", fontSize = 12.sp, color = muted)
                                    }
                                    Box {
                                        IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                            Icon(Icons.Rounded.MoreVert, contentDescription = "操作", tint = muted, modifier = Modifier.size(16.dp))
                                        }
                                        DropdownMenu(
                                            expanded = menuOpen,
                                            onDismissRequest = { menuOpen = false },
                                            modifier = Modifier.background(surface, RoundedCornerShape(12.dp)).border(0.6.dp, outline, RoundedCornerShape(12.dp)),
                                        ) {
                                            DropdownMenuItem(text = { Text("重命名") }, onClick = { menuOpen = false; onRenamePlaylist(pl) })
                                            DropdownMenuItem(text = { Text("删除歌单", color = accent) }, onClick = { menuOpen = false; onDeletePlaylist(pl) })
                                        }
                                    }
                                }
                                HorizontalDivider(thickness = 0.5.dp, color = outline.copy(alpha = 0.7f))
                            }
                        }
                        repeat(2 - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }
        DesktopThinScrollbar(listState = listState, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

// =================================================================================================
// 4. LISTENING REPORT PAGE (Opened cleanly from Playlists Bento Card with '<' Back)
// =================================================================================================
@Composable
private fun DesktopListeningReportPage(
    allSongs: List<DesktopSong>,
    mostPlayedSongs: List<DesktopSong>,
    artists: List<DesktopArtist>,
    playCounts: Map<String, Int>,
    hourlyPlays: Map<Int, Int>,
    currentSongId: String?,
    onBack: () -> Unit,
    onPlayAll: () -> Unit,
    onPlayAt: (Int) -> Unit,
    onSelectArtist: (DesktopArtist) -> Unit,
) {
    val cardShape = RoundedCornerShape(12.dp)
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outline
    val accent = MaterialTheme.colorScheme.primary
    val ink = MaterialTheme.colorScheme.onBackground
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    val totalPlays = remember(playCounts) { playCounts.values.sum() }
    val playedTracks = remember(playCounts) { playCounts.count { it.value > 0 } }
    val totalListeningText = remember(allSongs, playCounts) {
        val byPath = allSongs.associateBy { it.filePath }
        val ms = playCounts.entries.sumOf { (p, c) -> (byPath[p]?.durationMs ?: 210_000L) * c }
        val h = ms / 3_600_000.0
        when {
            ms <= 0L -> "0m"
            h < 1.0 -> "${(ms / 60_000L).coerceAtLeast(1L)}m"
            else -> String.format(Locale.US, "%.1fh", h)
        }
    }
    val topSongs = remember(mostPlayedSongs) { mostPlayedSongs.take(10) }
    val topArtists = remember(allSongs, artists, playCounts) {
        val map = mutableMapOf<String, Int>()
        allSongs.forEach { s -> val c = playCounts[s.filePath] ?: 0; if (c > 0) map[s.artist] = (map[s.artist] ?: 0) + c }
        val byName = artists.associateBy { it.name }
        map.entries.sortedByDescending { it.value }.take(5).mapNotNull { (n, c) -> byName[n]?.let { it to c } }
    }

    val listState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxHeight().widthIn(max = 1180.dp).fillMaxWidth().padding(horizontal = 36.dp),
            contentPadding = PaddingValues(vertical = 26.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "rep_hdr") {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回歌单", tint = ink)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("听歌报告", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ink)
                    }
                    HorizontalDivider(thickness = 0.6.dp, color = outline)
                }
            }

            item(key = "rep_metrics") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    listOf(totalListeningText to "累计聆听", "${totalPlays}次" to "累计播放", "${playedTracks}首" to "听过曲目").forEach { (v, l) ->
                        Column(
                            modifier = Modifier.weight(1f).clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(vertical = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(v, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ink)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(l, fontSize = 12.sp, color = muted)
                        }
                    }
                }
            }

            item(key = "rep_24h") {
                val maxH = remember(hourlyPlays) { hourlyPlays.values.maxOrNull() ?: 0 }
                val peakH = remember(hourlyPlays) { hourlyPlays.entries.maxByOrNull { it.value }?.takeIf { it.value > 0 }?.key }
                Column(modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("24h 聆听节律", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = ink)
                        if (peakH != null) Text("峰值时段 · ${peakH}:00", fontSize = 12.sp, color = accent)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    if (maxH > 0) {
                        Canvas(modifier = Modifier.fillMaxWidth().height(104.dp)) {
                            val step = size.width / 24f
                            val barW = (step * 0.40f).coerceIn(3f, 9f)
                            for (h in 0 until 24) {
                                val c = hourlyPlays[h] ?: 0
                                val ratio = if (c > 0) (c.toFloat() / maxH).coerceIn(0.10f, 1f) else 0.04f
                                val bh = size.height * ratio
                                val col = when { c == 0 -> ink.copy(alpha = 0.09f); h == peakH -> accent; else -> ink.copy(alpha = 0.68f) }
                                drawRoundRect(col, Offset(h * step + (step - barW) * 0.5f, size.height - bh), Size(barW, bh), CornerRadius(barW * 0.5f, barW * 0.5f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf("00:00", "06:00", "12:00", "18:00", "23:00").forEach { Text(it, fontSize = 11.sp, color = muted) }
                        }
                    } else {
                        Text("继续聆听后将自动绘制你的 24 小时节律", fontSize = 12.sp, color = muted, modifier = Modifier.padding(vertical = 18.dp))
                    }
                }
            }

            item(key = "rep_split") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Column(modifier = Modifier.weight(1.45f).clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("最常循环曲目", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = ink)
                            if (topSongs.isNotEmpty()) {
                                Text("播放全部", fontSize = 12.sp, color = accent, fontWeight = FontWeight.Medium, modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).clickable(onClick = onPlayAll))
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        if (topSongs.isEmpty()) {
                            Text("暂无常听记录", fontSize = 12.sp, color = muted)
                        } else {
                            topSongs.forEachIndexed { idx, s ->
                                val cnt = playCounts[s.filePath] ?: 1
                                val isCur = s.id == currentSongId
                                Row(
                                    modifier = Modifier.fillMaxWidth().desktopHoverClickable(shape = RoundedCornerShape(6.dp)) { onPlayAt(idx) }.padding(vertical = 7.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(String.format(Locale.US, "%02d", idx + 1), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (idx == 0) accent else muted, modifier = Modifier.width(26.dp))
                                    DesktopAlbumCover(filePath = s.filePath, modifier = Modifier.size(36.dp), cornerRadius = 4.dp, iconSize = 15.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(s.title, style = MaterialTheme.typography.bodySmall, fontWeight = if (isCur) FontWeight.SemiBold else FontWeight.Normal, color = if (isCur) accent else ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(s.artist, fontSize = 11.sp, color = muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Text("×$cnt", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = if (idx == 0) accent else muted)
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f).clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(20.dp)) {
                        Text("偏爱歌手", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = ink)
                        Spacer(modifier = Modifier.height(12.dp))
                        if (topArtists.isEmpty()) {
                            Text("暂无歌手统计", fontSize = 12.sp, color = muted)
                        } else {
                            val maxA = topArtists.first().second.coerceAtLeast(1)
                            topArtists.forEachIndexed { idx, (artist, cnt) ->
                                val frac = (cnt.toFloat() / maxA).coerceIn(0.08f, 1f)
                                Column(modifier = Modifier.fillMaxWidth().desktopHoverClickable(shape = RoundedCornerShape(6.dp)) { onSelectArtist(artist) }.padding(vertical = 7.dp, horizontal = 4.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(artist.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                        Text("$cnt 次", fontSize = 11.sp, color = muted)
                                    }
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Box(modifier = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape).background(outline)) {
                                        Box(modifier = Modifier.fillMaxWidth(frac).fillMaxHeight().clip(CircleShape).background(if (idx == 0) accent else ink.copy(alpha = 0.6f)))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        DesktopThinScrollbar(listState = listState, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

// =================================================================================================
// 5. RIGHT-SIDE EQUALIZER DRAWER (Clean, Always Accessible From Bottom Rail Without Cluttering Sidebar)
// =================================================================================================
@Composable
private fun DesktopEqualizerDrawer(
    enabled: Boolean,
    bandsDb: List<Float>,
    onClose: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit,
    onBandChange: (Int, Float) -> Unit,
    onApplyPreset: (EqPreset) -> Unit,
) {
    val labels = listOf("60Hz", "230Hz", "910Hz", "3.6kHz", "14kHz")
    val maxBoost = bandsDb.maxOrNull()?.coerceAtLeast(0f) ?: 0f
    val headroomDb = if (enabled && maxBoost > 0.5f) -(maxBoost * 0.85f) else 0f

    Box(
        modifier = Modifier
            .width(368.dp)
            .fillMaxHeight()
            .shadow(18.dp)
            .background(MaterialTheme.colorScheme.surface)
            .border(0.6.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("音频均衡器", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = if (enabled) "5 段 IIR · 自动防破音 ${String.format(Locale.US, "%.1f", headroomDb)}dB" else "原始 PCM 位透明直通",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = enabled, onCheckedChange = onToggleEnabled, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand)) {
                        Icon(Icons.Rounded.Close, contentDescription = "关闭", modifier = Modifier.size(16.dp))
                    }
                }
            }

            HorizontalDivider(thickness = 0.6.dp, color = MaterialTheme.colorScheme.outline)

            // 6 Quiet Presets (2x3 grid)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                EqPreset.entries.chunked(3).forEach { rowPresets ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        rowPresets.forEach { preset ->
                            val isMatch = enabled && preset.gainsDb.indices.all { abs((bandsDb.getOrNull(it) ?: 0f) - preset.gainsDb[it]) < 0.25f }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isMatch) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent)
                                    .border(0.6.dp, if (isMatch) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .clickable { onApplyPreset(preset) }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(preset.label, fontSize = 12.sp, color = if (isMatch) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // 5 Sleek Faders
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                labels.forEachIndexed { idx, freq ->
                    val gain = (bandsDb.getOrNull(idx) ?: 0f).coerceIn(-10f, 10f)
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(freq, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(48.dp))
                        DesktopSleekSlider(value = gain, onValueChange = { onBandChange(idx, it) }, valueRange = -10f..10f, modifier = Modifier.weight(1f))
                        Text(String.format(Locale.US, "%+.1f", gain), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.End, modifier = Modifier.width(38.dp))
                    }
                }
            }
        }
    }
}

// =================================================================================================
// 6. SETTINGS PAGE (Centered 840dp Magazine Settings + Live Left-Accent-Bar Preview Card)
// =================================================================================================
@Composable
private fun DesktopSettingsPage(
    config: DesktopConfig,
    onUpdateConfig: (DesktopConfig) -> Unit = {},
    sleepTimerRemainingSec: Int?,
    totalSongsCount: Int,
    visibleSongsCount: Int,
    onToggleDarkMode: (Boolean) -> Unit,
    onSelectAccent: (DesktopAccentColor) -> Unit,
    onSelectDensity: (DesktopLayoutDensity) -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onSelectAppFont: (DesktopFontStyle) -> Unit,
    onSelectLyricFont: (DesktopLyricFontStyle) -> Unit,
    onSelectFontWeight: (DesktopFontWeightLevel) -> Unit,
    onFontWeightValueChange: (Int) -> Unit,
    onLyricSlantChange: (Float) -> Unit,
    onToggleFloatingBar: (Boolean) -> Unit,
    onToggleLiquidGlass: (Boolean) -> Unit,
    onSelectLiquidGlassLevel: (DesktopLiquidGlassLevel) -> Unit,
    onControlBarHeightChange: (Int) -> Unit,
    onSelectShortAudioFilter: (Int) -> Unit,
    onToggleEqEnabled: (Boolean) -> Unit,
    onEqBandChange: (Int, Float) -> Unit,
    onApplyEqPreset: (EqPreset) -> Unit,
    onSelectScene: (DesktopSceneStyle) -> Unit,
    onSelectSpectrum: (DesktopSpectrumStyle) -> Unit,
    onPickCustomFont: () -> Unit,
    onSetSleepTimerMin: (Int) -> Unit,
    onAddFolder: () -> Unit,
    onRemoveFolder: (String) -> Unit,
    onRescan: () -> Unit,
) {
    var selectedSection by remember { mutableStateOf(0) }
    val sections = listOf("外观", "排印", "曲库", "音效")
    val cardShape = RoundedCornerShape(12.dp)
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outline
    val accent = MaterialTheme.colorScheme.primary
    val ink = MaterialTheme.colorScheme.onBackground
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    val listState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxHeight().widthIn(max = 860.dp).fillMaxWidth().padding(horizontal = 36.dp),
            contentPadding = PaddingValues(vertical = 26.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item(key = "set_hdr") {
                Column {
                    Text("设置", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ink)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.width(34.dp).height(2.dp).background(ink))
                    HorizontalDivider(thickness = 0.6.dp, color = outline)
                }
            }

            item(key = "set_tabs") {
                DesktopLiquidGlassSegmentedRow(
                    items = sections,
                    selectedIndex = selectedSection,
                    liquidGlassEnabled = config.liquidGlassEnabled,
                    glassLevel = config.liquidGlassLevel,
                    onSelect = { selectedSection = it },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            when (selectedSection) {
                // 0. 外观（悬浮胶囊 + 液态玻璃折射 + 纸色 + 书脊色 + 密度）
                0 -> item(key = "sec_appearance") {
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(config.layoutDensity.cardPaddingDp.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("悬浮与液态玻璃", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = ink)
                                Text("对齐移动端装帧质感，随时在书页纯色与琉璃玻璃间切换。", fontSize = 12.sp, color = muted)
                            }
                            Text(
                                text = "恢复默认",
                                fontSize = 12.sp,
                                color = muted,
                                modifier = Modifier.border(0.6.dp, outline, RoundedCornerShape(8.dp)).desktopSpringPress {
                                    onToggleDarkMode(false)
                                    onSelectAccent(DesktopAccentColor.CINNABAR)
                                    onSelectDensity(DesktopLayoutDensity.STANDARD)
                                    onToggleFloatingBar(true)
                                    onToggleLiquidGlass(true)
                                    onSelectLiquidGlassLevel(DesktopLiquidGlassLevel.MEDIUM)
                                    onControlBarHeightChange(62)
                                }.padding(horizontal = 12.dp, vertical = 6.dp),
                            )
                        }

                        // Live Liquid Glass Preview Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(84.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .border(0.6.dp, outline, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("曲库 · 宣纸排印底层透出预览 · $totalSongsCount 首本地音乐", fontSize = 11.sp, color = muted.copy(alpha = 0.55f))
                                Text("夜航星 · 光影之间 · 爱在深秋 · 180g 黑胶装帧", fontSize = 11.sp, color = muted.copy(alpha = 0.45f))
                            }
                            val previewShape = RoundedCornerShape(if (config.floatingControlBar) 18.dp else 6.dp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(if (config.floatingControlBar) 0.86f else 0.96f)
                                    .height(46.dp)
                                    .shadow(if (config.floatingControlBar) 8.dp else 1.dp, previewShape)
                                    .clip(previewShape)
                                    .background(if (config.liquidGlassEnabled) surface.copy(alpha = config.liquidGlassLevel.surfaceAlpha) else surface)
                                    .border(0.75.dp, if (config.liquidGlassEnabled) Color.White.copy(alpha = config.liquidGlassLevel.rimAlpha) else outline, previewShape)
                                    .padding(horizontal = 18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = if (config.liquidGlassEnabled) "液态玻璃 · ${config.liquidGlassLevel.label}" else "古典书页纯色",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ink,
                                )
                                Text(
                                    text = if (config.floatingControlBar) "悬浮胶囊 · ${config.controlBarHeightDp}dp" else "贴边底栏 · ${config.controlBarHeightDp}dp",
                                    fontSize = 11.5.sp,
                                    color = accent,
                                )
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("悬浮播放胶囊", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Text("开启后底部播放控制栏变为凌空圆角悬浮胶囊。", fontSize = 12.sp, color = muted)
                            }
                            Switch(checked = config.floatingControlBar, onCheckedChange = onToggleFloatingBar, colors = SwitchDefaults.colors(checkedTrackColor = accent))
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("液态玻璃折射", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Text("为侧边栏滑块、分段标签与悬浮底栏启用光学高光折射。", fontSize = 12.sp, color = muted)
                            }
                            Switch(checked = config.liquidGlassEnabled, onCheckedChange = onToggleLiquidGlass, colors = SwitchDefaults.colors(checkedTrackColor = accent))
                        }

                        if (config.liquidGlassEnabled) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("液态玻璃档位", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                    Text(config.liquidGlassLevel.desc, fontSize = 12.sp, color = muted)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DesktopLiquidGlassLevel.entries.forEach { lv ->
                                        val sel = config.liquidGlassLevel == lv
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (sel) accent.copy(alpha = 0.12f) else Color.Transparent)
                                                .border(0.6.dp, if (sel) accent else outline, RoundedCornerShape(8.dp))
                                                .desktopSpringPress { onSelectLiquidGlassLevel(lv) }
                                                .padding(horizontal = 14.dp, vertical = 7.dp),
                                        ) {
                                            Text(lv.label, fontSize = 12.sp, color = if (sel) accent else muted, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                                        }
                                    }
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.width(200.dp)) {
                                Text("底部播放条高度", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Text("${config.controlBarHeightDp}dp", fontSize = 12.sp, color = accent)
                            }
                            DesktopSleekSlider(
                                value = config.controlBarHeightDp.toFloat(),
                                onValueChange = { onControlBarHeightChange(it.toInt()) },
                                valueRange = 54f..76f,
                                activeColor = accent,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        HorizontalDivider(thickness = 0.5.dp, color = outline)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("纸色主题", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Text("宣纸浅色与夜读深色自由切换。", fontSize = 12.sp, color = muted)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(false to "宣纸浅色", true to "夜读深色").forEach { (dm, lbl) ->
                                    val sel = config.darkMode == dm
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (sel) accent.copy(alpha = 0.12f) else Color.Transparent)
                                            .border(0.6.dp, if (sel) accent else outline, RoundedCornerShape(8.dp))
                                            .desktopSpringPress { onToggleDarkMode(dm) }
                                            .padding(horizontal = 14.dp, vertical = 7.dp),
                                    ) {
                                        Text(lbl, fontSize = 12.sp, color = if (sel) accent else muted, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                                    }
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("书脊强调色", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Text("用于左侧书签竖线、当前播放与进度细线。", fontSize = 12.sp, color = muted)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                DesktopAccentColor.entries.forEach { ac ->
                                    val sel = ac == config.accentColor
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(ac.hex))
                                            .border(if (sel) 2.dp else 0.dp, if (sel) ink else Color.Transparent, RoundedCornerShape(6.dp))
                                            .desktopSpringPress { onSelectAccent(ac) },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (sel) Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("窗口自适应缩放", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Text("拖拽缩放窗口或切换 2K/4K 全屏时自动等比缩放界面比例。", fontSize = 12.sp, color = muted)
                            }
                            Switch(
                                checked = config.autoWindowScale,
                                onCheckedChange = { onUpdateConfig(config.copy(autoWindowScale = it)) },
                                colors = SwitchDefaults.colors(checkedTrackColor = accent),
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("排版密度", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Text("调节曲库列表与专辑画册的呼吸留白。", fontSize = 12.sp, color = muted)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                DesktopLayoutDensity.entries.forEach { d ->
                                    val sel = d == config.layoutDensity
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (sel) accent.copy(alpha = 0.12f) else Color.Transparent)
                                            .border(0.6.dp, if (sel) accent else outline, RoundedCornerShape(8.dp))
                                            .desktopSpringPress { onSelectDensity(d) }
                                            .padding(horizontal = 14.dp, vertical = 7.dp),
                                    ) {
                                        Text(d.label, fontSize = 12.sp, color = if (sel) accent else muted, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                                    }
                                }
                            }
                        }
                    }
                }

                // 1. 排印（实时书卷预览 + A-/滑块/A+ + 200-900 连续字重 + 0°-16° 倾斜度 + 多字体）
                1 -> item(key = "sec_typography") {
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(config.layoutDensity.cardPaddingDp.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("排印工作台", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = ink)
                                Text("全局字号、200–900 字重、倾斜度与本地自定义字体即时生效。", fontSize = 12.sp, color = muted)
                            }
                            Text(
                                text = "重置排印",
                                fontSize = 12.sp,
                                color = muted,
                                modifier = Modifier.border(0.6.dp, outline, RoundedCornerShape(8.dp)).desktopSpringPress {
                                    onFontScaleChange(1.0f)
                                    onSelectAppFont(DesktopFontStyle.YAHEI)
                                    onSelectLyricFont(DesktopLyricFontStyle.SERIF)
                                    onFontWeightValueChange(400)
                                    onLyricSlantChange(0f)
                                }.padding(horizontal = 12.dp, vertical = 6.dp),
                            )
                        }

                        // Live Editorial Preview Card
                        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.background).border(0.6.dp, outline, RoundedCornerShape(10.dp))) {
                            Box(modifier = Modifier.width(3.5.dp).height(118.dp).background(accent))
                            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text("排印实时预览 · 字重 ${config.fontWeightValue} · ${(config.fontScale * 100).toInt()}% · 倾斜 ${config.lyricSlantDegrees.toInt()}°", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = accent)
                                Text(
                                    text = "山海自有归期，风雨自有相逢",
                                    fontSize = 23.sp,
                                    fontFamily = LocalLyricFontFamily.current,
                                    fontStyle = if (config.lyricSlantDegrees >= 4f) FontStyle.Italic else FontStyle.Normal,
                                    fontWeight = FontWeight((config.fontWeightValue + 200).coerceIn(300, 900)),
                                    color = ink,
                                )
                                Text("把喜欢的音乐留在身边 · PureMusic Typography", style = MaterialTheme.typography.bodySmall, color = muted)
                            }
                        }

                        // Global Font Scale with A- / Slider / A+
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.width(150.dp)) {
                                Text("全局字体大小", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Text("${(config.fontScale * 100).toInt()}%", fontSize = 12.sp, color = accent)
                            }
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).border(0.6.dp, outline, RoundedCornerShape(8.dp)).desktopSpringPress { onFontScaleChange((config.fontScale - 0.05f).coerceIn(0.85f, 1.30f)) }.padding(horizontal = 10.dp, vertical = 5.dp),
                            ) { Text("A-", fontSize = 12.sp, color = ink) }
                            DesktopSleekSlider(value = config.fontScale, onValueChange = onFontScaleChange, valueRange = 0.85f..1.30f, activeColor = accent, modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).border(0.6.dp, outline, RoundedCornerShape(8.dp)).desktopSpringPress { onFontScaleChange((config.fontScale + 0.05f).coerceIn(0.85f, 1.30f)) }.padding(horizontal = 10.dp, vertical = 5.dp),
                            ) { Text("A+", fontSize = 12.sp, color = ink) }
                        }

                        // 200-900 Continuous Font Weight
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.width(180.dp)) {
                                Text("自定义字重微调", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Text("${config.fontWeightValue}（200 纤细 – 900 极粗）", fontSize = 12.sp, color = accent)
                            }
                            DesktopSleekSlider(
                                value = config.fontWeightValue.toFloat(),
                                onValueChange = { onFontWeightValueChange(((it / 50f).toInt() * 50).coerceIn(200, 900)) },
                                valueRange = 200f..900f,
                                activeColor = accent,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        // 0-16 degree Lyric Slant
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.width(180.dp)) {
                                Text("歌词倾斜度", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Text("${config.lyricSlantDegrees.toInt()}°（4° 以上启用手书斜体）", fontSize = 12.sp, color = accent)
                            }
                            DesktopSleekSlider(
                                value = config.lyricSlantDegrees,
                                onValueChange = onLyricSlantChange,
                                valueRange = 0f..16f,
                                activeColor = accent,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        HorizontalDivider(thickness = 0.5.dp, color = outline)

                        // App Font Family
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("全局界面字体", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Text(
                                    text = if (config.appFontStyle == DesktopFontStyle.CUSTOM && !config.customFontPath.isNullOrBlank()) {
                                        "当前自定义：${File(config.customFontPath).name}"
                                    } else {
                                        "切换微软雅黑、宋体书卷、楷体手书或导入本地 .ttf/.otf。"
                                    },
                                    fontSize = 12.sp,
                                    color = muted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(DesktopFontStyle.YAHEI, DesktopFontStyle.SONGTI, DesktopFontStyle.KAITI).forEach { fs ->
                                    val sel = config.appFontStyle == fs
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (sel) accent.copy(alpha = 0.12f) else Color.Transparent)
                                            .border(0.6.dp, if (sel) accent else outline, RoundedCornerShape(8.dp))
                                            .desktopSpringPress { onSelectAppFont(fs) }
                                            .padding(horizontal = 12.dp, vertical = 7.dp),
                                    ) {
                                        Text(fs.label, fontSize = 12.sp, color = if (sel) accent else muted, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                                    }
                                }
                                val customSel = config.appFontStyle == DesktopFontStyle.CUSTOM
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (customSel) accent.copy(alpha = 0.12f) else Color.Transparent)
                                        .border(0.6.dp, if (customSel) accent else outline, RoundedCornerShape(8.dp))
                                        .desktopSpringPress { onPickCustomFont() }
                                        .padding(horizontal = 12.dp, vertical = 7.dp),
                                ) {
                                    Text("导入字体", fontSize = 12.sp, color = if (customSel) accent else muted, fontWeight = if (customSel) FontWeight.SemiBold else FontWeight.Normal)
                                }
                            }
                        }

                        // Lyric Font Family
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("独立歌词字体", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Text("为沉浸歌词单独指定书卷衬线或手书字体。", fontSize = 12.sp, color = muted)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                DesktopLyricFontStyle.entries.forEach { lfs ->
                                    val sel = config.lyricFontStyle == lfs
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (sel) accent.copy(alpha = 0.12f) else Color.Transparent)
                                            .border(0.6.dp, if (sel) accent else outline, RoundedCornerShape(8.dp))
                                            .desktopSpringPress { onSelectLyricFont(lfs) }
                                            .padding(horizontal = 11.dp, vertical = 7.dp),
                                    ) {
                                        Text(lfs.label, fontSize = 12.sp, color = if (sel) accent else muted, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. 曲库（文件夹 + 短音频过滤 + 睡眠定时 + 快捷键）
                2 -> item(key = "sec_library") {
                    Column(verticalArrangement = Arrangement.spacedBy(config.layoutDensity.gridSpacingDp.dp)) {
                        Column(modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(config.layoutDensity.cardPaddingDp.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("本地音乐文件夹", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = ink)
                                    Text("当前显示 $visibleSongsCount / $totalSongsCount 首 · 内置 FFmpeg 支持 MP3/FLAC/WAV/M4A/AAC/ALAC/OGG/OPUS/WMA/APE/DSF。", fontSize = 12.sp, color = muted)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    TextButton(onClick = onRescan) { Text("重新扫描", fontSize = 12.sp) }
                                    TextButton(onClick = onAddFolder) { Text("+ 添加目录", fontSize = 12.sp) }
                                }
                            }
                            HorizontalDivider(thickness = 0.5.dp, color = outline)
                            if (config.folders.isEmpty()) Text("暂未添加目录", fontSize = 12.sp, color = muted)
                            else config.folders.forEach { path ->
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(path, fontSize = 13.sp, color = ink, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                    IconButton(onClick = { onRemoveFolder(path) }, modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)) {
                                        Icon(Icons.Rounded.DeleteOutline, contentDescription = "移除", tint = muted, modifier = Modifier.size(17.dp))
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(config.layoutDensity.cardPaddingDp.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("短音频过滤", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                    Text("自动隐藏低于指定时长的铃声、语音或提示音文件。", fontSize = 12.sp, color = muted)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(0 to "关闭", 15 to "15秒", 30 to "30秒", 60 to "60秒").forEach { (sec, lbl) ->
                                        val active = config.shortAudioFilterSec == sec
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (active) accent.copy(alpha = 0.12f) else Color.Transparent)
                                                .border(0.6.dp, if (active) accent else outline, RoundedCornerShape(8.dp))
                                                .desktopSpringPress { onSelectShortAudioFilter(sec) }
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                        ) {
                                            Text(lbl, fontSize = 12.sp, color = if (active) accent else muted, fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal)
                                        }
                                    }
                                }
                            }
                            HorizontalDivider(thickness = 0.5.dp, color = outline)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("睡眠定时暂停", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                    Text("定时结束后自动暂停播放。", fontSize = 12.sp, color = muted)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(0 to "关闭", 15 to "15分钟", 30 to "30分钟", 60 to "60分钟").forEach { (mins, lbl) ->
                                        val active = (mins == 0 && sleepTimerRemainingSec == null) || (mins > 0 && sleepTimerRemainingSec != null && ((sleepTimerRemainingSec + 59) / 60) in (mins - 14)..mins)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (active) accent.copy(alpha = 0.12f) else Color.Transparent)
                                                .border(0.6.dp, if (active) accent else outline, RoundedCornerShape(8.dp))
                                                .desktopSpringPress { onSetSleepTimerMin(mins) }
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                        ) {
                                            Text(lbl, fontSize = 12.sp, color = if (active) accent else muted)
                                        }
                                    }
                                }
                            }
                            HorizontalDivider(thickness = 0.5.dp, color = outline)
                            Text("快捷键：Space 播放/暂停 · F11 真全屏 · Esc 退出全屏或抽屉 · ←/→ 全屏快退/快进 5 秒", fontSize = 12.sp, color = muted)
                        }
                    }
                }

                // 3. 音效（切歌平滑过渡 + 智能音量平衡 + 5 段防削波 EQ + 逐字歌词 + 默认沉浸场景/频谱 + 关于）
                else -> item(key = "sec_audio") {
                    val bandLabels = listOf("60Hz", "230Hz", "910Hz", "3.6kHz", "14kHz")
                    Column(verticalArrangement = Arrangement.spacedBy(config.layoutDensity.gridSpacingDp.dp)) {
                        Column(modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(config.layoutDensity.cardPaddingDp.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text("播放与听感", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = ink)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("切歌平滑过渡 (Crossfade)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                    Text("切歌时自动平滑淡入，消除突兀断层。", fontSize = 12.sp, color = muted)
                                }
                                Text(if (config.crossfadeSec <= 0) "关闭" else "${config.crossfadeSec} 秒", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = accent)
                            }
                            DesktopSleekSlider(
                                value = config.crossfadeSec.toFloat(),
                                onValueChange = { onUpdateConfig(config.copy(crossfadeSec = it.roundToInt().coerceIn(0, 6))) },
                                valueRange = 0f..6f,
                                activeColor = accent,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            HorizontalDivider(thickness = 0.5.dp, color = outline)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("智能音量平衡", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                    Text("自动均衡不同专辑的响度差异，防止忽大忽小。", fontSize = 12.sp, color = muted)
                                }
                                Switch(
                                    checked = config.volumeNormalizationEnabled,
                                    onCheckedChange = { onUpdateConfig(config.copy(volumeNormalizationEnabled = it)) },
                                    colors = SwitchDefaults.colors(checkedTrackColor = accent),
                                )
                            }
                            HorizontalDivider(thickness = 0.5.dp, color = outline)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("逐字卡拉 OK 歌词", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                    Text(config.karaokeLyricMode.desc, fontSize = 12.sp, color = muted)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DesktopKaraokeLyricMode.entries.forEach { km ->
                                        val sel = config.karaokeLyricMode == km
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (sel) accent.copy(alpha = 0.14f) else Color.Transparent)
                                                .border(0.6.dp, if (sel) accent else outline, RoundedCornerShape(8.dp))
                                                .desktopSpringPress { onUpdateConfig(config.copy(karaokeLyricMode = km)) }
                                                .padding(horizontal = 11.dp, vertical = 6.dp),
                                        ) {
                                            Text(km.label, fontSize = 12.sp, color = if (sel) accent else muted)
                                        }
                                    }
                                }
                            }
                            HorizontalDivider(thickness = 0.5.dp, color = outline)
                            var showDesktopApiDialog by remember { mutableStateOf(false) }
                            Row(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).pointerHoverIcon(PointerIcon.Hand).clickable { showDesktopApiDialog = true }.padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text("在线歌词翻译接口", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                    Text(
                                        text = if (config.translateApiKey.isNotBlank()) "当前：自定义 API · 模型 ${config.translateModel}" else "当前：公开免费（MyMemory） · 点击可自填 API Key",
                                        fontSize = 12.sp,
                                        color = muted,
                                    )
                                }
                                Text("配置", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = accent)
                            }
                            if (showDesktopApiDialog) {
                                var urlDraft by remember(config) { mutableStateOf(config.translateBaseUrl) }
                                var keyDraft by remember(config) { mutableStateOf(config.translateApiKey) }
                                var modelDraft by remember(config) { mutableStateOf(config.translateModel) }
                                AlertDialog(
                                    onDismissRequest = { showDesktopApiDialog = false },
                                    title = { Text("自定义在线翻译 API", fontWeight = FontWeight.Bold) },
                                    text = {
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text("留空 Key 时默认走公开免费的 MyMemory 接口；填入 Key 后使用 OpenAI 兼容接口。", fontSize = 12.sp, color = muted)
                                            OutlinedTextField(value = urlDraft, onValueChange = { urlDraft = it }, label = { Text("接口地址") }, placeholder = { Text("https://api.openai.com/v1") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                                            OutlinedTextField(value = keyDraft, onValueChange = { keyDraft = it }, label = { Text("API Key（选填）") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                                            OutlinedTextField(value = modelDraft, onValueChange = { modelDraft = it }, label = { Text("模型名称") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            onUpdateConfig(config.copy(translateBaseUrl = urlDraft.trim(), translateApiKey = keyDraft.trim(), translateModel = modelDraft.trim().ifBlank { "gpt-4o-mini" }))
                                            showDesktopApiDialog = false
                                        }) { Text("保存", fontWeight = FontWeight.SemiBold) }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = {
                                            onUpdateConfig(config.copy(translateBaseUrl = "", translateApiKey = "", translateModel = "gpt-4o-mini"))
                                            showDesktopApiDialog = false
                                        }) { Text("恢复免费接口") }
                                    },
                                )
                            }
                        }
                        Column(modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(config.layoutDensity.cardPaddingDp.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("5 段防削波均衡器 (EQ)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = ink)
                                    Text("纯 Biquad IIR 滤波器 + 自动防爆音前级余量，关闭时 100% 直通。", fontSize = 12.sp, color = muted)
                                }
                                Switch(checked = config.eqEnabled, onCheckedChange = onToggleEqEnabled, colors = SwitchDefaults.colors(checkedTrackColor = accent))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                EqPreset.entries.forEach { preset ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                                            .border(0.6.dp, outline, RoundedCornerShape(8.dp))
                                            .desktopSpringPress { onApplyEqPreset(preset) }
                                            .padding(vertical = 7.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(preset.label, fontSize = 12.sp, color = ink)
                                    }
                                }
                            }
                            bandLabels.forEachIndexed { idx, lbl ->
                                val db = config.eqBandsDb.getOrElse(idx) { 0f }
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(lbl, fontSize = 12.sp, color = muted, modifier = Modifier.width(52.dp))
                                    DesktopSleekSlider(
                                        value = db,
                                        onValueChange = { onEqBandChange(idx, it) },
                                        valueRange = -10f..10f,
                                        activeColor = if (config.eqEnabled) accent else muted,
                                        modifier = Modifier.weight(1f),
                                    )
                                    Text("${if (db >= 0) "+" else ""}${String.format(Locale.US, "%.1f", db)} dB", fontSize = 11.5.sp, color = if (config.eqEnabled) accent else muted, modifier = Modifier.width(58.dp), textAlign = TextAlign.End)
                                }
                            }
                        }

                        Column(modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(config.layoutDensity.cardPaddingDp.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("默认沉浸场景", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DesktopSceneStyle.entries.forEach { sc ->
                                        val sel = config.sceneStyle == sc
                                        Box(
                                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if (sel) accent.copy(alpha = 0.12f) else Color.Transparent).border(0.6.dp, if (sel) accent else outline, RoundedCornerShape(8.dp)).desktopSpringPress { onSelectScene(sc) }.padding(horizontal = 11.dp, vertical = 6.dp),
                                        ) { Text(sc.label, fontSize = 12.sp, color = if (sel) accent else muted) }
                                    }
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("默认实时频谱", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DesktopSpectrumStyle.entries.forEach { sp ->
                                        val sel = config.spectrumStyle == sp
                                        Box(
                                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if (sel) accent.copy(alpha = 0.12f) else Color.Transparent).border(0.6.dp, if (sel) accent else outline, RoundedCornerShape(8.dp)).desktopSpringPress { onSelectSpectrum(sp) }.padding(horizontal = 11.dp, vertical = 6.dp),
                                        ) { Text(sp.label, fontSize = 12.sp, color = if (sel) accent else muted) }
                                    }
                                }
                            }
                            HorizontalDivider(thickness = 0.5.dp, color = outline)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text("简约音乐 · PureMusic Desktop", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = ink)
                                    Text("回归音乐本身。本地离线 · 零广告 · 遵循 Apache-2.0 / LGPL-2.1 开源声明（详见 THIRD_PARTY_LICENSES.txt）。", fontSize = 12.sp, color = muted)
                                }
                                Text("v4.2.0", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = accent)
                            }
                        }
                    }
                }
            }
        }
        DesktopThinScrollbar(listState = listState, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

// =================================================================================================
// 7. COLLECTION DETAIL PAGE
// =================================================================================================
@Composable
private fun DesktopCollectionDetailPage(
    title: String,
    subtitle: String,
    songs: List<DesktopSong>,
    currentSongId: String?,
    favoritePaths: Set<String>,
    density: DesktopLayoutDensity,
    onBack: () -> Unit,
    onPlayAt: (Int) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddToPlaylist: (DesktopSong) -> Unit,
) {
    val listState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(modifier = Modifier.fillMaxHeight().widthIn(max = 1180.dp).fillMaxWidth().padding(horizontal = 36.dp, vertical = 26.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                if (songs.isNotEmpty()) {
                    Text(
                        text = "▶ 播放全部",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.border(0.7.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(8.dp)).pointerHoverIcon(PointerIcon.Hand).clickable { onPlayAt(0) }.padding(horizontal = 14.dp, vertical = 7.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 0.6.dp, color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(end = 10.dp)) {
                    itemsIndexed(songs, key = { _, s -> s.id }) { idx, song ->
                        BookTrackRow(
                            index = idx + 1,
                            song = song,
                            isCurrent = song.id == currentSongId,
                            isFavorite = song.filePath in favoritePaths,
                            density = density,
                            onClick = { onPlayAt(idx) },
                            onToggleFavorite = { onToggleFavorite(song.filePath) },
                            onAddToPlaylist = { onAddToPlaylist(song) },
                        )
                    }
                }
                DesktopThinScrollbar(listState = listState, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
    }
}

// =================================================================================================
// 8. UNIFIED FULL-SCREEN IMMERSIVE STAGE (4 Cohesive Scenes + Exact Same Unified Bottom Rail!)
// =================================================================================================
@Composable
private fun UnifiedImmersiveStage(
    song: DesktopSong,
    allSongs: List<DesktopSong>,
    playerState: DesktopPlayerState,
    isFavorite: Boolean,
    lyrics: List<LyricLine>,
    activeLyricIdx: Int,
    vinylRotation: Float,
    sceneStyle: DesktopSceneStyle,
    spectrumStyle: DesktopSpectrumStyle,
    showSpectrum: Boolean,
    lyricOffsetMs: Long,
    lyricFontScale: Float,
    isDarkTheme: Boolean,
    accentColor: Color,
    isFullscreen: Boolean,
    eqActive: Boolean,
    queueOpen: Boolean,
    onSelectScene: (DesktopSceneStyle) -> Unit,
    onSelectSpectrum: (DesktopSpectrumStyle) -> Unit,
    onToggleSpectrum: () -> Unit,
    onAdjustLyricOffset: (Long) -> Unit,
    onResetLyricOffset: () -> Unit,
    onAdjustLyricScale: (Float) -> Unit,
    onResetLyricScale: () -> Unit,
    onImportLyrics: () -> Unit,
    onReplaceCover: () -> Unit,
    onClose: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onSelectSong: (DesktopSong) -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onSkipPrevious: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleEq: () -> Unit,
    onToggleQueue: () -> Unit,
    karaokeLyricMode: DesktopKaraokeLyricMode = DesktopKaraokeLyricMode.SMART,
    isTranslatingLyrics: Boolean = false,
    translateStatusTip: String? = null,
    onTranslateLyrics: () -> Unit = {},
) {
    val isDarkScene = when (sceneStyle) {
        DesktopSceneStyle.VINYL_STUDY -> isDarkTheme
        DesktopSceneStyle.VINYL_LIGHTFIELD, DesktopSceneStyle.SPATIAL_GALLERY, DesktopSceneStyle.POSTER_WALL -> true
    }
    val extractedMood = rememberSongDominantColor(song.filePath)
    val animatedMood by animateColorAsState(targetValue = extractedMood, animationSpec = tween(650), label = "immMood")
    val bgBrush = when (sceneStyle) {
        DesktopSceneStyle.VINYL_STUDY -> if (isDarkTheme) {
            Brush.verticalGradient(listOf(lerp(Color(0xFF1B2328), animatedMood, 0.44f), lerp(Color(0xFF12181C), animatedMood, 0.28f), lerp(Color(0xFF0A0F12), animatedMood, 0.14f)))
        } else {
            Brush.verticalGradient(listOf(Color(0xFFF5F0E6), Color(0xFFF5F0E6)))
        }
        DesktopSceneStyle.VINYL_LIGHTFIELD -> Brush.verticalGradient(listOf(lerp(Color(0xFF1D1615), animatedMood, 0.50f), lerp(Color(0xFF140F0F), animatedMood, 0.30f), Color(0xFF0C0A0A)))
        DesktopSceneStyle.SPATIAL_GALLERY -> Brush.verticalGradient(listOf(lerp(Color(0xFF181214), animatedMood, 0.42f), lerp(Color(0xFF100C0E), animatedMood, 0.24f), Color(0xFF090809)))
        DesktopSceneStyle.POSTER_WALL -> Brush.verticalGradient(listOf(Color(0xFF0E1116), Color(0xFF0A0C10)))
    }
    val primaryInk = if (isDarkScene) Color(0xFFF4EFE6) else MaterialTheme.colorScheme.onBackground
    val secondaryInk = if (isDarkScene) Color(0xFFB3ABA0) else MaterialTheme.colorScheme.onSurfaceVariant
    val activeAccent = if (isDarkScene) Color(0xFFE5C487) else accentColor

    var menuExpanded by remember { mutableStateOf(false) }
    var lastPointerMoveMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var chromeVisible by remember { mutableStateOf(true) }

    LaunchedEffect(lastPointerMoveMs, menuExpanded) {
        chromeVisible = true
        if (!menuExpanded) {
            delay(3200)
            chromeVisible = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val ev = awaitPointerEvent()
                        if (ev.type == PointerEventType.Move || ev.type == PointerEventType.Press) {
                            lastPointerMoveMs = System.currentTimeMillis()
                        }
                    }
                }
            },
    ) {
        // Minimal Auto-Hiding Top Bar (Left '<' Back, Center Calm Scene Pill, Right F11)
        Box(modifier = Modifier.fillMaxWidth().height(54.dp).padding(horizontal = 24.dp)) {
            androidx.compose.animation.AnimatedVisibility(
                visible = chromeVisible || menuExpanded,
                enter = fadeIn(tween(180)),
                exit = fadeOut(tween(420)),
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.align(Alignment.CenterStart).pointerHoverIcon(PointerIcon.Hand),
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回", tint = primaryInk)
                    }

                    // Centered calm scene selector pill (exact reference aesthetic)
                    Box(modifier = Modifier.align(Alignment.Center)) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDarkScene) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.045f))
                                .border(0.6.dp, primaryInk.copy(alpha = 0.14f), RoundedCornerShape(16.dp))
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable { menuExpanded = true }
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(sceneStyle.label, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = primaryInk)
                            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "切换场景", tint = primaryInk, modifier = Modifier.size(16.dp))
                        }
                        DesktopImmersiveControlMenu(
                            expanded = menuExpanded,
                            isDarkSurface = isDarkScene,
                            currentScene = sceneStyle,
                            currentSpectrum = spectrumStyle,
                            lyricOffsetMs = lyricOffsetMs,
                            lyricFontScale = lyricFontScale,
                            accentColor = accentColor,
                            onDismiss = { menuExpanded = false },
                            onSelectScene = onSelectScene,
                            onSelectSpectrum = onSelectSpectrum,
                            onAdjustLyricOffset = onAdjustLyricOffset,
                            onResetLyricOffset = onResetLyricOffset,
                            onAdjustLyricScale = onAdjustLyricScale,
                            onResetLyricScale = onResetLyricScale,
                            onImportLyrics = onImportLyrics,
                            onReplaceCover = onReplaceCover,
                        )
                    }

                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        if (!translateStatusTip.isNullOrBlank()) {
                            Text(translateStatusTip, fontSize = 11.5.sp, color = activeAccent)
                        }
                        if (lyrics.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDarkScene) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.045f))
                                    .border(0.6.dp, primaryInk.copy(alpha = 0.16f), RoundedCornerShape(14.dp))
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .clickable(enabled = !isTranslatingLyrics) { onTranslateLyrics() }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(Icons.Rounded.Translate, contentDescription = "歌词翻译", tint = if (isTranslatingLyrics) activeAccent else primaryInk, modifier = Modifier.size(14.dp))
                                Text(if (isTranslatingLyrics) "译..." else "译", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = if (isTranslatingLyrics) activeAccent else primaryInk)
                            }
                        }
                    IconButton(
                        onClick = onToggleFullscreen,
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    ) {
                        Icon(
                            imageVector = if (isFullscreen) Icons.Rounded.FullscreenExit else Icons.Rounded.Fullscreen,
                            contentDescription = "F11 全屏",
                            tint = primaryInk,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    }
                }
            }
        }

        // 16:9 Desktop-Native 1/3 Left (Vinyl/Gallery + Info) + 2/3 Right (Multi-Line Lyrics + Fluid Spectrum)
        val isSplitMode = sceneStyle != DesktopSceneStyle.POSTER_WALL
        AnimatedContent(
            targetState = sceneStyle,
            transitionSpec = {
                (fadeIn(tween(240)) + scaleIn(initialScale = 0.98f, animationSpec = tween(240))) togetherWith
                    fadeOut(tween(180))
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = if (isSplitMode) 44.dp else 0.dp, vertical = if (isSplitMode) 8.dp else 0.dp),
        ) { targetScene ->
            if (targetScene == DesktopSceneStyle.POSTER_WALL) {
                DesktopPosterWallStage(
                    currentSong = song,
                    allSongs = allSongs,
                    vinylRotation = vinylRotation,
                    lyrics = lyrics,
                    activeLyricIdx = activeLyricIdx,
                    accentColor = activeAccent,
                    lyricFontScale = lyricFontScale,
                    onSelectSong = onSelectSong,
                    onTogglePlayPause = onTogglePlayPause,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                // Exact 1/3 Left + 2/3 Right 16:9 Architectural Split (1:1 with approved mockups!)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(42.dp),
                ) {
                    // LEFT 1/3 (~36%): Sculptural Visual Showcase + Real Mirror Reflection + Centered Song Info
                    BoxWithConstraints(
                        modifier = Modifier.weight(0.37f).fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        val colW = maxWidth
                        val colH = maxHeight
                        val sleeveSize = min(colW * 0.72f, colH * 0.46f).coerceIn(190.dp, 380.dp)
                        val discSize = sleeveSize * 0.90f
                        val titleSp = (colW.value * 0.056f).coerceIn(18f, 28f)
                        val artistSp = (colW.value * 0.034f).coerceIn(13f, 18f)

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            when (targetScene) {
                                DesktopSceneStyle.VINYL_STUDY -> {
                                    val reflH = 38.dp
                                    Box(
                                        modifier = Modifier
                                            .width(sleeveSize + discSize * 0.36f)
                                            .height(sleeveSize + reflH)
                                            .pointerHoverIcon(PointerIcon.Hand)
                                            .clickable(onClick = onTogglePlayPause),
                                        contentAlignment = Alignment.TopStart,
                                    ) {
                                        if (isDarkScene) {
                                            Canvas(modifier = Modifier.align(Alignment.TopCenter).size(sleeveSize * 1.18f)) {
                                                drawCircle(
                                                    brush = Brush.radialGradient(
                                                        0.0f to animatedMood.copy(alpha = 0.28f),
                                                        0.55f to animatedMood.copy(alpha = 0.09f),
                                                        1.0f to Color.Transparent,
                                                        center = center,
                                                        radius = size.minDimension / 2f,
                                                    ),
                                                )
                                            }
                                        }
                                        // Real vertically-mirrored sleeve reflection dissolving cleanly into obsidian surface
                                        val reflAlpha = if (isDarkScene) 0.32f else 0.14f
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .offset(y = sleeveSize)
                                                .width(sleeveSize)
                                                .height(reflH)
                                                .clip(RectangleShape)
                                                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                                                .drawWithContent {
                                                    drawContent()
                                                    drawRect(
                                                        brush = Brush.verticalGradient(
                                                            0.00f to Color.Black.copy(alpha = reflAlpha),
                                                            0.30f to Color.Black.copy(alpha = reflAlpha * 0.44f),
                                                            0.70f to Color.Black.copy(alpha = reflAlpha * 0.10f),
                                                            1.00f to Color.Transparent,
                                                        ),
                                                        blendMode = BlendMode.DstIn,
                                                    )
                                                },
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .size(sleeveSize)
                                                    .graphicsLayer { scaleY = -1f },
                                            ) {
                                                DesktopAlbumCover(filePath = song.filePath, modifier = Modifier.fillMaxSize(), cornerRadius = 0.dp, iconSize = 44.dp)
                                            }
                                        }
                                        // Razor-thin tabletop contact shadow
                                        Canvas(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .offset(y = sleeveSize - 1.dp)
                                                .width(sleeveSize)
                                                .height(7.dp),
                                        ) {
                                            drawRect(
                                                brush = Brush.verticalGradient(
                                                    0.0f to Color.Black.copy(alpha = if (isDarkScene) 0.62f else 0.22f),
                                                    0.45f to Color.Black.copy(alpha = if (isDarkScene) 0.22f else 0.07f),
                                                    1.0f to Color.Transparent,
                                                ),
                                            )
                                        }
                                        DesktopSpinningVinylDisc(
                                            filePath = song.filePath,
                                            rotationDegrees = vinylRotation,
                                            discSize = discSize,
                                            modifier = Modifier.align(Alignment.TopEnd).offset(y = (sleeveSize - discSize) / 2f),
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .size(sleeveSize)
                                                .shadow(22.dp, RoundedCornerShape(3.dp))
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(if (isDarkScene) Color(0xFF1F1D1B) else Color(0xFFF3EFE6)),
                                        ) {
                                            DesktopAlbumCover(filePath = song.filePath, modifier = Modifier.fillMaxSize(), cornerRadius = 0.dp, iconSize = 48.dp)
                                        }
                                    }
                                }

                                DesktopSceneStyle.SPATIAL_GALLERY -> {
                                    DesktopSpatialRecordGallery(
                                        currentSong = song,
                                        allSongs = allSongs,
                                        onTogglePlayPause = onTogglePlayPause,
                                        onSkipPrevious = onSkipPrevious,
                                        onSkipNext = onSkipNext,
                                        modifier = Modifier.fillMaxWidth().height((colH * 0.56f).coerceIn(240.dp, 460.dp)),
                                    )
                                }

                                else -> {
                                    // VINYL_LIGHTFIELD
                                    val giantDisc = min(colW * 0.84f, colH * 0.54f).coerceIn(210.dp, 400.dp)
                                    Box(contentAlignment = Alignment.Center) {
                                        DesktopSpinningVinylDisc(
                                            filePath = song.filePath,
                                            rotationDegrees = vinylRotation,
                                            discSize = giantDisc,
                                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).clickable(onClick = onTogglePlayPause),
                                        )
                                        DesktopTonearm(
                                            isPlaying = playerState.isPlaying,
                                            length = giantDisc * 0.72f,
                                            modifier = Modifier.align(Alignment.TopEnd).offset(x = giantDisc * 0.14f, y = (-6).dp),
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = song.title,
                                fontSize = titleSp.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryInk,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = song.artist,
                                fontSize = artistSp.sp,
                                color = secondaryInk,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            DesktopAudioSpecStamp(song = song, isDarkSurface = isDarkScene, accentColor = activeAccent, scale = 1.05f)
                        }
                    }

                    // RIGHT 2/3 (~63%): Upper Multi-Line Scrolling Bilingual Karaoke Lyrics + Lower Fluid Spectrum & Minimal Controls
                    Column(
                        modifier = Modifier.weight(0.63f).fillMaxHeight().padding(top = 8.dp, bottom = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Upper ~74%: 5-7 Line Smoothly Faded Bilingual Scrolling Karaoke Lyrics
                        DesktopMultiLineScrollingLyrics(
                            lyrics = lyrics,
                            activeIdx = activeLyricIdx,
                            positionMs = playerState.positionMs + lyricOffsetMs,
                            isPlaying = playerState.isPlaying,
                            karaokeMode = karaokeLyricMode,
                            activeColor = primaryInk,
                            accentColor = activeAccent,
                            secondaryColor = secondaryInk,
                            mutedColor = secondaryInk,
                            lyricFontScale = lyricFontScale,
                            onSeekTo = onSeekTo,
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Lower ~26%: Fluid Silk-Ribbon Spectrum + Razor-Thin Progress Line + Timestamps
                        if (showSpectrum) {
                            DesktopLiveSpectrumBar(
                                songId = song.id,
                                positionMs = playerState.positionMs,
                                durationMs = playerState.durationMs,
                                isPlaying = playerState.isPlaying,
                                spectrumStyle = spectrumStyle,
                                playedColor = if (isDarkScene) lerp(primaryInk, animatedMood, 0.32f) else accentColor,
                                unplayedColor = primaryInk.copy(alpha = 0.24f),
                                accentColor = activeAccent,
                                timestampColor = secondaryInk.copy(alpha = 0.85f),
                                onSeekTo = onSeekTo,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Discreet Minimalist Transport Controls strictly tucked beneath the Right-2/3 Spectrum
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            IconButton(onClick = onToggleShuffle, modifier = Modifier.size(32.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                Icon(Icons.Rounded.Shuffle, contentDescription = "随机", tint = if (playerState.shuffleEnabled) activeAccent else secondaryInk.copy(alpha = 0.72f), modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = onSkipPrevious, modifier = Modifier.size(34.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                Icon(Icons.Rounded.SkipPrevious, contentDescription = "上一首", tint = primaryInk, modifier = Modifier.size(21.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isDarkScene) Color.White.copy(alpha = 0.14f) else accentColor)
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .clickable(onClick = onTogglePlayPause),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    contentDescription = "播放/暂停",
                                    tint = if (isDarkScene) primaryInk else Color(0xFFFDFBF7),
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            IconButton(onClick = onSkipNext, modifier = Modifier.size(34.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                Icon(Icons.Rounded.SkipNext, contentDescription = "下一首", tint = primaryInk, modifier = Modifier.size(21.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                Icon(if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, contentDescription = "收藏", tint = if (isFavorite) activeAccent else secondaryInk.copy(alpha = 0.72f), modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = onToggleEq, modifier = Modifier.size(32.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                Icon(Icons.Rounded.Equalizer, contentDescription = "均衡器", tint = if (eqActive) activeAccent else secondaryInk.copy(alpha = 0.72f), modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = onToggleQueue, modifier = Modifier.size(32.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                Icon(Icons.Rounded.QueueMusic, contentDescription = "队列", tint = if (queueOpen) activeAccent else secondaryInk.copy(alpha = 0.72f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Only Poster Wall uses the full-width bottom rail; the 3 split scenes have their own integrated Right-2/3 spectrum & controls!
        if (!isSplitMode) {
            UnifiedBottomControlRail(
                playerState = playerState,
                isFavorite = isFavorite,
                isImmersive = true,
                isDarkSurface = isDarkScene,
                accentColor = activeAccent,
                eqActive = eqActive,
                queueOpen = queueOpen,
                spectrumVisible = showSpectrum,
                onOpenImmersive = onClose,
                onToggleFavorite = onToggleFavorite,
                onToggleShuffle = onToggleShuffle,
                onCycleRepeat = onCycleRepeat,
                onSkipPrevious = onSkipPrevious,
                onTogglePlayPause = onTogglePlayPause,
                onSkipNext = onSkipNext,
                onSeekTo = onSeekTo,
                onVolumeChange = onVolumeChange,
                onToggleEq = onToggleEq,
                onToggleQueue = onToggleQueue,
                onToggleSpectrum = onToggleSpectrum,
            )
        }
    }
}
