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

## 4. Sprint S1 Updates (Kotlin, Lifecycle & Context Foundation)

In Sprint S1, the application architecture was expanded to establish practical native Android runtime patterns:

### 4.1 Activity Lifecycle Observation & UI State Bridge
MainActivity intercepts system lifecycle callbacks (onCreate, onStart, onResume, onPause, onStop, onDestroy) and bridges these events to Jetpack Compose using an observable state field (currentLifecycleState). This demonstrates how the Android OS manages component lifecycles and drives reactive UI updates.

### 4.2 Android Context Bridge
Using LocalContext.current within the Compose tree, the app retrieves active environment properties:
- Package Name (context.packageName)
- Application Label (context.applicationInfo.loadLabel(...))

This provides a direct gateway to platform APIs without introducing premature abstractions.

### 4.3 Compose State & Kotlin Domain Model
- **DeviceSnapshot Data Class**: Demonstrates Kotlin's immutable properties (al) and nullable types (String?) utilizing the safe elvis operator (?:) fallback.
- **Unidirectional State Flow**: Click interactions mutate a state-backed snapshot, triggering immediate atomic recomposition.

## 5. Sprint S2 Updates (Compose Dashboard & UI Architecture)

In Sprint S2, the temporary S1 learning screen was evolved into the first structured Darpan dashboard, introducing UI decomposition and state hoisting while maintaining a zero-unnecessary-dependency baseline:

### 5.1 UI Decomposition Rationale
The monolithic `MainActivity.kt` was decomposed by UI responsibility:
- **`MainActivity.kt`**: Retains pure Android ComponentActivity responsibilities, system lifecycle Logcat telemetry (`DarpanLifecycle`), edge-to-edge layout bootstrap, and the `DeviceSnapshot` model definition.
- **`ui/DarpanDashboard.kt`**: Parent dashboard composable managing state hoisting, `LazyColumn` container scrolling, and user interaction (manual refresh).
- **`ui/DashboardComponents.kt`**: Modular, reusable Compose cards (`DeviceCard`, `BatteryCard`, `StorageCard`, `NetworkCard`, `SnapshotHistorySection`, `MetricRow`, `SectionContainer`) and their corresponding UI state data models (`DeviceUiState`, `BatteryUiState`, `StorageUiState`, `NetworkUiState`).

### 5.2 State Hoisting & Unidirectional Data Flow
Card composables receive immutable UI state instances rather than querying system services or context directly. State mutation on user refresh occurs at the hoisted dashboard root and flows downward into child composables, triggering atomic recomposition.

### 5.3 Deferred System APIs
All device, battery, storage, and network metrics are modeled synthetically for S2. Native Android system services (`Build`, `BatteryManager`, `StatFs`, `ConnectivityManager`) are strictly deferred to S3+.
