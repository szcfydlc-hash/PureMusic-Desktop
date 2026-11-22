package com.puremusic.desktop

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jetbrains.skia.Image as SkiaImage
import java.io.File
import java.nio.charset.Charset
import java.util.Properties
import java.util.logging.Level
import java.util.logging.Logger

object LibraryScanner {
    private val supportedExtensions = setOf("mp3", "flac", "wav", "aiff", "aif")
    private val lrcTimestampRegex = Regex("""\[(\d{1,3}):(\d{2})(?:\.(\d{1,3}))?]""")

    init {
        // Silence noisy JAudiotagger console logging
        runCatching {
            Logger.getLogger("org.jaudiotagger").level = Level.OFF
        }
    }

    fun scanFolders(folders: List<String>): List<DesktopSong> {
        val results = mutableListOf<DesktopSong>()
        var nextId = 1L
        val seenPaths = HashSet<String>()

        for (dirPath in folders) {
            val dir = File(dirPath)
            if (!dir.exists() || !dir.isDirectory) continue
            dir.walkTopDown()
                .filter { it.isFile && it.extension.lowercase() in supportedExtensions }
                .forEach { file ->
                    val canonical = file.absolutePath
                    if (!seenPaths.add(canonical)) return@forEach
                    val song = readSongMetadata(nextId++, file)
                    results.add(song)
                }
        }
        return results.sortedBy { it.title.lowercase() }
    }

    private fun readSongMetadata(id: Long, file: File): DesktopSong {
        var title = file.nameWithoutExtension
        var artist = "未知歌手"
        var album = file.parentFile?.name ?: "本地曲库"
        var durationMs = 0L
        var embeddedLyrics: String? = null
        val formatBadge = file.extension.uppercase()

        runCatching {
            val audioFile = AudioFileIO.read(file)
            durationMs = (audioFile.audioHeader?.trackLength ?: 0) * 1000L
            val tag = audioFile.tag
            if (tag != null) {
                tag.getFirst(FieldKey.TITLE)?.trim()?.takeIf { it.isNotBlank() }?.let { title = it }
                tag.getFirst(FieldKey.ARTIST)?.trim()?.takeIf { it.isNotBlank() }?.let { artist = it }
                tag.getFirst(FieldKey.ALBUM)?.trim()?.takeIf { it.isNotBlank() }?.let { album = it }
                tag.getFirst(FieldKey.LYRICS)?.trim()?.takeIf { it.isNotBlank() }?.let { embeddedLyrics = it }
            }
        }

        return DesktopSong(
            id = id,
            path = file.absolutePath,
            title = title,
            artist = artist,
            album = album,
            folderName = file.parentFile?.name ?: "根目录",
            durationMs = durationMs.coerceAtLeast(1_000L),
            formatBadge = formatBadge,
            embeddedLyrics = embeddedLyrics,
        )
    }

    fun loadArtwork(songPath: String): ImageBitmap? {
        if (ArtworkCache.contains(songPath)) return ArtworkCache.get(songPath)
        val bmp = runCatching {
            val file = File(songPath)
            val audioFile = AudioFileIO.read(file)
            val bytes = audioFile.tag?.firstArtwork?.binaryData
            if (bytes != null && bytes.isNotEmpty()) {
                SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
            } else {
                // Fallback to cover.jpg / folder.jpg in the same directory
                val folderCover = file.parentFile?.listFiles()?.firstOrNull { f ->
                    val n = f.name.lowercase()
                    n == "cover.jpg" || n == "cover.png" || n == "folder.jpg" || n == "folder.png"
                }
                folderCover?.readBytes()?.let { SkiaImage.makeFromEncoded(it).toComposeImageBitmap() }
            }
        }.getOrNull()
        ArtworkCache.put(songPath, bmp)
        return bmp
    }

    fun loadLyricsForSong(song: DesktopSong): List<LyricLine> {
        // 1. Check sidecar .lrc file next to audio file
        val audioFile = File(song.path)
        val lrcFile = File(audioFile.parentFile, "${audioFile.nameWithoutExtension}.lrc")
        val rawText = when {
            lrcFile.exists() && lrcFile.isFile -> readTextAutoCharset(lrcFile)
            !song.embeddedLyrics.isNullOrBlank() -> song.embeddedLyrics
            else -> null
        } ?: return emptyList()

        return parseLrc(rawText)
    }

