# Guide de Compilation et Installation

## Prérequis

Avant de compiler l'application, assurez-vous d'avoir :

1. **JDK 8 ou supérieur** installé
2. **Android SDK** (API 24-34) installé
3. **Gradle 8.9+** (inclus avec le wrapper Gradle du projet)

## Configuration du SDK Android

### Méthode Automatique (Android Studio)

Si vous utilisez Android Studio, le SDK sera automatiquement détecté et configuré lors de l'ouverture du projet.

### Méthode Manuelle (Ligne de Commande)

Si vous compilez en ligne de commande, vous devez créer un fichier `local.properties` à la racine du projet :

```properties
sdk.dir=/path/to/your/android/sdk
```

**Exemples de chemins SDK typiques :**

- **Linux** : `sdk.dir=/home/username/Android/Sdk`
- **macOS** : `sdk.dir=/Users/username/Library/Android/Sdk`
- **Windows** : `sdk.dir=C\:\\Users\\username\\AppData\\Local\\Android\\Sdk`

> **Important** : Le fichier `local.properties` ne doit jamais être commité dans Git (il est déjà dans `.gitignore`).

**Alternative : Variable d'environnement**

Vous pouvez aussi définir la variable d'environnement `ANDROID_HOME` :

```bash
# Linux/Mac
export ANDROID_HOME=/path/to/android/sdk

# Windows (cmd)
set ANDROID_HOME=C:\path\to\android\sdk

# Windows (PowerShell)
$env:ANDROID_HOME="C:\path\to\android\sdk"
```

## Compilation des Applications

### Méthode 1 : Avec Android Studio

1. **Ouvrir le projet**
   - Lancer Android Studio
   - File → Open → Sélectionner le dossier LocAPP
   - Attendre la synchronisation Gradle

2. **Compiler Version Basic**
   - Build → Select Build Variant
   - Sélectionner "basicDebug"
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - APK généré dans : `app/build/outputs/apk/basic/debug/`

3. **Compiler Version WithCity**
   - Build → Select Build Variant
   - Sélectionner "withcityDebug"
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - APK généré dans : `app/build/outputs/apk/withcity/debug/`

### Méthode 2 : Ligne de Commande (Linux/Mac)

```bash
# Se placer dans le dossier du projet
cd LocAPP

# Compiler la version Basic
./gradlew assembleBasicDebug

# Compiler la version WithCity
./gradlew assembleWithcityDebug

# Compiler les deux en une seule commande
./gradlew assembleDebug
```

### Méthode 3 : Ligne de Commande (Windows)

```cmd
cd LocAPP

REM Compiler la version Basic
gradlew.bat assembleBasicDebug

REM Compiler la version WithCity
gradlew.bat assembleWithcityDebug

REM Compiler les deux versions
gradlew.bat assembleDebug
```

## Installation sur le Téléphone

### Via USB (ADB)

1. **Activer le Mode Développeur sur Android**
   - Paramètres → À propos du téléphone
   - Appuyer 7 fois sur "Numéro de build"
   - Retour → Options développeur
   - Activer "Débogage USB"

2. **Installer l'APK**
   ```bash
   # Version Basic
   adb install app/build/outputs/apk/basic/debug/app-basic-debug.apk
   
   # Version WithCity
   adb install app/build/outputs/apk/withcity/debug/app-withcity-debug.apk
   ```

### Via Transfert de Fichier

1. Copier l'APK sur le téléphone (USB, Email, Drive, etc.)
2. Ouvrir le fichier APK depuis le gestionnaire de fichiers
3. Autoriser l'installation depuis des sources inconnues si demandé
4. Suivre les instructions d'installation

## Configuration Initiale

### 1. Permissions

Au premier lancement, l'application demandera plusieurs permissions :

- **Localisation** : OBLIGATOIRE - Cliquer sur "Autoriser tout le temps" pour le fonctionnement en arrière-plan
- **SMS** : OBLIGATOIRE - Pour envoyer la localisation
- **Notifications** : RECOMMANDÉ - Pour voir l'état du service

