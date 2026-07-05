# Pre-built APK downloads

This folder is populated by GitHub Actions — APK files are **not** committed to git.

## Install without building

1. Open the [latest GitHub Release](https://github.com/zahnma/atelier-android/releases/latest)
2. Download `atelier-*.apk`
3. On your Android phone:
   - Enable **Install unknown apps** for your browser or file manager
   - Open the downloaded APK and tap **Install**

No Android Studio or Gradle required.

## CI artifacts

Every successful CI run also uploads the APK as a workflow artifact:

1. Go to [Actions](https://github.com/zahnma/atelier-android/actions)
2. Open the latest **Android CI** run
3. Download the **atelier-apk** artifact

## For maintainers

Trigger a release manually:

```bash
gh workflow run release-apk.yml
```

Or tag a version:

```bash
git tag v1.0.0
git push origin v1.0.0
```
