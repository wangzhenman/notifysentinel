@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "PACKAGE_NAME=com.wangzhenman.notifysentinel"
set "ACTIVITY_NAME=.MainActivity"
set "APK_PATH=%SCRIPT_DIR%app\build\outputs\apk\debug\app-debug.apk"
set "INSTALL_LOG=%TEMP%\notifysentinel-adb-install.log"

call "%SCRIPT_DIR%win-env.cmd"
if errorlevel 1 exit /b %ERRORLEVEL%

if not "%~1"=="" set "ANDROID_SERIAL=%~1"

set "ADB_TARGET="
if defined ANDROID_SERIAL set "ADB_TARGET=-s %ANDROID_SERIAL%"

for /f "skip=1 tokens=1,2" %%A in ('adb %ADB_TARGET% devices') do (
    if "%%B"=="device" set "DEVICE_FOUND=1"
)

if not defined DEVICE_FOUND (
    echo [NotifySentinel] No online Android device found.
    echo Connect a device with USB debugging enabled, or pass a serial: win-install.cmd SERIAL
    exit /b 1
)

if not exist "%APK_PATH%" (
    echo [NotifySentinel] Debug APK not found. Building first...
    call "%SCRIPT_DIR%win-build.cmd" assembleDebug
    if errorlevel 1 exit /b %ERRORLEVEL%
)

echo [NotifySentinel] Installing APK to device...
adb %ADB_TARGET% install -r "%APK_PATH%" > "%INSTALL_LOG%" 2>&1
if errorlevel 1 (
    type "%INSTALL_LOG%"
    findstr /I "INSTALL_FAILED_USER_RESTRICTED canceled by user" "%INSTALL_LOG%" >nul
    if not errorlevel 1 (
        echo [NotifySentinel] Device rejected USB install.
        echo [NotifySentinel] On Xiaomi/HyperOS, enable Developer options, USB debugging, and USB install, then confirm the install prompt on the phone.
    )
    echo [NotifySentinel] APK install failed.
    exit /b 1
)

type "%INSTALL_LOG%"

echo [NotifySentinel] Launching app...
adb %ADB_TARGET% shell am start -W -n %PACKAGE_NAME%/%ACTIVITY_NAME%
if errorlevel 1 (
    echo [NotifySentinel] App launch failed.
    exit /b 1
)

echo [NotifySentinel] APK installed and app launched successfully.
endlocal & exit /b 0
