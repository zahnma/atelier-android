# Atelier

A simple Android app for browsing luxury fashion houses and their creative leadership history — current and past creative directors with tenure dates.

## Features

- Browse ~25 curated luxury and contemporary fashion brands
- Search brands by name, country, or category
- View creative director timelines with roles, dates, and bios
- Offline-first with bundled seed data
- Pull-to-refresh to fetch updates from GitHub-hosted JSON

## Build & Run

Requirements: Android Studio Ladybug or newer, JDK 17, Android SDK 35.

```bash
./gradlew assembleDebug
```

Install the debug APK on a device or emulator:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Updating Data

Edit [`data/brands.json`](data/brands.json) and validate:

```bash
python3 scripts/validate_brands_json.py data/brands.json
```

Copy the same file to the app seed bundle:

```bash
cp data/brands.json app/src/main/assets/brands_seed.json
```

After merging to `main`, the app pulls updates from:

`https://raw.githubusercontent.com/Zahnma/atelier-android/main/data/brands.json`

## Project Structure

- `app/` — Android application (Kotlin, Jetpack Compose, Material 3)
- `data/brands.json` — canonical remote-updatable dataset
- `scripts/validate_brands_json.py` — schema and integrity checks

## License

MIT
