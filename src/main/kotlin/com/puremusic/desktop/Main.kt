package com.puremusic.desktop

import androidx.compose.animation.AnimatedContent
import androidx.compose.ui.graphics.lerp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.min
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.material.icons.rounded.MoreHoriz
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
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Timer
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
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
import kotlin.math.abs
import javax.swing.JFileChooser
import javax.swing.UIManager

private sealed interface DesktopDetailView {
    data class AlbumView(val album: DesktopAlbum) : DesktopDetailView
    data class ArtistView(val artist: DesktopArtist) : DesktopDetailView
    data class FolderView(val folder: DesktopFolder) : DesktopDetailView
    data class PlaylistView(val playlistId: String) : DesktopDetailView
    data object FavoritesView : DesktopDetailView
    data object RecentView : DesktopDetailView
}

fun main() = application {
    runCatching { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) }

    val initialConfig = remember { DesktopConfigStore.load() }
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
    var showMiniVinylWindow by remember { mutableStateOf(false) }
    var showSplash by remember { mutableStateOf(true) }

    var playerState by remember {
        mutableStateOf(
            DesktopPlayerState(
                volume = initialConfig.volume,
                repeatMode = initialConfig.repeatMode,
                shuffleEnabled = initialConfig.shuffleEnabled,
            ),
        )
    }
    var showQueueDrawer by remember { mutableStateOf(false) }
    var currentLyrics by remember { mutableStateOf<List<LyricLine>>(emptyList()) }

    // Sleep timer state (null = off, remaining seconds)
    var sleepTimerRemainingSec by remember { mutableStateOf<Int?>(null) }

    // Playlist dialogs
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var renamingPlaylist by remember { mutableStateOf<DesktopPlaylist?>(null) }
    var addingSongToPlaylist by remember { mutableStateOf<DesktopSong?>(null) }

    val scope = rememberCoroutineScope()

    fun updateConfig(transform: (DesktopConfig) -> DesktopConfig) {
        val updated = transform(config)
        config = updated
        scope.launch(Dispatchers.IO) { DesktopConfigStore.save(updated) }
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
            cfg.copy(recentPaths = nextRecent, playCounts = nextCounts, hourlyPlays = nextHourly, lastSongPath = song.filePath)
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
        val prevIdx = when {
            currentIndex <= 0 -> q.lastIndex
            else -> currentIndex - 1
        }
        playFromQueue(q, prevIdx)
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

    // Keep EQ DSP & initial volume synchronized with config
    LaunchedEffect(Unit) {
        engine.setVolume(initialConfig.volume)
    }
    LaunchedEffect(config.eqEnabled, config.eqBandsDb) {
        engine.equalizer.update(config.eqEnabled, config.eqBandsDb)
    }

    // Cold-start splash dismissal
    LaunchedEffect(Unit) {
        delay(820)
        showSplash = false
    }

    // Sleep timer countdown loop
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
        if (defaultFolders.isNotEmpty()) {
            rescanLibrary(defaultFolders)
        }
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
            SortOrder.TITLE -> base.sortedBy { it.title.lowercase(Locale.getDefault()) }
            SortOrder.ARTIST -> base.sortedBy { it.artist.lowercase(Locale.getDefault()) }
            SortOrder.ALBUM -> base.sortedBy { it.album.lowercase(Locale.getDefault()) }
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
            .sortedBy { it.name.lowercase(Locale.getDefault()) }
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

    // Continuous 60fps vinyl rotation angle shared between main window & mini floating window
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

    val currentAccent = Color(config.accentColor.hex)
    val colorScheme = if (config.darkMode) {
        darkColorScheme(
            primary = if (config.accentColor == DesktopAccentColor.PURE_INK) Color(0xFFE0C08E) else currentAccent,
            onPrimary = Color(0xFFF6F2EB),
            background = Color(0xFF141312),
            onBackground = Color(0xFFF3EEE5),
            surface = Color(0xFF1D1C1A),
            onSurface = Color(0xFFF3EEE5),
            surfaceVariant = Color(0xFF282623),
            onSurfaceVariant = Color(0xFFA0998F),
            outline = Color(0xFF33302C),
        )
    } else {
        lightColorScheme(
            primary = currentAccent,
            onPrimary = Color(0xFFFDFBF7),
            background = Color(0xFFF5F0E6),
            onBackground = Color(0xFF1E1B18),
            surface = Color(0xFFFAF7F0),
            onSurface = Color(0xFF1E1B18),
            surfaceVariant = Color(0xFFECE6D9),
            onSurfaceVariant = Color(0xFF6E675E),
            outline = Color(0xFFDDD6C8),
        )
    }

    val scale = config.fontScale.coerceIn(0.85f, 1.25f)
    val baseTypo = Typography()
    val uiSans = DesktopTypographyFamilies.uiSans
    val scaledTypo = Typography(
        headlineMedium = baseTypo.headlineMedium.copy(fontFamily = uiSans, fontSize = baseTypo.headlineMedium.fontSize * scale),
        headlineSmall = baseTypo.headlineSmall.copy(fontFamily = uiSans, fontSize = baseTypo.headlineSmall.fontSize * scale),
        titleLarge = baseTypo.titleLarge.copy(fontFamily = uiSans, fontSize = baseTypo.titleLarge.fontSize * scale),
        titleMedium = baseTypo.titleMedium.copy(fontFamily = uiSans, fontSize = baseTypo.titleMedium.fontSize * scale),
        titleSmall = baseTypo.titleSmall.copy(fontFamily = uiSans, fontSize = baseTypo.titleSmall.fontSize * scale),
        bodyLarge = baseTypo.bodyLarge.copy(fontFamily = uiSans, fontSize = baseTypo.bodyLarge.fontSize * scale),
        bodyMedium = baseTypo.bodyMedium.copy(fontFamily = uiSans, fontSize = baseTypo.bodyMedium.fontSize * scale),
        bodySmall = baseTypo.bodySmall.copy(fontFamily = uiSans, fontSize = baseTypo.bodySmall.fontSize * scale),
        labelLarge = baseTypo.labelLarge.copy(fontFamily = uiSans, fontSize = baseTypo.labelLarge.fontSize * scale),
        labelMedium = baseTypo.labelMedium.copy(fontFamily = uiSans, fontSize = baseTypo.labelMedium.fontSize * scale),
        labelSmall = baseTypo.labelSmall.copy(fontFamily = uiSans, fontSize = baseTypo.labelSmall.fontSize * scale),
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
                            if (playerState.currentSong != null) engine.togglePlayPause()
                            else if (allSongs.isNotEmpty()) playFromQueue(allSongs, 0)
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
                    .border(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Frameless Quiet-Luxury Title Bar
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
                                .height(42.dp)
                                .background(MaterialTheme.colorScheme.background)
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(9.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFA9321E)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .border(1.3.dp, Color(0xFFF4E8D4), CircleShape),
                                    )
                                }
                                Text(
                                    text = "简约音乐 · PureMusic",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                if (sleepTimerRemainingSec != null) {
                                    val mins = (sleepTimerRemainingSec ?: 0) / 60
                                    val secs = (sleepTimerRemainingSec ?: 0) % 60
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
                                            .clickable { sleepTimerRemainingSec = null }
                                            .padding(horizontal = 8.dp, vertical = 2.dp),
                                    ) {
                                        Text(
                                            text = String.format(Locale.US, "定时 %02d:%02d", mins, secs),
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                IconButton(
                                    onClick = { showMiniVinylWindow = !showMiniVinylWindow },
                                    modifier = Modifier.size(30.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PictureInPictureAlt,
                                        contentDescription = "迷你黑胶悬浮窗",
                                        tint = if (showMiniVinylWindow) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                                IconButton(
                                    onClick = { mainWindowState.isMinimized = true },
                                    modifier = Modifier.size(30.dp),
                                ) {
                                    Icon(Icons.Rounded.Minimize, contentDescription = "最小化", modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = {
                                        mainWindowState.placement = if (mainWindowState.placement == WindowPlacement.Maximized) {
                                            WindowPlacement.Floating
                                        } else {
                                            WindowPlacement.Maximized
                                        }
                                    },
                                    modifier = Modifier.size(30.dp),
                                ) {
                                    Icon(Icons.Rounded.CropSquare, contentDescription = "最大化", modifier = Modifier.size(15.dp))
                                }
                                IconButton(
                                    onClick = { saveAndExit() },
                                    modifier = Modifier.size(30.dp),
                                ) {
                                    Icon(Icons.Rounded.Close, contentDescription = "关闭", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    HorizontalDivider(thickness = 0.7.dp, color = MaterialTheme.colorScheme.outline)

                    // Main Workspace (Left Sidebar + Content Area)
                    Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        // Left Navigation Sidebar
                        Column(
                            modifier = Modifier
                                .width(206.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                val navItems = listOf(
                                    DesktopTab.LIBRARY to Icons.Rounded.LibraryMusic,
                                    DesktopTab.ALBUMS to Icons.Rounded.Album,
                                    DesktopTab.PLAYLISTS to Icons.Rounded.QueueMusic,
                                    DesktopTab.REPORT to Icons.Rounded.BarChart,
                                    DesktopTab.EQUALIZER to Icons.Rounded.Equalizer,
                                    DesktopTab.SETTINGS to Icons.Rounded.Settings,
                                )
                                navItems.forEach { (tab, icon) ->
                                    val selected = tab == activeTab && activeDetail == null
                                    val itemShape = RoundedCornerShape(10.dp)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(itemShape)
                                            .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent)
                                            .desktopHoverClickable(shape = itemShape) {
                                                activeTab = tab
                                                activeDetail = null
                                            }
                                            .padding(horizontal = 12.dp, vertical = 9.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp),
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = tab.label,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                            }

                            // Quick library action at bottom of sidebar
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.background)
                                        .border(0.75.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                        .clickable { pickAndAddFolder() }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Icon(Icons.Rounded.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("添加音乐文件夹", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Text(
                                    text = if (isScanning) "正在扫描本地曲库..." else "${allSongs.size} 首本地唱片 · ${albums.size} 张专辑",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                )
                            }
                        }

                        VerticalDivider(thickness = 0.7.dp, color = MaterialTheme.colorScheme.outline)

                        // Right Main Content Area
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            val detail = activeDetail
                            if (detail != null) {
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
                                }
                                DesktopSongCollectionDetail(
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
                                    DesktopTab.LIBRARY -> DesktopLibraryPane(
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

                                    DesktopTab.ALBUMS -> DesktopAlbumsPane(
                                        albums = albums,
                                        density = config.layoutDensity,
                                        onSelectAlbum = { activeDetail = DesktopDetailView.AlbumView(it) },
                                    )

                                    DesktopTab.PLAYLISTS -> DesktopPlaylistsPane(
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
                                        onOpenReport = { activeTab = DesktopTab.REPORT },
                                        onCreatePlaylist = { showCreatePlaylistDialog = true },
                                        onOpenPlaylist = { activeDetail = DesktopDetailView.PlaylistView(it.id) },
                                        onRenamePlaylist = { renamingPlaylist = it },
                                        onDeletePlaylist = { pl ->
                                            updateConfig { cfg -> cfg.copy(playlists = cfg.playlists.filterNot { it.id == pl.id }) }
                                        },
                                    )

                                    DesktopTab.REPORT -> DesktopListeningReportPane(
                                        allSongs = allSongs,
                                        mostPlayedSongs = mostPlayedSongs,
                                        artists = artists,
                                        playCounts = config.playCounts,
                                        hourlyPlays = config.hourlyPlays,
                                        currentSongId = playerState.currentSong?.id,
                                        onPlayAll = { if (mostPlayedSongs.isNotEmpty()) playFromQueue(mostPlayedSongs, 0) },
                                        onPlayAt = { idx -> playFromQueue(mostPlayedSongs, idx) },
                                        onSelectArtist = { activeDetail = DesktopDetailView.ArtistView(it) },
                                    )

                                    DesktopTab.EQUALIZER -> DesktopEqualizerPane(
                                        enabled = config.eqEnabled,
                                        bandsDb = config.eqBandsDb,
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

                                    DesktopTab.SETTINGS -> DesktopSettingsPane(
                                        config = config,
                                        sleepTimerRemainingSec = sleepTimerRemainingSec,
                                        onToggleDarkMode = { dm -> updateConfig { it.copy(darkMode = dm) } },
                                        onSelectAccent = { ac -> updateConfig { it.copy(accentColor = ac) } },
                                        onSelectDensity = { dn -> updateConfig { it.copy(layoutDensity = dn) } },
                                        onFontScaleChange = { sc -> updateConfig { it.copy(fontScale = sc) } },
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

                    HorizontalDivider(thickness = 0.7.dp, color = MaterialTheme.colorScheme.outline)

                    // Bottom Docked Playback Bar
                    DesktopBottomPlaybackBar(
                        playerState = playerState,
                        isFavorite = playerState.currentSong?.filePath in config.favoritePaths,
                        onOpenImmersive = { if (playerState.currentSong != null) showImmersiveStage = true },
                        onToggleFavorite = {
                            val path = playerState.currentSong?.filePath ?: return@DesktopBottomPlaybackBar
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
                        onTogglePlayPause = {
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
                        },
                        onSkipNext = { skipNext() },
                        onSeekTo = { engine.seekTo(it) },
                        onVolumeChange = { vol ->
                            playerState = playerState.copy(volume = vol)
                            engine.setVolume(vol)
                            updateConfig { it.copy(volume = vol) }
                        },
                        queueOpen = showQueueDrawer,
                        onToggleQueue = { showQueueDrawer = !showQueueDrawer },
                    )
                }

                // Full-Screen Immersive Stage (All 4 Mobile Scenes + Top-Right 2x2 Floating Switcher)
                AnimatedVisibility(
                    visible = showImmersiveStage && playerState.currentSong != null,
                    enter = fadeIn(tween(240)) + scaleIn(initialScale = 0.97f, animationSpec = tween(240)),
                    exit = fadeOut(tween(180)) + scaleOut(targetScale = 0.98f, animationSpec = tween(180)),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val song = playerState.currentSong
                    if (song != null) {
                        DesktopImmersiveStage(
                            song = song,
                            allSongs = allSongs,
                            playerState = playerState,
                            lyrics = currentLyrics,
                            activeLyricIdx = activeLyricIdx,
                            vinylRotation = vinylAngle.value,
                            sceneStyle = config.sceneStyle,
                            spectrumStyle = config.spectrumStyle,
                            lyricOffsetMs = currentLyricOffset,
                            isDarkTheme = config.darkMode,
                            accentColor = MaterialTheme.colorScheme.primary,
                            onSelectScene = { st -> updateConfig { it.copy(sceneStyle = st) } },
                            onSelectSpectrum = { sp -> updateConfig { it.copy(spectrumStyle = sp) } },
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
                            onClose = { showImmersiveStage = false },
                            onSelectSong = { chosen ->
                                val idx = allSongs.indexOfFirst { it.id == chosen.id }
                                if (idx >= 0) playFromQueue(allSongs, idx)
                            },
                            onTogglePlayPause = { engine.togglePlayPause() },
                            onSkipPrevious = { skipPrevious() },
                            onSkipNext = { skipNext() },
                            onCycleRepeat = {
                                val next = when (playerState.repeatMode) {
                                    RepeatMode.OFF -> RepeatMode.ALL
                                    RepeatMode.ALL -> RepeatMode.ONE
                                    RepeatMode.ONE -> RepeatMode.OFF
                                }
                                playerState = playerState.copy(repeatMode = next)
                            },
                            onToggleShuffle = { playerState = playerState.copy(shuffleEnabled = !playerState.shuffleEnabled) },
                            onSeekTo = { engine.seekTo(it) },
                            onVolumeChange = { vol ->
                                playerState = playerState.copy(volume = vol)
                                engine.setVolume(vol)
                                updateConfig { it.copy(volume = vol) }
                            },
                            isFullscreen = mainWindowState.placement == WindowPlacement.Fullscreen,
                            onToggleFullscreen = {
                                mainWindowState.placement = if (mainWindowState.placement == WindowPlacement.Fullscreen) {
                                    WindowPlacement.Floating
                                } else {
                                    WindowPlacement.Fullscreen
                                }
                            },
                        )
                    }
                }

                // Right-Side Sliding Play Queue Drawer
                AnimatedVisibility(
                    visible = showQueueDrawer && !showImmersiveStage,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(tween(180)),
                    exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(tween(150)),
                    modifier = Modifier.align(Alignment.CenterEnd).padding(top = 42.dp, bottom = 74.dp),
                ) {
                    val queueListState = rememberLazyListState()
                    Box(
                        modifier = Modifier
                            .width(340.dp)
                            .fillMaxHeight()
                            .shadow(18.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.98f))
                            .border(0.75.dp, MaterialTheme.colorScheme.outline),
                    ) {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("播放队列 (${activeQueue.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                IconButton(onClick = { showQueueDrawer = false }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Rounded.Close, contentDescription = "关闭队列", modifier = Modifier.size(16.dp))
                                }
                            }
                            HorizontalDivider(thickness = 0.6.dp, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 8.dp))
                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                LazyColumn(state = queueListState, modifier = Modifier.fillMaxSize()) {
                                    itemsIndexed(activeQueue, key = { i, s -> "${s.id}_$i" }) { idx, s ->
                                        val isCur = idx == currentIndex
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .desktopHoverClickable(shape = RoundedCornerShape(8.dp)) { playFromQueue(activeQueue, idx) }
                                                .background(if (isCur) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f) else Color.Transparent, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            DesktopAlbumCover(filePath = s.filePath, modifier = Modifier.size(34.dp), cornerRadius = 5.dp, iconSize = 14.dp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(s.title, style = MaterialTheme.typography.bodySmall, fontWeight = if (isCur) FontWeight.Bold else FontWeight.Medium, color = if (isCur) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                Text(s.artist, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            }
                                        }
                                    }
                                }
                                DesktopThinScrollbar(listState = queueListState, modifier = Modifier.align(Alignment.CenterEnd))
                            }
                        }
                    }
                }

                // Cold-Start Cinnabar Archival Splash Overlay
                DesktopSplashOverlay(visible = showSplash, darkMode = config.darkMode)
            }

            // Create Playlist Dialog
            if (showCreatePlaylistDialog) {
                var newName by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showCreatePlaylistDialog = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                    title = { Text("新建歌单") },
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
                    dismissButton = {
                        TextButton(onClick = { showCreatePlaylistDialog = false }) { Text("取消") }
                    },
                )
            }

            // Rename Playlist Dialog
            renamingPlaylist?.let { targetPl ->
                var editName by remember(targetPl.id) { mutableStateOf(targetPl.name) }
                AlertDialog(
                    onDismissRequest = { renamingPlaylist = null },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                    title = { Text("重命名歌单") },
                    text = {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            singleLine = true,
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val trimmed = editName.trim()
                                if (trimmed.isNotEmpty()) {
                                    updateConfig { cfg ->
                                        cfg.copy(
                                            playlists = cfg.playlists.map {
                                                if (it.id == targetPl.id) it.copy(name = trimmed) else it
                                            },
                                        )
                                    }
                                }
                                renamingPlaylist = null
                            },
                        ) { Text("保存") }
                    },
                    dismissButton = {
                        TextButton(onClick = { renamingPlaylist = null }) { Text("取消") }
                    },
                )
            }

            // Add Song To Playlist Dialog
            addingSongToPlaylist?.let { targetSong ->
                AlertDialog(
                    onDismissRequest = { addingSongToPlaylist = null },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                    title = { Text("添加到歌单") },
                    text = {
                        if (config.playlists.isEmpty()) {
                            Text("还没有自建歌单，请先在「歌单」页创建一个歌单。")
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                config.playlists.forEach { pl ->
                                    val checked = targetSong.filePath in pl.songPaths
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                updateConfig { cfg ->
                                                    cfg.copy(
                                                        playlists = cfg.playlists.map { item ->
                                                            if (item.id != pl.id) item
                                                            else {
                                                                val nextPaths = if (checked) item.songPaths - targetSong.filePath
                                                                else item.songPaths + targetSong.filePath
                                                                item.copy(songPaths = nextPaths)
                                                            }
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
                    confirmButton = {
                        TextButton(onClick = { addingSongToPlaylist = null }) { Text("完成") }
                    },
                )
            }
        }
    }

    // Independent Always-On-Top Draggable Mini Vinyl Floating Window
    if (showMiniVinylWindow) {
        val miniState = rememberWindowState(
            size = DpSize(336.dp, 108.dp),
            position = WindowPosition(Alignment.BottomEnd),
        )
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
                            .shadow(14.dp, RoundedCornerShape(22.dp))
                            .clip(RoundedCornerShape(22.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.96f))
                            .border(0.8.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DesktopSpinningVinylDisc(
                            filePath = playerState.currentSong?.filePath,
                            rotationDegrees = vinylAngle.value,
                            discSize = 68.dp,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = playerState.currentSong?.title ?: "暂未播放",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = playerState.currentSong?.artist ?: "简约音乐",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                IconButton(onClick = { skipPrevious() }, modifier = Modifier.size(26.dp)) {
                                    Icon(Icons.Rounded.SkipPrevious, contentDescription = "上一首", modifier = Modifier.size(18.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onBackground)
                                        .clickable {
                                            if (playerState.currentSong != null) engine.togglePlayPause()
                                            else if (allSongs.isNotEmpty()) playFromQueue(allSongs, 0)
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                        contentDescription = "播放/暂停",
                                        tint = MaterialTheme.colorScheme.background,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                                IconButton(onClick = { skipNext() }, modifier = Modifier.size(26.dp)) {
                                    Icon(Icons.Rounded.SkipNext, contentDescription = "下一首", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        IconButton(onClick = { showMiniVinylWindow = false }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Rounded.Close, contentDescription = "关闭悬浮窗", modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

// =================================================================================================
// 1. LIBRARY PANE (Songs / Artists / Folders + Search + Sort + Density-Aware Rows)
// =================================================================================================
@Composable
private fun DesktopLibraryPane(
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

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 22.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("曲库", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LibrarySubTab.entries.forEach { tab ->
                        val selected = tab == subTab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) MaterialTheme.colorScheme.onBackground else Color.Transparent)
                                .clickable { onSelectSubTab(tab) }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = tab.label,
                                fontSize = 13.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Search Box
                Row(
                    modifier = Modifier
                        .width(240.dp)
                        .height(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(0.75.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text("搜索歌曲、歌手或专辑", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }
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
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "清除",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp).clickable { onSearchQueryChange("") },
                        )
                    }
                }

                // Sort Selector
                if (subTab == LibrarySubTab.SONGS) {
                    Box {
                        Row(
                            modifier = Modifier
                                .height(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(0.75.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                .clickable { sortMenuExpanded = true }
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(sortOrder.label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
                                .border(0.75.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
                        ) {
                            SortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (order == sortOrder) "✓ ${order.label}" else order.label,
                                            fontWeight = if (order == sortOrder) FontWeight.SemiBold else FontWeight.Normal,
                                        )
                                    },
                                    onClick = {
                                        sortMenuExpanded = false
                                        onSelectSortOrder(order)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (subTab) {
            LibrarySubTab.SONGS -> {
                if (songs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("本地曲库暂无匹配歌曲", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .clickable { onPickFolder() }
                                    .padding(horizontal = 18.dp, vertical = 9.dp),
                            ) {
                                Text("选择音乐文件夹", color = MaterialTheme.colorScheme.onPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                } else {
                    val songsListState = rememberLazyListState()
                    Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(state = songsListState, modifier = Modifier.fillMaxSize().padding(end = 8.dp)) {
                        itemsIndexed(songs, key = { _, s -> s.id }) { idx, song ->
                            DesktopSongRow(
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
                    DesktopThinScrollbar(listState = songsListState, modifier = Modifier.align(Alignment.CenterEnd))
                    }
                }
            }

            LibrarySubTab.ARTISTS -> {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(artists, key = { it.name }) { artist ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(0.75.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                .clickable { onSelectArtist(artist) }
                                .padding(horizontal = 14.dp, vertical = (density.rowPaddingDp + 3).dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(density.artSizeDp.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Rounded.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(artist.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${artist.songs.size} 首作品", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            LibrarySubTab.FOLDERS -> {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(folders, key = { it.path }) { folder ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(0.75.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                .clickable { onSelectFolder(folder) }
                                .padding(horizontal = 14.dp, vertical = (density.rowPaddingDp + 3).dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(density.artSizeDp.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Rounded.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(folder.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${folder.songs.size} 首 · ${folder.path}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopSongRow(
    song: DesktopSong,
    isCurrent: Boolean,
    isFavorite: Boolean,
    density: DesktopLayoutDensity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent)
            .desktopHoverClickable(shape = RoundedCornerShape(10.dp), onClick = onClick)
            .padding(horizontal = 10.dp, vertical = density.rowPaddingDp.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DesktopAlbumCover(
            filePath = song.filePath,
            modifier = Modifier.size(density.artSizeDp.dp),
            cornerRadius = 6.dp,
            iconSize = 18.dp,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (song.isLossless) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .border(0.7.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                    ) {
                        Text(
                            text = if (song.isHiRes) "Hi-Res" else "SQ",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
            Text(
                text = "${song.artist} · ${song.album}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = formatDuration(song.durationMs),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        IconButton(onClick = onToggleFavorite, modifier = Modifier.size(30.dp)) {
            Icon(
                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = "喜欢",
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(17.dp),
            )
        }
        IconButton(onClick = onAddToPlaylist, modifier = Modifier.size(30.dp)) {
            Icon(
                imageVector = Icons.Rounded.PlaylistAdd,
                contentDescription = "添加到歌单",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
}

// =================================================================================================
// 2. ALBUMS PANE (Vinyl Sleeve Grid)
// =================================================================================================
@Composable
private fun DesktopAlbumsPane(
    albums: List<DesktopAlbum>,
    density: DesktopLayoutDensity,
    onSelectAlbum: (DesktopAlbum) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 22.dp)) {
        Text("专辑", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(14.dp))
        val albumsListState = rememberLazyListState()
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val cols = ((maxWidth.value / 176f).toInt()).coerceIn(3, 8)
            val rows = remember(albums, cols) { albums.chunked(cols) }
            LazyColumn(
                state = albumsListState,
                modifier = Modifier.fillMaxSize().padding(end = 10.dp),
                verticalArrangement = Arrangement.spacedBy((density.gridSpacingDp + 6).dp),
            ) {
                items(rows, key = { row -> row.joinToString("|") { it.name } }) { rowAlbums ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(density.gridSpacingDp.dp),
                    ) {
                        rowAlbums.forEach { album ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .desktopHoverClickable(shape = RoundedCornerShape(10.dp)) { onSelectAlbum(album) }
                                    .padding(6.dp),
                            ) {
                                DesktopAlbumCover(
                                    filePath = album.songs.firstOrNull()?.filePath,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .shadow(6.dp, RoundedCornerShape(8.dp)),
                                    cornerRadius = 8.dp,
                                    iconSize = 38.dp,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(album.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${album.artist} · ${album.songs.size} 首", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        repeat(cols - rowAlbums.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
            DesktopThinScrollbar(listState = albumsListState, modifier = Modifier.align(Alignment.CenterEnd))
        }
    }
}

// =================================================================================================
// 3. PLAYLISTS PANE (Mobile v2.1.0 Hero Favorites + Bento Recent/Report + User Playlists Grid)
// =================================================================================================
@Composable
private fun DesktopPlaylistsPane(
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
    val cardShape = RoundedCornerShape(14.dp)
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outline
    val accent = MaterialTheme.colorScheme.primary
    val ink = MaterialTheme.colorScheme.onBackground

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
        val totalMs = playCounts.entries.sumOf { (path, cnt) -> (byPath[path]?.durationMs ?: 210_000L) * cnt }
        val h = totalMs / 3_600_000.0
        when {
            totalMs <= 0L -> "0 分钟"
            h < 1.0 -> "${(totalMs / 60_000L).coerceAtLeast(1L)} 分钟"
            else -> String.format(Locale.US, "%.1f 小时", h)
        }
    }

    val playlistsListState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
        state = playlistsListState,
        modifier = Modifier.fillMaxHeight().widthIn(max = 980.dp).fillMaxWidth().padding(horizontal = 28.dp),
        contentPadding = PaddingValues(vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(density.gridSpacingDp.dp),
    ) {
        item(key = "pl_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("歌单", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent)
                        .clickable(onClick = onCreatePlaylist)
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("新建歌单", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }

        // Hero Card: 我喜欢的音乐
        item(key = "fav_hero") {
            val favThumbs = remember(favoriteSongs) { favoriteSongs.take(3) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(cardShape)
                    .background(surface)
                    .border(0.75.dp, outline, cardShape)
                    .clickable(onClick = onOpenFavorites)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (favThumbs.isNotEmpty()) {
                    Box(modifier = Modifier.width(74.dp).height(58.dp), contentAlignment = Alignment.CenterStart) {
                        favThumbs.reversed().forEachIndexed { revIdx, s ->
                            val layer = favThumbs.lastIndex - revIdx
                            Box(
                                modifier = Modifier
                                    .offset(x = (layer * 10).dp)
                                    .size((54 - layer * 5).dp)
                                    .shadow(if (layer == 0) 6.dp else 2.dp, RoundedCornerShape(6.dp))
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(0.8.dp, surface, RoundedCornerShape(6.dp)),
                            ) {
                                DesktopAlbumCover(filePath = s.filePath, modifier = Modifier.fillMaxSize(), cornerRadius = 6.dp, iconSize = 18.dp)
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.size(54.dp).clip(RoundedCornerShape(10.dp)).background(ink),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Favorite, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("我喜欢的音乐", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(favSub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (favoriteSongs.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ink)
                            .clickable(onClick = onPlayFavorites),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = "播放我喜欢的音乐", tint = MaterialTheme.colorScheme.background, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }

        // Bento Row: 最近播放 + 听歌报告
        item(key = "bento_row") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // 最近播放
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(136.dp)
                        .clip(cardShape)
                        .background(surface)
                        .border(0.75.dp, outline, cardShape)
                        .clickable(onClick = onOpenRecent)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("最近播放", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    val recentThumbs = remember(recentSongs) { recentSongs.take(3) }
                    if (recentThumbs.isNotEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.CenterStart) {
                            recentThumbs.forEachIndexed { idx, s ->
                                Box(
                                    modifier = Modifier
                                        .offset(x = (idx * 34).dp)
                                        .size(if (idx == 0) 46.dp else 40.dp)
                                        .shadow(if (idx == 0) 5.dp else 2.dp, RoundedCornerShape(6.dp))
                                        .clip(RoundedCornerShape(6.dp))
                                        .border(0.8.dp, surface, RoundedCornerShape(6.dp)),
                                ) {
                                    DesktopAlbumCover(filePath = s.filePath, modifier = Modifier.fillMaxSize(), cornerRadius = 6.dp, iconSize = 16.dp)
                                }
                            }
                        }
                    } else {
                        Icon(Icons.Rounded.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f), modifier = Modifier.size(26.dp))
                    }
                    Text(
                        text = if (recentSongs.isNotEmpty()) "${recentSongs.size} 首 · ${recentSongs.first().title}" else "暂无播放记录",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // 听歌报告入口卡
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(136.dp)
                        .clip(cardShape)
                        .background(surface)
                        .border(0.75.dp, outline, cardShape)
                        .clickable(onClick = onOpenReport)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("听歌报告", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(hoursText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("$totalPlays 次播放", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Canvas(modifier = Modifier.width(96.dp).height(44.dp)) {
                            val buckets = FloatArray(12) { b -> ((hourlyPlays[b * 2] ?: 0) + (hourlyPlays[b * 2 + 1] ?: 0)).toFloat() }
                            val maxV = buckets.maxOrNull() ?: 0f
                            val peakIdx = if (maxV > 0f) buckets.indices.maxByOrNull { buckets[it] } ?: -1 else -1
                            val step = size.width / buckets.size
                            val barW = (step * 0.48f).coerceAtLeast(3f)
                            buckets.forEachIndexed { idx, v ->
                                val ratio = if (maxV > 0f) (v / maxV).coerceIn(0.14f, 1f) else (0.18f + (idx % 3) * 0.1f)
                                val h = size.height * ratio
                                val col = when {
                                    maxV <= 0f -> ink.copy(alpha = 0.15f)
                                    idx == peakIdx -> accent
                                    else -> ink.copy(alpha = 0.56f)
                                }
                                drawRoundRect(col, Offset(idx * step + (step - barW) * 0.5f, size.height - h), Size(barW, h), CornerRadius(barW * 0.5f, barW * 0.5f))
                            }
                        }
                    }
                    Text(if (totalPlays > 0) "查看完整聆听档案 →" else "听歌后自动生成", style = MaterialTheme.typography.labelSmall, color = accent)
                }
            }
        }

        item(key = "user_pl_hdr") {
            Text("自建歌单 (${playlists.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp))
        }

        if (playlists.isEmpty()) {
            item(key = "empty_pl") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(cardShape)
                        .background(surface.copy(alpha = 0.7f))
                        .border(0.75.dp, outline, cardShape)
                        .clickable(onClick = onCreatePlaylist)
                        .padding(vertical = 22.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("新建你的第一个自建歌单", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            val rows = playlists.chunked(3)
            items(rows, key = { row -> row.joinToString("_") { it.id } }) { rowItems ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowItems.forEach { pl ->
                        val coverPath = pl.songPaths.firstOrNull { it in songsByPath }
                        var menuOpen by remember { mutableStateOf(false) }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(cardShape)
                                .background(surface)
                                .border(0.75.dp, outline, cardShape)
                                .clickable { onOpenPlaylist(pl) }
                                .padding(12.dp),
                        ) {
                            DesktopAlbumCover(
                                filePath = coverPath,
                                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                                cornerRadius = 8.dp,
                                iconSize = 36.dp,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(pl.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("${pl.songPaths.size} 首", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Box {
                                    IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Rounded.MoreVert, contentDescription = "操作", modifier = Modifier.size(16.dp))
                                    }
                                    DropdownMenu(
                                        expanded = menuOpen,
                                        onDismissRequest = { menuOpen = false },
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
                                            .border(0.75.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
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
    DesktopThinScrollbar(listState = playlistsListState, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

// =================================================================================================
// 4. LISTENING REPORT PANE (Mobile v2.1.0 Archival Dashboard)
// =================================================================================================
@Composable
private fun DesktopListeningReportPane(
    allSongs: List<DesktopSong>,
    mostPlayedSongs: List<DesktopSong>,
    artists: List<DesktopArtist>,
    playCounts: Map<String, Int>,
    hourlyPlays: Map<Int, Int>,
    currentSongId: String?,
    onPlayAll: () -> Unit,
    onPlayAt: (Int) -> Unit,
    onSelectArtist: (DesktopArtist) -> Unit,
) {
    val cardShape = RoundedCornerShape(14.dp)
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
        allSongs.forEach { s ->
            val c = playCounts[s.filePath] ?: 0
            if (c > 0) map[s.artist] = (map[s.artist] ?: 0) + c
        }
        val byName = artists.associateBy { it.name }
        map.entries.sortedByDescending { it.value }.take(5).mapNotNull { (n, c) -> byName[n]?.let { it to c } }
    }

    val reportListState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
        state = reportListState,
        modifier = Modifier.fillMaxHeight().widthIn(max = 980.dp).fillMaxWidth().padding(horizontal = 28.dp),
        contentPadding = PaddingValues(vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "rep_hdr") {
            Text("听歌报告", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        // 3 Archival Metric Cards
        item(key = "rep_metrics") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(
                    totalListeningText to "累计聆听",
                    "${totalPlays}次" to "累计播放",
                    "${playedTracks}首" to "听过曲目",
                ).forEach { (v, l) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(cardShape)
                            .background(surface)
                            .border(0.75.dp, outline, cardShape)
                            .padding(vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(v, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = ink)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(l, style = MaterialTheme.typography.bodySmall, color = muted)
                    }
                }
            }
        }

        // 24h Rhythm Bar Chart
        item(key = "rep_24h") {
            val maxH = remember(hourlyPlays) { hourlyPlays.values.maxOrNull() ?: 0 }
            val peakH = remember(hourlyPlays) { hourlyPlays.entries.maxByOrNull { it.value }?.takeIf { it.value > 0 }?.key }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(cardShape)
                    .background(surface)
                    .border(0.75.dp, outline, cardShape)
                    .padding(18.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("24h 聆听节律", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    if (peakH != null) {
                        Text("峰值时段 · ${peakH}:00", style = MaterialTheme.typography.labelSmall, color = accent, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                if (maxH > 0) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(110.dp)) {
                        val step = size.width / 24f
                        val barW = (step * 0.42f).coerceIn(3f, 10f)
                        for (h in 0 until 24) {
                            val c = hourlyPlays[h] ?: 0
                            val ratio = if (c > 0) (c.toFloat() / maxH).coerceIn(0.10f, 1f) else 0.04f
                            val bh = size.height * ratio
                            val col = when {
                                c == 0 -> ink.copy(alpha = 0.10f)
                                h == peakH -> accent
                                else -> ink.copy(alpha = 0.72f)
                            }
                            drawRoundRect(col, Offset(h * step + (step - barW) * 0.5f, size.height - bh), Size(barW, bh), CornerRadius(barW * 0.5f, barW * 0.5f))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("00:00", "06:00", "12:00", "18:00", "23:00").forEach { Text(it, fontSize = 11.sp, color = muted) }
                    }
                } else {
                    Text("播放本地唱片后将自动绘制你的 24 小时聆听节律", style = MaterialTheme.typography.bodySmall, color = muted, modifier = Modifier.padding(vertical = 18.dp))
                }
            }
        }

        // Top Played Songs + Top Artists Side-by-Side on Desktop Widescreen
        item(key = "rep_bottom_split") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(
                    modifier = Modifier
                        .weight(1.45f)
                        .clip(cardShape)
                        .background(surface)
                        .border(0.75.dp, outline, cardShape)
                        .padding(18.dp),
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("最常循环曲目", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        if (topSongs.isNotEmpty()) {
                            Text(
                                text = "播放全部",
                                style = MaterialTheme.typography.labelMedium,
                                color = accent,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onPlayAll).padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (topSongs.isEmpty()) {
                        Text("暂无常听曲目记录", style = MaterialTheme.typography.bodySmall, color = muted, modifier = Modifier.padding(vertical = 12.dp))
                    } else {
                        topSongs.forEachIndexed { idx, s ->
                            val cnt = playCounts[s.filePath] ?: 1
                            val isCur = s.id == currentSongId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onPlayAt(idx) }
                                    .padding(vertical = 7.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(String.format(Locale.US, "%02d", idx + 1), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (idx == 0) accent else muted, modifier = Modifier.width(26.dp))
                                DesktopAlbumCover(filePath = s.filePath, modifier = Modifier.size(38.dp), cornerRadius = 6.dp, iconSize = 16.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(s.title, style = MaterialTheme.typography.bodyMedium, fontWeight = if (isCur) FontWeight.Bold else FontWeight.Medium, color = if (isCur) accent else ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(s.artist, style = MaterialTheme.typography.labelSmall, color = muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Box(
                                    modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 8.dp, vertical = 3.dp),
                                ) {
                                    Text("×$cnt", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (idx == 0) accent else muted)
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(cardShape)
                        .background(surface)
                        .border(0.75.dp, outline, cardShape)
                        .padding(18.dp),
                ) {
                    Text("偏爱歌手", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (topArtists.isEmpty()) {
                        Text("暂无歌手统计", style = MaterialTheme.typography.bodySmall, color = muted)
                    } else {
                        val maxA = topArtists.first().second.coerceAtLeast(1)
                        topArtists.forEachIndexed { idx, (artist, cnt) ->
                            val frac = (cnt.toFloat() / maxA).coerceIn(0.08f, 1f)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onSelectArtist(artist) }
                                    .padding(vertical = 7.dp),
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(artist.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                    Text("$cnt 次", style = MaterialTheme.typography.labelSmall, color = muted)
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Box(modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)) {
                                    Box(modifier = Modifier.fillMaxWidth(frac).fillMaxHeight().clip(CircleShape).background(if (idx == 0) accent else ink.copy(alpha = 0.65f)))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    DesktopThinScrollbar(listState = reportListState, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

// =================================================================================================
// 5. 5-BAND ANTI-CLIPPING EQUALIZER PANE
// =================================================================================================
@Composable
private fun DesktopEqualizerPane(
    enabled: Boolean,
    bandsDb: List<Float>,
    onToggleEnabled: (Boolean) -> Unit,
    onBandChange: (Int, Float) -> Unit,
    onApplyPreset: (EqPreset) -> Unit,
) {
    val cardShape = RoundedCornerShape(14.dp)
    val labels = listOf("60Hz", "230Hz", "910Hz", "3.6kHz", "14kHz")
    val maxBoost = bandsDb.maxOrNull()?.coerceAtLeast(0f) ?: 0f
    val headroomDb = if (enabled && maxBoost > 0.5f) -(maxBoost * 0.85f) else 0f

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(
        modifier = Modifier.fillMaxHeight().widthIn(max = 980.dp).fillMaxWidth().padding(horizontal = 28.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("音频均衡器", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = if (enabled) {
                        "5 段实时 IIR 滤波器已开启 · 自动抗削波前级 ${String.format(Locale.US, "%.1f", headroomDb)}dB"
                    } else {
                        "关闭状态：100% 原始 PCM 位透明直通"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggleEnabled,
                colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary),
            )
        }

        // 6 Presets Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EqPreset.entries.forEach { preset ->
                val isMatch = enabled && preset.gainsDb.indices.all { abs((bandsDb.getOrNull(it) ?: 0f) - preset.gainsDb[it]) < 0.25f }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isMatch) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface)
                        .border(0.75.dp, if (isMatch) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                        .clickable { onApplyPreset(preset) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = preset.label,
                        fontSize = 13.sp,
                        fontWeight = if (isMatch) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isMatch) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        // 5-Band Sliders Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(0.75.dp, MaterialTheme.colorScheme.outline, cardShape)
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            labels.forEachIndexed { idx, freq ->
                val gain = (bandsDb.getOrNull(idx) ?: 0f).coerceIn(-10f, 10f)
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(freq, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(64.dp))
                    DesktopSleekSlider(
                        value = gain,
                        onValueChange = { onBandChange(idx, it) },
                        valueRange = -10f..10f,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = String.format(Locale.US, "%+.1f dB", gain),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(64.dp),
                    )
                }
            }
        }
    }
    }
}

// =================================================================================================
// 6. SETTINGS PANE (6 Book-Spine Accents, Paper/Ink Mode, Density, Font Scale, Sleep Timer, Folders)
// =================================================================================================
@Composable
private fun DesktopSettingsPane(
    config: DesktopConfig,
    sleepTimerRemainingSec: Int?,
    onToggleDarkMode: (Boolean) -> Unit,
    onSelectAccent: (DesktopAccentColor) -> Unit,
    onSelectDensity: (DesktopLayoutDensity) -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onSetSleepTimerMin: (Int) -> Unit,
    onAddFolder: () -> Unit,
    onRemoveFolder: (String) -> Unit,
    onRescan: () -> Unit,
) {
    var selectedSection by remember { mutableStateOf(0) }
    val sections = listOf("外观与字体", "音乐文件夹", "播放与定时", "关于")
    val cardShape = RoundedCornerShape(16.dp)
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outline
    val accent = MaterialTheme.colorScheme.primary
    val ink = MaterialTheme.colorScheme.onBackground
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        val settingsListState = rememberLazyListState()
        LazyColumn(
            state = settingsListState,
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 860.dp)
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            contentPadding = PaddingValues(vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item(key = "set_hdr") {
                Text("设置", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = ink)
            }

            // Sleek Horizontal Segmented Pill Bar (matching reference #6)
            item(key = "set_tabs") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    sections.forEachIndexed { idx, label ->
                        val sel = idx == selectedSection
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) surface else Color.Transparent)
                                .then(if (sel) Modifier.border(0.7.dp, outline, RoundedCornerShape(10.dp)) else Modifier)
                                .clickable { selectedSection = idx }
                                .padding(horizontal = 18.dp, vertical = 9.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (sel) accent else muted,
                            )
                        }
                    }
                }
            }

            when (selectedSection) {
                0 -> {
                    item(key = "sec_appearance") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(cardShape)
                                .background(surface)
                                .border(0.75.dp, outline, cardShape)
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text("外观与字体", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ink)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("选择后自动保存，让阅读和聆听更自在。", style = MaterialTheme.typography.bodySmall, color = muted)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(0.75.dp, outline, RoundedCornerShape(10.dp))
                                        .clickable {
                                            onToggleDarkMode(false)
                                            onSelectAccent(DesktopAccentColor.CINNABAR)
                                            onSelectDensity(DesktopLayoutDensity.STANDARD)
                                            onFontScaleChange(1.0f)
                                        }
                                        .padding(horizontal = 14.dp, vertical = 7.dp),
                                ) {
                                    Text("恢复默认", fontSize = 12.sp, color = ink)
                                }
                            }

                            // Signature Live Appearance & Typography Preview Card (with left accent bar)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.background)
                                    .border(0.75.dp, outline, RoundedCornerShape(12.dp)),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(132.dp)
                                        .background(accent),
                                )
                                Column(
                                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text("简约音乐 · 外观预览", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = accent)
                                    Text("把喜欢的音乐，留在身边。", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ink)
                                    Text("让晚风陪着音乐", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = ink)
                                    Text("Simple moments, beautiful music.", style = MaterialTheme.typography.bodySmall, color = muted)
                                }
                            }

                            // Theme Mode Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text("界面主题", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = ink)
                                    Text("应用于音乐库、歌单和设置；沉浸场景保留各自材质与氛围。", style = MaterialTheme.typography.bodySmall, color = muted)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(false to "宣纸浅色", true to "夜读深色").forEach { (dm, lbl) ->
                                        val sel = config.darkMode == dm
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (sel) ink else MaterialTheme.colorScheme.surfaceVariant)
                                                .clickable { onToggleDarkMode(dm) }
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                        ) {
                                            Text(lbl, fontSize = 12.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal, color = if (sel) MaterialTheme.colorScheme.background else muted)
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(thickness = 0.6.dp, color = outline)

                            // Accent Color Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text("书脊强调色", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = ink)
                                    Text("用于导航选中状态、主按钮与进度高亮。", style = MaterialTheme.typography.bodySmall, color = muted)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    DesktopAccentColor.entries.forEach { ac ->
                                        val sel = ac == config.accentColor
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(ac.hex))
                                                .border(if (sel) 2.dp else 0.dp, if (sel) ink else Color.Transparent, RoundedCornerShape(8.dp))
                                                .clickable { onSelectAccent(ac) },
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            if (sel) Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(thickness = 0.6.dp, color = outline)

                            // Density Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text("列表间距与排版密度", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = ink)
                                    Text("调整音乐列表、专辑网格与歌单卡片的留白节奏。", style = MaterialTheme.typography.bodySmall, color = muted)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DesktopLayoutDensity.entries.forEach { d ->
                                        val sel = d == config.layoutDensity
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (sel) ink else MaterialTheme.colorScheme.surfaceVariant)
                                                .clickable { onSelectDensity(d) }
                                                .padding(horizontal = 14.dp, vertical = 8.dp),
                                        ) {
                                            Text(d.label, fontSize = 12.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal, color = if (sel) MaterialTheme.colorScheme.background else muted)
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(thickness = 0.6.dp, color = outline)

                            // Font Scale Row
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.width(220.dp)) {
                                    Text("全局字号缩放", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = ink)
                                    Text("当前 ${(config.fontScale * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = accent)
                                }
                                DesktopSleekSlider(
                                    value = config.fontScale,
                                    onValueChange = onFontScaleChange,
                                    valueRange = 0.85f..1.25f,
                                    activeColor = accent,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }

                1 -> {
                    item(key = "sec_folders") {
                        Column(
                            modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.75.dp, outline, cardShape).padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("本地音乐文件夹", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ink)
                                    Text("支持 MP3 / FLAC / WAV / AIFF 无损与高解析音频及内嵌/同目录 LRC 歌词。", style = MaterialTheme.typography.bodySmall, color = muted)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = onRescan) {
                                        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("重新扫描")
                                    }
                                    TextButton(onClick = onAddFolder) {
                                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("添加目录")
                                    }
                                }
                            }
                            HorizontalDivider(thickness = 0.6.dp, color = outline)
                            if (config.folders.isEmpty()) {
                                Text("暂未添加本地音乐目录", style = MaterialTheme.typography.bodySmall, color = muted)
                            } else {
                                config.folders.forEach { folderPath ->
                                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(folderPath, style = MaterialTheme.typography.bodyMedium, color = ink, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                        IconButton(onClick = { onRemoveFolder(folderPath) }) {
                                            Icon(Icons.Rounded.DeleteOutline, contentDescription = "移除目录", modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    item(key = "sec_playback") {
                        Column(
                            modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.75.dp, outline, cardShape).padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("睡眠定时暂停", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ink)
                                    Text("到达设定时间后自动停止播放并静音释放音频通道。", style = MaterialTheme.typography.bodySmall, color = muted)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(0 to "关闭", 15 to "15分钟", 30 to "30分钟", 60 to "60分钟").forEach { (mins, lbl) ->
                                        val active = (mins == 0 && sleepTimerRemainingSec == null) || (mins > 0 && sleepTimerRemainingSec != null && ((sleepTimerRemainingSec + 59) / 60) in (mins - 14)..mins)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (active) accent.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant)
                                                .clickable { onSetSleepTimerMin(mins) }
                                                .padding(horizontal = 14.dp, vertical = 8.dp),
                                        ) {
                                            Text(lbl, fontSize = 12.sp, color = if (active) accent else muted, fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal)
                                        }
                                    }
                                }
                            }
                            HorizontalDivider(thickness = 0.6.dp, color = outline)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("快捷键一览", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ink)
                                Text("• Space：播放 / 暂停     • F11：真全屏切换     • Esc：退出全屏 / 队列 / 详情     • ← / →：全屏快退 / 快进 5 秒", style = MaterialTheme.typography.bodySmall, color = muted)
                            }
                        }
                    }
                }

                else -> {
                    item(key = "sec_about") {
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(cardShape).background(surface).border(0.75.dp, outline, cardShape).padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("简约音乐 · PureMusic Desktop", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ink)
                                Text("回归音乐本身。纯粹、克制、安静陪伴每一张本地唱片。", style = MaterialTheme.typography.bodySmall, color = muted)
                            }
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(accent.copy(alpha = 0.14f)).padding(horizontal = 12.dp, vertical = 6.dp),
                            ) {
                                Text("v2.3.1", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = accent)
                            }
                        }
                    }
                }
            }
        }
        DesktopThinScrollbar(listState = settingsListState, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

// =================================================================================================
// 7. COLLECTION DETAIL VIEW (Album / Artist / Folder / Playlist / Favorites / Recent)
// =================================================================================================
@Composable
private fun DesktopSongCollectionDetail(
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
    val detailListState = rememberLazyListState()
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 22.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            if (songs.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.onBackground)
                        .clickable { onPlayAt(0) }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.background, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("播放全部", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.background)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
        LazyColumn(state = detailListState, modifier = Modifier.fillMaxSize().padding(end = 8.dp)) {
            itemsIndexed(songs, key = { _, s -> s.id }) { idx, song ->
                DesktopSongRow(
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
        DesktopThinScrollbar(listState = detailListState, modifier = Modifier.align(Alignment.CenterEnd))
        }
    }
}

// =================================================================================================
// 8. BOTTOM DOCKED PLAYBACK BAR
// =================================================================================================
@Composable
private fun DesktopBottomPlaybackBar(
    playerState: DesktopPlayerState,
    isFavorite: Boolean,
    onOpenImmersive: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onSkipPrevious: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onVolumeChange: (Float) -> Unit,
    queueOpen: Boolean = false,
    onToggleQueue: () -> Unit = {},
) {
    val song = playerState.currentSong
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: Cover + Song Info (click to open Full-Screen Immersive Stage)
        Row(
            modifier = Modifier
                .width(270.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable(enabled = song != null, onClick = onOpenImmersive)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DesktopAlbumCover(filePath = song?.filePath, modifier = Modifier.size(48.dp), cornerRadius = 8.dp, iconSize = 20.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(song?.title ?: "暂未播放", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(song?.artist ?: "选择一首本地唱片开始聆听", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (song != null) {
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(30.dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "喜欢",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        // Center: Controls + Thin Seek Slider
        Column(modifier = Modifier.weight(1f).padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IconButton(onClick = onToggleShuffle, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Rounded.Shuffle, contentDescription = "随机", tint = if (playerState.shuffleEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onSkipPrevious, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Rounded.SkipPrevious, contentDescription = "上一首", modifier = Modifier.size(21.dp))
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onBackground)
                        .clickable(onClick = onTogglePlayPause),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "播放/暂停",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.size(21.dp),
                    )
                }
                IconButton(onClick = onSkipNext, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Rounded.SkipNext, contentDescription = "下一首", modifier = Modifier.size(21.dp))
                }
                IconButton(onClick = onCycleRepeat, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (playerState.repeatMode == RepeatMode.ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                        contentDescription = "循环模式",
                        tint = if (playerState.repeatMode != RepeatMode.OFF) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth().height(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(formatDuration(playerState.positionMs), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(36.dp))
                DesktopSleekSlider(
                    value = if (playerState.durationMs > 0L) (playerState.positionMs.toFloat() / playerState.durationMs).coerceIn(0f, 1f) else 0f,
                    onValueChange = { frac -> if (playerState.durationMs > 0L) onSeekTo((frac * playerState.durationMs).toLong()) },
                    modifier = Modifier.weight(1f),
                )
                Text(formatDuration(playerState.durationMs), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
            }
        }

        // Right: Volume + Play Queue Drawer + Full-Screen Immersive Trigger
        Row(modifier = Modifier.width(246.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
            Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = "音量", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
            DesktopSleekSlider(
                value = playerState.volume,
                onValueChange = onVolumeChange,
                valueRange = 0f..1f,
                modifier = Modifier.width(100.dp).padding(horizontal = 6.dp),
            )
            IconButton(onClick = onToggleQueue, modifier = Modifier.size(32.dp).pointerHoverIcon(PointerIcon.Hand)) {
                Icon(
                    imageVector = Icons.Rounded.QueueMusic,
                    contentDescription = "播放队列",
                    tint = if (queueOpen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
            if (song != null) {
                IconButton(onClick = onOpenImmersive, modifier = Modifier.size(32.dp).pointerHoverIcon(PointerIcon.Hand)) {
                    Icon(Icons.Rounded.OpenInFull, contentDescription = "沉浸式全屏", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(17.dp))
                }
            }
        }
    }
}

// =================================================================================================
// 9. FULL-SCREEN IMMERSIVE STAGE (4 Mobile Scenes + Top-Right 2x2 Floating Menu + 60fps FFT Spectrum)
// =================================================================================================
@Composable
private fun DesktopImmersiveStage(
    song: DesktopSong,
    allSongs: List<DesktopSong>,
    playerState: DesktopPlayerState,
    lyrics: List<LyricLine>,
    activeLyricIdx: Int,
    vinylRotation: Float,
    sceneStyle: DesktopSceneStyle,
    spectrumStyle: DesktopSpectrumStyle,
    lyricOffsetMs: Long,
    isDarkTheme: Boolean,
    accentColor: Color,
    onSelectScene: (DesktopSceneStyle) -> Unit,
    onSelectSpectrum: (DesktopSpectrumStyle) -> Unit,
    onAdjustLyricOffset: (Long) -> Unit,
    onResetLyricOffset: () -> Unit,
    onClose: () -> Unit,
    onSelectSong: (DesktopSong) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleShuffle: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onVolumeChange: (Float) -> Unit,
    isFullscreen: Boolean = false,
    onToggleFullscreen: () -> Unit = {},
) {
    val isDarkScene = when (sceneStyle) {
        DesktopSceneStyle.VINYL_STUDY -> isDarkTheme
        DesktopSceneStyle.VINYL_LIGHTFIELD,
        DesktopSceneStyle.POSTER_WALL,
        DesktopSceneStyle.VINTAGE_HIFI,
        DesktopSceneStyle.SPATIAL_GALLERY,
        -> true
    }
    val extractedMood = rememberSongDominantColor(song.filePath)
    val animatedMood by animateColorAsState(
        targetValue = extractedMood,
        animationSpec = tween(650),
        label = "immersiveMoodColor",
    )
    val bgBrush = when (sceneStyle) {
        DesktopSceneStyle.VINYL_STUDY -> Brush.verticalGradient(listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.background))
        DesktopSceneStyle.VINYL_LIGHTFIELD -> Brush.verticalGradient(
            listOf(
                lerp(Color(0xFF1D1615), animatedMood, 0.52f),
                lerp(Color(0xFF140F0F), animatedMood, 0.34f),
                Color(0xFF0C0A0A),
            ),
        )
        DesktopSceneStyle.POSTER_WALL -> Brush.verticalGradient(listOf(Color(0xFF0E1116), Color(0xFF0A0C10)))
        DesktopSceneStyle.VINTAGE_HIFI -> Brush.verticalGradient(listOf(Color(0xFF534A41), Color(0xFF3C352E), Color(0xFF221E1A)))
        DesktopSceneStyle.SPATIAL_GALLERY -> Brush.verticalGradient(
            listOf(
                lerp(Color(0xFF181214), animatedMood, 0.42f),
                lerp(Color(0xFF100C0E), animatedMood, 0.24f),
                Color(0xFF090809),
            ),
        )
    }
    val primaryInk = if (isDarkScene) Color(0xFFF5F0E6) else MaterialTheme.colorScheme.onBackground
    val secondaryInk = if (isDarkScene) Color(0xFFB5ACA0) else MaterialTheme.colorScheme.onSurfaceVariant
    val activeAccent = if (isDarkScene) Color(0xFFE5C487) else accentColor
    var menuExpanded by remember { mutableStateOf(false) }
    var lastPointerMoveMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var controlsVisible by remember { mutableStateOf(true) }

    LaunchedEffect(lastPointerMoveMs, menuExpanded) {
        controlsVisible = true
        if (!menuExpanded) {
            delay(3200)
            controlsVisible = false
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
            }
            .padding(
                horizontal = if (sceneStyle == DesktopSceneStyle.POSTER_WALL) 16.dp else 40.dp,
                vertical = 18.dp,
            ),
    ) {
        // Auto-hiding Minimal Top Bar (fades out after 3s idle for 100% pure art; glides back on mouse move)
        Box(modifier = Modifier.fillMaxWidth().height(42.dp)) {
        AnimatedVisibility(
            visible = controlsVisible || menuExpanded,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(420)),
            modifier = Modifier.fillMaxSize(),
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose, modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回曲库", tint = primaryInk)
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDarkScene) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.045f))
                            .border(0.7.dp, primaryInk.copy(alpha = 0.14f), RoundedCornerShape(12.dp))
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable { menuExpanded = true }
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(sceneStyle.label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = primaryInk)
                        Icon(Icons.Rounded.MoreHoriz, contentDescription = "切换场景与频谱", tint = primaryInk, modifier = Modifier.size(16.dp))
                    }
                    DesktopImmersiveControlMenu(
                    expanded = menuExpanded,
                    isDarkSurface = isDarkScene,
                    currentScene = sceneStyle,
                    currentSpectrum = spectrumStyle,
                    lyricOffsetMs = lyricOffsetMs,
                    accentColor = accentColor,
                    onDismiss = { menuExpanded = false },
                    onSelectScene = onSelectScene,
                    onSelectSpectrum = onSelectSpectrum,
                    onAdjustLyricOffset = onAdjustLyricOffset,
                    onResetLyricOffset = onResetLyricOffset,
                )
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDarkScene) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.045f))
                        .border(0.7.dp, primaryInk.copy(alpha = 0.14f), RoundedCornerShape(10.dp))
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable(onClick = onToggleFullscreen),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isFullscreen) Icons.Rounded.FullscreenExit else Icons.Rounded.Fullscreen,
                        contentDescription = "F11 真全屏",
                        tint = primaryInk,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
        }
        }

        // Main Widescreen Stage (5 Flagship Scenes)
        AnimatedContent(
            targetState = sceneStyle,
            transitionSpec = {
                (fadeIn(tween(240)) + scaleIn(initialScale = 0.97f, animationSpec = tween(240))) togetherWith
                    (fadeOut(tween(180)) + scaleOut(targetScale = 1.02f, animationSpec = tween(180)))
            },
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) { targetScene ->
            when (targetScene) {
                // 1. 动感黑胶书房 (Strict 16:9 proportional scale matching image_7d991e.jpg Top-Left!)
                DesktopSceneStyle.VINYL_STUDY -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val stageMaxW = (maxHeight * 1.82f).coerceAtMost(maxWidth)
                        val sleeveSize = min(maxHeight * 0.66f, stageMaxW * 0.33f).coerceIn(220.dp, 540.dp)
                        val discSize = sleeveSize * 0.90f
                        val lyricSp = (maxHeight.value * 0.068f).coerceIn(34f, 62f)
                        Row(
                            modifier = Modifier
                                .width(stageMaxW)
                                .fillMaxHeight()
                                .padding(horizontal = (stageMaxW * 0.04f).coerceIn(16.dp, 56.dp)),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy((stageMaxW * 0.055f).coerceIn(32.dp, 76.dp)),
                        ) {
                            Column(
                                modifier = Modifier.width(sleeveSize + discSize * 0.35f),
                                horizontalAlignment = Alignment.Start,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(sleeveSize + discSize * 0.35f)
                                        .height(sleeveSize)
                                        .clickable(onClick = onTogglePlayPause),
                                    contentAlignment = Alignment.CenterStart,
                                ) {
                                    DesktopSpinningVinylDisc(
                                        filePath = song.filePath,
                                        rotationDegrees = vinylRotation,
                                        discSize = discSize,
                                        modifier = Modifier.align(Alignment.CenterEnd),
                                    )
                                    // Archival matte cream physical sleeve frame with recessed artwork
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .size(sleeveSize)
                                            .shadow(24.dp, RoundedCornerShape(4.dp))
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isDarkScene) Color(0xFF1E1D1B) else Color(0xFFF6F2E9))
                                            .border(0.75.dp, primaryInk.copy(alpha = 0.12f), RoundedCornerShape(4.dp)),
                                    ) {
                                        DesktopAlbumCover(
                                            filePath = song.filePath,
                                            modifier = Modifier.fillMaxSize(),
                                            cornerRadius = 4.dp,
                                            iconSize = 54.dp,
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(song.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = primaryInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(song.artist, style = MaterialTheme.typography.bodyMedium, color = secondaryInk, maxLines = 1)
                                Spacer(modifier = Modifier.height(6.dp))
                                DesktopAudioSpecStamp(song = song, isDarkSurface = isDarkScene, accentColor = activeAccent)
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
                                maxActiveSp = lyricSp,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                // 2. 黑胶光场 (Strict 16:9 proportional scale matching image_7d991e.jpg Top-Right!)
                DesktopSceneStyle.VINYL_LIGHTFIELD -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val stageMaxW = (maxHeight * 1.82f).coerceAtMost(maxWidth)
                        val giantDisc = min(maxHeight * 0.80f, stageMaxW * 0.45f).coerceIn(280.dp, 680.dp)
                        val lyricSp = (maxHeight.value * 0.056f).coerceIn(28f, 54f)
                        val playBtnSize = (giantDisc * 0.145f).coerceIn(54.dp, 78.dp)
                        Row(
                            modifier = Modifier
                                .width(stageMaxW)
                                .fillMaxHeight()
                                .padding(horizontal = (stageMaxW * 0.03f).coerceIn(14.dp, 44.dp)),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy((stageMaxW * 0.045f).coerceIn(28.dp, 68.dp)),
                        ) {
                            Box(
                                modifier = Modifier.weight(1.12f).fillMaxHeight(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    DesktopSpinningVinylDisc(
                                        filePath = song.filePath,
                                        rotationDegrees = vinylRotation,
                                        discSize = giantDisc,
                                        modifier = Modifier.clickable(onClick = onTogglePlayPause),
                                    )
                                    DesktopTonearm(
                                        isPlaying = playerState.isPlaying,
                                        length = giantDisc * 0.72f,
                                        modifier = Modifier.align(Alignment.TopEnd).offset(x = giantDisc * 0.15f, y = (-6).dp),
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.weight(0.95f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Text(song.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = primaryInk, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(song.artist, style = MaterialTheme.typography.bodyMedium, color = secondaryInk, textAlign = TextAlign.Center, maxLines = 1)
                                Spacer(modifier = Modifier.height(10.dp))
                                DesktopAudioSpecStamp(song = song, isDarkSurface = true, accentColor = activeAccent)
                                Spacer(modifier = Modifier.height(26.dp))
                                DesktopEditorialLyrics(
                                    lyrics = lyrics,
                                    activeIdx = activeLyricIdx,
                                    activeColor = primaryInk,
                                    accentColor = activeAccent,
                                    secondaryColor = secondaryInk,
                                    mutedColor = secondaryInk,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    textAlign = TextAlign.Center,
                                    maxActiveSp = lyricSp,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Spacer(modifier = Modifier.height(28.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                                ) {
                                    IconButton(onClick = onSkipPrevious) { Icon(Icons.Rounded.SkipPrevious, contentDescription = "上一首", tint = primaryInk, modifier = Modifier.size(28.dp)) }
                                    Box(
                                        modifier = Modifier
                                            .size(playBtnSize)
                                            .shadow(14.dp, CircleShape)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF3EFE6))
                                            .clickable(onClick = onTogglePlayPause),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                            contentDescription = "播放/暂停",
                                            tint = Color(0xFF1D1A17),
                                            modifier = Modifier.size(playBtnSize * 0.50f),
                                        )
                                    }
                                    IconButton(onClick = onSkipNext) { Icon(Icons.Rounded.SkipNext, contentDescription = "下一首", tint = primaryInk, modifier = Modifier.size(28.dp)) }
                                }
                            }
                        }
                    }
                }

                // 3. 唱片海报墙 (Reference #3: Full-Bleed Clickable Album Wall + Giant Centered Vinyl)
                DesktopSceneStyle.POSTER_WALL -> {
                    DesktopPosterWallStage(
                        currentSong = song,
                        allSongs = allSongs,
                        vinylRotation = vinylRotation,
                        lyrics = lyrics,
                        activeLyricIdx = activeLyricIdx,
                        accentColor = activeAccent,
                        onSelectSong = onSelectSong,
                        onTogglePlayPause = onTogglePlayPause,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                // 4. 复古音响 (Reference #4: Sunlit Upper Showcase + Walnut Brushed-Aluminum Dual-VU Stereo Receiver)
                DesktopSceneStyle.VINTAGE_HIFI -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Soft warm afternoon sunlit window rays on upper right studio wall
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    0.0f to Color(0xFFFFE1B0).copy(alpha = 0.22f),
                                    0.55f to Color(0xFFE0B47A).copy(alpha = 0.08f),
                                    1.0f to Color.Transparent,
                                    center = Offset(size.width * 0.84f, size.height * 0.20f),
                                    radius = size.width * 0.48f,
                                ),
                                radius = size.width * 0.48f,
                                center = Offset(size.width * 0.84f, size.height * 0.20f),
                            )
                        }
                    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val stageW = (maxHeight * 1.85f).coerceAtMost(maxWidth)
                        val coverSz = (maxHeight * 0.36f).coerceIn(190.dp, 320.dp)
                        val deckH = (maxHeight * 0.38f).coerceIn(228.dp, 310.dp)
                        val lyricSp = (maxHeight.value * 0.048f).coerceIn(28f, 44f)
                        Column(
                            modifier = Modifier.width(stageW).fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(36.dp),
                            ) {
                                DesktopAlbumCover(
                                    filePath = song.filePath,
                                    modifier = Modifier
                                        .size(coverSz)
                                        .shadow(22.dp, RoundedCornerShape(4.dp))
                                        .clickable(onClick = onTogglePlayPause),
                                    cornerRadius = 4.dp,
                                    iconSize = 54.dp,
                                )
                                Column(modifier = Modifier.width(220.dp)) {
                                    Text(song.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFFF6F1E6), maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(song.artist, style = MaterialTheme.typography.bodyLarge, color = Color(0xFFC4BBB0), maxLines = 1)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    DesktopAudioSpecStamp(song = song, isDarkSurface = true, accentColor = Color(0xFFE5C487))
                                }
                                DesktopEditorialLyrics(
                                    lyrics = lyrics,
                                    activeIdx = activeLyricIdx,
                                    activeColor = Color(0xFFF6F1E6),
                                    accentColor = Color(0xFFE5C487),
                                    secondaryColor = Color(0xFFC8BFB4),
                                    mutedColor = Color(0xFFA69D92),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    textAlign = TextAlign.Center,
                                    maxActiveSp = lyricSp,
                                    modifier = Modifier.weight(1f),
                                )
                            }

                            DesktopVintageReceiverDeck(
                                playerState = playerState,
                                onTogglePlayPause = onTogglePlayPause,
                                onSkipPrevious = onSkipPrevious,
                                onSkipNext = onSkipNext,
                                onCycleRepeat = onCycleRepeat,
                                onToggleShuffle = onToggleShuffle,
                                onSeekTo = onSeekTo,
                                onVolumeChange = onVolumeChange,
                                deckHeight = deckH,
                            )
                        }
                    }
                    }
                }

                // 5. 空间唱片廊 (Flagship 3D Metallic-Spined Stepped Corridor)
                DesktopSceneStyle.SPATIAL_GALLERY -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        DesktopSpatialRecordGallery(
                            currentSong = song,
                            allSongs = allSongs,
                            onTogglePlayPause = onTogglePlayPause,
                            onSkipPrevious = onSkipPrevious,
                            onSkipNext = onSkipNext,
                            modifier = Modifier.weight(1.3f).fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(song.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = primaryInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(song.artist, style = MaterialTheme.typography.bodyMedium, color = secondaryInk, maxLines = 1)
                        Spacer(modifier = Modifier.height(6.dp))
                        DesktopAudioSpecStamp(song = song, isDarkSurface = true, accentColor = activeAccent)
                        Spacer(modifier = Modifier.height(10.dp))
                        DesktopEditorialLyrics(
                            lyrics = lyrics,
                            activeIdx = activeLyricIdx,
                            activeColor = primaryInk,
                            accentColor = activeAccent,
                            secondaryColor = secondaryInk,
                            mutedColor = secondaryInk.copy(alpha = 0.68f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            textAlign = TextAlign.Center,
                            useEditorialSerif = true,
                            maxActiveSp = 30f,
                            modifier = Modifier.fillMaxWidth(0.72f),
                        )
                    }
                }
            }
        }

        // Silky 60fps 256-Point FFT Live Spectrum (shown on all scenes except Vintage Hi-Fi which has its own dual VU + seek bar)
        if (sceneStyle != DesktopSceneStyle.VINTAGE_HIFI) {
            Spacer(modifier = Modifier.height(8.dp))
            DesktopLiveSpectrumBar(
                songId = song.id,
                positionMs = playerState.positionMs,
                durationMs = playerState.durationMs,
                isPlaying = playerState.isPlaying,
                spectrumStyle = spectrumStyle,
                playedColor = primaryInk,
                unplayedColor = primaryInk.copy(alpha = 0.24f),
                accentColor = activeAccent,
                timestampColor = secondaryInk,
                onSeekTo = onSeekTo,
            )
        }
    }
}
