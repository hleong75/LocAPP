#!/bin/bash

# Script de compilation des deux versions de LocAPP
# Usage: ./build_both.sh

echo "======================================"
echo "  Compilation de LocAPP"
echo "======================================"
echo ""

# Vérifier que nous sommes dans le bon répertoire
if [ ! -f "settings.gradle" ]; then
    echo "❌ Erreur: Ce script doit être exécuté depuis le répertoire racine du projet"
    exit 1
fi

# Nettoyer les builds précédents
echo "🧹 Nettoyage des builds précédents..."
./gradlew clean

echo ""
echo "======================================"
echo "  Version 1: Basic (GPS uniquement)"
echo "======================================"
echo ""

# Compiler la version Basic
./gradlew assembleBasicDebug

if [ $? -eq 0 ]; then
    echo "✅ Version Basic compilée avec succès!"
    echo "📦 APK: app/build/outputs/apk/basic/debug/app-basic-debug.apk"
else
    echo "❌ Erreur lors de la compilation de la version Basic"
    exit 1
fi

echo ""
echo "======================================"
echo "  Version 2: WithCity (GPS + Ville)"
echo "======================================"
echo ""

# Compiler la version WithCity
./gradlew assembleWithcityDebug

if [ $? -eq 0 ]; then
    echo "✅ Version WithCity compilée avec succès!"
    echo "📦 APK: app/build/outputs/apk/withcity/debug/app-withcity-debug.apk"
else
    echo "❌ Erreur lors de la compilation de la version WithCity"
    exit 1
fi

echo ""
echo "======================================"
echo "  ✅ Compilation terminée!"
echo "======================================"
echo ""
echo "Les deux APKs sont disponibles dans:"
echo "  - app/build/outputs/apk/basic/debug/app-basic-debug.apk"
echo "  - app/build/outputs/apk/withcity/debug/app-withcity-debug.apk"
echo ""
echo "Pour installer sur un appareil Android via ADB:"
echo "  adb install app/build/outputs/apk/basic/debug/app-basic-debug.apk"
echo "  adb install app/build/outputs/apk/withcity/debug/app-withcity-debug.apk"
echo ""
