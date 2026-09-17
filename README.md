# Aryntra Darpan (दर्पण)

> A lightweight native Android utility that exposes structured diagnostic and environment insights about the host device.

---

## Overview

**Aryntra Darpan** (दर्पण — *mirror / reflection*) is a native Android utility built using **Kotlin** and **Jetpack Compose**. It serves as an introspective lens into the underlying Android operating system, device hardware, battery state, connectivity, and storage environment.

This project is also designed as an idiomatic, clean Kotlin/Android learning foundation focusing directly on modern Android SDK APIs without extraneous third-party abstractions.

---

## Goals

- Provide clear, direct visibility into device hardware, OS properties, battery status, and local environment.
- Demonstrate idiomatic native Android architecture using Jetpack Compose.
- Maintain minimal third-party dependencies and rely directly on standard Android SDK APIs.
- Deliver an understandable, well-documented codebase for native Android development.

---

## Tech Stack & Toolchain Baseline

| Component | Version / Specification |
|---|---|
| **Language** | Kotlin `2.0.21` |
| **UI Framework** | Jetpack Compose (Compose BOM `2024.10.01`) |
| **Android Gradle Plugin** | `8.7.3` |
| **Gradle** | `8.10.2` |
| **Compile SDK** | API 35 (Android 15) |
| **Target SDK** | API 35 (Android 15) |
| **Minimum SDK** | API 26 (Android 8.0 Oreo) |
| **JDK** | OpenJDK 21 (bundled with Android Studio) |
| **Package Name** | `com.aryntra.darpan` |

---

## Prerequisites

- **Android Studio** (Ladybug / Jellyfish or newer recommended)
- **JDK 21**
- **Android SDK** (API 35 SDK Platform & Build-Tools)
- **Android SDK Platform-Tools** (`adb`)
- Physical Android device (USB Debugging enabled) OR an Android Virtual Device (AVD)

---

## Setup

1. Clone or open the repository root:
```bash
   cd aryntra-darpan
```
2. Ensure local.properties specifies your Android SDK directory:

```properties
sdk.dir=C\:\\Users\\<YourUser>\\AppData\\Local\\Android\\Sdk
```

3. Build the debug APK via the Gradle wrapper:

```PowerShell
.\gradlew.bat assembleDebug
```

## Running

>Ensure your Android device or emulator is connected:

```PowerShell
adb devices
```

### Install and start the application:

```PowerShell
.\gradlew.bat installDebug
adb shell am start -n "com.aryntra.darpan/com.aryntra.darpan.MainActivity"
```

## Project Structure
```text
aryntra-darpan/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/aryntra/darpan/
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/
│   │   │   │   └── values/
│   │   │   │       ├── strings.xml
│   │   │   │       └── themes.xml
│   │   │   └── AndroidManifest.xml
│   │   ├── test/
│   │   └── androidTest/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
│
├── docs/
│   └── architecture.md
│
├── screenshots/
│   └── s0-baseline.png
│
├── gradle/
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   └── gradle-wrapper.properties
│   └── libs.versions.toml
│
├── .gitignore
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── LICENSE
└── README.md
```

## Development Roadmap

# Development Roadmap

*   [x] **Sprint S0 — Foundation Baseline (v0.0.0)**
    *   Clean Kotlin + Jetpack Compose project bootstrap
    *   Reproducible Gradle build workflow
    *   Device deployment and verification baseline
*   [x] **Sprint S1 — Kotlin & Activity Lifecycle**
    *   Activity lifecycle logging & state handling
    *   Idiomatic Kotlin data models
*   [x] **Sprint S2 — Core Device & OS Inspection**
    *   `Build`, `VERSION`, and system property inspection
*   [x] **Sprint S3 — Power & Battery Diagnostics**
    *   `BatteryManager` broadcast receiver and charge status
*   [x] **Sprint S4 — Storage & Memory Diagnostics**
    *   Internal/external storage stats and RAM breakdown
*   [x] **Sprint S5 — Network & Connectivity State**
    *   `ConnectivityManager` and network capability reporting