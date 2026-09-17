# Architecture Baseline — Aryntra Darpan

## 1. Overview

>Aryntra Darpan is structured as a single-module, idiomatic native Android application built on Kotlin and Jetpack Compose.

```text
Android OS
    │
    ▼
MainActivity (ComponentActivity)
    │
    ▼
Jetpack Compose (DarpanBaselineScreen)
    │
    ▼
Baseline Material3 UI
```

## 2. Core Responsibilities (Sprint S0 Baseline)

### MainActivity.

- Serves as the primary entry point registered in the Android Manifest.
- Inherits from androidx.activity.ComponentActivity.
- Sets up edge-to-edge display and binds the Compose UI hierarchy via setContent.

### AndroidManifest.

- Declares the application identity, theme, icon, and main launcher intent filter.
- Currently minimal with no special runtime permissions requested during bootstrap.

### Compose UI (DarpanBaselineScreen)

- Declarative UI tree utilizing Material3 Scaffold, Surface, and Text components.
- Serves as the visual verification anchor for the development environment.

### Gradle Configuration (build.gradle.kts & libs.versions.toml)

- Centralized dependency catalog managed via gradle/libs.versions.toml.
- Configured with Kotlin Compose compiler plugin (Kotlin 2.0+).
- Targets Android API 35 with compatibility down to API 26 (Android 8.0).

## 3. Planned Evolution (Future Sprints)

The following architectural components are intentionally deferred to future sprints:

- State Holders / ViewModels: Will be introduced when dynamic system data streams are integrated.
- Provider Layer: Direct native SDK API wrappers for Build, BatteryManager, StorageStatsManager, and ConnectivityManager.
- Local Persistence: Snapshot caching mechanism.