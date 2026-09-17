---

# Sprint S0 Completion Report — Aryntra Darpan

**Project:** Aryntra Darpan (Local Device Inspector)
**Sprint:** S0 — Project Bootstrap & Development Foundation
**Date:** 2025
**Author:** Junior Developer
**Reviewer:** Senior Developer
**Baseline Tag:** `v0.0.0`
**Repository:** `C:\Users\ragha\Documents\Anti-grav\aryntra-darpan`

---

## 1. Executive Summary

Sprint S0 is **complete**. The repository contains a clean, reproducible, runnable Kotlin + Android + Jetpack Compose project that builds from the Gradle wrapper, installs on a physical device, and launches a baseline Compose screen. No feature functionality was implemented — this is strictly the frozen development foundation per the sprint brief.

The project was named **Aryntra Darpan** (दर्पण — mirror/reflection) to align with the existing `aryntra-*` project ecosystem in the workspace. Package name: `com.aryntra.darpan`.

---

## 2. What Was Done

### 2.1 Repository Bootstrap
- Created `aryntra-darpan/` inside the existing `Anti-grav` workspace alongside sibling projects (`aryntra-aayaam`, `aryntra-avni`, `aryntra-flux`, etc.).
- Initialized a fresh Git repository. No pre-existing files were overwritten or displaced.
- Created an Android/Gradle-aware `.gitignore` covering `.gradle/`, build outputs, IDE state, APKs, keystores, and `local.properties`.

### 2.2 Toolchain Discovery & Configuration
The host machine did **not** have `java`, `adb`, `ANDROID_HOME`, or `JAVA_HOME` on the system `PATH`. A deep scan of standard Windows installation directories located:

| Tool | Discovered Path | Version |
|---|---|---|
| **JDK** | `C:\Program Files\Android\Android Studio\jbr\bin\java.exe` | OpenJDK 21.0.10 |
| **Android SDK** | `C:\Users\ragha\AppData\Local\Android\Sdk` | — |
| **ADB** | `...\Sdk\platform-tools\adb.exe` | 1.0.41 (37.0.0) |
| **Gradle** | Downloaded via wrapper | 8.10.2 |

Session-level environment variables (`JAVA_HOME`, `ANDROID_HOME`, `ANDROID_SDK_ROOT`, `PATH`) were configured in PowerShell for the build session. These are **not** persisted system-wide — the Gradle wrapper and `local.properties` handle SDK resolution independently.

### 2.3 Gradle Build Infrastructure
- Downloaded the official Gradle 8.10.2 wrapper (`gradle-wrapper.jar`, `gradlew`, `gradlew.bat`) from the Gradle GitHub repository.
- Created `gradle/wrapper/gradle-wrapper.properties` pointing to `gradle-8.10.2-bin.zip`.
- Created `gradle.properties` with standard AndroidX and Kotlin code style flags.
- Created `local.properties` with the discovered SDK path.
- Created `gradle/libs.versions.toml` as a centralized version catalog:
  - AGP `8.7.3`
  - Kotlin `2.0.21`
  - Compose BOM `2024.10.01`
  - Core KTX `1.15.0`
  - Lifecycle Runtime KTX `2.8.7`
  - Activity Compose `1.9.3`
- Created `settings.gradle.kts` with Google/Maven Central/Gradle Plugin Portal repositories and `rootProject.name = "Aryntra Darpan"`.
- Created root `build.gradle.kts` declaring plugins with `apply false`.
- Created `app/build.gradle.kts` targeting `compileSdk = 35`, `minSdk = 26`, `targetSdk = 35`, JVM target 21, Compose enabled via the `kotlin-compose` plugin.

