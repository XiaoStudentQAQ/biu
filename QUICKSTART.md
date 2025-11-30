# 快速开始指南 🚀

欢迎使用 Biu Android 音乐播放器！本指南将帮助你快速上手。

## ⚡ 5 分钟快速开始

### 方式一：直接安装 APK（最快）

1. **下载 APK**
   - 前往项目 [Releases](https://github.com/wood3n/biu/releases) 页面
   - 下载最新的 `app-debug.apk`

2. **安装到设备**
   ```bash
   # 方法 1: 通过 ADB
   adb install app-debug.apk

   # 方法 2: 直接在设备上打开 APK 文件安装
   ```

3. **打开应用，开始使用！** 🎉

### 方式二：从源码构建（推荐开发者）

#### 前置要求
- ✅ Android Studio Ladybug | 2024.2.1+
- ✅ JDK 17+
- ✅ Android SDK 35

#### 步骤

```bash
# 1. 克隆项目
git clone https://github.com/wood3n/biu.git
cd biu/android

# 2. Windows 构建
gradlew.bat assembleDebug

# 或 macOS/Linux 构建
./gradlew assembleDebug

# 3. 安装
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 📱 使用指南

### 首页 - 音乐排行榜

1. 打开应用后自动加载 B站音乐区排行榜
2. 点击任意歌曲开始播放
3. 底部会显示迷你播放器

### 播放控制

- **播放/暂停**: 点击迷你播放器的播放按钮
- **锁屏控制**: 在锁屏界面可以看到媒体通知
- **通知栏控制**: 下拉通知栏进行控制

### 搜索（开发中）

点击底部导航栏的搜索图标，输入关键词搜索音乐。

### 设置

点击底部导航栏的设置图标，查看应用信息。

## 🎵 功能演示

### 1. 播放音乐

```
首页 → 选择歌曲 → 点击播放 → 自动获取高品质音频 → 开始播放
```

### 2. 后台播放

```
播放音乐 → 按 Home 键 → 应用进入后台 → 音乐继续播放
```

### 3. 锁屏控制

```
播放音乐 → 锁定屏幕 → 在锁屏界面控制播放
```

## ⚙️ 系统权限

应用需要以下权限：

| 权限 | 用途 | 必需 |
|------|------|------|
| 网络访问 | 获取音乐数据 | ✅ 是 |
| 前台服务 | 后台播放 | ✅ 是 |
| 通知 | 播放控制通知 | ⚠️ 推荐 |

## 🐛 常见问题

### Q: 无法播放音乐？
**A**: 检查网络连接，确保可以访问 Bilibili。

### Q: 没有声音？
**A**:
1. 检查设备音量
2. 检查应用是否有音频权限
3. 尝试重启应用

### Q: 应用闪退？
**A**:
1. 确保 Android 版本 >= 8.0
2. 清除应用数据重试
3. 查看日志：`adb logcat | grep Biu`

### Q: APK 安装失败？
**A**:
1. 启用"允许安装未知来源应用"
2. 卸载旧版本后重新安装
3. 检查存储空间是否足够

### Q: 如何查看日志？
**A**:
```bash
# 实时日志
adb logcat -s Biu

# 保存日志到文件
adb logcat > biu.log
```

## 🔧 开发相关

### 启用开发者模式

1. 设置 → 关于手机
2. 连续点击"版本号" 7 次
3. 返回设置 → 开发者选项
4. 启用 USB 调试

### 连接 ADB

```bash
# 检查设备连接
adb devices

# 查看设备日志
adb logcat

# 安装 APK
adb install path/to/app.apk

# 卸载应用
adb uninstall com.biu.music
```

### Android Studio 调试

1. 打开 Android Studio
2. File → Open → 选择 `biu/android`
3. 连接设备或启动模拟器
4. 点击 Debug 按钮（小虫图标）

## 📖 更多文档

- [详细 README](README.md) - 项目完整说明
- [构建指南](android/BUILD.md) - 详细构建步骤
- [迁移说明](MIGRATION.md) - 从 Electron 迁移的说明

## 💬 获取帮助

- 📝 [提交 Issue](https://github.com/wood3n/biu/issues)
- 💬 [参与讨论](https://github.com/wood3n/biu/discussions)
- 📧 联系开发者

## ⭐ 支持项目

如果你喜欢这个项目，请给一个 Star ⭐

---

祝你使用愉快！🎶

