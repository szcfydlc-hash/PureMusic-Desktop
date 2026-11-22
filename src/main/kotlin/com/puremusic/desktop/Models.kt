package com.puremusic.desktop

import java.util.Locale

data class DesktopSong(
    val id: String,
    val filePath: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val formatLabel: String = "AUDIO",
    val sampleRateHz: Int = 44100,
    val bitRateKbps: Int = 0,
    val bitsPerSample: Int = 16,
    val folderName: String = "",
) {
    val isLossless: Boolean
        get() = formatLabel in setOf("FLAC", "WAV", "AIFF", "ALAC", "APE")

    val isHiRes: Boolean
        get() = isLossless && (sampleRateHz >= 48000 || bitsPerSample >= 24 || bitRateKbps >= 900)

    val audioSpecText: String
        get() {
            val sr = if (sampleRateHz > 0) {
                val khz = sampleRateHz / 1000.0
                if (sampleRateHz % 1000 == 0) "${sampleRateHz / 1000}kHz" else String.format(Locale.US, "%.1fkHz", khz)
            } else {
                ""
            }
            val br = if (bitRateKbps > 0) "${bitRateKbps}kbps" else ""
            return listOf(formatLabel, sr, br).filter { it.isNotBlank() }.joinToString(" · ")
        }
}

data class DesktopAlbum(
    val name: String,
    val artist: String,
    val songs: List<DesktopSong>,
)

data class DesktopArtist(
    val name: String,
    val songs: List<DesktopSong>,
)

data class DesktopFolder(
    val name: String,
    val path: String,
    val songs: List<DesktopSong>,
)

data class DesktopPlaylist(
    val id: String,
    val name: String,
    val songPaths: List<String>,
)

data class LyricLine(
    val timeMs: Long,
    val primaryText: String,
    val secondaryText: String? = null,
)

enum class DesktopTab(val label: String) {
    LIBRARY("曲库"),
    ALBUMS("专辑"),
    PLAYLISTS("歌单"),
    REPORT("听歌报告"),
    EQUALIZER("均衡器"),
    SETTINGS("设置"),
}

enum class LibrarySubTab(val label: String) {
    SONGS("歌曲"),
    ARTISTS("歌手"),
    FOLDERS("文件夹"),
}

enum class SortOrder(val label: String) {
    TITLE("按歌名"),
    ARTIST("按歌手"),
    ALBUM("按专辑"),
    DURATION("按时长"),
}

enum class DesktopSceneStyle(val label: String) {
    VINYL_STUDY("动感黑胶书房"),
    VINYL_LIGHTFIELD("黑胶光场"),
    POSTER_WALL("唱片海报墙"),
    VINTAGE_HIFI("复古音响"),
    SPATIAL_GALLERY("空间唱片廊"),
}

enum class DesktopSpectrumStyle(val label: String) {
    SILK_RIBBON("丝带微胶囊"),
    HARMONIC_STRINGS("极简弦波"),
    DOT_MATRIX("点阵峰标"),
    INK_MOUNTAIN("水墨声山"),
}

enum class DesktopAccentColor(val label: String, val shortLabel: String, val hex: Long) {
    CINNABAR("朱砂红", "朱砂", 0xFFA9321E),
    PINE_GREEN("黛青绿", "黛绿", 0xFF2F5D50),
    PRUSSIAN_BLUE("普鲁士蓝", "普蓝", 0xFF2B4666),
    WALNUT("胡桃棕", "胡桃", 0xFF7A5638),
    OCHRE_GOLD("赭石金", "赭金", 0xFF9A7230),
    PURE_INK("纯墨黑", "纯墨", 0xFF262320),
}

enum class DesktopLayoutDensity(
    val label: String,
    val artSizeDp: Int,
    val rowPaddingDp: Int,
    val gridSpacingDp: Int,
    val cardPaddingDp: Int,
) {
    COMPACT("紧凑", 38, 5, 10, 12),
    STANDARD("标准", 46, 9, 14, 16),
    SPACIOUS("宽松", 54, 14, 18, 20),
}

enum class RepeatMode(val label: String) {
    OFF("顺序播放"),
    ALL("列表循环"),
    ONE("单曲循环"),
}

enum class EqPreset(val label: String, val gainsDb: List<Float>) {
    FLAT("原声", listOf(0f, 0f, 0f, 0f, 0f)),
    VOCAL("人声", listOf(-1.5f, 1.0f, 4.0f, 3.0f, 0.5f)),
    BASS("重低音", listOf(6.0f, 3.5f, 0f, -0.5f, 1.0f)),
    CLASSICAL("古典", listOf(2.5f, 1.0f, 0f, 2.0f, 3.5f)),
    JAZZ("爵士", listOf(3.0f, 2.0f, -1.0f, 2.0f, 3.0f)),
    VINYL_WARM("黑胶暖声", listOf(3.5f, 2.5f, 1.0f, -1.0f, -2.5f)),
}

data class DesktopPlayerState(
    val currentSong: DesktopSong? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val volume: Float = 0.85f,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.ALL,
)

data class DesktopConfig(
    val folders: List<String> = emptyList(),
    val darkMode: Boolean = false,
    val accentColor: DesktopAccentColor = DesktopAccentColor.CINNABAR,
    val layoutDensity: DesktopLayoutDensity = DesktopLayoutDensity.STANDARD,
    val fontScale: Float = 1.0f,
    val sceneStyle: DesktopSceneStyle = DesktopSceneStyle.SPATIAL_GALLERY,
    val spectrumStyle: DesktopSpectrumStyle = DesktopSpectrumStyle.SILK_RIBBON,
    val favoritePaths: Set<String> = emptySet(),
    val recentPaths: List<String> = emptyList(),
    val playCounts: Map<String, Int> = emptyMap(),
    val hourlyPlays: Map<Int, Int> = emptyMap(),
    val playlists: List<DesktopPlaylist> = emptyList(),
    val eqEnabled: Boolean = false,
    val eqBandsDb: List<Float> = listOf(0f, 0f, 0f, 0f, 0f),
    val lyricOffsetsMs: Map<String, Long> = emptyMap(),
)

fun formatDuration(ms: Long): String {
    val totalSeconds = (ms.coerceAtLeast(0L) / 1000L).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}

fun splitBilingualLyric(raw: String, existingSecondary: String? = null): Pair<String, String?> {
    if (!existingSecondary.isNullOrBlank()) return raw.trim() to existingSecondary.trim()
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return "" to null
    val delims = listOf(" / ", " | ", "｜", "　　", "  ")
    for (d in delims) {
        val idx = trimmed.indexOf(d)
        if (idx > 0 && idx < trimmed.length - d.length) {
            val left = trimmed.substring(0, idx).trim()
            val right = trimmed.substring(idx + d.length).trim()
            if (left.isNotEmpty() && right.isNotEmpty()) return left to right
        }
    }
    val latinCjk = Regex("""^([A-Za-z0-9\p{Punct}\s']{3,}?)\s+([\u4e00-\u9fff\u3040-\u30ff\uac00-\ud7af].+)$""")
    latinCjk.matchEntire(trimmed)?.let { m ->
        val left = m.groupValues[1].trim()
        val right = m.groupValues[2].trim()
        if (left.any { it.isLetter() } && right.isNotEmpty()) return left to right
    }
    return trimmed to null
}
