# 简约音乐 PureMusic Desktop (Windows v1.0)

与 Android 版同源设计的原生 Windows 本地离线音乐播放器，基于 **Kotlin + Compose Multiplatform (Desktop / Skia Direct3D)** 构建。

## 核心特性
- **无边框沉浸窗口 + 独立可置顶迷你黑胶浮窗**：支持一键呼出桌面迷你黑胶小窗，随时拖拽停靠。
- **3 套沉浸听歌场景**：黑胶书房（暖纸/曜石双主题）、空间唱片廊（3D 透视唱片栈与镜面倒影）、黑胶光场（大黑胶与联动机械唱臂）。
- **4 款 60fps 实时 PCM 节拍频谱**：丝带微胶囊、极简弦波、点阵峰标、水墨声山，支持点击与拖拽精准跳转进度。
- **固定三行自适应双语歌词**：自动读取同目录 `.lrc` 与音频内嵌歌词，支持同行中英双语自动拆分与第二行自动缩字完整显示。
- **纯本地离线播放**：支持扫描本地音乐文件夹（MP3 / FLAC / WAV），自动读取内嵌专辑封面与元信息，支持快捷键盘控制（`Space` 播放/暂停，`←`/`→` 快退/快进）。

## GitHub Actions 自动打包
将本项目文件推送至 GitHub 仓库（`main` 分支）后，Actions 会自动在 `windows-latest` 环境下构建并产出两个文件：
1. `PureMusic-Windows-Setup-v1.0.0.msi`（双击安装版）
2. `PureMusic-Windows-Portable-v1.0.0.zip`（解压即用的免安装绿色版）
