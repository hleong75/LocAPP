#!/usr/bin/env sh
# Simplified Gradle wrapper
# Note: This delegates to system gradle. 
# For a proper wrapper, open in Android Studio or run: gradle wrapper --gradle-version 8.0

if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
else
    echo "Error: gradle not found. Please install Gradle or use Android Studio."
    exit 1
fi
