package com.puremusic.desktop

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.logging.Level
import java.util.logging.Logger

object DesktopLibraryScanner {
    private val supportedExtensions = setOf("mp3", "flac", "wav", "aiff", "aif")

    init {
        Logger.getLogger("org.jaudiotagger").level = Level.OFF
    }

    fun scanFolders(folderPaths: List<String>): List<DesktopSong> {
        val result = mutableListOf<DesktopSong>()
        val seenPaths = HashSet<String>()

        folderPaths.forEach { dirPath ->
            val root = File(dirPath)
            if (!root.exists() || !root.isDirectory) return@forEach
            root.walkTopDown()
                .onFail { _, _ -> }
                .filter { it.isFile && it.extension.lowercase(Locale.US) in supportedExtensions }
                .forEach { file ->
                    val canonical = runCatching { file.canonicalPath }.getOrDefault(file.absolutePath)
                    if (!seenPaths.add(canonical)) return@forEach
                    result.add(readSongMetadata(file, canonical))
                }
        }

        return result.sortedWith(
            compareBy<DesktopSong> { it.title.lowercase(Locale.getDefault()) }
                .thenBy { it.artist.lowercase(Locale.getDefault()) },
        )
    }

    private fun readSongMetadata(file: File, canonicalPath: String): DesktopSong {
        val ext = file.extension.uppercase(Locale.US)
        var title = file.nameWithoutExtension
        var artist = "未知歌手"
        var album = "未知专辑"
        var durationMs = 210_000L
        var sampleRateHz = 44100
        var bitRateKbps = 0
        var bitsPerSample = 16

        runCatching {
            val audioFile = AudioFileIO.read(file)
            val header = audioFile.audioHeader
            if (header != null) {
                if (header.trackLength > 0) durationMs = header.trackLength * 1000L
                val sr = header.sampleRateAsNumber
                if (sr > 0) sampleRateHz = sr
                val br = header.bitRateAsNumber.toInt()
                if (br > 0) bitRateKbps = br
                val bps = header.bitsPerSample
                if (bps > 0) bitsPerSample = bps
            }
            val tag = audioFile.tag
            if (tag != null) {
                tag.getFirst(FieldKey.TITLE)?.trim()?.takeIf { it.isNotEmpty() }?.let { title = it }
                tag.getFirst(FieldKey.ARTIST)?.trim()?.takeIf { it.isNotEmpty() }?.let { artist = it }
                tag.getFirst(FieldKey.ALBUM)?.trim()?.takeIf { it.isNotEmpty() }?.let { album = it }
            }
        }

        return DesktopSong(
            id = canonicalPath,
            filePath = canonicalPath,
            title = title,
            artist = artist,
            album = album,
            durationMs = durationMs,
            formatLabel = if (ext == "AIF") "AIFF" else ext,
            sampleRateHz = sampleRateHz,
            bitRateKbps = bitRateKbps,
            bitsPerSample = bitsPerSample,
            folderName = file.parentFile?.name ?: "本地音乐",
        )
    }

    fun loadArtwork(filePath: String): ByteArray? {
        return runCatching {
            val file = File(filePath)
            if (!file.exists()) return null
            val audioFile = AudioFileIO.read(file)
            val embedded = audioFile.tag?.firstArtwork?.binaryData
            if (embedded != null && embedded.isNotEmpty()) return embedded

            val dir = file.parentFile ?: return null
            val candidates = listOf("cover.jpg", "cover.png", "folder.jpg", "folder.png", "front.jpg")
            for (name in candidates) {
                val f = File(dir, name)
                if (f.exists() && f.length() in 1024L..10_000_000L) return f.readBytes()
            }
            null
        }.getOrNull()
    }

    fun loadLyricsForSong(song: DesktopSong): List<LyricLine> {
        val audioFile = File(song.filePath)
        val lrcFile = File(audioFile.parentFile, "${audioFile.nameWithoutExtension}.lrc")
        if (lrcFile.exists() && lrcFile.length() in 4L..512_000L) {
            val rawText = readTextAutoCharset(lrcFile)
            val parsed = parseLrc(rawText)
            if (parsed.isNotEmpty()) return parsed
        }
        val embeddedText = runCatching {
            AudioFileIO.read(audioFile).tag?.getFirst(FieldKey.LYRICS)?.trim()
        }.getOrNull()
        if (!embeddedText.isNullOrBlank()) {
            val parsed = parseLrc(embeddedText)
            if (parsed.isNotEmpty()) return parsed
            val plain = embeddedText.lines().map { it.trim() }.filter { it.isNotEmpty() }
            if (plain.isNotEmpty()) {
                val stepMs = (song.durationMs / plain.size.coerceAtLeast(1)).coerceAtLeast(2500L)
                return plain.mapIndexed { idx, line ->
                    val (p, s) = splitBilingualLyric(line)
                    LyricLine(idx * stepMs, p, s)
                }
            }
        }
        return emptyList()
    }

