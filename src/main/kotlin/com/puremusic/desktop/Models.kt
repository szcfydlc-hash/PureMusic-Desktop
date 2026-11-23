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
    val genre: String = "",
) {
    val isLossless: Boolean
        get() = formatLabel in setOf("FLAC", "WAV", "AIFF", "ALAC", "APE", "WV", "DSF", "DFF")

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
            val bits = if (bitsPerSample >= 24) "${bitsPerSample}bit" else ""
            val br = if (bitRateKbps > 0) "${bitRateKbps}kbps" else ""
            return listOf(formatLabel, sr, bits, br).filter { it.isNotBlank() }.joinToString(" · ")
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

data class LyricWord(
    val startMs: Long,
    val endMs: Long,
    val text: String,
)

enum class DesktopKaraokeLyricMode(val label: String, val desc: String) {
    SMART("智能逐字", "优先精确词轴，普通 LRC 智能加权推进"),
    EXACT_ONLY("仅精确", "仅对含逐字时间轴的歌词启用"),
    CLASSIC("经典整行", "经典整行高亮，不逐字扫描"),
}

data class LyricLine(
    val timeMs: Long,
    val primaryText: String,
    val secondaryText: String? = null,
    val words: List<LyricWord> = emptyList(),
)

// Clean 4-Chapter Book-Index Navigation (100% aligned with Mobile)
enum class DesktopTab(val label: String) {
    LIBRARY("曲库"),
    ALBUMS("专辑"),
    PLAYLISTS("歌单"),
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

// 4 Cohesive Immersive Scenes sharing ONE unified top/bottom control language
enum class DesktopSceneStyle(val label: String) {
    VINYL_STUDY("黑胶书房"),
    VINYL_LIGHTFIELD("黑胶光场"),
    SPATIAL_GALLERY("空间唱片廊"),
    POSTER_WALL("唱片海报墙"),
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
    COMPACT("紧凑", 38, 6, 12, 14),
    STANDARD("标准", 44, 11, 16, 18),
    SPACIOUS("宽松", 52, 16, 20, 22),
}

enum class DesktopFontStyle(val label: String) {
    YAHEI("微软雅黑"),
    SONGTI("宋体书卷"),
    KAITI("楷体手书"),
    CUSTOM("自定义字体"),
}

enum class DesktopLyricFontStyle(val label: String) {
    SERIF("书卷衬线"),
    SANS("清雅无衬线"),
    KAITI("楷体手书"),
    FOLLOW_APP("跟随全局"),
}

enum class DesktopFontWeightLevel(val label: String) {
    LIGHT("纤细"),
    STANDARD("标准"),
    BOLD("厚重"),
}

enum class DesktopLiquidGlassLevel(
    val label: String,
    val desc: String,
    val surfaceAlpha: Float,
    val rimAlpha: Float,
) {
    LOW("低档", "轻盈薄雾，高可读低开销", 0.90f, 0.24f),
    MEDIUM("中档", "柔和折射，温润通透", 0.78f, 0.36f),
    HIGH("高档", "琉璃高光，层次通透", 0.66f, 0.50f),
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
    val appFontStyle: DesktopFontStyle = DesktopFontStyle.YAHEI,
    val lyricFontStyle: DesktopLyricFontStyle = DesktopLyricFontStyle.SERIF,
    val fontWeightLevel: DesktopFontWeightLevel = DesktopFontWeightLevel.STANDARD,
    val fontWeightValue: Int = 400,
    val lyricSlantDegrees: Float = 0f,
    val customFontPath: String? = null,
    val lyricFontScale: Float = 1.0f,
    val floatingControlBar: Boolean = true,
    val liquidGlassEnabled: Boolean = true,
    val liquidGlassLevel: DesktopLiquidGlassLevel = DesktopLiquidGlassLevel.MEDIUM,
    val controlBarHeightDp: Int = 62,
    val shortAudioFilterSec: Int = 0,
    val sceneStyle: DesktopSceneStyle = DesktopSceneStyle.VINYL_STUDY,
    val spectrumStyle: DesktopSpectrumStyle = DesktopSpectrumStyle.SILK_RIBBON,
    val favoritePaths: Set<String> = emptySet(),
    val recentPaths: List<String> = emptyList(),
    val playCounts: Map<String, Int> = emptyMap(),
    val hourlyPlays: Map<Int, Int> = emptyMap(),
    val playlists: List<DesktopPlaylist> = emptyList(),
    val karaokeLyricMode: DesktopKaraokeLyricMode = DesktopKaraokeLyricMode.SMART,
    val crossfadeSec: Int = 0,
    val volumeNormalizationEnabled: Boolean = false,
    val eqEnabled: Boolean = false,
    val eqBandsDb: List<Float> = listOf(0f, 0f, 0f, 0f, 0f),
    val lyricOffsetsMs: Map<String, Long> = emptyMap(),
    val customCoverPaths: Map<String, String> = emptyMap(),
    val customLyricPaths: Map<String, String> = emptyMap(),
    val volume: Float = 0.85f,
    val repeatMode: RepeatMode = RepeatMode.ALL,
    val shuffleEnabled: Boolean = false,
    val lastSongPath: String? = null,
    val windowWidthDp: Int = 1240,
    val windowHeightDp: Int = 800,
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
