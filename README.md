# Aryntra Darpan (दर्पण)

> A lightweight native Android utility that exposes structured diagnostic and environment insights about the host device.

---

## Overview

**Aryntra Darpan** (दर्पण — *mirror / reflection*) is a native Android utility built using **Kotlin** and **Jetpack Compose**. It serves as an introspective lens into the underlying Android operating system, device hardware, battery state, connectivity, and storage environment.

This project has completed **Sprint S5 (Network & Connectivity Inspection)**. The dashboard renders real native metrics:
- **Device & OS**: Hardware identity and Android release via Build APIs
- **Battery**: Real-time percentage, charging state, and power status via BatteryManager
- **Storage**: Real filesystem capacity, used space, and free space via StatFs
- **Network**: Real-time network transport (Wi-Fi, Cellular, Ethernet) and capability validation via ConnectivityManager
- **Snapshot History**: Persistent point-in-time device observations stored in local SQLite via Room (S6)

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
| **Local Database** | Room `2.6.1` (SQLite) |
| **Annotation Processing** | KSP `2.0.21-1.0.28` |
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
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/aryntra/darpan/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── battery/
│   │   │   │   │   └── BatteryInfoProvider.kt
│   │   │   │   ├── device/
│   │   │   │   │   └── DeviceInfoProvider.kt
│   │   │   │   ├── network/
│   │   │   │   │   └── NetworkInfoProvider.kt
│   │   │   │   ├── snapshot/
│   │   │   │   │   ├── DeviceSnapshot.kt
│   │   │   │   │   └── SnapshotStore.kt
│   │   │   │   ├── persistence/
│   │   │   │   │   ├── DarpanDatabase.kt
│   │   │   │   │   ├── SnapshotEntity.kt
│   │   │   │   │   ├── SnapshotDao.kt
│   │   │   │   │   └── RoomSnapshotStore.kt
│   │   │   │   ├── storage/
│   │   │   │   │   └── StorageInfoProvider.kt
│   │   │   │   └── ui/
│   │   │   │       ├── DarpanDashboard.kt
│   │   │   │       └── DashboardComponents.kt
│   │   │   ├── res/
│   │   │   │   └── values/
│   │   │   │       ├── strings.xml
│   │   │   │       └── themes.xml
│   │   │   └── AndroidManifest.xml
│   │   ├── test/
│   │   │   └── java/com/aryntra/darpan/
│   │   │       ├── battery/
│   │   │       │   └── BatteryInfoProviderTest.kt
│   │   │       ├── device/
│   │   │       │   └── DeviceInfoProviderTest.kt
│   │   │       ├── network/
│   │   │       │   └── NetworkInfoProviderTest.kt
│   │   │       ├── persistence/
│   │   │       │   └── SnapshotMappingTest.kt
│   │   │       └── storage/
│   │   │           └── StorageInfoProviderTest.kt
│   │   └── androidTest/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
│
├── docs/
│   ├── architecture.md
│   └── sprints/
│       ├── s3/
│       └── s4/
│           ├── post_completion_report.md
│           └── s4_device_verification.png
│       └── s5/
│           └── post_completion_report.md
│
├── gradle/
│   ├── wrapper/
│   └── libs.versions.toml
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── LICENSE
└── README.md
```

## Development Roadmap

*   [x] **Sprint S0 — Foundation Baseline (`v0.0.0`)**
    *   Clean Kotlin + Jetpack Compose project bootstrap & Gradle verification
*   [x] **Sprint S1 — Kotlin & Activity Lifecycle (`v0.1.0`)**
    *   Activity lifecycle logging, state hoisting, and foundational domain model
*   [x] **Sprint S2 — Compose Dashboard & Information Architecture (`v0.2.0`)**
    *   Structured Darpan dashboard with decomposed Compose UI components
*   [x] **Sprint S3 — Core Device & OS Inspection (`v0.3.0`)**
    *   Native Android `Build` API integration via `DeviceInfoProvider`
*   [x] **Sprint S4 — Battery & Storage Inspection (`v0.4.0`)**
    *   Real-time `BatteryManager` broadcast intent and `StatFs` filesystem metrics
*   [x] **Sprint S5 — Network & Connectivity State (`v0.5.0`)**
    *   `ConnectivityManager` and network capability reporting
*   [x] **Sprint S6 — Local Persistence & Snapshot Architecture (0.6.0)**
    *   Room-backed SQLite persistence with hybrid RAM + disk state architecture