### 2.4 Android Application Baseline
- Created `app/src/main/AndroidManifest.xml` with a single `MainActivity` launcher activity, no special permissions.
- Created `app/src/main/res/values/strings.xml` and `themes.xml` (Material Light NoActionBar).
- Created `app/src/main/java/com/aryntra/darpan/MainActivity.kt`:
  - Extends `ComponentActivity`.
  - Calls `enableEdgeToEdge()`.
  - Sets Compose content with `MaterialTheme` → `Scaffold` → `DarpanBaselineScreen`.
  - `DarpanBaselineScreen` is a simple centered `Column` displaying the app name, subtitle ("दर्पण • Local Device Inspector"), and sprint status text.
  - No ViewModels, no navigation, no state management, no data layer. Intentionally flat.

### 2.5 Build Verification
- First build attempt **failed** due to a UTF-8 BOM (`\ufeff`) injected by PowerShell's `Set-Content -Encoding utf8` into `libs.versions.toml`. Gradle's TOML parser rejects BOM-prefixed files.
- **Fix:** Re-wrote all `.kts` and `.toml` configuration files using `[System.IO.File]::WriteAllText()` with `UTF8Encoding($false)` (no BOM). This is a known PowerShell gotcha on Windows.
- Second build **succeeded**: `BUILD SUCCESSFUL in 4m 2s`, 35 tasks executed.
- Generated APK: `app/build/outputs/apk/debug/app-debug.apk` (~9 MB).
- Note: Gradle auto-installed Android SDK Build-Tools 34.0.0 during the build since it was missing from the local SDK.

### 2.6 Device Deployment & Runtime Verification
- No emulators or AVDs were available on the machine.
- Connected a physical Android device via USB:
  - **Device:** Vivo I2207 (Serial: `10BD3G0K620002T`)
  - USB Debugging authorized on-device.
- `adb install -r` succeeded.
- `adb shell am start` launched the app successfully.
- `adb shell pidof com.aryntra.darpan` confirmed running process (PID 26417).
- Baseline screenshot captured via `adb exec-out screencap -p` and saved to `screenshots/s0-baseline.png`.

### 2.7 Documentation
- **`README.md`**: Overview, goals, tech stack table with exact versions, prerequisites, setup instructions, run instructions, project structure tree, and development roadmap (S0–S5).
- **`docs/architecture.md`**: Baseline architecture diagram, component responsibilities (MainActivity, Manifest, Compose UI, Gradle), and planned evolution notes.
- **`LICENSE`**: MIT license (no pre-existing license was present to preserve).

### 2.8 Git History
Six atomic commits by capability, tagged `v0.0.0`:

```
* a5ed516 (HEAD -> master, tag: v0.0.0) doc: add readme and architecture design document
* 0ee985e doc: add baseline run screenshot verification
* f901b6e feat(app): implement main activity and compose baseline UI
* ea369e4 feat(app): configure android app module and manifest skeleton
* 094f7e4 build: establish gradle wrapper and version catalog infrastructure
* d825563 chore: initialize repository infrastructure and license
```

Working tree is clean. No uncommitted files.

---

## 3. Issues Encountered & Resolutions

| # | Issue | Root Cause | Resolution |
|---|---|---|---|
| 1 | `java`, `adb` not found on `PATH` | Android Studio installed but environment variables not set system-wide | Deep-scanned standard Windows paths; configured session-level env vars; `local.properties` handles SDK path for Gradle |
| 2 | No `gradle-wrapper.jar` available locally | Fresh project, no sibling projects with Gradle | Downloaded official wrapper files from Gradle GitHub release tag |
| 3 | Build failed: `Unexpected '\ufeff'` in TOML | PowerShell `Set-Content -Encoding utf8` writes UTF-8 with BOM | Re-wrote files using `[System.IO.File]::WriteAllText()` with BOM-less UTF-8 encoding |
| 4 | No emulator/AVD available | No virtual devices configured in Android SDK | Used physical device (Vivo I2207) via USB debugging |
| 5 | Missing Build-Tools 34 | SDK had newer platform but not matching build-tools | Gradle auto-downloaded Build-Tools 34.0.0 during build |

