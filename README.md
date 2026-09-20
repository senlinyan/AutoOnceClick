# AutoOnceClick

Android 定时自动点击工具 —— 在指定时间自动点击屏幕指定坐标。

## 功能

- 自定义时分秒 + 毫秒的触发时间
- 自定义 X / Y 点击坐标
- 基于无障碍服务执行手势点击
- 自动持久化上次输入（关闭 App 后重开自动恢复）
- 后台 15 分钟超时保护
- 遗留任务检测弹窗

## 使用

1. 安装 APK，打开 App
2. 点击「打开无障碍设置」，启用 AutoOnceClick 服务
3. 输入目标时间（时/分/秒/毫秒）和坐标（X/Y）
4. 点击「开始定时任务」
5. 到点后自动点击一次指定坐标

## 项目结构

```
AutoOnceClick/
├── .github/workflows/build.yml   # GitHub Actions 自动打包
├── app/
│   ├── src/main/
│   │   ├── java/com/example/autoonceclick/
│   │   │   ├── MainActivity.kt   # 主界面 + SharedPreferences 持久化
│   │   │   └── AutoClickAccessibilityService.kt  # 无障碍服务 + 定时点击
│   │   ├── res/
│   │   │   ├── layout/activity_main.xml
│   │   │   ├── values/
│   │   │   └── xml/accessibility_service_config.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## 技术栈

- Kotlin
- Android AccessibilityService
- SharedPreferences
- GitHub Actions 自动构建