    private fun readTextAutoCharset(file: File): String {
        val bytes = file.readBytes()
        if (bytes.size >= 3 && bytes[0] == 0xEF.toByte() && bytes[1] == 0xBB.toByte() && bytes[2] == 0xBF.toByte()) {
            return String(bytes, 3, bytes.size - 3, StandardCharsets.UTF_8)
        }
        val utf8 = String(bytes, StandardCharsets.UTF_8)
        return if (!utf8.contains('\uFFFD')) utf8 else runCatching { String(bytes, Charset.forName("GB18030")) }.getOrDefault(utf8)
    }

    private val timeRegex = Regex("""\[(\d{1,2}):(\d{2})(?:[.:](\d{1,3}))?]""")

    fun parseLrc(raw: String): List<LyricLine> {
        val grouped = linkedMapOf<Long, MutableList<String>>()
        raw.lines().forEach { line ->
            val matches = timeRegex.findAll(line).toList()
            if (matches.isEmpty()) return@forEach
            val text = line.replace(timeRegex, "").trim()
            if (text.isEmpty()) return@forEach
            matches.forEach { m ->
                val min = m.groupValues[1].toLongOrNull() ?: 0L
                val sec = m.groupValues[2].toLongOrNull() ?: 0L
                val fracRaw = m.groupValues[3]
                val ms = when (fracRaw.length) {
                    1 -> (fracRaw.toLongOrNull() ?: 0L) * 100L
                    2 -> (fracRaw.toLongOrNull() ?: 0L) * 10L
                    3 -> fracRaw.toLongOrNull() ?: 0L
                    else -> 0L
                }
                val totalMs = min * 60_000L + sec * 1000L + ms
                grouped.getOrPut(totalMs) { mutableListOf() }.add(text)
            }
        }
        return grouped.entries.sortedBy { it.key }.map { (timeMs, texts) ->
            if (texts.size >= 2) {
                LyricLine(timeMs, texts[0], texts[1])
            } else {
                val (p, s) = splitBilingualLyric(texts[0])
                LyricLine(timeMs, p, s)
            }
        }
    }
}

/**
 * Lightweight zero-dependency local config & stats persistence under ~/.puremusic/config_v2.txt
 */
object DesktopConfigStore {
    private val configFile: File
        get() {
            val dir = File(System.getProperty("user.home"), ".puremusic")
            if (!dir.exists()) dir.mkdirs()
            return File(dir, "config_v2.txt")
        }

