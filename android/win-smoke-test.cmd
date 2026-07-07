@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "PACKAGE_NAME=com.wangzhenman.notifysentinel"
set "ACTIVITY_NAME=.MainActivity"

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
    exit /b 1
)

for /f %%A in ('adb %ADB_TARGET% shell pm list packages %PACKAGE_NAME% ^| findstr /I %PACKAGE_NAME%') do set "PACKAGE_INSTALLED=1"

if not defined PACKAGE_INSTALLED (
    echo [NotifySentinel] App is not installed. Run win-install.cmd first.
    exit /b 1
)

adb %ADB_TARGET% shell am force-stop %PACKAGE_NAME%
adb %ADB_TARGET% shell am start -W -n %PACKAGE_NAME%/%ACTIVITY_NAME%
if errorlevel 1 (
    echo [NotifySentinel] Failed to launch activity.
    exit /b 1
)

for /f %%A in ('adb %ADB_TARGET% shell pidof %PACKAGE_NAME%') do set "APP_PID=%%A"

if not defined APP_PID (
    echo [NotifySentinel] App process not found after launch.
    exit /b 1
)

echo [NotifySentinel] Smoke test passed. PID=%APP_PID%
endlocal & exit /b 0
