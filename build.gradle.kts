import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.0.21"
    id("org.jetbrains.compose") version "1.7.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
}

group = "com.puremusic.desktop"
version = "4.3.1"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")

    // Native FFmpeg Windows x86_64 decoder (MP3/FLAC/24-bit Hi-Res/WAV/M4A/AAC/ALAC/OGG/OPUS/WMA/APE/WV/AIFF/DSD)
    implementation("org.bytedeco:javacpp:1.5.11")
    implementation("org.bytedeco:javacpp:1.5.11:windows-x86_64")
    implementation("org.bytedeco:ffmpeg:7.1-1.5.11")
    implementation("org.bytedeco:ffmpeg:7.1-1.5.11:windows-x86_64")

    // Pure-Java fallback offline audio decoding SPI (MP3 + FLAC + built-in WAV/AIFF)
    implementation("com.googlecode.soundlibs:mp3spi:1.9.5.4")
    implementation("org.jflac:jflac-codec:1.5.2")

    // Embedded audio metadata, album art & embedded lyrics reader
    implementation("net.jthink:jaudiotagger:3.0.1")
}

kotlin {
    jvmToolchain(17)
}

compose.desktop {
    application {
        mainClass = "com.puremusic.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "PureMusic"
            packageVersion = "4.3.1"
            description = "PureMusic - Native Offline Hi-Res Local Music Player"
            vendor = "PureMusic"
            includeAllModules = true

            windows {
                iconFile.set(project.file("src/main/resources/app_icon.ico"))
                menuGroup = "PureMusic"
                shortcut = true
                perUserInstall = true
                upgradeUuid = "7e4c1d32-9b8a-4e21-8f56-3a2c9d1b6e40"
            }
        }
    }
}