    private fun readTextAutoCharset(file: File): String {
        val bytes = file.readBytes()
        val utf8 = bytes.toString(Charsets.UTF_8)
        return if (utf8.contains('\uFFFD')) {
            runCatching { bytes.toString(Charset.forName("GBK")) }.getOrDefault(utf8)
        } else {
            utf8
        }
    }

    private fun parseLrc(raw: String): List<LyricLine> {
        val byTime = LinkedHashMap<Long, MutableList<String>>()
        for (line in raw.lines()) {
            val matches = lrcTimestampRegex.findAll(line).toList()
            if (matches.isEmpty()) continue
            val text = line.replace(lrcTimestampRegex, "").trim()
            if (text.isBlank()) continue
            for (m in matches) {
                val min = m.groupValues[1].toLongOrNull() ?: 0L
                val sec = m.groupValues[2].toLongOrNull() ?: 0L
                val fracRaw = m.groupValues[3]
                val ms = when (fracRaw.length) {
                    1 -> (fracRaw.toLongOrNull() ?: 0L) * 100L
                    2 -> (fracRaw.toLongOrNull() ?: 0L) * 10L
                    3 -> (fracRaw.toLongOrNull() ?: 0L)
                    else -> 0L
                }
                val timeMs = min * 60_000L + sec * 1_000L + ms
                byTime.getOrPut(timeMs) { mutableListOf() }.add(text)
            }
        }
        return byTime.entries
            .sortedBy { it.key }
            .map { (timeMs, texts) ->
                LyricLine(
                    timeMs = timeMs,
                    primaryText = texts.first(),
                    secondaryText = texts.getOrNull(1),
                )
            }
    }
}

object DesktopConfigStore {
    private val configDir: File
        get() {
            val appData = System.getenv("APPDATA")
            val base = if (!appData.isNullOrBlank()) File(appData, "PureMusic") else File(System.getProperty("user.home"), ".puremusic")
            if (!base.exists()) base.mkdirs()
            return base
        }

    private val configFile: File get() = File(configDir, "config.properties")

    data class SavedConfig(
        val folders: List<String> = emptyList(),
        val favoritePaths: Set<String> = emptySet(),
        val isDarkTheme: Boolean = true,
        val sceneStyle: DesktopSceneStyle = DesktopSceneStyle.VINYL_STUDY,
        val spectrumStyle: DesktopSpectrumStyle = DesktopSpectrumStyle.SILK_RIBBON,
        val volume: Float = 0.85f,
    )

    fun load(): SavedConfig {
        if (!configFile.exists()) {
            val defaultMusic = File(System.getProperty("user.home"), "Music")
            return SavedConfig(
                folders = if (defaultMusic.exists() && defaultMusic.isDirectory) listOf(defaultMusic.absolutePath) else emptyList(),
            )
        }
        val props = Properties()
        runCatching { configFile.inputStream().use { props.load(it) } }
        val folders = props.getProperty("folders", "")
            .split("|")
            .map { it.trim() }
            .filter { it.isNotBlank() && File(it).exists() }
        val favs = props.getProperty("favorites", "")
            .split("|")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toSet()
        val isDark = props.getProperty("dark_theme", "true").toBoolean()
        val scene = DesktopSceneStyle.entries.firstOrNull { it.name == props.getProperty("scene") } ?: DesktopSceneStyle.VINYL_STUDY
        val spectrum = DesktopSpectrumStyle.entries.firstOrNull { it.name == props.getProperty("spectrum") } ?: DesktopSpectrumStyle.SILK_RIBBON
        val vol = props.getProperty("volume", "0.85").toFloatOrNull()?.coerceIn(0f, 1f) ?: 0.85f
        return SavedConfig(folders, favs, isDark, scene, spectrum, vol)
    }

    fun save(config: SavedConfig) {
        runCatching {
            val props = Properties().apply {
                setProperty("folders", config.folders.joinToString("|"))
                setProperty("favorites", config.favoritePaths.joinToString("|"))
                setProperty("dark_theme", config.isDarkTheme.toString())
                setProperty("scene", config.sceneStyle.name)
                setProperty("spectrum", config.spectrumStyle.name)
                setProperty("volume", config.volume.toString())
            }
            configFile.outputStream().use { props.store(it, "PureMusic Desktop Config") }
        }
    }
}
