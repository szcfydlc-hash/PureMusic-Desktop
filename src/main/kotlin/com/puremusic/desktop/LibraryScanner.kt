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
    private val supportedExtensions = setOf(
        "mp3", "flac", "wav", "aiff", "aif",
        "m4a", "aac", "alac", "ogg", "opus",
        "wma", "ape", "wv", "dsf", "dff",
    )

    @Volatile private var customCoverOverrides: Map<String, String> = emptyMap()
    @Volatile private var customLyricOverrides: Map<String, String> = emptyMap()

    init {
        Logger.getLogger("org.jaudiotagger").level = Level.OFF
    }

    fun setCustomOverrides(covers: Map<String, String>, lyrics: Map<String, String>) {
        customCoverOverrides = covers
        customLyricOverrides = lyrics
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
        var genre = ""
        var durationMs = 0L
        var sampleRateHz = 44100
        var bitRateKbps = 0
        var bitsPerSample = 16
        var taggerSucceeded = false

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
                taggerSucceeded = durationMs > 0L
            }
            val tag = audioFile.tag
            if (tag != null) {
                tag.getFirst(FieldKey.TITLE)?.trim()?.takeIf { it.isNotEmpty() }?.let { title = it }
                tag.getFirst(FieldKey.ARTIST)?.trim()?.takeIf { it.isNotEmpty() }?.let { artist = it }
                tag.getFirst(FieldKey.ALBUM)?.trim()?.takeIf { it.isNotEmpty() }?.let { album = it }
                tag.getFirst(FieldKey.GENRE)?.trim()?.takeIf { it.isNotEmpty() }?.let { genre = normalizeGenre(it) }
            }
        }

        // Fallback to bundled ffprobe for formats JAudiotagger doesn't parse (OPUS, APE, WV, DSF, WMA, etc.)
        if (!taggerSucceeded || durationMs <= 0L) {
            DesktopFfmpegLocator.probeMetadata(file)?.let { probed ->
                if (probed.durationMs > 0L) durationMs = probed.durationMs
                if (probed.sampleRateHz > 0) sampleRateHz = probed.sampleRateHz
                if (probed.bitRateKbps > 0) bitRateKbps = probed.bitRateKbps
                if (probed.bitsPerSample > 0) bitsPerSample = probed.bitsPerSample
                if (title == file.nameWithoutExtension && !probed.title.isNullOrBlank()) title = probed.title
                if (artist == "未知歌手" && !probed.artist.isNullOrBlank()) artist = probed.artist
                if (album == "未知专辑" && !probed.album.isNullOrBlank()) album = probed.album
            }
        }

        if (durationMs <= 0L) durationMs = 210_000L

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
            genre = genre,
        )
    }

    fun loadArtwork(filePath: String): ByteArray? {
        return runCatching {
            // 1. Check user-replaced custom cover first
            customCoverOverrides[filePath]?.let { customPath ->
                val cf = File(customPath)
                if (cf.exists() && cf.length() in 256L..20_000_000L) {
                    return cf.readBytes()
                }
            }

            val file = File(filePath)
            if (!file.exists()) return null

            // 2. Embedded artwork via JAudiotagger
            val embedded = runCatching {
                AudioFileIO.read(file).tag?.firstArtwork?.binaryData
            }.getOrNull()
            if (embedded != null && embedded.isNotEmpty()) return embedded

            // 3. Same-folder cover image
            val dir = file.parentFile ?: return null
            val candidates = listOf("cover.jpg", "cover.png", "cover.webp", "folder.jpg", "folder.png", "front.jpg")
            for (name in candidates) {
                val f = File(dir, name)
                if (f.exists() && f.length() in 1024L..12_000_000L) return f.readBytes()
            }
            null
        }.getOrNull()
    }

    fun loadLyricsForSong(song: DesktopSong): List<LyricLine> {
        // 1. Check user-imported custom LRC file first
        customLyricOverrides[song.filePath]?.let { customPath ->
            val cf = File(customPath)
            if (cf.exists() && cf.length() in 4L..1_024_000L) {
                val parsed = parseLrcOrPlain(readTextAutoCharset(cf), song.durationMs)
                if (parsed.isNotEmpty()) return parsed
            }
        }

        // 2. Check sibling .lrc file
        val audioFile = File(song.filePath)
        val lrcFile = File(audioFile.parentFile, "${audioFile.nameWithoutExtension}.lrc")
        if (lrcFile.exists() && lrcFile.length() in 4L..1_024_000L) {
            val parsed = parseLrcOrPlain(readTextAutoCharset(lrcFile), song.durationMs)
            if (parsed.isNotEmpty()) return parsed
        }

        // 3. Embedded tag lyrics
        val embeddedText = runCatching {
            AudioFileIO.read(audioFile).tag?.getFirst(FieldKey.LYRICS)?.trim()
        }.getOrNull()
        if (!embeddedText.isNullOrBlank()) {
            val parsed = parseLrcOrPlain(embeddedText, song.durationMs)
            if (parsed.isNotEmpty()) return parsed
        }
        return emptyList()
    }

    private fun parseLrcOrPlain(rawText: String, durationMs: Long): List<LyricLine> {
        val parsed = parseLrc(rawText)
        if (parsed.isNotEmpty()) return parsed
        val plain = rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (plain.isEmpty()) return emptyList()
        val stepMs = (durationMs / plain.size.coerceAtLeast(1)).coerceAtLeast(2500L)
        return plain.mapIndexed { idx, line ->
            val (p, s) = splitBilingualLyric(line)
            LyricLine(idx * stepMs, p, s)
        }
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
    private val enhancedWordRegex = Regex("""<(\d{1,2}):(\d{2})(?:[.:](\d{1,3}))?>([^<]*)""")

    private fun decodeHtmlEntities(s: String): String = s
        .replace("&apos;", "'")
        .replace("&#39;", "'")
        .replace("&quot;", "\"")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")

    private fun parseFracMs(fracRaw: String): Long = when (fracRaw.length) {
        1 -> (fracRaw.toLongOrNull() ?: 0L) * 100L
        2 -> (fracRaw.toLongOrNull() ?: 0L) * 10L
        3 -> fracRaw.toLongOrNull() ?: 0L
        else -> 0L
    }

    private fun parseEnhancedWords(rawLineBody: String): List<LyricWord> {
        val matches = enhancedWordRegex.findAll(rawLineBody).toList()
        if (matches.isEmpty()) return emptyList()
        val starts = matches.map { m ->
            val min = m.groupValues[1].toLongOrNull() ?: 0L
            val sec = m.groupValues[2].toLongOrNull() ?: 0L
            min * 60_000L + sec * 1000L + parseFracMs(m.groupValues[3])
        }
        return matches.mapIndexedNotNull { idx, m ->
            val txt = decodeHtmlEntities(m.groupValues[4])
            if (txt.isEmpty()) return@mapIndexedNotNull null
            val st = starts[idx]
            val en = if (idx + 1 < starts.size) starts[idx + 1].coerceAtLeast(st + 60L) else st + 420L
            LyricWord(startMs = st, endMs = en, text = txt)
        }
    }

    fun parseLrc(raw: String): List<LyricLine> {
        val grouped = linkedMapOf<Long, MutableList<Pair<String, List<LyricWord>>>>()
        raw.lines().forEach { line ->
            val matches = timeRegex.findAll(line).toList()
            if (matches.isEmpty()) return@forEach
            val body = line.replace(timeRegex, "").trim()
            if (body.isEmpty()) return@forEach
            val words = parseEnhancedWords(body)
            val cleanText = decodeHtmlEntities(body.replace(Regex("""<\d{1,2}:\d{2}(?:[.:]\d{1,3})?>"""), "")).trim()
            if (cleanText.isEmpty()) return@forEach
            matches.forEach { m ->
                val min = m.groupValues[1].toLongOrNull() ?: 0L
                val sec = m.groupValues[2].toLongOrNull() ?: 0L
                val totalMs = min * 60_000L + sec * 1000L + parseFracMs(m.groupValues[3])
                grouped.getOrPut(totalMs) { mutableListOf() }.add(cleanText to words)
            }
        }
        return grouped.entries.sortedBy { it.key }.map { (timeMs, entries) ->
            val primaryWords = entries.firstOrNull { it.second.isNotEmpty() }?.second.orEmpty()
            if (entries.size >= 2) {
                LyricLine(timeMs, entries[0].first, entries[1].first, primaryWords)
            } else {
                val (p, s) = splitBilingualLyric(entries[0].first)
                LyricLine(timeMs, p, s, primaryWords)
            }
        }
    }

    private fun normalizeGenre(raw: String?): String {
        val cleaned = raw
            ?.replace(Regex("""^\(\d+\)\s*"""), "")
            ?.trim()
            .orEmpty()
        if (cleaned.length <= 1) return ""
        val lower = cleaned.lowercase()
        if (lower == "<unknown>" || lower == "unknown" || lower == "other" || lower == "misc" || lower == "none" || lower.all { it.isDigit() }) {
            return ""
        }
        return when {
            lower.contains("singer") || lower.contains("songwriter") || lower.contains("folk") || lower.contains("acoustic") || lower.contains("民谣") -> "民谣"
            lower.contains("electronic") || lower.contains("electronica") || lower.contains("edm") || lower.contains("trip-hop") || lower.contains("trip hop") || lower.contains("downtempo") || lower.contains("house") || lower.contains("techno") || lower.contains("trance") || lower.contains("synth") || lower.contains("lo-fi") || lower.contains("lofi") || lower.contains("电子") -> "电子"
            lower.contains("new age") || lower.contains("ambient") || lower.contains("easy listening") || lower.contains("instrumental") || lower.contains("lounge") || lower.contains("chillout") || lower.contains("piano") || lower.contains("world") || lower.contains("轻音乐") || lower.contains("纯音乐") || lower.contains("器乐") -> "轻音乐"
            lower.contains("indie") || lower.contains("alternative") || lower.contains("shoegaze") || lower.contains("post-rock") || lower.contains("britpop") || lower.contains("独立") || lower.contains("另类") -> "独立"
            lower.contains("soundtrack") || lower.contains("ost") || lower.contains("score") || lower.contains("film") || lower.contains("game") || lower.contains("anime") || lower.contains("原声") || lower.contains("影视") -> "原声"
            lower.contains("jazz") || lower.contains("swing") || lower.contains("bossa") || lower.contains("爵士") -> "爵士"
            lower.contains("metal") || lower.contains("金属") -> "金属"
            lower.contains("rock") || lower.contains("punk") || lower.contains("grunge") || lower.contains("摇滚") -> "摇滚"
            lower.contains("classical") || lower.contains("orchestra") || lower.contains("symphony") || lower.contains("opera") || lower.contains("古典") -> "古典"
            lower.contains("hip-hop") || lower.contains("hip hop") || lower.contains("rap") || lower.contains("trap") || lower.contains("说唱") || lower.contains("嘻哈") -> "说唱"
            lower.contains("r&b") || lower.contains("rnb") || lower.contains("soul") || lower.contains("funk") || lower.contains("节奏布鲁斯") -> "R&B"
            lower.contains("blues") || lower.contains("蓝调") -> "蓝调"
            lower.contains("country") || lower.contains("乡村") -> "乡村"
            lower.contains("pop") || lower.contains("top 40") || lower.contains("top40") || lower.contains("dance") || lower.contains("vocal") || lower.contains("ballad") || lower.contains("流行") || lower.contains("华语") -> "流行"
            else -> ""
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
        var appFontStyle = DesktopFontStyle.YAHEI
        var lyricFontStyle = DesktopLyricFontStyle.SERIF
        var fontWeightLevel = DesktopFontWeightLevel.STANDARD
        var fontWeightValue = 400
        var lyricSlantDegrees = 0f
        var customFontPath: String? = null
        var lyricFontScale = 1.0f
        var floatingControlBar = true
        var liquidGlassEnabled = true
        var liquidGlassLevel = DesktopLiquidGlassLevel.MEDIUM
        var controlBarHeightDp = 62
        var shortAudioFilterSec = 0
        var scene = DesktopSceneStyle.VINYL_STUDY
        var spectrum = DesktopSpectrumStyle.SILK_RIBBON
        var favs = emptySet<String>()
        var recents = emptyList<String>()
        val playCounts = mutableMapOf<String, Int>()
        val hourlyPlays = mutableMapOf<Int, Int>()
        val playlists = mutableListOf<DesktopPlaylist>()
        var karaokeLyricMode = DesktopKaraokeLyricMode.SMART
        var crossfadeSec = 0
        var volumeNormalizationEnabled = false
        var eqEnabled = false
        var eqBands = listOf(0f, 0f, 0f, 0f, 0f)
        val lyricOffsets = mutableMapOf<String, Long>()
        val customCovers = mutableMapOf<String, String>()
        val customLyrics = mutableMapOf<String, String>()
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
                    "fontScale" -> fontScale = (v.toFloatOrNull() ?: 1.0f).coerceIn(0.85f, 1.30f)
                    "appFontStyle" -> appFontStyle = DesktopFontStyle.entries.firstOrNull { it.name == v } ?: DesktopFontStyle.YAHEI
                    "lyricFontStyle" -> lyricFontStyle = DesktopLyricFontStyle.entries.firstOrNull { it.name == v } ?: DesktopLyricFontStyle.SERIF
                    "fontWeightLevel" -> fontWeightLevel = DesktopFontWeightLevel.entries.firstOrNull { it.name == v } ?: DesktopFontWeightLevel.STANDARD
                    "fontWeightValue" -> fontWeightValue = (v.toIntOrNull() ?: 400).coerceIn(200, 900)
                    "lyricSlantDegrees" -> lyricSlantDegrees = (v.toFloatOrNull() ?: 0f).coerceIn(0f, 16f)
                    "customFontPath" -> customFontPath = v.takeIf { it.isNotBlank() }
                    "lyricFontScale" -> lyricFontScale = (v.toFloatOrNull() ?: 1.0f).coerceIn(0.75f, 1.45f)
                    "floatingControlBar" -> floatingControlBar = v.toBoolean()
                    "liquidGlassEnabled" -> liquidGlassEnabled = v.toBoolean()
                    "liquidGlassLevel" -> liquidGlassLevel = DesktopLiquidGlassLevel.entries.firstOrNull { it.name == v } ?: DesktopLiquidGlassLevel.MEDIUM
                    "controlBarHeightDp" -> controlBarHeightDp = (v.toIntOrNull() ?: 62).coerceIn(54, 76)
                    "shortAudioFilterSec" -> shortAudioFilterSec = (v.toIntOrNull() ?: 0).coerceIn(0, 120)
                    "scene" -> scene = DesktopSceneStyle.entries.firstOrNull { it.name == v } ?: DesktopSceneStyle.VINYL_STUDY
                    "spectrum" -> spectrum = DesktopSpectrumStyle.entries.firstOrNull { it.name == v } ?: DesktopSpectrumStyle.SILK_RIBBON
                    "favorites" -> favs = v.split('|').filter { it.isNotBlank() }.toSet()
                    "recents" -> recents = v.split('|').filter { it.isNotBlank() }
                    "karaokeLyricMode" -> DesktopKaraokeLyricMode.entries.firstOrNull { it.name == v }?.let { karaokeLyricMode = it }
                    "crossfadeSec" -> v.toIntOrNull()?.let { crossfadeSec = it.coerceIn(0, 6) }
                    "volumeNormalizationEnabled" -> volumeNormalizationEnabled = v.toBoolean()
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
                    "customCover" -> {
                        val sep = v.lastIndexOf("::")
                        if (sep > 0) {
                            val songPath = v.substring(0, sep)
                            val imgPath = v.substring(sep + 2)
                            if (songPath.isNotBlank() && imgPath.isNotBlank()) customCovers[songPath] = imgPath
                        }
                    }
                    "customLyric" -> {
                        val sep = v.lastIndexOf("::")
                        if (sep > 0) {
                            val songPath = v.substring(0, sep)
                            val lrcPath = v.substring(sep + 2)
                            if (songPath.isNotBlank() && lrcPath.isNotBlank()) customLyrics[songPath] = lrcPath
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
            appFontStyle = appFontStyle,
            lyricFontStyle = lyricFontStyle,
            fontWeightLevel = fontWeightLevel,
            fontWeightValue = fontWeightValue,
            lyricSlantDegrees = lyricSlantDegrees,
            customFontPath = customFontPath,
            lyricFontScale = lyricFontScale,
            floatingControlBar = floatingControlBar,
            liquidGlassEnabled = liquidGlassEnabled,
            liquidGlassLevel = liquidGlassLevel,
            controlBarHeightDp = controlBarHeightDp,
            shortAudioFilterSec = shortAudioFilterSec,
            sceneStyle = scene,
            spectrumStyle = spectrum,
            favoritePaths = favs,
            recentPaths = recents,
            playCounts = playCounts,
            hourlyPlays = hourlyPlays,
            playlists = playlists,
            karaokeLyricMode = karaokeLyricMode,
            crossfadeSec = crossfadeSec,
            volumeNormalizationEnabled = volumeNormalizationEnabled,
            eqEnabled = eqEnabled,
            eqBandsDb = eqBands,
            lyricOffsetsMs = lyricOffsets,
            customCoverPaths = customCovers,
            customLyricPaths = customLyrics,
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
            sb.appendLine("appFontStyle=${cfg.appFontStyle.name}")
            sb.appendLine("lyricFontStyle=${cfg.lyricFontStyle.name}")
            sb.appendLine("fontWeightLevel=${cfg.fontWeightLevel.name}")
            sb.appendLine("fontWeightValue=${cfg.fontWeightValue}")
            sb.appendLine("lyricSlantDegrees=${cfg.lyricSlantDegrees}")
            sb.appendLine("customFontPath=${cfg.customFontPath.orEmpty()}")
            sb.appendLine("lyricFontScale=${cfg.lyricFontScale}")
            sb.appendLine("floatingControlBar=${cfg.floatingControlBar}")
            sb.appendLine("liquidGlassEnabled=${cfg.liquidGlassEnabled}")
            sb.appendLine("liquidGlassLevel=${cfg.liquidGlassLevel.name}")
            sb.appendLine("controlBarHeightDp=${cfg.controlBarHeightDp}")
            sb.appendLine("shortAudioFilterSec=${cfg.shortAudioFilterSec}")
            sb.appendLine("scene=${cfg.sceneStyle.name}")
            sb.appendLine("spectrum=${cfg.spectrumStyle.name}")
            sb.appendLine("favorites=${cfg.favoritePaths.joinToString("|")}")
            sb.appendLine("recents=${cfg.recentPaths.take(50).joinToString("|")}")
            sb.appendLine("karaokeLyricMode=${cfg.karaokeLyricMode.name}")
            sb.appendLine("crossfadeSec=${cfg.crossfadeSec}")
            sb.appendLine("volumeNormalizationEnabled=${cfg.volumeNormalizationEnabled}")
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
            cfg.customCoverPaths.forEach { (songPath, imgPath) ->
                if (songPath.isNotBlank() && imgPath.isNotBlank()) sb.appendLine("customCover=$songPath::$imgPath")
            }
            cfg.customLyricPaths.forEach { (songPath, lrcPath) ->
                if (songPath.isNotBlank() && lrcPath.isNotBlank()) sb.appendLine("customLyric=$songPath::$lrcPath")
            }
            configFile.writeText(sb.toString(), StandardCharsets.UTF_8)
        }
    }
}
