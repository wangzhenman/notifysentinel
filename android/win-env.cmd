@echo off

set "SCRIPT_DIR=%~dp0"

if not defined JAVA_HOME (
    for /d %%D in ("%ProgramFiles%\Microsoft\jdk-17*") do (
        set "JAVA_HOME=%%~fD"
        goto :java_home_ready
    )
)

:java_home_ready
if not defined ANDROID_SDK_ROOT (
    if exist "%SCRIPT_DIR%local.properties" for /f "tokens=2 delims==" %%I in ('findstr /B /C:"sdk.dir=" "%SCRIPT_DIR%local.properties"') do set "ANDROID_SDK_ROOT=%%I"
)

if defined ANDROID_SDK_ROOT call set "ANDROID_SDK_ROOT=%%ANDROID_SDK_ROOT:/=\%%"

if not defined ANDROID_SDK_ROOT (
    if exist "%LOCALAPPDATA%\Android\Sdk" set "ANDROID_SDK_ROOT=%LOCALAPPDATA%\Android\Sdk"
)

if not defined ANDROID_HOME if defined ANDROID_SDK_ROOT set "ANDROID_HOME=%ANDROID_SDK_ROOT%"

if not defined JAVA_HOME (
    echo [NotifySentinel] JAVA_HOME not found.
    echo Set JAVA_HOME manually or install JDK 17 under "%ProgramFiles%\Microsoft".
    exit /b 1
)

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [NotifySentinel] java.exe not found under "%JAVA_HOME%".
    exit /b 1
)

if not defined ANDROID_SDK_ROOT (
    echo [NotifySentinel] ANDROID_SDK_ROOT not found.
    echo Update android\local.properties or set ANDROID_SDK_ROOT manually.
    exit /b 1
)

if not exist "%ANDROID_SDK_ROOT%\platform-tools\adb.exe" (
    echo [NotifySentinel] Android SDK seems incomplete: "%ANDROID_SDK_ROOT%\platform-tools\adb.exe" not found.
    exit /b 1
)

echo [NotifySentinel] JAVA_HOME=%JAVA_HOME%
echo [NotifySentinel] ANDROID_SDK_ROOT=%ANDROID_SDK_ROOT%

set "PATH=%JAVA_HOME%\bin;%ANDROID_SDK_ROOT%\platform-tools;%PATH%"

exit /b 0