---

## 4. What Was Explicitly NOT Done (Per Brief)

- ❌ No device info APIs (`Build`, `BatteryManager`, `StorageStatsManager`, `ConnectivityManager`)
- ❌ No persistence (Room, SQLite, SharedPreferences, DataStore)
- ❌ No MVVM / ViewModel / Repository / Use-case layers
- ❌ No dependency injection
- ❌ No navigation
- ❌ No networking
- ❌ No background work
- ❌ No multi-module architecture
- ❌ No custom theme engineering beyond generated Material3 baseline
- ❌ No animations or custom design system
- ❌ No permissions beyond the generated baseline

---

## 5. Acceptance Criteria Status

### Repository
- [x] Root directory exists (`aryntra-darpan/`)
- [x] Git repository initialized
- [x] `.gitignore` is correct (Android/Gradle/IDE exclusions)
- [x] No build artifacts or machine-specific files committed (`local.properties` is gitignored)

### Toolchain
- [x] Android Studio installed (confirmed via bundled JBR)
- [x] JDK 21 configured
- [x] Android SDK installed (API 35)
- [x] Platform Tools available (ADB 37.0.0)
- [x] Physical device available (Vivo I2207)

### Application
- [x] Kotlin Android application generated
- [x] Jetpack Compose enabled (BOM 2024.10.01, Kotlin Compose plugin 2.0.21)
- [x] `MainActivity` exists
- [x] Manifest exists
- [x] Project builds successfully (`assembleDebug` — 35 tasks, 0 errors)
- [x] Application installs (`adb install` — Success)
- [x] Application launches (PID confirmed)

### Documentation
- [x] README exists
- [x] Prerequisites documented
- [x] Setup instructions documented
- [x] Project structure documented
- [x] Architecture baseline documented
- [x] Roadmap documented without claiming future features as implemented

### Git
- [x] Initial baseline committed (6 atomic commits)
- [x] Working tree clean after commit
- [x] Baseline tagged (`v0.0.0`)

---

## 6. Handoff Notes for S1

The frozen baseline at `v0.0.0` is ready for Sprint S1 work. Key context for the next developer picking this up:

1. **Environment variables are session-only.** Each new PowerShell session needs `JAVA_HOME` and `ANDROID_HOME` set before running Gradle or ADB. Consider adding them to `$PROFILE` or system environment variables if this becomes tedious. The Gradle wrapper itself works without them because `local.properties` handles SDK resolution.

2. **The BOM issue is real.** Any future PowerShell script that generates `.kts`, `.toml`, or `.xml` files must use `[System.IO.File]::WriteAllText($path, $content, (New-Object System.Text.UTF8Encoding $false))` instead of `Set-Content -Encoding utf8`.

3. **`MainActivity.kt` is intentionally flat.** It contains both the Activity and the Composable in a single file. This is deliberate per the brief — S1 should introduce Kotlin fundamentals and Activity lifecycle concepts before splitting files.

4. **No `local.properties` in Git.** It contains the machine-specific SDK path. It is gitignored. New developers must create their own.

5. **Physical device was used for testing.** No AVD is configured. If S1 requires emulator testing, an AVD will need to be created via Android Studio's Device Manager.

6. **The LF/CRLF warnings in git are cosmetic.** They occur because the files were written with LF line endings on Windows. They do not affect functionality. A `.gitattributes` file could normalize this in a future sprint if desired.

---

## 7. Conclusion

> *This sprint establishes the ground, not the building.*

S0 delivered exactly what the brief specified: a clean, reproducible, documented native Android/Kotlin development foundation. The application compiles, installs, and runs on a physical device. The repository is tagged at `v0.0.0` with a clean working tree and an atomic commit history.

S1 can begin from this frozen baseline and deliberately introduce **Kotlin fundamentals + Android Activity lifecycle + Context**.

---

**End of Sprint S0 Report**