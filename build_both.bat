@echo off
REM Script de compilation des deux versions de LocAPP
REM Usage: build_both.bat

echo ======================================
echo   Compilation de LocAPP
echo ======================================
echo.

REM Vérifier que nous sommes dans le bon répertoire
if not exist "settings.gradle" (
    echo Erreur: Ce script doit être execute depuis le repertoire racine du projet
    exit /b 1
)

REM Nettoyer les builds précédents
echo Nettoyage des builds precedents...
call gradlew.bat clean

echo.
echo ======================================
echo   Version 1: Basic (GPS uniquement)
echo ======================================
echo.

REM Compiler la version Basic
call gradlew.bat assembleBasicDebug

if %ERRORLEVEL% EQU 0 (
    echo Version Basic compilee avec succes!
    echo APK: app\build\outputs\apk\basic\debug\app-basic-debug.apk
) else (
    echo Erreur lors de la compilation de la version Basic
    exit /b 1
)

echo.
echo ======================================
echo   Version 2: WithCity (GPS + Ville)
echo ======================================
echo.

REM Compiler la version WithCity
call gradlew.bat assembleWithcityDebug

if %ERRORLEVEL% EQU 0 (
    echo Version WithCity compilee avec succes!
    echo APK: app\build\outputs\apk\withcity\debug\app-withcity-debug.apk
) else (
    echo Erreur lors de la compilation de la version WithCity
    exit /b 1
)

echo.
echo ======================================
echo   Compilation terminee!
echo ======================================
echo.
echo Les deux APKs sont disponibles dans:
echo   - app\build\outputs\apk\basic\debug\app-basic-debug.apk
echo   - app\build\outputs\apk\withcity\debug\app-withcity-debug.apk
echo.
echo Pour installer sur un appareil Android via ADB:
echo   adb install app\build\outputs\apk\basic\debug\app-basic-debug.apk
echo   adb install app\build\outputs\apk\withcity\debug\app-withcity-debug.apk
echo.
pause
