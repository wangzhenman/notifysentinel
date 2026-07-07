# notifysentinel
自托管的设备监控与实时通知平台，支持 NAS、服务器和 IoT。

## Project Layout

- `server/`：Go 服务端，包含 SQLite、事件接口、设备接口和 PushProvider。
- `android/`：Android 客户端工程与 Windows 辅助脚本。
- `docs/`：项目文档，只放可阅读的说明文档。
- `third_party/`：外部 SDK、官方资料包和解压后的参考材料。

## Android Windows 脚本

Android 工程目录下提供了两个 Windows 批处理脚本：

- `android\\win-env.cmd`：解析并设置 `JAVA_HOME`、`ANDROID_SDK_ROOT`、`ANDROID_HOME`。
- `android\\win-build.cmd`：先加载环境，再调用 `gradlew.bat` 执行构建。
- `android\\win-install.cmd`：将 `app-debug.apk` 安装到已连接设备，并启动 App。
- `android\\win-smoke-test.cmd`：验证 App 已安装、可启动，且进程成功拉起。

常用示例：

```bat
cd android
call win-env.cmd
win-build.cmd
win-build.cmd clean assembleDebug
win-install.cmd
win-smoke-test.cmd
```

设备侧注意事项：

- 如果 `win-install.cmd` 报 `INSTALL_FAILED_USER_RESTRICTED`，说明手机拒绝了 USB 安装。
- 小米 / HyperOS 设备通常还需要在开发者选项里额外打开 USB 安装，并在手机弹窗上确认安装。
- 放行后重新执行 `win-install.cmd`，再执行 `win-smoke-test.cmd`。

默认行为：

- JDK 优先从 `%JAVA_HOME%` 读取，否则尝试匹配 `%ProgramFiles%\\Microsoft\\jdk-17*`。
- Android SDK 优先从 `%ANDROID_SDK_ROOT%` 读取，否则读取 `android\\local.properties` 里的 `sdk.dir`。

## MiPush

服务端已经具备 MiPush Provider，说明见 [docs/mipush.md](docs/mipush.md)。
MiPush 官方资料包与解压后的参考工程已整理到 [third_party/mipush](third_party/mipush)。

## Progress

当前项目暂停前的详细进度、已完成项、阻塞项和恢复开发建议见 [docs/progress.md](docs/progress.md)。
