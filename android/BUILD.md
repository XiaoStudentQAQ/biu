# 构建指南

## 环境要求

### 必需
- **Android Studio**: Ladybug | 2024.2.1 或更高版本
- **JDK**: 17 或更高版本
- **Android SDK**: 
  - compileSdk: 35
  - minSdk: 26
  - targetSdk: 35

### 可选
- **Android 模拟器** 或 **真实设备**（Android 8.0+）

## 构建步骤

### 1. 使用 Android Studio（推荐）

#### 打开项目
```
1. 启动 Android Studio
2. File -> Open -> 选择 biu/android 目录
3. 等待 Gradle 同步完成（首次可能需要下载依赖）
```

#### 运行应用
```
1. 连接 Android 设备或启动模拟器
2. 点击工具栏的运行按钮（绿色三角形）或按 Shift + F10
3. 选择目标设备
4. 等待构建完成并自动安装
```

#### 构建 APK
```
1. Build -> Build Bundle(s) / APK(s) -> Build APK(s)
2. 等待构建完成
3. APK 位置：app/build/outputs/apk/debug/app-debug.apk
```

### 2. 使用命令行

#### Windows

```bash
# 进入 Android 项目目录
cd android

# 构建 Debug APK
gradlew.bat assembleDebug

# 构建 Release APK（需要配置签名）
gradlew.bat assembleRelease

# 安装到设备
gradlew.bat installDebug

# 清理构建
gradlew.bat clean
```

#### macOS / Linux

```bash
# 进入 Android 项目目录
cd android

# 赋予执行权限（首次需要）
chmod +x gradlew

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK（需要配置签名）
./gradlew assembleRelease

# 安装到设备
./gradlew installDebug

# 清理构建
./gradlew clean
```

## 输出文件位置

### Debug 构建
```
android/app/build/outputs/apk/debug/
└── app-debug.apk
```

### Release 构建
```
android/app/build/outputs/apk/release/
└── app-release.apk
```

## 签名配置（Release）

### 1. 生成密钥库

```bash
keytool -genkey -v -keystore biu-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias biu
```

### 2. 配置签名

在 `android/app/build.gradle.kts` 中添加：

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("path/to/biu-release.jks")
            storePassword = "your_store_password"
            keyAlias = "biu"
            keyPassword = "your_key_password"
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ...
        }
    }
}
```

> ⚠️ **安全提示**: 不要将密钥库和密码提交到版本控制系统！

### 3. 使用环境变量（推荐）

创建 `android/keystore.properties`:

```properties
storePassword=your_store_password
keyPassword=your_key_password
keyAlias=biu
storeFile=path/to/biu-release.jks
```

在 `build.gradle.kts` 中读取：

```kotlin
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}
```

## 常见问题

### 1. Gradle 同步失败

```bash
# 清理缓存
cd android
./gradlew clean

# 删除 .gradle 目录
rm -rf .gradle

# 重新同步
./gradlew build
```

### 2. 依赖下载慢

编辑 `android/build.gradle.kts`，使用国内镜像：

```kotlin
repositories {
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
    google()
    mavenCentral()
}
```

### 3. 编译错误

```bash
# 检查 JDK 版本
java -version  # 应该是 17+

# 检查 Android SDK
# 确保安装了 API 35 和 Build Tools

# 清理并重新构建
./gradlew clean build
```

### 4. 安装失败

```bash
# 检查设备连接
adb devices

# 卸载旧版本
adb uninstall com.biu.music

# 重新安装
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 性能优化

### ProGuard/R8 配置

Release 构建默认启用代码混淆和压缩。

查看混淆规则：`android/app/proguard-rules.pro`

### APK 大小优化

1. **启用资源压缩**（已配置）
2. **移除未使用资源**（已配置）
3. **使用 WebP 图片格式**
4. **分析 APK**：
   ```
   Build -> Analyze APK -> 选择 APK 文件
   ```

## CI/CD 集成

### GitHub Actions 示例

```yaml
name: Android Build

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Grant execute permission for gradlew
      run: chmod +x android/gradlew
      
    - name: Build with Gradle
      run: |
        cd android
        ./gradlew assembleDebug
        
    - name: Upload APK
      uses: actions/upload-artifact@v4
      with:
        name: app-debug
        path: android/app/build/outputs/apk/debug/app-debug.apk
```

## 开发建议

1. **使用 Android Studio** 的热重载功能加速开发
2. **启用 Compose Preview** 预览 UI 组件
3. **使用 Logcat** 查看日志
4. **使用 Layout Inspector** 调试 UI
5. **使用 Profiler** 分析性能

## 更多信息

- [Android 开发文档](https://developer.android.com/)
- [Jetpack Compose 教程](https://developer.android.com/jetpack/compose/tutorial)
- [Gradle 构建配置](https://developer.android.com/studio/build)

