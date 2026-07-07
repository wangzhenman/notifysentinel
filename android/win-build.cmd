@echo off
setlocal

set "SCRIPT_DIR=%~dp0"

call "%SCRIPT_DIR%win-env.cmd"
if errorlevel 1 exit /b %ERRORLEVEL%

if "%~1"=="" (
    set "GRADLE_ARGS=assembleDebug"
) else (
    set "GRADLE_ARGS=%*"
)

if not exist "%SCRIPT_DIR%gradlew.bat" (
    echo [NotifySentinel] gradlew.bat not found in "%SCRIPT_DIR%".
    exit /b 1
)

pushd "%SCRIPT_DIR%"
call gradlew.bat %GRADLE_ARGS%
set "BUILD_EXIT_CODE=%ERRORLEVEL%"
popd

if not "%BUILD_EXIT_CODE%"=="0" (
    echo [NotifySentinel] Build failed with exit code %BUILD_EXIT_CODE%.
) else (
    echo [NotifySentinel] Build completed successfully.
)

endlocal & exit /b %BUILD_EXIT_CODE%