    fun load(): DesktopConfig {
        val f = configFile
        if (!f.exists()) return DesktopConfig()
        var folders = emptyList<String>()
        var darkMode = false
        var accent = DesktopAccentColor.CINNABAR
        var density = DesktopLayoutDensity.STANDARD
        var fontScale = 1.0f
        var scene = DesktopSceneStyle.VINYL_STUDY
        var spectrum = DesktopSpectrumStyle.SILK_RIBBON
        var favs = emptySet<String>()
        var recents = emptyList<String>()
        val playCounts = mutableMapOf<String, Int>()
        val hourlyPlays = mutableMapOf<Int, Int>()
        val playlists = mutableListOf<DesktopPlaylist>()
        var eqEnabled = false
        var eqBands = listOf(0f, 0f, 0f, 0f, 0f)
        val lyricOffsets = mutableMapOf<String, Long>()
        var volume = 0.85f
        var repeatMode = RepeatMode.ALL
        var shuffleEnabled = false
        var lastSongPath: String? = null
        var windowWidthDp = 1240
        var windowHeightDp = 800

        runCatching {
            f.readLines(StandardCharsets.UTF_8).forEach { line ->
                val idx = line.indexOf('=')
                if (idx <= 0) return@forEach
                val k = line.substring(0, idx)
                val v = line.substring(idx + 1)
                when (k) {
                    "folders" -> folders = v.split('|').filter { it.isNotBlank() }
                    "darkMode" -> darkMode = v.toBoolean()
                    "accent" -> accent = DesktopAccentColor.entries.firstOrNull { it.name == v } ?: DesktopAccentColor.CINNABAR
                    "density" -> density = DesktopLayoutDensity.entries.firstOrNull { it.name == v } ?: DesktopLayoutDensity.STANDARD
                    "fontScale" -> fontScale = (v.toFloatOrNull() ?: 1.0f).coerceIn(0.85f, 1.25f)
                    "scene" -> scene = DesktopSceneStyle.entries.firstOrNull { it.name == v } ?: DesktopSceneStyle.VINYL_STUDY
                    "spectrum" -> spectrum = DesktopSpectrumStyle.entries.firstOrNull { it.name == v } ?: DesktopSpectrumStyle.SILK_RIBBON
                    "favorites" -> favs = v.split('|').filter { it.isNotBlank() }.toSet()
                    "recents" -> recents = v.split('|').filter { it.isNotBlank() }
                    "eqEnabled" -> eqEnabled = v.toBoolean()
                    "volume" -> volume = (v.toFloatOrNull() ?: 0.85f).coerceIn(0f, 1f)
                    "repeatMode" -> repeatMode = RepeatMode.entries.firstOrNull { it.name == v } ?: RepeatMode.ALL
                    "shuffleEnabled" -> shuffleEnabled = v.toBoolean()
                    "lastSongPath" -> lastSongPath = v.takeIf { it.isNotBlank() }
                    "windowWidthDp" -> windowWidthDp = (v.toIntOrNull() ?: 1240).coerceIn(900, 3840)
                    "windowHeightDp" -> windowHeightDp = (v.toIntOrNull() ?: 800).coerceIn(600, 2160)
                    "eqBands" -> {
                        val parsed = v.split(',').mapNotNull { it.toFloatOrNull() }
                        if (parsed.size == 5) eqBands = parsed
                    }
                    "playCount" -> {
                        val sep = v.lastIndexOf("::")
                        if (sep > 0) {
                            val path = v.substring(0, sep)
                            val cnt = v.substring(sep + 2).toIntOrNull() ?: 0
                            if (cnt > 0) playCounts[path] = cnt
                        }
                    }
                    "hourly" -> {
                        v.split(',').forEach { pair ->
                            val parts = pair.split(':')
                            if (parts.size == 2) {
                                val h = parts[0].toIntOrNull()
                                val c = parts[1].toIntOrNull()
                                if (h != null && h in 0..23 && c != null && c > 0) hourlyPlays[h] = c
                            }
                        }
                    }
                    "playlist" -> {
                        val parts = v.split("::", limit = 3)
                        if (parts.size == 3) {
                            val paths = parts[2].split('|').filter { it.isNotBlank() }
                            playlists.add(DesktopPlaylist(parts[0], parts[1], paths))
                        }
                    }
                    "lyricOffset" -> {
                        val sep = v.lastIndexOf("::")
                        if (sep > 0) {
                            val path = v.substring(0, sep)
                            val ms = v.substring(sep + 2).toLongOrNull() ?: 0L
                            if (ms != 0L) lyricOffsets[path] = ms
                        }
                    }
                }
            }
        }
        return DesktopConfig(
            folders = folders,
            darkMode = darkMode,
            accentColor = accent,
            layoutDensity = density,
            fontScale = fontScale,
            sceneStyle = scene,
            spectrumStyle = spectrum,
            favoritePaths = favs,
            recentPaths = recents,
            playCounts = playCounts,
            hourlyPlays = hourlyPlays,
            playlists = playlists,
            eqEnabled = eqEnabled,
            eqBandsDb = eqBands,
            lyricOffsetsMs = lyricOffsets,
            volume = volume,
            repeatMode = repeatMode,
            shuffleEnabled = shuffleEnabled,
            lastSongPath = lastSongPath,
            windowWidthDp = windowWidthDp,
            windowHeightDp = windowHeightDp,
        )
    }

    fun save(cfg: DesktopConfig) {
        runCatching {
            val sb = StringBuilder()
            sb.appendLine("folders=${cfg.folders.joinToString("|")}")
            sb.appendLine("darkMode=${cfg.darkMode}")
            sb.appendLine("accent=${cfg.accentColor.name}")
            sb.appendLine("density=${cfg.layoutDensity.name}")
            sb.appendLine("fontScale=${cfg.fontScale}")
            sb.appendLine("scene=${cfg.sceneStyle.name}")
            sb.appendLine("spectrum=${cfg.spectrumStyle.name}")
            sb.appendLine("favorites=${cfg.favoritePaths.joinToString("|")}")
            sb.appendLine("recents=${cfg.recentPaths.take(50).joinToString("|")}")
            sb.appendLine("eqEnabled=${cfg.eqEnabled}")
            sb.appendLine("volume=${cfg.volume}")
            sb.appendLine("repeatMode=${cfg.repeatMode.name}")
            sb.appendLine("shuffleEnabled=${cfg.shuffleEnabled}")
            sb.appendLine("lastSongPath=${cfg.lastSongPath.orEmpty()}")
            sb.appendLine("windowWidthDp=${cfg.windowWidthDp}")
            sb.appendLine("windowHeightDp=${cfg.windowHeightDp}")
            sb.appendLine("eqBands=${cfg.eqBandsDb.joinToString(",")}")
            val hourlyStr = cfg.hourlyPlays.entries.joinToString(",") { "${it.key}:${it.value}" }
            sb.appendLine("hourly=$hourlyStr")
            cfg.playCounts.forEach { (path, cnt) ->
                if (cnt > 0) sb.appendLine("playCount=$path::$cnt")
            }
            cfg.playlists.forEach { pl ->
                sb.appendLine("playlist=${pl.id}::${pl.name.replace("::", " ")}::${pl.songPaths.joinToString("|")}")
            }
            cfg.lyricOffsetsMs.forEach { (path, ms) ->
                if (ms != 0L) sb.appendLine("lyricOffset=$path::$ms")
            }
            configFile.writeText(sb.toString(), StandardCharsets.UTF_8)
        }
    }
}
