# Biu - Android 音乐播放器

基于 Bilibili API 的 Android 音乐播放器

## 🎯 功能特性

- ✅ 音乐播放（高品质音频流）
- ✅ 音乐排行榜
- ✅ 后台播放
- ✅ 媒体通知控制
- 🚧 搜索功能（开发中）
- 🚧 下载功能（开发中）
- 🚧 收藏功能（开发中）

## 🏗️ 技术栈

- **Kotlin** - 编程语言
- **Jetpack Compose** - 现代化 UI 框架
- **Media3 (ExoPlayer)** - 音频播放
- **Retrofit + OkHttp** - 网络请求
- **Hilt** - 依赖注入
- **Coroutines + Flow** - 异步处理
- **Kotlinx Serialization** - JSON 序列化
- **Coil** - 图片加载

## 📱 系统要求

- Android 8.0 (API 26) 及以上
- 推荐 Android 15 (API 35)

## 🚀 构建项目

### 1. 环境准备

- Android Studio Ladybug | 2024.2.1 或更高版本
- JDK 17
- Android SDK 35

### 2. 克隆项目

```bash
git clone <repository-url>
cd biu/android
```

### 3. 构建 APK

#### 方法一：使用 Android Studio

1. 用 Android Studio 打开 `android` 目录
2. 等待 Gradle 同步完成
3. 点击 `Build` -> `Build Bundle(s) / APK(s)` -> `Build APK(s)`
4. APK 文件位于 `app/build/outputs/apk/debug/`

#### 方法二：使用命令行

```bash
cd android

# Windows
gradlew.bat assembleDebug

# macOS/Linux
./gradlew assembleDebug
```

生成的 APK 位于：`app/build/outputs/apk/debug/app-debug.apk`

### 4. 安装到设备

```bash
# 通过 ADB 安装
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 📦 发布构建

```bash
# 生成签名的 Release APK
cd android
./gradlew assembleRelease

# APK 位置
# app/build/outputs/apk/release/app-release.apk
```

> 注意：Release 构建需要配置签名密钥

## 📂 项目结构

```
android/
├── app/
│   ├── src/main/
│   │   ├── java/com/biu/music/
│   │   │   ├── BiuApplication.kt          # Application 入口
│   │   │   ├── MainActivity.kt            # 主 Activity
│   │   │   ├── data/                      # 数据层
│   │   │   │   ├── api/                   # API 接口
│   │   │   │   ├── model/                 # 数据模型
│   │   │   │   └── repository/            # 数据仓库
│   │   │   ├── di/                        # 依赖注入
│   │   │   ├── player/                    # 音频播放
│   │   │   ├── ui/                        # UI 层
│   │   │   │   ├── screen/                # 页面
│   │   │   │   └── theme/                 # 主题
│   │   │   └── viewmodel/                 # ViewModel
│   │   ├── res/                           # 资源文件
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```

## 🎨 核心功能说明

### 音频播放

使用 Media3 (ExoPlayer) 实现高质量音频播放：
- 支持 DASH 格式
- 自动选择最佳音质
- 后台播放支持
- 媒体通知控制

### API 集成

与 Bilibili API 深度集成：
- 视频信息获取
- 播放地址解析
- 音乐排行榜
- 搜索功能

## 📄 许可证

PolyForm Noncommercial License 1.0.0 - 仅供学习和个人使用

## ⚖️ 法律声明

- 本项目仅供学习研究使用，禁止商业用途
- 与 Bilibili 无任何官方关联
- 使用时需遵守 Bilibili 用户协议和相关法律法规

## 🙏 致谢

- [Bilibili API 文档](https://github.com/SocialSisterYi/bilibili-API-collect)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Media3](https://developer.android.com/guide/topics/media/media3)

