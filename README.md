# LocAPP - Location Tracking SMS Application

## Description

LocAPP est une application Android complète qui permet de suivre et d'envoyer automatiquement la localisation GPS du téléphone par SMS.

## Fonctionnalités

### Version 1 (Basic)
- ✅ Suivi de localisation en arrière-plan
- ✅ Envoi automatique par SMS des coordonnées GPS (latitude, longitude)
- ✅ Configuration du numéro de téléphone destinataire
- ✅ Configuration de la fréquence d'envoi (en minutes)
- ✅ Service en arrière-plan avec notification
- ✅ Redémarrage automatique après reboot du téléphone
- ✅ Lien Google Maps inclus dans le SMS

### Version 2 (WithCity)
- ✅ Toutes les fonctionnalités de la version Basic
- ✅ Nom de la ville ajouté dans le SMS (géocodage inversé)

## Structure du Projet

```
LocAPP/
├── app/
│   ├── build.gradle                 # Configuration Gradle de l'app
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml  # Permissions et composants
│   │       ├── java/com/locapp/tracker/
│   │       │   ├── MainActivity.java              # Interface utilisateur
│   │       │   ├── LocationService.java           # Service de localisation
│   │       │   ├── LocationAlarmReceiver.java     # Récepteur d'alarmes
│   │       │   └── BootReceiver.java              # Redémarrage auto
│   │       └── res/
│   │           ├── layout/
│   │           │   └── activity_main.xml          # Interface UI
│   │           └── values/
│   │               └── strings.xml                # Textes de l'app
├── build.gradle                      # Configuration projet
├── settings.gradle                   # Configuration modules
└── gradle.properties                 # Propriétés Gradle
```

## Permissions Requises

L'application demande les permissions suivantes :
- `ACCESS_FINE_LOCATION` : Localisation GPS précise
- `ACCESS_COARSE_LOCATION` : Localisation réseau
- `SEND_SMS` : Envoi de SMS
- `FOREGROUND_SERVICE` : Service en arrière-plan
- `FOREGROUND_SERVICE_LOCATION` : Service de localisation
- `POST_NOTIFICATIONS` : Notifications (Android 13+)
- `SCHEDULE_EXACT_ALARM` : Alarmes précises
- `RECEIVE_BOOT_COMPLETED` : Redémarrage automatique
- `WAKE_LOCK` : Maintien du téléphone éveillé

## Comment Compiler

### Prérequis
- JDK 8 ou supérieur
- Android SDK (API 24-34)
- Gradle 8.9+

### Compilation

#### Version Basic (sans nom de ville)
```bash
./gradlew assembleBasicDebug
```
APK généré : `app/build/outputs/apk/basic/debug/app-basic-debug.apk`

#### Version WithCity (avec nom de ville)
```bash
./gradlew assembleWithcityDebug
```
APK généré : `app/build/outputs/apk/withcity/debug/app-withcity-debug.apk`

#### Compiler les deux versions
```bash
./gradlew assembleDebug
```

#### Version Release (signée)
```bash
./gradlew assembleBasicRelease
./gradlew assembleWithcityRelease
```

## Utilisation

1. **Installation** : Installer l'APK sur votre téléphone Android
2. **Permissions** : Accorder toutes les permissions demandées
3. **Configuration** :
   - Entrer le numéro de téléphone destinataire (format international, ex: +33612345678)
   - Définir la fréquence en minutes (ex: 15 pour envoyer la position toutes les 15 minutes)
4. **Démarrage** : Appuyer sur "Démarrer le suivi"
5. **Arrêt** : Appuyer sur "Arrêter le suivi" pour stopper l'envoi

## Format des SMS

### Version Basic
```
Position: 48.856614, 2.352222
Google Maps: https://maps.google.com/?q=48.856614,2.352222
```

### Version WithCity
```
Position: 48.856614, 2.352222
Ville: Paris
Google Maps: https://maps.google.com/?q=48.856614,2.352222
```

## Architecture Technique

### MainActivity
- Interface utilisateur pour la configuration
- Gestion des permissions
- Sauvegarde des paramètres (SharedPreferences)
- Planification des alarmes périodiques

### LocationService
- Service en premier plan (Foreground Service)
- Récupération de la position GPS via FusedLocationProviderClient
- Géocodage inversé (version WithCity)
- Envoi de SMS

### LocationAlarmReceiver
- Réception des alarmes périodiques
- Déclenchement du service de localisation

### BootReceiver
- Redémarrage automatique du suivi après reboot
- Restauration de la configuration sauvegardée

## Build Variants

Le projet utilise les Product Flavors Gradle pour créer deux versions :

- **basic** : Version simple avec coordonnées GPS uniquement
- **withcity** : Version avancée avec nom de ville

## Technologies Utilisées

- Android SDK (minSdk 24, targetSdk 34)
- Google Play Services Location (21.0.1)
- Material Design Components
- ConstraintLayout
- FusedLocationProviderClient pour le GPS
- AlarmManager pour les tâches périodiques
- SmsManager pour l'envoi de SMS
- Geocoder pour le reverse geocoding

## Notes de Sécurité

- Les permissions sont vérifiées au runtime (Android 6.0+)
- Le service utilise une notification pour la transparence
- Les SMS sont envoyés uniquement si les permissions sont accordées
- Les données de localisation ne sont pas stockées localement

## Compatibilité

- **Android 7.0 (API 24) et supérieur**
- Testé jusqu'à Android 14 (API 34)

### Pourquoi minSDK 24?
Le minimum SDK a été choisi comme Android 7.0 (API 24) pour les raisons suivantes:
- Support natif des services en premier plan avec notifications améliorées
- Meilleure gestion des permissions runtime
- API de localisation plus stable et performante
- Support de l'optimisation de batterie Doze mode
- Représente >95% des appareils Android actifs en 2024

## Licence

Ce projet est fourni tel quel pour usage personnel ou éducatif.
