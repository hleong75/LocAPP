@echo off
REM Simplified Gradle wrapper for Windows
REM Note: This delegates to system gradle
REM For a proper wrapper, open in Android Studio or run: gradle wrapper --gradle-version 8.0

where gradle >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: gradle not found. Please install Gradle or use Android Studio.
    exit /b 1
)

gradle %*
