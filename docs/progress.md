# NotifySentinel Progress

Last updated: 2026-07-07

This document records the current project state before pausing development.

## Project overview

NotifySentinel currently has:

- A Go server with SQLite persistence, event ingestion, device management, and pluggable push providers.
- An Android app built with Kotlin + Jetpack Compose that can connect to the server, register the current device, and view event history.
- Windows helper scripts for building, installing, and smoke-testing the Android app locally.

## Completed work

### Server

- Health endpoint is available at `GET /health`.
- Event ingestion is available at `POST /api/events`.
- Event history is available at `GET /api/events`.
- Device registration is available at `POST /api/devices/register`.
- Device list is available at `GET /api/devices`.
- Device deletion is available at `DELETE /api/devices/:id`.
- Current-device lookup is available at `GET /api/devices/me?token=...`.
- Device registration is idempotent by token: re-registering the same token updates the existing record instead of creating duplicates.
- Push provider abstraction is in place.
- Console push provider is working for end-to-end notification flow validation.
- MiPush server-side provider is implemented and can be enabled by environment variables.

### Android app

- Android project scaffold is complete and builds successfully.
- Compose UI includes Home, Settings, and Device screens.
- Server URL is persisted locally.
- Startup automatically refreshes connection status.
- Startup automatically refreshes current-device registration status.
- Device registration flow is working against the Go server.
- Current device state now uses the dedicated `GET /api/devices/me` endpoint instead of reusing the device list.
- Event history can be refreshed from the Home screen.
- Cleartext HTTP to local LAN server addresses is allowed by Android network security config.
- Windows scripts are available for build, install, and smoke-test flows.

### MiPush integration status

- Official Android MiPush client AAR has been added to the project.
- Android app-side MiPush integration code has been added:
  - custom `Application`
  - MiPush initialization in main process
  - MiPush receiver
  - RegID persistence via DataStore
  - registration flow can switch from fallback token to MiPush RegID
- Android app still builds and installs successfully after MiPush integration.
- Go server-side MiPush provider is implemented with direct HTTP calls to Xiaomi push API.

## Verified environment state

### Android local environment

- JDK 17 is installed and used by the project build scripts.
- Android SDK is installed and available to the build scripts.
- Gradle wrapper download source has been switched to a faster mirror.
- Android project builds through `win-build.cmd assembleDebug`.
- APK installs to connected device through `win-install.cmd`.

### Real-device validation already completed

- APK built successfully.
- APK installed successfully to the connected Xiaomi / HyperOS device.
- App cold start works.
- Startup request flow was observed server-side:
  - `GET /health`
  - `GET /api/devices/me?token=...`
- Device registration state sync works through server API.

## Current blockers

### MiPush production activation blocker

The project is paused before true Xiaomi push delivery is validated because Xiaomi developer registration is currently unavailable.

Without Xiaomi developer access, the following items cannot be obtained:

- MiPush AppID
- MiPush AppKey
- MiPush AppSecret

Because of that:

- Android client cannot complete real MiPush registration against Xiaomi services.
- Server cannot authenticate real MiPush send requests with production credentials.
- True push delivery to the phone cannot be validated yet.

## Current MiPush code readiness

The codebase is prepared up to the point where credentials can be filled in later.

### Server readiness

Environment variables expected by server:

- `MIPUSH_APP_SECRET`
- `MIPUSH_PACKAGE_NAME`
- `MIPUSH_CHANNEL_ID` optional
- `MIPUSH_ENDPOINT` optional

### Android readiness

Project expects these Gradle properties:

- `MIPUSH_APP_ID`
- `MIPUSH_APP_KEY`

The official client AAR currently placed in the project:

- `android/app/libs/MiPush_SDK_Client_7_12_4-C_3rd.aar`

Official downloaded Xiaomi SDK archives and extracted reference material:

- `third_party/mipush/`

## Recommended resume path

When work resumes, continue in this order:

1. Obtain Xiaomi developer credentials.
2. Fill `MIPUSH_APP_ID` and `MIPUSH_APP_KEY` in `android/gradle.properties` or move them to a local private override.
3. Export `MIPUSH_APP_SECRET` and `MIPUSH_PACKAGE_NAME` before starting the server.
4. Rebuild and reinstall the Android app.
5. Confirm MiPush RegID is returned and stored.
6. Re-register the device so the server stores `platform = mipush` and the real RegID.
7. Trigger `POST /api/events` and validate real phone notification delivery.

## Alternative path if Xiaomi registration remains blocked

If Xiaomi developer registration remains unavailable, the recommended fallback is:

1. Pause MiPush-only validation.
2. Keep current MiPush code in place.
3. Add an `ntfy` push provider for real-device notification validation without Xiaomi developer credentials.

## Important files

### Server

- `server/main.go`
- `server/api/device.go`
- `server/api/event.go`
- `server/push/provider.go`
- `server/push/manager.go`
- `server/push/console.go`
- `server/push/mipush.go`

### Android

- `android/app/build.gradle.kts`
- `android/gradle.properties`
- `android/app/src/main/AndroidManifest.xml`
- `android/app/src/main/java/com/wangzhenman/notifysentinel/NotifySentinelApp.kt`
- `android/app/src/main/java/com/wangzhenman/notifysentinel/push/MiPushReceiver.kt`
- `android/app/src/main/java/com/wangzhenman/notifysentinel/data/SettingsStore.kt`
- `android/app/src/main/java/com/wangzhenman/notifysentinel/ui/AppViewModel.kt`
- `android/app/src/main/java/com/wangzhenman/notifysentinel/ui/screens/DeviceScreen.kt`
- `android/win-env.cmd`
- `android/win-build.cmd`
- `android/win-install.cmd`
- `android/win-smoke-test.cmd`