### 2. Configuration des Paramètres

1. **Numéro de téléphone**
   - Format international : +33612345678 (France)
   - Format international : +1234567890 (USA)
   - Format international : +44... (UK)

2. **Fréquence**
   - Valeur en minutes (minimum 1)
   - Exemples :
     - 5 = Envoi toutes les 5 minutes
     - 15 = Envoi toutes les 15 minutes
     - 60 = Envoi toutes les heures
     - 1440 = Envoi une fois par jour

### 3. Démarrage du Suivi

1. Entrer les paramètres
2. Cliquer sur "Démarrer le suivi"
3. Le statut passe à "Suivi actif"
4. Une notification persistante apparaît
5. Les SMS seront envoyés automatiquement selon la fréquence définie

## Vérification du Fonctionnement

### Vérifier le Service

1. Ouvrir les Paramètres Android
2. Applications → LocAPP Tracker
3. Vérifier que le service est actif dans "Services en cours d'exécution"

### Vérifier les Permissions

1. Paramètres → Applications → LocAPP Tracker → Autorisations
2. Vérifier :
   - Localisation : "Autoriser tout le temps"
   - SMS : "Autoriser"
   - Notifications : "Autoriser"

### Logs de Débogage

Avec ADB, vous pouvez voir les logs :

```bash
# Tous les logs de l'application
adb logcat | grep -i locapp

# Logs du service de localisation
adb logcat | grep LocationService

# Logs d'envoi SMS
adb logcat | grep SMS
```

## Dépannage

### Le suivi ne démarre pas
- Vérifier que toutes les permissions sont accordées
- Vérifier que la localisation est activée sur le téléphone
- Redémarrer l'application

### Les SMS ne sont pas envoyés
- Vérifier que le numéro est au format international
- Vérifier que la permission SMS est accordée
- Vérifier le crédit SMS du téléphone
- Vérifier les logs pour les erreurs

### Le service s'arrête après quelques heures
- Désactiver l'optimisation de batterie pour l'application :
  - Paramètres → Batterie → Optimisation batterie
  - Rechercher LocAPP Tracker
  - Sélectionner "Ne pas optimiser"

### La localisation est imprécise
- S'assurer que le GPS est activé (pas seulement réseau)
- Être dans une zone avec une bonne visibilité du ciel
- Attendre quelques secondes pour que le GPS se fixe

### Le nom de ville n'apparaît pas (version WithCity)
- Le géocodage nécessite une connexion Internet
- Certaines zones isolées peuvent ne pas avoir de nom de ville
- Vérifier que les services Google Play sont à jour

## Désinstallation

1. Arrêter le suivi depuis l'application
2. Paramètres → Applications → LocAPP Tracker
3. Désinstaller

Ou via ADB :
```bash
adb uninstall com.locapp.tracker.basic     # Version Basic
adb uninstall com.locapp.tracker.withcity  # Version WithCity
```

## Performance et Batterie

### Consommation de Batterie

La consommation dépend de la fréquence configurée :
- **5 minutes** : Consommation élevée (~5-10% par heure)
- **15 minutes** : Consommation moyenne (~2-5% par heure)
- **60 minutes** : Consommation faible (~1-2% par heure)

### Recommandations

- Utiliser une fréquence de 15-30 minutes pour un équilibre usage/batterie
- Désactiver le suivi quand il n'est pas nécessaire
- Utiliser un chargeur ou batterie externe pour un suivi intensif

## Versions Compilées

Les deux versions sont disponibles après compilation :

1. **app-basic-debug.apk** : ~4-5 MB
   - Envoi coordonnées GPS uniquement
   - Package : com.locapp.tracker.basic

2. **app-withcity-debug.apk** : ~4-5 MB
   - Envoi coordonnées GPS + nom de ville
   - Package : com.locapp.tracker.withcity

Note : Les deux versions peuvent être installées en même temps car elles ont des packages différents.
