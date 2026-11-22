package com.puremusic.desktop

import androidx.compose.ui.graphics.ImageBitmap

data class DesktopSong(
    val id: Long,
    val path: String,
    val title: String,
    val artist: String,
    val album: String,
    val folderName: String,
    val durationMs: Long,
    val formatBadge: String,
    val embeddedLyrics: String? = null,
)

data class LyricLine(
    val timeMs: Long,
    val primaryText: String,
    val secondaryText: String? = null,
)

enum class DesktopTab(val label: String) {
    NOW_PLAYING("正在播放"),
    LIBRARY("全部曲库"),
    ALBUMS("专辑"),
    FOLDERS("文件夹"),
    FAVORITES("我喜欢"),
    SETTINGS("设置"),
}

enum class DesktopSceneStyle(val label: String) {
    VINYL_STUDY("黑胶书房"),
    SPATIAL_GALLERY("空间唱片廊"),
    VINYL_LIGHTFIELD("黑胶光场"),
}

enum class DesktopSpectrumStyle(val label: String) {
    SILK_RIBBON("丝带微胶囊"),
    HARMONIC_STRINGS("极简弦波"),
    DOT_MATRIX("点阵峰标"),
    INK_MOUNTAIN("水墨声山"),
}

enum class RepeatMode(val label: String) {
    ALL("列表循环"),
    ONE("单曲循环"),
    SHUFFLE("随机播放"),
}

fun formatDuration(ms: Long): String {
    val totalSec = (ms.coerceAtLeast(0L) / 1000L)
    val min = totalSec / 60L
    val sec = totalSec % 60L
    return "%02d:%02d".format(min, sec)
}

private val inlineBilingualRegex = Regex("""^([A-Za-z0-9\p{Punct}\s'’\-—–]+?)[\s/|]+([\u4e00-\u9fff].+)$""")

fun splitBilingualLyric(line: LyricLine): Pair<String, String?> {
    val primary = line.primaryText.trim()
    val explicitTrans = line.secondaryText?.trim()?.takeIf { it.isNotBlank() }
    if (explicitTrans != null) return primary to explicitTrans

    val match = inlineBilingualRegex.matchEntire(primary)
    if (match != null) {
        val orig = match.groupValues[1].trim()
        val trans = match.groupValues[2].trim()
        val latinLetters = orig.count { it.isLetter() }
        val hanChars = trans.count { it in '\u4e00'..'\u9fff' }
        if (latinLetters >= 3 && hanChars >= 2) {
            return orig to trans
        }
    }
    return primary to null
}

object ArtworkCache {
    private val cache = LinkedHashMap<String, ImageBitmap?>(64, 0.75f, true)

    @Synchronized
    fun get(path: String): ImageBitmap? = cache[path]

    @Synchronized
    fun contains(path: String): Boolean = cache.containsKey(path)

    @Synchronized
    fun put(path: String, bmp: ImageBitmap?) {
        if (cache.size > 120) {
            val firstKey = cache.keys.firstOrNull()
            if (firstKey != null) cache.remove(firstKey)
        }
        cache[path] = bmp
    }
}
