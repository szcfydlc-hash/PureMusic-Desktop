import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.0.21"
    id("org.jetbrains.compose") version "1.7.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
}

group = "com.puremusic.desktop"
version = "2.1.0"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")

    // Pure-Java offline audio decoding SPI (MP3 + FLAC + built-in WAV/AIFF)
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
            packageVersion = "2.1.0"
            description = "PureMusic - Native Offline Hi-Res Local Music Player"
            vendor = "PureMusic"
            includeAllModules = true

            windows {
                menuGroup = "PureMusic"
                shortcut = true
                perUserInstall = true
                upgradeUuid = "7e4c1d32-9b8a-4e21-8f56-3a2c9d1b6e40"
            }
        }
    }
}
