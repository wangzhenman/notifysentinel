# MiPush Integration

This repository now includes both:

- server-side MiPush provider support
- Android app-side MiPush client integration scaffolding

## Server configuration

Set these environment variables before starting the Go server:

- `MIPUSH_APP_SECRET`: Xiaomi Mi Push AppSecret
- `MIPUSH_PACKAGE_NAME`: Android application package name registered in Xiaomi console
- `MIPUSH_CHANNEL_ID`: optional Android notification channel id
- `MIPUSH_ENDPOINT`: optional override, defaults to `https://api.xmpush.xiaomi.com/v3/message/regid`

When the variables are present, the server registers the `mipush` provider automatically.

## Android client prerequisites

The Android client integration code is already in the project, but true push delivery still requires Xiaomi credentials.

You still need:

- Xiaomi Mi Push AppID
- Xiaomi Mi Push AppKey
- Xiaomi Mi Push AppSecret

The official Android client AAR is already placed in:

- `android/app/libs/MiPush_SDK_Client_7_12_4-C_3rd.aar`

The downloaded official Xiaomi SDK archives and extracted reference projects are organized under:

- `third_party/mipush/`

Gradle properties expected by the Android app:

- `MIPUSH_APP_ID`
- `MIPUSH_APP_KEY`

## Current server behavior

- Devices registered with `platform = mipush` are sent through Xiaomi Mi Push.
- Devices registered with `platform = console` are still logged to the console provider.
- Event push failures are now logged by the server.

## Current Android behavior

- App initializes MiPush in the main process through a custom `Application`.
- MiPush registration result is received by a custom `PushMessageReceiver`.
- RegID is persisted locally through DataStore.
- Device registration flow can switch from fallback token to MiPush RegID when available.

## Current blocker

True Xiaomi push delivery is currently blocked because Xiaomi developer registration is not available yet.

Until AppID, AppKey, and AppSecret are available, the code can build but cannot complete real MiPush delivery validation.