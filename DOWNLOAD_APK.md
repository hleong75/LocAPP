# Comment Télécharger les APK

## Méthode 1: Via GitHub Actions (Automatique)

Le projet est maintenant configuré pour compiler automatiquement les APK à chaque push.

### Pour télécharger les APK:

1. Allez sur la page du repository GitHub
2. Cliquez sur l'onglet **Actions**
3. Cliquez sur le workflow **Build Android APK** le plus récent (avec une coche verte ✅)
4. Faites défiler vers le bas jusqu'à la section **Artifacts**
5. Téléchargez:
   - `app-basic-debug` - Version basic (coordonnées GPS seulement)
   - `app-withcity-debug` - Version withcity (GPS + nom de ville)

## Méthode 2: Compilation Locale

### Prérequis
- JDK 17 ou supérieur
- Android SDK (API 24-34)

### Commandes

```bash
# Cloner le repository
git clone https://github.com/hleong75/LocAPP.git
cd LocAPP

# Compiler les deux versions
./gradlew assembleDebug

# Ou compiler individuellement
./gradlew assembleBasicDebug      # Version Basic
./gradlew assembleWithcityDebug   # Version WithCity
```

### Localisation des APK

Après compilation, les APK se trouvent dans:
- `app/build/outputs/apk/basic/debug/app-basic-debug.apk`
- `app/build/outputs/apk/withcity/debug/app-withcity-debug.apk`

## Méthode 3: Android Studio

1. Ouvrir Android Studio
2. File → Open → Sélectionner le dossier LocAPP
3. Attendre la synchronisation Gradle
4. Build → Build Bundle(s) / APK(s) → Build APK(s)
5. Les APK seront générés et un lien apparaîtra dans la notification

## Versions Disponibles

### Version Basic (`app-basic-debug.apk`)
- Package: `com.locapp.tracker.basic`
- Fonctionnalité: Envoi des coordonnées GPS uniquement
- Taille: ~4-5 MB

### Version WithCity (`app-withcity-debug.apk`)
- Package: `com.locapp.tracker.withcity`
- Fonctionnalité: Envoi des coordonnées GPS + nom de ville
- Taille: ~4-5 MB
- Nécessite une connexion Internet pour le géocodage

## Installation sur Android

1. Télécharger l'APK souhaité
2. Transférer sur le téléphone Android (USB, email, etc.)
3. Ouvrir le fichier APK depuis le gestionnaire de fichiers
4. Autoriser l'installation depuis des sources inconnues si demandé
5. Suivre les instructions d'installation

## Permissions Requises

Au premier lancement, l'application demandera:
- 📍 **Localisation** (OBLIGATOIRE) - Autoriser "Tout le temps" pour le fonctionnement en arrière-plan
- 💬 **SMS** (OBLIGATOIRE) - Pour envoyer la localisation
- 🔔 **Notifications** (RECOMMANDÉ) - Pour voir l'état du service

## Support

Pour toute question ou problème, consultez:
- [BUILD_GUIDE.md](BUILD_GUIDE.md) - Guide de compilation détaillé
- [README.md](README.md) - Documentation complète de l'application
- [Issues GitHub](https://github.com/hleong75/LocAPP/issues) - Signaler un problème
