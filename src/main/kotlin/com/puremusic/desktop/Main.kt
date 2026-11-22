package com.puremusic.desktop

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
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
    val filteredSongs = remember(allSongs, searchQuery, sortOrder) {
        val q = searchQuery.trim().lowercase(Locale.getDefault())
        val base = if (q.isEmpty()) allSongs else allSongs.filter {
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

    val vinylAngle = remember { Animatable(0f) }
    LaunchedEffect(playerState.isPlaying) {
        if (playerState.isPlaying) {
            while (isActive) {
                val start = vinylAngle.value % 360f
                vinylAngle.snapTo(start)
                vinylAngle.animateTo(start + 360f, animationSpec = tween(16_000, easing = LinearEasing))
            }
        }
    }

    // Strict 3-Color Bookbinding Palette (Washi Paper / Ink Charcoal + Single Accent)
    val rawAccent = Color(config.accentColor.hex)
    val sidebarBg = if (config.darkMode) Color(0xFF121110) else Color(0xFFF0EBE1)
    val colorScheme = if (config.darkMode) {
        darkColorScheme(
            primary = if (config.accentColor == DesktopAccentColor.PURE_INK) Color(0xFFE0C08E) else rawAccent,
            onPrimary = Color(0xFFF6F2EA),
            background = Color(0xFF161513),
            onBackground = Color(0xFFF2ECE1),
            surface = Color(0xFF1D1C19),
            onSurface = Color(0xFFF2ECE1),
            surfaceVariant = Color(0xFF262421),
            onSurfaceVariant = Color(0xFF989187),
            outline = Color(0xFF2E2B27),
        )
    } else {
        lightColorScheme(
            primary = rawAccent,
            onPrimary = Color(0xFFFDFBF7),
            background = Color(0xFFF6F2EA),
            onBackground = Color(0xFF221F1C),
            surface = Color(0xFFFAF8F3),
            onSurface = Color(0xFF221F1C),
            surfaceVariant = Color(0xFFECE6DA),
            onSurfaceVariant = Color(0xFF7A736A),
            outline = Color(0xFFE2DBCE),
        )
    }

    val scale = config.fontScale.coerceIn(0.85f, 1.30f)
    val appFont = remember(config.appFontStyle, config.customFontPath) {
        DesktopTypographyFamilies.resolveAppFont(config.appFontStyle, config.customFontPath)
    }
    val lyricFont = remember(config.lyricFontStyle, config.appFontStyle, config.customFontPath) {
        DesktopTypographyFamilies.resolveLyricFont(config.lyricFontStyle, config.appFontStyle, config.customFontPath)
    }
    val bodyWeight = when (config.fontWeightLevel) {
        DesktopFontWeightLevel.LIGHT -> FontWeight.Light
        DesktopFontWeightLevel.STANDARD -> FontWeight.Normal
        DesktopFontWeightLevel.BOLD -> FontWeight.Medium
    }
    val titleWeight = when (config.fontWeightLevel) {
        DesktopFontWeightLevel.LIGHT -> FontWeight.Normal
        DesktopFontWeightLevel.STANDARD -> FontWeight.SemiBold
        DesktopFontWeightLevel.BOLD -> FontWeight.Bold
    }
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

    Window(
        onCloseRequest = { saveAndExit() },
        state = mainWindowState,
        title = "简约音乐 · PureMusic",
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
        CompositionLocalProvider(
            LocalDensity provides Density(baseDensity.density, fontScale = baseDensity.fontScale * scale),
            LocalLyricFontFamily provides lyricFont,
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
                    // Minimal Quiet Title Bar (double-click to maximize/restore)
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
                                .height(40.dp)
                                .background(MaterialTheme.colorScheme.background)
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = "简约音乐 · PureMusic",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                if (sleepTimerRemainingSec != null) {
                                    val mins = (sleepTimerRemainingSec ?: 0) / 60
                                    val secs = (sleepTimerRemainingSec ?: 0) % 60
                                    Text(
                                        text = String.format(Locale.US, "· 定时 %02d:%02d", mins, secs),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).clickable { sleepTimerRemainingSec = null },
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
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
                                    Icon(Icons.Rounded.CropSquare, contentDescription = "最大化", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                }
                                IconButton(
                                    onClick = { saveAndExit() },
                                    modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand),
                                ) {
                                    Icon(Icons.Rounded.Close, contentDescription = "关闭", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
                                }
                            }
                        }
                    }

                    // Main Book Spread: Slim 4-Chapter Book-Index Sidebar + Airy Monograph Content
                    Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        // Left Book-Index Sidebar (176dp, ONLY 4 chapters, 3dp bookmark spine indicator)
                        Column(
                            modifier = Modifier
                                .width(176.dp)
                                .fillMaxHeight()
                                .background(sidebarBg)
                                .padding(top = 24.dp, bottom = 18.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                DesktopTab.entries.forEach { tab ->
                                    val selected = tab == activeTab && activeDetail == null
                                    val hoverSrc = remember { MutableInteractionSource() }
                                    val hovered by hoverSrc.collectIsHoveredAsState()
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp)
                                            .background(
                                                when {
                                                    selected -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.055f)
                                                    hovered -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.03f)
                                                    else -> Color.Transparent
                                                },
                                            )
                                            .hoverable(hoverSrc)
                                            .pointerHoverIcon(PointerIcon.Hand)
                                            .clickable {
                                                activeTab = tab
                                                activeDetail = null
                                            },
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        // 3dp Bookmark Spine Line on active chapter
                                        Box(
                                            modifier = Modifier
                                                .width(3.dp)
                                                .height(if (selected) 24.dp else 0.dp)
                                                .background(MaterialTheme.colorScheme.primary),
                                        )
                                        Spacer(modifier = Modifier.width(24.dp))
                                        Text(
                                            text = tab.label,
                                            fontSize = 15.sp,
                                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }

                            // Quiet bottom library import & count
                            Column(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .pointerHoverIcon(PointerIcon.Hand)
                                        .clickable { pickAndAddFolder() }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                ) {
                                    Icon(Icons.Rounded.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                                    Text("导入音乐目录", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    text = if (isScanning) "扫描中..." else "${allSongs.size} 首 · ${albums.size} 专辑",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                                )
                            }
                        }

                        VerticalDivider(thickness = 0.6.dp, color = MaterialTheme.colorScheme.outline)

                        // Right Monograph Content Page
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            val detail = activeDetail
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
                                when (activeTab) {
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
                                        sleepTimerRemainingSec = sleepTimerRemainingSec,
                                        onToggleDarkMode = { dm -> updateConfig { it.copy(darkMode = dm) } },
                                        onSelectAccent = { ac -> updateConfig { it.copy(accentColor = ac) } },
                                        onSelectDensity = { dn -> updateConfig { it.copy(layoutDensity = dn) } },
                                        onFontScaleChange = { sc -> updateConfig { it.copy(fontScale = sc) } },
                                        onSelectAppFont = { f -> updateConfig { it.copy(appFontStyle = f) } },
                                        onSelectLyricFont = { lf -> updateConfig { it.copy(lyricFontStyle = lf) } },
                                        onSelectFontWeight = { fw -> updateConfig { it.copy(fontWeightLevel = fw) } },
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
                                    )
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
                    enter = fadeIn(tween(240)) + scaleIn(initialScale = 0.98f, animationSpec = tween(240)),
                    exit = fadeOut(tween(180)) + scaleOut(targetScale = 0.99f, animationSpec = tween(180)),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val song = playerState.currentSong
                    if (song != null) {
                        UnifiedImmersiveStage(
                            song = song,
                            allSongs = allSongs,
                            playerState = playerState,
                            isFavorite = song.filePath in config.favoritePaths,
                            lyrics = currentLyrics,
                            activeLyricIdx = activeLyricIdx,
                            vinylRotation = vinylAngle.value,
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

    // Independent Always-On-Top Draggable Mini Vinyl Floating Window
    if (showMiniVinylWindow) {
        val miniState = rememberWindowState(size = DpSize(336.dp, 106.dp), position = WindowPosition(Alignment.BottomEnd))
        Window(
            onCloseRequest = { showMiniVinylWindow = false },
            state = miniState,
            title = "简约音乐 · 迷你黑胶",
            undecorated = true,
            transparent = true,
            alwaysOnTop = true,
            resizable = false,
        ) {
            MaterialTheme(colorScheme = colorScheme) {
                WindowDraggableArea(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                            .shadow(14.dp, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.97f))
                            .border(0.75.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DesktopSpinningVinylDisc(filePath = playerState.currentSong?.filePath, rotationDegrees = vinylAngle.value, discSize = 66.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(playerState.currentSong?.title ?: "暂未播放", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(playerState.currentSong?.artist ?: "简约音乐", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(onClick = { skipPrevious() }, modifier = Modifier.size(26.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                    Icon(Icons.Rounded.SkipPrevious, contentDescription = "上一首", modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { togglePlayOrResume() }, modifier = Modifier.size(30.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                    Icon(if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, contentDescription = "播放/暂停", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                                }
                                IconButton(onClick = { skipNext() }, modifier = Modifier.size(26.dp).pointerHoverIcon(PointerIcon.Hand)) {
                                    Icon(Icons.Rounded.SkipNext, contentDescription = "下一首", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        IconButton(onClick = { showMiniVinylWindow = false }, modifier = Modifier.size(24.dp).pointerHoverIcon(PointerIcon.Hand)) {
                            Icon(Icons.Rounded.Close, contentDescription = "关闭", modifier = Modifier.size(14.dp))
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
    val barBg = if (isImmersive) Color.Transparent else MaterialTheme.colorScheme.surface
    val ink = if (isDarkSurface) Color(0xFFF3EEE4) else MaterialTheme.colorScheme.onBackground
    val muted = if (isDarkSurface) Color(0xFFA69F95) else MaterialTheme.colorScheme.onSurfaceVariant
    val hairline = if (isDarkSurface) Color.White.copy(alpha = 0.14f) else MaterialTheme.colorScheme.outline
    val progress = if (playerState.durationMs > 0L) (playerState.positionMs.toFloat() / playerState.durationMs).coerceIn(0f, 1f) else 0f

    var railWidthPx by remember { mutableFloatStateOf(1f) }

    Column(modifier = Modifier.fillMaxWidth().background(barBg)) {
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
            modifier = Modifier.fillMaxWidth().height(58.dp).padding(horizontal = 24.dp),
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
                IconButton(onClick = onSkipPrevious, modifier = Modifier.size(34.dp).pointerHoverIcon(PointerIcon.Hand)) {
                    Icon(Icons.Rounded.SkipPrevious, contentDescription = "上一首", tint = ink, modifier = Modifier.size(22.dp))
                }
                IconButton(onClick = onTogglePlayPause, modifier = Modifier.size(40.dp).pointerHoverIcon(PointerIcon.Hand)) {
                    Icon(
                        imageVector = if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "播放/暂停",
                        tint = ink,
                        modifier = Modifier.size(28.dp),
                    )
                }
                IconButton(onClick = onSkipNext, modifier = Modifier.size(34.dp).pointerHoverIcon(PointerIcon.Hand)) {
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
                .widthIn(max = 980.dp)
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

                    Row(horizontalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.padding(bottom = 6.dp)) {
                        LibrarySubTab.entries.forEach { tab ->
                            val sel = tab == subTab
                            Text(
                                text = tab.label,
                                fontSize = 13.5.sp,
                                fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).clickable { onSelectSubTab(tab) },
                            )
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
            Spacer(modifier = Modifier.height(6.dp))

            when (subTab) {
                LibrarySubTab.SONGS -> {
                    if (songs.isEmpty()) {
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
                        val letterIndexMap = remember(songs, sortOrder) {
                            val map = LinkedHashMap<String, Int>()
                            songs.forEachIndexed { idx, s ->
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
                        val activeLetter = remember(firstVisibleIdx, songs, sortOrder) {
                            val s = songs.getOrNull(firstVisibleIdx) ?: return@remember null
                            val keyText = when (sortOrder) {
                                SortOrder.TITLE -> s.title
                                SortOrder.ARTIST -> s.artist
                                SortOrder.ALBUM -> s.album
                                SortOrder.DURATION -> s.title
                            }
                            DesktopAlphabetIndex.initialLetter(keyText)
                        }
                        Box(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize().padding(end = if (showAlphaBar) 32.dp else 10.dp),
                            ) {
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                when {
                    isCurrent -> MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                    hovered -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.032f)
                    else -> Color.Transparent
                },
            )
            .hoverable(hoverSrc)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = density.rowPaddingDp.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DesktopAlbumCover(filePath = song.filePath, modifier = Modifier.size(density.artSizeDp.dp), cornerRadius = 4.dp, iconSize = 16.dp)
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = String.format(Locale.US, "%02d.", index),
            fontSize = 12.sp,
            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.width(30.dp),
        )
        Text(
            text = song.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.3f),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = song.artist,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.75f),
        )
        if (hovered || isFavorite) {
            IconButton(onClick = onToggleFavorite, modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand)) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = "喜欢",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp),
                )
            }
        } else {
            Spacer(modifier = Modifier.width(28.dp))
        }
        if (hovered) {
            IconButton(onClick = onAddToPlaylist, modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand)) {
                Icon(Icons.Rounded.PlaylistAdd, contentDescription = "添加到歌单", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            }
        } else {
            Spacer(modifier = Modifier.width(28.dp))
        }
        Text(
            text = formatDuration(song.durationMs),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.width(44.dp),
        )
    }
    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.65f))
}

// =================================================================================================
// 2. ALBUMS PAGE (Clean Gallery Monograph Grid)
// =================================================================================================
@Composable
private fun DesktopAlbumsPage(
    albums: List<DesktopAlbum>,
    density: DesktopLayoutDensity,
    onSelectAlbum: (DesktopAlbum) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 980.dp)
                .fillMaxWidth()
                .padding(horizontal = 36.dp, vertical = 26.dp),
        ) {
            Text("专辑", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.width(34.dp).height(2.dp).background(MaterialTheme.colorScheme.onBackground))
            HorizontalDivider(thickness = 0.6.dp, color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(18.dp))

            val listState = rememberLazyListState()
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val cols = ((maxWidth.value / 184f).toInt()).coerceIn(3, 6)
                val rows = remember(albums, cols) { albums.chunked(cols) }
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(end = 10.dp),
                    verticalArrangement = Arrangement.spacedBy((density.gridSpacingDp + 8).dp),
                ) {
                    items(rows, key = { row -> row.joinToString("|") { it.name } }) { rowAlbums ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(density.gridSpacingDp.dp)) {
                            rowAlbums.forEach { album ->
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .desktopHoverClickable(shape = RoundedCornerShape(8.dp)) { onSelectAlbum(album) }
                                        .padding(6.dp),
                                ) {
                                    DesktopAlbumCover(
                                        filePath = album.songs.firstOrNull()?.filePath,
                                        modifier = Modifier.fillMaxWidth().aspectRatio(1f).shadow(5.dp, RoundedCornerShape(4.dp)),
                                        cornerRadius = 4.dp,
                                        iconSize = 36.dp,
                                    )
                                    Spacer(modifier = Modifier.height(9.dp))
                                    Text(album.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("${album.artist} · ${album.songs.size} 首", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                            repeat(cols - rowAlbums.size) { Spacer(modifier = Modifier.weight(1f)) }
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
    val cardShape = RoundedCornerShape(10.dp)
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
            modifier = Modifier.fillMaxHeight().widthIn(max = 980.dp).fillMaxWidth().padding(horizontal = 36.dp),
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

            // Quiet Paper Hero Card: 我喜欢的音乐
            item(key = "fav_hero") {
                val favThumbs = remember(favoriteSongs) { favoriteSongs.take(3) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(cardShape)
                        .background(surface)
                        .border(0.6.dp, outline, cardShape)
                        .desktopHoverClickable(shape = cardShape, onClick = onOpenFavorites)
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (favThumbs.isNotEmpty()) {
                        Box(modifier = Modifier.width(74.dp).height(56.dp), contentAlignment = Alignment.CenterStart) {
                            favThumbs.reversed().forEachIndexed { revIdx, s ->
                                val layer = favThumbs.lastIndex - revIdx
                                Box(
                                    modifier = Modifier
                                        .offset(x = (layer * 10).dp)
                                        .size((52 - layer * 5).dp)
                                        .shadow(if (layer == 0) 5.dp else 2.dp, RoundedCornerShape(4.dp))
                                        .clip(RoundedCornerShape(4.dp))
                                        .border(0.7.dp, surface, RoundedCornerShape(4.dp)),
                                ) {
                                    DesktopAlbumCover(filePath = s.filePath, modifier = Modifier.fillMaxSize(), cornerRadius = 4.dp, iconSize = 16.dp)
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.size(52.dp).clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Rounded.Favorite, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("我喜欢的音乐", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = ink)
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(favSub, style = MaterialTheme.typography.bodySmall, color = muted)
                    }
                    if (favoriteSongs.isNotEmpty()) {
                        IconButton(onClick = onPlayFavorites, modifier = Modifier.size(38.dp).pointerHoverIcon(PointerIcon.Hand)) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = "播放我喜欢的音乐", tint = accent, modifier = Modifier.size(26.dp))
                        }
                    }
                }
            }

            // Bento Row: 最近播放 + 听歌报告
            item(key = "bento_row") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(134.dp)
                            .clip(cardShape)
                            .background(surface)
                            .border(0.6.dp, outline, cardShape)
                            .desktopHoverClickable(shape = cardShape, onClick = onOpenRecent)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("最近播放", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = ink)
                        val recentThumbs = remember(recentSongs) { recentSongs.take(3) }
                        if (recentThumbs.isNotEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().height(46.dp), contentAlignment = Alignment.CenterStart) {
                                recentThumbs.forEachIndexed { idx, s ->
                                    Box(
                                        modifier = Modifier
                                            .offset(x = (idx * 32).dp)
                                            .size(if (idx == 0) 44.dp else 38.dp)
                                            .shadow(if (idx == 0) 4.dp else 2.dp, RoundedCornerShape(4.dp))
                                            .clip(RoundedCornerShape(4.dp))
                                            .border(0.7.dp, surface, RoundedCornerShape(4.dp)),
                                    ) {
                                        DesktopAlbumCover(filePath = s.filePath, modifier = Modifier.fillMaxSize(), cornerRadius = 4.dp, iconSize = 15.dp)
                                    }
                                }
                            }
                        } else {
                            Icon(Icons.Rounded.History, contentDescription = null, tint = muted.copy(alpha = 0.5f), modifier = Modifier.size(24.dp))
                        }
                        Text(
                            text = if (recentSongs.isNotEmpty()) "${recentSongs.size} 首 · ${recentSongs.first().title}" else "暂无播放记录",
                            fontSize = 12.sp,
                            color = muted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(134.dp)
                            .clip(cardShape)
                            .background(surface)
                            .border(0.6.dp, outline, cardShape)
                            .desktopHoverClickable(shape = cardShape, onClick = onOpenReport)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("听歌报告", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = ink)
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(hoursText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = ink)
                                Text("$totalPlays 次播放", fontSize = 11.sp, color = muted)
                            }
                            Canvas(modifier = Modifier.width(92.dp).height(42.dp)) {
                                val buckets = FloatArray(12) { b -> ((hourlyPlays[b * 2] ?: 0) + (hourlyPlays[b * 2 + 1] ?: 0)).toFloat() }
                                val maxV = buckets.maxOrNull() ?: 0f
                                val peakIdx = if (maxV > 0f) buckets.indices.maxByOrNull { buckets[it] } ?: -1 else -1
                                val step = size.width / buckets.size
                                val barW = (step * 0.44f).coerceAtLeast(2.5f)
                                buckets.forEachIndexed { idx, v ->
                                    val ratio = if (maxV > 0f) (v / maxV).coerceIn(0.14f, 1f) else (0.18f + (idx % 3) * 0.08f)
                                    val h = size.height * ratio
                                    val col = when {
                                        maxV <= 0f -> ink.copy(alpha = 0.14f)
                                        idx == peakIdx -> accent
                                        else -> ink.copy(alpha = 0.52f)
                                    }
                                    drawRoundRect(col, Offset(idx * step + (step - barW) * 0.5f, size.height - h), Size(barW, h), CornerRadius(barW * 0.5f, barW * 0.5f))
                                }
                            }
                        }
                        Text(if (totalPlays > 0) "翻阅聆听档案 →" else "听歌后自动生成", fontSize = 12.sp, color = accent)
                    }
                }
            }

            item(key = "user_pl_hdr") {
                Text("自建歌单 (${playlists.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = ink, modifier = Modifier.padding(top = 6.dp))
            }

            if (playlists.isEmpty()) {
                item(key = "empty_pl") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(cardShape)
                            .border(0.6.dp, outline, cardShape)
                            .desktopHoverClickable(shape = cardShape, onClick = onCreatePlaylist)
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
                val rows = playlists.chunked(3)
                items(rows, key = { row -> row.joinToString("_") { it.id } }) { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        rowItems.forEach { pl ->
                            val coverPath = pl.songPaths.firstOrNull { it in songsByPath }
                            var menuOpen by remember { mutableStateOf(false) }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(cardShape)
                                    .background(surface)
                                    .border(0.6.dp, outline, cardShape)
                                    .desktopHoverClickable(shape = cardShape) { onOpenPlaylist(pl) }
                                    .padding(12.dp),
                            ) {
                                DesktopAlbumCover(filePath = coverPath, modifier = Modifier.fillMaxWidth().aspectRatio(1f), cornerRadius = 4.dp, iconSize = 34.dp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(pl.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("${pl.songPaths.size} 首", fontSize = 11.sp, color = muted)
                                    }
                                    Box {
                                        IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(26.dp).pointerHoverIcon(PointerIcon.Hand)) {
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
                            }
                        }
                        repeat(3 - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
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
    val cardShape = RoundedCornerShape(10.dp)
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
            modifier = Modifier.fillMaxHeight().widthIn(max = 980.dp).fillMaxWidth().padding(horizontal = 36.dp),
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
    sleepTimerRemainingSec: Int?,
    onToggleDarkMode: (Boolean) -> Unit,
    onSelectAccent: (DesktopAccentColor) -> Unit,
    onSelectDensity: (DesktopLayoutDensity) -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onSelectAppFont: (DesktopFontStyle) -> Unit,
    onSelectLyricFont: (DesktopLyricFontStyle) -> Unit,
    onSelectFontWeight: (DesktopFontWeightLevel) -> Unit,
    onPickCustomFont: () -> Unit,
    onSetSleepTimerMin: (Int) -> Unit,
    onAddFolder: () -> Unit,
    onRemoveFolder: (String) -> Unit,
    onRescan: () -> Unit,
) {
    var selectedSection by remember { mutableStateOf(0) }
    val sections = listOf("外观与字体", "音乐文件夹", "播放与定时", "关于")
    val cardShape = RoundedCornerShape(10.dp)
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outline
    val accent = MaterialTheme.colorScheme.primary
    val ink = MaterialTheme.colorScheme.onBackground
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    val listState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxHeight().widthIn(max = 840.dp).fillMaxWidth().padding(horizontal = 36.dp),
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
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)).padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    sections.forEachIndexed { idx, label ->
                        val sel = idx == selectedSection
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (sel) surface else Color.Transparent)
                                .then(if (sel) Modifier.border(0.6.dp, outline, RoundedCornerShape(8.dp)) else Modifier)
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable { selectedSection = idx }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Text(label, fontSize = 13.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal, color = if (sel) accent else muted)
                        }
                    }
                }
            }

            when (selectedSection) {
                0 -> item(key = "sec_app") {
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("外观与字体", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = ink)
                                Text("选择后自动保存，让阅读和聆听更自在。", fontSize = 12.sp, color = muted)
                            }
                            Text(
                                text = "恢复默认",
                                fontSize = 12.sp,
                                color = muted,
                                modifier = Modifier.border(0.6.dp, outline, RoundedCornerShape(8.dp)).pointerHoverIcon(PointerIcon.Hand).clickable {
                                    onToggleDarkMode(false); onSelectAccent(DesktopAccentColor.CINNABAR); onSelectDensity(DesktopLayoutDensity.STANDARD); onFontScaleChange(1.0f); onSelectAppFont(DesktopFontStyle.YAHEI); onSelectLyricFont(DesktopLyricFontStyle.SERIF); onSelectFontWeight(DesktopFontWeightLevel.STANDARD)
                                }.padding(horizontal = 12.dp, vertical = 6.dp),
                            )
                        }

                        // Signature Live Preview Card with 3.5dp left book-spine accent bar
                        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.background).border(0.6.dp, outline, RoundedCornerShape(8.dp))) {
                            Box(modifier = Modifier.width(3.5.dp).height(126.dp).background(accent))
                            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text("简约音乐 · 外观预览", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = accent)
                                Text("把喜欢的音乐，留在身边。", style = MaterialTheme.typography.bodyMedium, color = ink)
                                Text("让晚风陪着音乐", fontSize = 24.sp, fontFamily = LocalLyricFontFamily.current, fontWeight = FontWeight.Bold, color = ink)
                                Text("Simple moments, beautiful music.", fontSize = 12.sp, color = muted)
                            }
                        }

                        // Theme Mode
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
                                            .pointerHoverIcon(PointerIcon.Hand)
                                            .clickable { onToggleDarkMode(dm) }
                                            .padding(horizontal = 14.dp, vertical = 7.dp),
                                    ) {
                                        Text(lbl, fontSize = 12.sp, color = if (sel) accent else muted, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                                    }
                                }
                            }
                        }

                        HorizontalDivider(thickness = 0.5.dp, color = outline)

                        // Book-Spine Accent Colors
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
                                            .pointerHoverIcon(PointerIcon.Hand)
                                            .clickable { onSelectAccent(ac) },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (sel) Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }

                        HorizontalDivider(thickness = 0.5.dp, color = outline)

                        // Density
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("列表间距", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
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
                                            .pointerHoverIcon(PointerIcon.Hand)
                                            .clickable { onSelectDensity(d) }
                                            .padding(horizontal = 14.dp, vertical = 7.dp),
                                    ) {
                                        Text(d.label, fontSize = 12.sp, color = if (sel) accent else muted, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                                    }
                                }
                            }
                        }

                        HorizontalDivider(thickness = 0.5.dp, color = outline)

                        // Font Scale (Global LocalDensity.fontScale)
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.width(200.dp)) {
                                Text("全局字号缩放", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Text("${(config.fontScale * 100).toInt()}%（全界面实时生效）", fontSize = 12.sp, color = accent)
                            }
                            DesktopSleekSlider(value = config.fontScale, onValueChange = onFontScaleChange, valueRange = 0.85f..1.30f, activeColor = accent, modifier = Modifier.weight(1f))
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
                                        "切换微软雅黑、宋体书卷、楷体手书或导入本地字体。"
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
                                            .pointerHoverIcon(PointerIcon.Hand)
                                            .clickable { onSelectAppFont(fs) }
                                            .padding(horizontal = 12.dp, vertical = 7.dp),
                                        contentAlignment = Alignment.Center,
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
                                        .pointerHoverIcon(PointerIcon.Hand)
                                        .clickable { onPickCustomFont() }
                                        .padding(horizontal = 12.dp, vertical = 7.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("导入字体", fontSize = 12.sp, color = if (customSel) accent else muted, fontWeight = if (customSel) FontWeight.SemiBold else FontWeight.Normal)
                                }
                            }
                        }

                        HorizontalDivider(thickness = 0.5.dp, color = outline)

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
                                            .pointerHoverIcon(PointerIcon.Hand)
                                            .clickable { onSelectLyricFont(lfs) }
                                            .padding(horizontal = 11.dp, vertical = 7.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(lfs.label, fontSize = 12.sp, color = if (sel) accent else muted, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                                    }
                                }
                            }
                        }

                        HorizontalDivider(thickness = 0.5.dp, color = outline)

                        // Font Weight
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("字重风格", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                Text("微调正文与标题的笔划粗细。", fontSize = 12.sp, color = muted)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                DesktopFontWeightLevel.entries.forEach { fw ->
                                    val sel = config.fontWeightLevel == fw
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (sel) accent.copy(alpha = 0.12f) else Color.Transparent)
                                            .border(0.6.dp, if (sel) accent else outline, RoundedCornerShape(8.dp))
                                            .pointerHoverIcon(PointerIcon.Hand)
                                            .clickable { onSelectFontWeight(fw) }
                                            .padding(horizontal = 14.dp, vertical = 7.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(fw.label, fontSize = 12.sp, color = if (sel) accent else muted, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> item(key = "sec_folders") {
                    Column(modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("本地音乐文件夹", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = ink)
                                Text("内置 FFmpeg 全格式解码，支持 MP3 / FLAC (含 24-bit Hi-Res) / WAV / M4A / AAC / ALAC / OGG / OPUS / WMA / APE / AIFF / DSF。", fontSize = 12.sp, color = muted)
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
                }

                2 -> item(key = "sec_play") {
                    Column(modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("睡眠定时暂停", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = ink)
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
                                            .pointerHoverIcon(PointerIcon.Hand)
                                            .clickable { onSetSleepTimerMin(mins) }
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

                else -> item(key = "sec_about") {
                    Row(modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.6.dp, outline, cardShape).padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("简约音乐 · PureMusic Desktop", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = ink)
                            Text("回归音乐本身。纯粹、克制、安静陪伴每一张本地唱片。", fontSize = 12.sp, color = muted)
                        }
                        Text("v3.1.2", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = accent)
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
        Column(modifier = Modifier.fillMaxHeight().widthIn(max = 980.dp).fillMaxWidth().padding(horizontal = 36.dp, vertical = 26.dp)) {
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
) {
    val isDarkScene = when (sceneStyle) {
        DesktopSceneStyle.VINYL_STUDY -> isDarkTheme
        DesktopSceneStyle.VINYL_LIGHTFIELD, DesktopSceneStyle.SPATIAL_GALLERY, DesktopSceneStyle.POSTER_WALL -> true
    }
    val extractedMood = rememberSongDominantColor(song.filePath)
    val animatedMood by animateColorAsState(targetValue = extractedMood, animationSpec = tween(650), label = "immMood")
    val bgBrush = when (sceneStyle) {
        DesktopSceneStyle.VINYL_STUDY -> Brush.verticalGradient(listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.background))
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

                    IconButton(
                        onClick = onToggleFullscreen,
                        modifier = Modifier.align(Alignment.CenterEnd).pointerHoverIcon(PointerIcon.Hand),
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

        // 4 Cohesive Widescreen Scenes (Locked to 16:9 golden ratio, zero per-scene mismatched controls!)
        AnimatedContent(
            targetState = sceneStyle,
            transitionSpec = {
                (fadeIn(tween(240)) + scaleIn(initialScale = 0.98f, animationSpec = tween(240))) togetherWith
                    fadeOut(tween(180))
            },
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = if (sceneStyle == DesktopSceneStyle.POSTER_WALL) 0.dp else 36.dp),
        ) { targetScene ->
            when (targetScene) {
                // 1. 黑胶书房 (Matches v3.0 mockup 1-to-1!)
                DesktopSceneStyle.VINYL_STUDY -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val stageW = (maxHeight * 1.80f).coerceAtMost(maxWidth)
                        val sleeveSize = min(maxHeight * 0.64f, stageW * 0.34f).coerceIn(220.dp, 500.dp)
                        val discSize = sleeveSize * 0.90f
                        val lyricSp = (maxHeight.value * 0.064f).coerceIn(30f, 56f)
                        val titleSp = (sleeveSize.value * 0.056f).coerceIn(16f, 26f)
                        val stampScale = (sleeveSize.value / 280f).coerceIn(1.0f, 1.35f)
                        Row(
                            modifier = Modifier.width(stageW).fillMaxHeight().padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(56.dp),
                        ) {
                            Column(modifier = Modifier.width(sleeveSize + discSize * 0.35f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.width(sleeveSize + discSize * 0.35f).height(sleeveSize).pointerHoverIcon(PointerIcon.Hand).clickable(onClick = onTogglePlayPause),
                                    contentAlignment = Alignment.CenterStart,
                                ) {
                                    DesktopSpinningVinylDisc(filePath = song.filePath, rotationDegrees = vinylRotation, discSize = discSize, modifier = Modifier.align(Alignment.CenterEnd))
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .size(sleeveSize)
                                            .shadow(22.dp, RoundedCornerShape(3.dp))
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(if (isDarkScene) Color(0xFF1F1D1B) else Color(0xFFF3EFE6))
                                            .padding(14.dp),
                                    ) {
                                        DesktopAlbumCover(filePath = song.filePath, modifier = Modifier.fillMaxSize(), cornerRadius = 2.dp, iconSize = 48.dp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Text("${song.title}  -  ${song.artist}", fontSize = titleSp.sp, fontWeight = FontWeight.SemiBold, color = primaryInk, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(8.dp))
                                DesktopAudioSpecStamp(song = song, isDarkSurface = isDarkScene, accentColor = activeAccent, scale = stampScale)
                            }

                            DesktopEditorialLyrics(
                                lyrics = lyrics,
                                activeIdx = activeLyricIdx,
                                activeColor = primaryInk,
                                accentColor = activeAccent,
                                secondaryColor = secondaryInk,
                                mutedColor = secondaryInk,
                                horizontalAlignment = Alignment.Start,
                                textAlign = TextAlign.Start,
                                highlightAccent = true,
                                useEditorialSerif = true,
                                maxActiveSp = lyricSp,
                                lyricFontScale = lyricFontScale,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                // 2. 黑胶光场
                DesktopSceneStyle.VINYL_LIGHTFIELD -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val stageW = (maxHeight * 1.80f).coerceAtMost(maxWidth)
                        val giantDisc = min(maxHeight * 0.78f, stageW * 0.44f).coerceIn(260.dp, 620.dp)
                        val lyricSp = (maxHeight.value * 0.058f).coerceIn(28f, 52f)
                        val titleSp = (maxHeight.value * 0.042f).coerceIn(22f, 36f)
                        val artistSp = (maxHeight.value * 0.024f).coerceIn(14f, 20f)
                        val stampScale = (maxHeight.value / 700f).coerceIn(1.0f, 1.35f)
                        Row(
                            modifier = Modifier.width(stageW).fillMaxHeight().padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(52.dp),
                        ) {
                            Box(modifier = Modifier.weight(1.1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                                Box(contentAlignment = Alignment.Center) {
                                    DesktopSpinningVinylDisc(filePath = song.filePath, rotationDegrees = vinylRotation, discSize = giantDisc, modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).clickable(onClick = onTogglePlayPause))
                                    DesktopTonearm(isPlaying = playerState.isPlaying, length = giantDisc * 0.72f, modifier = Modifier.align(Alignment.TopEnd).offset(x = giantDisc * 0.15f, y = (-6).dp))
                                }
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(song.title, fontSize = titleSp.sp, fontWeight = FontWeight.Bold, color = primaryInk, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(song.artist, fontSize = artistSp.sp, color = secondaryInk, textAlign = TextAlign.Center, maxLines = 1)
                                Spacer(modifier = Modifier.height(10.dp))
                                DesktopAudioSpecStamp(song = song, isDarkSurface = true, accentColor = activeAccent, scale = stampScale)
                                Spacer(modifier = Modifier.height(28.dp))
                                DesktopEditorialLyrics(
                                    lyrics = lyrics,
                                    activeIdx = activeLyricIdx,
                                    activeColor = primaryInk,
                                    accentColor = activeAccent,
                                    secondaryColor = secondaryInk,
                                    mutedColor = secondaryInk,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    textAlign = TextAlign.Center,
                                    useEditorialSerif = true,
                                    maxActiveSp = lyricSp,
                                    lyricFontScale = lyricFontScale,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }

                // 3. 空间唱片廊
                DesktopSceneStyle.SPATIAL_GALLERY -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val titleSp = (maxHeight.value * 0.036f).coerceIn(20f, 32f)
                        val artistSp = (maxHeight.value * 0.022f).coerceIn(13.5f, 19f)
                        val stampScale = (maxHeight.value / 700f).coerceIn(1.0f, 1.32f)
                        val lyricSp = (maxHeight.value * 0.044f).coerceIn(26f, 40f)
                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            DesktopSpatialRecordGallery(
                                currentSong = song,
                                allSongs = allSongs,
                                onTogglePlayPause = onTogglePlayPause,
                                onSkipPrevious = onSkipPrevious,
                                onSkipNext = onSkipNext,
                                modifier = Modifier.weight(1.3f).fillMaxWidth(),
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(song.title, fontSize = titleSp.sp, fontWeight = FontWeight.Bold, color = primaryInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(song.artist, fontSize = artistSp.sp, color = secondaryInk, maxLines = 1)
                            Spacer(modifier = Modifier.height(8.dp))
                            DesktopAudioSpecStamp(song = song, isDarkSurface = true, accentColor = activeAccent, scale = stampScale)
                            Spacer(modifier = Modifier.height(12.dp))
                            DesktopEditorialLyrics(
                                lyrics = lyrics,
                                activeIdx = activeLyricIdx,
                                activeColor = primaryInk,
                                accentColor = activeAccent,
                                secondaryColor = secondaryInk,
                                mutedColor = secondaryInk,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                textAlign = TextAlign.Center,
                                useEditorialSerif = true,
                                maxActiveSp = lyricSp,
                                lyricFontScale = lyricFontScale,
                                modifier = Modifier.fillMaxWidth(0.76f),
                            )
                        }
                    }
                }

                // 4. 唱片海报墙
                DesktopSceneStyle.POSTER_WALL -> {
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
                }
            }
        }

        // Optional Silky 60fps 256-Point FFT Live Spectrum right above the Unified Control Rail
        AnimatedVisibility(
            visible = showSpectrum,
            enter = fadeIn(tween(200)) + expandVertically(),
            exit = fadeOut(tween(180)) + shrinkVertically(),
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 36.dp, vertical = 4.dp)) {
                DesktopLiveSpectrumBar(
                    songId = song.id,
                    positionMs = playerState.positionMs,
                    durationMs = playerState.durationMs,
                    isPlaying = playerState.isPlaying,
                    spectrumStyle = spectrumStyle,
                    playedColor = primaryInk,
                    unplayedColor = primaryInk.copy(alpha = 0.22f),
                    accentColor = activeAccent,
                    timestampColor = Color.Transparent,
                    onSeekTo = onSeekTo,
                )
            }
        }

        // Exact Same Unified Bottom Control Rail across ALL 4 Immersive Scenes!
        UnifiedBottomControlRail(
            playerState = playerState,
            isFavorite = isFavorite,
            isImmersive = true,
            isDarkSurface = isDarkScene,
            accentColor = activeAccent,
            eqActive = eqActive,
            queueOpen = queueOpen,
            spectrumVisible = showSpectrum,
            onOpenImmersive = {},
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
