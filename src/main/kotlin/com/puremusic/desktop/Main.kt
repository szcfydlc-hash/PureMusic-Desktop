package com.puremusic.desktop

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PictureInPictureAlt
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() = application {
    runCatching { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) }

    val initialConfig = remember { DesktopConfigStore.load() }
    var folders by remember { mutableStateOf(initialConfig.folders) }
    var favoritePaths by remember { mutableStateOf(initialConfig.favoritePaths) }
    var isDarkTheme by remember { mutableStateOf(initialConfig.isDarkTheme) }
    var sceneStyle by remember { mutableStateOf(initialConfig.sceneStyle) }
    var spectrumStyle by remember { mutableStateOf(initialConfig.spectrumStyle) }
    var volume by remember { mutableFloatStateOf(initialConfig.volume) }

    var songs by remember { mutableStateOf<List<DesktopSong>>(emptyList()) }
    var currentSong by remember { mutableStateOf<DesktopSong?>(null) }
    var lyrics by remember { mutableStateOf<List<LyricLine>>(emptyList()) }
    var isPlaying by remember { mutableStateOf(false) }
    var positionMs by remember { mutableLongStateOf(0L) }
    var repeatMode by remember { mutableStateOf(RepeatMode.ALL) }
    var activeTab by remember { mutableStateOf(DesktopTab.NOW_PLAYING) }
    var searchQuery by remember { mutableStateOf("") }
    var showMiniFloatingWindow by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun persistConfig() {
        DesktopConfigStore.save(
            DesktopConfigStore.SavedConfig(
                folders = folders,
                favoritePaths = favoritePaths,
                isDarkTheme = isDarkTheme,
                sceneStyle = sceneStyle,
                spectrumStyle = spectrumStyle,
                volume = volume,
            ),
        )
    }

    lateinit var audioPlayer: DesktopAudioPlayer

    fun playSong(song: DesktopSong) {
        currentSong = song
        positionMs = 0L
        audioPlayer.setVolume(volume)
        audioPlayer.playFile(File(song.path), song.durationMs, 0L)
        scope.launch(Dispatchers.IO) {
            val loaded = LibraryScanner.loadLyricsForSong(song)
            withContext(Dispatchers.Main) { lyrics = loaded }
        }
    }

    fun skipNext() {
        val list = songs
        if (list.isEmpty()) return
        val cur = currentSong
        val next = when (repeatMode) {
            RepeatMode.ONE -> cur ?: list.first()
            RepeatMode.SHUFFLE -> list.random()
            RepeatMode.ALL -> {
                val idx = list.indexOfFirst { it.id == cur?.id }
                list[(idx + 1) % list.size]
            }
        }
        playSong(next)
    }

    fun skipPrevious() {
        val list = songs
        if (list.isEmpty()) return
        val cur = currentSong
        val idx = list.indexOfFirst { it.id == cur?.id }.coerceAtLeast(0)
        val prevIdx = if (idx - 1 < 0) list.lastIndex else idx - 1
        playSong(list[prevIdx])
    }

    audioPlayer = remember {
        DesktopAudioPlayer(
            onPositionUpdate = { pos -> SwingUtilities.invokeLater { positionMs = pos } },
            onPlayingChanged = { playing -> SwingUtilities.invokeLater { isPlaying = playing } },
            onTrackCompleted = { SwingUtilities.invokeLater { skipNext() } },
        )
    }

    DisposableEffect(Unit) {
        onDispose { audioPlayer.stop() }
    }

    fun rescan() {
        scope.launch {
            isScanning = true
            val scanned = withContext(Dispatchers.IO) { LibraryScanner.scanFolders(folders) }
            songs = scanned
            if (currentSong == null && scanned.isNotEmpty()) {
                currentSong = scanned.first()
                lyrics = withContext(Dispatchers.IO) { LibraryScanner.loadLyricsForSong(scanned.first()) }
            }
            isScanning = false
        }
    }

    fun pickAndAddFolder() {
        SwingUtilities.invokeLater {
            val chooser = JFileChooser().apply {
                dialogTitle = "选择本地音乐文件夹"
                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                isAcceptAllFileFilterUsed = false
            }
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                val path = chooser.selectedFile.absolutePath
                if (path !in folders) {
                    folders = folders + path
                    persistConfig()
                    rescan()
                }
            }
        }
    }

    LaunchedEffect(Unit) { rescan() }

    val vinylAngle = remember { Animatable(0f) }
    LaunchedEffect(isPlaying) {
        while (isActive && isPlaying) {
            val start = vinylAngle.value % 360f
            vinylAngle.snapTo(start)
            vinylAngle.animateTo(
                targetValue = start + 360f,
                animationSpec = tween(durationMillis = 16_000, easing = LinearEasing),
            )
        }
    }

    val colorScheme = if (isDarkTheme) {
        darkColorScheme(
            background = Color(0xFF141312),
            surface = Color(0xFF1B1A18),
            primary = Color(0xFFC83E2B),
            onBackground = Color(0xFFF2ECE1),
            onSurface = Color(0xFFF2ECE1),
            outline = Color(0xFF2D2B28),
        )
    } else {
        lightColorScheme(
            background = Color(0xFFF5F0E6),
            surface = Color(0xFFECE6DA),
            primary = Color(0xFFC83E2B),
            onBackground = Color(0xFF1E1B18),
            onSurface = Color(0xFF1E1B18),
            outline = Color(0xFFD9D2C5),
        )
    }

    val mainWindowState = rememberWindowState(
        size = DpSize(1160.dp, 740.dp),
        position = WindowPosition.Aligned(Alignment.Center),
    )

    MaterialTheme(colorScheme = colorScheme) {
        Window(
            onCloseRequest = {
                audioPlayer.stop()
                exitApplication()
            },
            state = mainWindowState,
            title = "简约音乐 PureMusic",
            undecorated = true,
            transparent = true,
            onPreviewKeyEvent = { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.Spacebar -> {
                            if (currentSong != null) audioPlayer.togglePlayPause()
                            true
                        }
                        Key.DirectionRight -> {
                            if (currentSong != null) audioPlayer.seekTo(positionMs + 5_000L)
                            true
                        }
                        Key.DirectionLeft -> {
                            if (currentSong != null) audioPlayer.seekTo((positionMs - 5_000L).coerceAtLeast(0L))
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            },
        ) {
            val bg = MaterialTheme.colorScheme.background
            val surface = MaterialTheme.colorScheme.surface
            val ink = MaterialTheme.colorScheme.onBackground
            val mutedInk = ink.copy(alpha = 0.56f)
            val outline = MaterialTheme.colorScheme.outline
            val cinnabar = Color(0xFFC83E2B)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(bg)
                    .border(1.dp, outline, RoundedCornerShape(16.dp)),
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Minimalist Sidebar
                    Column(
                        modifier = Modifier
                            .width(210.dp)
                            .fillMaxHeight()
                            .background(surface)
                            .padding(vertical = 16.dp, horizontal = 12.dp),
                    ) {
                        WindowDraggableArea {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(cinnabar),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "简约音乐",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ink,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val navItems = listOf(
                            DesktopTab.NOW_PLAYING to Icons.Rounded.Album,
                            DesktopTab.LIBRARY to Icons.Rounded.LibraryMusic,
                            DesktopTab.ALBUMS to Icons.Rounded.Album,
                            DesktopTab.FOLDERS to Icons.Rounded.Folder,
                            DesktopTab.FAVORITES to Icons.Rounded.Favorite,
                            DesktopTab.SETTINGS to Icons.Rounded.Settings,
                        )

                        navItems.forEach { (tab, icon) ->
                            val selected = activeTab == tab
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) ink.copy(alpha = 0.08f) else Color.Transparent)
                                    .clickable { activeTab = tab }
                                    .padding(horizontal = 10.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(14.dp)
                                        .clip(CircleShape)
                                        .background(if (selected) cinnabar else Color.Transparent),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tab.label,
                                    tint = if (selected) ink else mutedInk,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = tab.label,
                                    fontSize = 13.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (selected) ink else mutedInk,
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Add local music folder CTA
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, outline, RoundedCornerShape(10.dp))
                                .clickable { pickAndAddFolder() }
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null, tint = ink, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("添加音乐文件夹", fontSize = 12.sp, color = ink, fontWeight = FontWeight.Medium)
                        }
                    }

                    // Right Main Content Column
                    Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        // Frameless Draggable Top Title Bar
                        WindowDraggableArea {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .padding(horizontal = 18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = if (isScanning) "正在扫描本地曲库..." else "${songs.size} 首本地曲目 · v1.0 Windows",
                                    fontSize = 12.sp,
                                    color = mutedInk,
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { showMiniFloatingWindow = !showMiniFloatingWindow },
                                        modifier = Modifier.size(32.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.PictureInPictureAlt,
                                            contentDescription = "切换桌面迷你黑胶浮窗",
                                            tint = if (showMiniFloatingWindow) cinnabar else mutedInk,
                                            modifier = Modifier.size(17.dp),
                                        )
                                    }
                                    IconButton(
                                        onClick = { mainWindowState.isMinimized = true },
                                        modifier = Modifier.size(32.dp),
                                    ) {
                                        Icon(Icons.Rounded.Minimize, contentDescription = "最小化", tint = mutedInk, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            mainWindowState.placement = if (mainWindowState.placement == WindowPlacement.Maximized) {
                                                WindowPlacement.Floating
                                            } else {
                                                WindowPlacement.Maximized
                                            }
                                        },
                                        modifier = Modifier.size(32.dp),
                                    ) {
                                        Icon(Icons.Rounded.CropSquare, contentDescription = "最大化", tint = mutedInk, modifier = Modifier.size(15.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            audioPlayer.stop()
                                            exitApplication()
                                        },
                                        modifier = Modifier.size(32.dp),
                                    ) {
                                        Icon(Icons.Rounded.Close, contentDescription = "关闭", tint = mutedInk, modifier = Modifier.size(17.dp))
                                    }
                                }
                            }
                        }

                        // Primary Workspace Area
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 28.dp),
                        ) {
                            when (activeTab) {
                                DesktopTab.NOW_PLAYING -> {
                                    DesktopNowPlayingStage(
                                        song = currentSong,
                                        allSongs = songs,
                                        lyrics = lyrics,
                                        positionMs = positionMs,
                                        isPlaying = isPlaying,
                                        vinylRotationProvider = { vinylAngle.value },
                                        sceneStyle = sceneStyle,
                                        ink = ink,
                                        mutedInk = mutedInk,
                                        isDarkTheme = isDarkTheme,
                                        onTogglePlayPause = {
                                            if (currentSong != null) audioPlayer.togglePlayPause()
                                        },
                                        onPickFolder = { pickAndAddFolder() },
                                    )
                                }

                                DesktopTab.LIBRARY, DesktopTab.FAVORITES -> {
                                    val filtered = remember(songs, activeTab, favoritePaths, searchQuery) {
                                        val base = if (activeTab == DesktopTab.FAVORITES) {
                                            songs.filter { it.path in favoritePaths }
                                        } else {
                                            songs
                                        }
                                        if (searchQuery.isBlank()) base else base.filter {
                                            it.title.contains(searchQuery, true) ||
                                                it.artist.contains(searchQuery, true) ||
                                                it.album.contains(searchQuery, true)
                                        }
                                    }
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        OutlinedTextField(
                                            value = searchQuery,
                                            onValueChange = { searchQuery = it },
                                            placeholder = { Text("搜索歌名、歌手或专辑...", fontSize = 13.sp) },
                                            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                        )
                                        LazyColumn(modifier = Modifier.weight(1f)) {
                                            itemsIndexed(filtered, key = { _, s -> s.id }) { idx, song ->
                                                val isCur = song.id == currentSong?.id
                                                val isFav = song.path in favoritePaths
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(if (isCur) cinnabar.copy(alpha = 0.12f) else Color.Transparent)
                                                        .clickable {
                                                            playSong(song)
                                                            activeTab = DesktopTab.NOW_PLAYING
                                                        }
                                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Text(
                                                        text = "%02d".format(idx + 1),
                                                        fontSize = 12.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        color = if (isCur) cinnabar else mutedInk,
                                                        modifier = Modifier.width(34.dp),
                                                    )
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = song.title,
                                                            fontSize = 14.sp,
                                                            fontWeight = if (isCur) FontWeight.Bold else FontWeight.Medium,
                                                            color = if (isCur) cinnabar else ink,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                        )
                                                        Text(
                                                            text = "${song.artist} · ${song.album}",
                                                            fontSize = 12.sp,
                                                            color = mutedInk,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                        )
                                                    }
                                                    Text(
                                                        text = song.formatBadge,
                                                        fontSize = 10.sp,
                                                        color = Color(0xFF0D9488),
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier
                                                            .padding(end = 14.dp)
                                                            .border(0.8.dp, Color(0xFF0D9488).copy(alpha = 0.45f), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 5.dp, vertical = 1.dp),
                                                    )
                                                    Text(
                                                        text = formatDuration(song.durationMs),
                                                        fontSize = 12.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        color = mutedInk,
                                                        modifier = Modifier.width(48.dp),
                                                    )
                                                    IconButton(
                                                        onClick = {
                                                            favoritePaths = if (isFav) favoritePaths - song.path else favoritePaths + song.path
                                                            persistConfig()
                                                        },
                                                        modifier = Modifier.size(30.dp),
                                                    ) {
                                                        Icon(
                                                            imageVector = if (isFav) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                                            contentDescription = "喜欢",
                                                            tint = if (isFav) cinnabar else mutedInk,
                                                            modifier = Modifier.size(16.dp),
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                DesktopTab.ALBUMS -> {
                                    val albums = remember(songs) { songs.groupBy { it.album }.entries.toList() }
                                    LazyVerticalGrid(
                                        columns = GridCells.Adaptive(160.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(18.dp),
                                    ) {
                                        items(albums, key = { it.key }) { (albumName, albumSongs) ->
                                            val firstSong = albumSongs.first()
                                            val bmp = rememberSongArtwork(firstSong.path)
                                            Column(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        playSong(firstSong)
                                                        activeTab = DesktopTab.NOW_PLAYING
                                                    },
                                            ) {
                                                DesktopAlbumCover(
                                                    bitmap = bmp,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .aspectRatio(1f)
                                                        .border(0.75.dp, outline),
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(albumName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                Text("${firstSong.artist} · ${albumSongs.size} 首", fontSize = 11.sp, color = mutedInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            }
                                        }
                                    }
                                }

                                DesktopTab.FOLDERS -> {
                                    Column(modifier = Modifier.fillMaxSize().padding(vertical = 8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text("已监控本地目录 (${folders.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ink)
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                IconButton(onClick = { rescan() }) {
                                                    Icon(Icons.Rounded.Refresh, contentDescription = "重新扫描", tint = ink)
                                                }
                                                IconButton(onClick = { pickAndAddFolder() }) {
                                                    Icon(Icons.Rounded.Add, contentDescription = "添加目录", tint = cinnabar)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            itemsIndexed(folders) { _, path ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(surface)
                                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Icon(Icons.Rounded.Folder, contentDescription = null, tint = cinnabar, modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text(path, fontSize = 13.sp, color = ink, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    IconButton(
                                                        onClick = {
                                                            folders = folders - path
                                                            persistConfig()
                                                            rescan()
                                                        },
                                                        modifier = Modifier.size(28.dp),
                                                    ) {
                                                        Icon(Icons.Rounded.DeleteOutline, contentDescription = "移除", tint = mutedInk, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                DesktopTab.SETTINGS -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(vertical = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                    ) {
                                        Text("外观与听音室设置", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ink)
                                        Row(
                                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(surface).padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Column {
                                                Text("深色曜石主题", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ink)
                                                Text("关闭后切换为浅色暖白宣纸主题", fontSize = 12.sp, color = mutedInk)
                                            }
                                            Switch(
                                                checked = isDarkTheme,
                                                onCheckedChange = {
                                                    isDarkTheme = it
                                                    persistConfig()
                                                },
                                            )
                                        }

                                        Text("默认沉浸场景", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = mutedInk)
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            DesktopSceneStyle.entries.forEach { st ->
                                                val sel = st == sceneStyle
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(if (sel) cinnabar else surface)
                                                        .clickable {
                                                            sceneStyle = st
                                                            persistConfig()
                                                        }
                                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                                ) {
                                                    Text(st.label, fontSize = 13.sp, color = if (sel) Color.White else ink)
                                                }
                                            }
                                        }

                                        Text("默认实时频谱样式", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = mutedInk)
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            DesktopSpectrumStyle.entries.forEach { sp ->
                                                val sel = sp == spectrumStyle
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(if (sel) cinnabar else surface)
                                                        .clickable {
                                                            spectrumStyle = sp
                                                            persistConfig()
                                                        }
                                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                                ) {
                                                    Text(sp.label, fontSize = 13.sp, color = if (sel) Color.White else ink)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Full-Width Bottom Spectrum Scrubber & Transport Deck
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 28.dp, vertical = 14.dp),
                        ) {
                            DesktopLiveSpectrumBar(
                                songId = currentSong?.id ?: 0L,
                                positionMs = positionMs,
                                durationMs = currentSong?.durationMs ?: 1L,
                                isPlaying = isPlaying,
                                spectrumStyle = spectrumStyle,
                                playedColor = ink,
                                unplayedColor = ink.copy(alpha = 0.22f),
                                needleColor = cinnabar,
                                timestampColor = mutedInk,
                                onSeekTo = { target -> audioPlayer.seekTo(target) },
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                // Left transport cluster
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    IconButton(onClick = { skipPrevious() }, modifier = Modifier.size(34.dp)) {
                                        Icon(Icons.Rounded.SkipPrevious, contentDescription = "上一首", tint = ink, modifier = Modifier.size(22.dp))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(cinnabar)
                                            .clickable {
                                                if (currentSong != null) audioPlayer.togglePlayPause()
                                            },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                            contentDescription = "播放/暂停",
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp),
                                        )
                                    }
                                    IconButton(onClick = { skipNext() }, modifier = Modifier.size(34.dp)) {
                                        Icon(Icons.Rounded.SkipNext, contentDescription = "下一首", tint = ink, modifier = Modifier.size(22.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            repeatMode = when (repeatMode) {
                                                RepeatMode.ALL -> RepeatMode.ONE
                                                RepeatMode.ONE -> RepeatMode.SHUFFLE
                                                RepeatMode.SHUFFLE -> RepeatMode.ALL
                                            }
                                        },
                                        modifier = Modifier.size(34.dp),
                                    ) {
                                        Icon(
                                            imageVector = when (repeatMode) {
                                                RepeatMode.ALL -> Icons.Rounded.Repeat
                                                RepeatMode.ONE -> Icons.Rounded.RepeatOne
                                                RepeatMode.SHUFFLE -> Icons.Rounded.Shuffle
                                            },
                                            contentDescription = repeatMode.label,
                                            tint = mutedInk,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                }

                                // Center quick-cycle Scene & Spectrum pills
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(surface)
                                            .border(0.8.dp, outline, RoundedCornerShape(16.dp))
                                            .clickable {
                                                val entries = DesktopSceneStyle.entries
                                                sceneStyle = entries[(entries.indexOf(sceneStyle) + 1) % entries.size]
                                                persistConfig()
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                    ) {
                                        Text("场景 · ${sceneStyle.label}", fontSize = 12.sp, color = ink)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(surface)
                                            .border(0.8.dp, outline, RoundedCornerShape(16.dp))
                                            .clickable {
                                                val entries = DesktopSpectrumStyle.entries
                                                spectrumStyle = entries[(entries.indexOf(spectrumStyle) + 1) % entries.size]
                                                persistConfig()
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                    ) {
                                        Text("频谱 · ${spectrumStyle.label}", fontSize = 12.sp, color = ink)
                                    }
                                }

                                // Right Volume Slider
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.width(150.dp),
                                ) {
                                    Icon(Icons.Rounded.VolumeUp, contentDescription = "音量", tint = mutedInk, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Slider(
                                        value = volume,
                                        onValueChange = {
                                            volume = it
                                            audioPlayer.setVolume(it)
                                        },
                                        onValueChangeFinished = { persistConfig() },
                                        colors = SliderDefaults.colors(thumbColor = ink, activeTrackColor = ink, inactiveTrackColor = outline),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Independent Always-On-Top Draggable Mini Vinyl Floating Desktop Widget
        if (showMiniFloatingWindow) {
            MiniVinylFloatingWindow(
                song = currentSong,
                isPlaying = isPlaying,
                vinylRotationProvider = { vinylAngle.value },
                onTogglePlayPause = { if (currentSong != null) audioPlayer.togglePlayPause() },
                onSkipPrevious = { skipPrevious() },
                onSkipNext = { skipNext() },
                onClose = { showMiniFloatingWindow = false },
            )
        }
    }
}

@Composable
private fun DesktopNowPlayingStage(
    song: DesktopSong?,
    allSongs: List<DesktopSong>,
    lyrics: List<LyricLine>,
    positionMs: Long,
    isPlaying: Boolean,
    vinylRotationProvider: () -> Float,
    sceneStyle: DesktopSceneStyle,
    ink: Color,
    mutedInk: Color,
    isDarkTheme: Boolean,
    onTogglePlayPause: () -> Unit,
    onPickFolder: () -> Unit,
) {
    if (song == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("还没有扫描到本地歌曲", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = ink)
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFC83E2B))
                        .clickable(onClick = onPickFolder)
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                ) {
                    Text("选择本地音乐文件夹", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        return
    }

    val artwork = rememberSongArtwork(song.path)
    val activeIdx = remember(lyrics, positionMs) {
        val found = lyrics.indexOfLast { it.timeMs <= positionMs }
        if (found >= 0) found else if (lyrics.isNotEmpty()) 0 else -1
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(42.dp),
    ) {
        // Left Visual Stage (3 Selectable Scenes)
        Box(
            modifier = Modifier
                .weight(0.50f)
                .fillMaxHeight()
                .clickable(onClick = onTogglePlayPause),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = sceneStyle,
                transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(200)) },
                label = "desktopSceneAnim",
            ) { st ->
                when (st) {
                    DesktopSceneStyle.VINYL_STUDY -> {
                        val sleeveSize = 270.dp
                        val discSize = 250.dp
                        Box(modifier = Modifier.width(sleeveSize + discSize * 0.46f).height(sleeveSize + 18.dp)) {
                            Canvas(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().height(16.dp)) {
                                drawOval(
                                    brush = Brush.radialGradient(
                                        0f to Color.Black.copy(alpha = if (isDarkTheme) 0.55f else 0.18f),
                                        1f to Color.Transparent,
                                        center = Offset(size.width * 0.45f, size.height * 0.5f),
                                        radius = size.width * 0.5f,
                                    ),
                                    size = Size(size.width * 0.92f, size.height),
                                )
                            }
                            Box(modifier = Modifier.align(Alignment.TopStart).offset(x = sleeveSize * 0.50f, y = 10.dp)) {
                                DesktopSpinningVinylDisc(artwork = artwork, rotationProvider = vinylRotationProvider, discSize = discSize)
                            }
                            DesktopAlbumCover(
                                bitmap = artwork,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .size(sleeveSize)
                                    .shadow(18.dp, RectangleShape)
                                    .border(0.75.dp, Color.White.copy(alpha = 0.12f), RectangleShape),
                            )
                        }
                    }

                    DesktopSceneStyle.SPATIAL_GALLERY -> {
                        val stackSongs = remember(song.id, allSongs) {
                            val idx = allSongs.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
                            if (allSongs.isEmpty()) listOf(song) else List(minOf(4, allSongs.size)) { step -> allSongs[(idx + step) % allSongs.size] }
                        }
                        val frontSize = 250.dp
                        Box(modifier = Modifier.width(frontSize + 90.dp).height(frontSize + 40.dp), contentAlignment = Alignment.TopStart) {
                            for (depth in (stackSongs.lastIndex downTo 1)) {
                                val dArt = rememberSongArtwork(stackSongs[depth].path)
                                val sc = 1f - depth * 0.085f
                                DesktopAlbumCover(
                                    bitmap = dArt,
                                    cornerRadius = 3.dp,
                                    modifier = Modifier
                                        .offset(x = (depth * 26).dp, y = (30 - depth * 10).dp)
                                        .size(frontSize)
                                        .graphicsLayer {
                                            scaleX = sc; scaleY = sc
                                            rotationY = 14f; cameraDistance = 16f
                                            alpha = (1f - depth * 0.20f).coerceAtLeast(0.32f)
                                        }
                                        .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(3.dp)),
                                )
                            }
                            DesktopAlbumCover(
                                bitmap = artwork,
                                cornerRadius = 3.dp,
                                modifier = Modifier
                                    .offset(x = 0.dp, y = 30.dp)
                                    .size(frontSize)
                                    .graphicsLayer { rotationY = 14f; cameraDistance = 16f }
                                    .shadow(24.dp, RoundedCornerShape(3.dp))
                                    .border(1.2.dp, Color.White.copy(alpha = 0.30f), RoundedCornerShape(3.dp)),
                            )
                        }
                    }

                    DesktopSceneStyle.VINYL_LIGHTFIELD -> {
                        val discSize = 310.dp
                        Box(modifier = Modifier.size(width = discSize + 70.dp, height = discSize)) {
                            Box(modifier = Modifier.align(Alignment.CenterStart)) {
                                DesktopSpinningVinylDisc(artwork = artwork, rotationProvider = vinylRotationProvider, discSize = discSize)
                            }
                            DesktopTonearm(
                                isPlaying = isPlaying,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(width = 140.dp, height = 270.dp),
                            )
                        }
                    }
                }
            }
        }

        // Right Editorial Song Info + 3-Slot Auto-Fit Bilingual Lyrics
        Column(
            modifier = Modifier.weight(0.50f).fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = song.title,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${song.artist} · ${song.album} · ${song.formatBadge}",
                fontSize = 14.sp,
                color = mutedInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(thickness = 0.6.dp, color = mutedInk.copy(alpha = 0.25f))
            Spacer(modifier = Modifier.height(22.dp))
            DesktopThreeSlotLyrics(
                lyrics = lyrics,
                activeIdx = activeIdx,
                activeColor = ink,
                secondaryColor = mutedInk,
                mutedColor = mutedInk.copy(alpha = 0.55f),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun MiniVinylFloatingWindow(
    song: DesktopSong?,
    isPlaying: Boolean,
    vinylRotationProvider: () -> Float,
    onTogglePlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onClose: () -> Unit,
) {
    val miniState = rememberWindowState(
        size = DpSize(270.dp, 290.dp),
        position = WindowPosition(Alignment.BottomEnd),
    )
    Window(
        onCloseRequest = onClose,
        state = miniState,
        title = "简约音乐 · 迷你浮窗",
        undecorated = true,
        transparent = true,
        alwaysOnTop = true,
        resizable = false,
    ) {
        val artwork = rememberSongArtwork(song?.path)
        val cinnabar = Color(0xFFC83E2B)
        WindowDraggableArea {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
                    .shadow(16.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF181716))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                    .padding(14.dp),
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.TopEnd).size(24.dp),
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "关闭浮窗", tint = Color.White.copy(alpha = 0.55f), modifier = Modifier.size(14.dp))
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.width(176.dp).height(126.dp)) {
                        Box(modifier = Modifier.align(Alignment.CenterStart).offset(x = 62.dp)) {
                            DesktopSpinningVinylDisc(artwork = artwork, rotationProvider = vinylRotationProvider, discSize = 114.dp)
                        }
                        DesktopAlbumCover(
                            bitmap = artwork,
                            cornerRadius = 6.dp,
                            modifier = Modifier.align(Alignment.CenterStart).size(120.dp).shadow(12.dp, RoundedCornerShape(6.dp)),
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = song?.title ?: "简约音乐",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF2ECE1),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = song?.artist ?: "等待播放",
                            fontSize = 11.sp,
                            color = Color(0xFFA0988E),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        IconButton(onClick = onSkipPrevious, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Rounded.SkipPrevious, contentDescription = "上一首", tint = Color(0xFFF2ECE1), modifier = Modifier.size(20.dp))
                        }
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(cinnabar)
                                .clickable(onClick = onTogglePlayPause),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = "播放/暂停",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        IconButton(onClick = onSkipNext, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Rounded.SkipNext, contentDescription = "下一首", tint = Color(0xFFF2ECE1), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}
