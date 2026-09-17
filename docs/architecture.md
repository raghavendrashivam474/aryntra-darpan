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

## 6. Sprint S3 Updates (Core Device & OS Inspection)

Sprint S3 connects the first real Android platform data layer to the Compose UI:

### 6.1 Provider Abstraction (device/DeviceInfoProvider.kt)
- **Single Responsibility**: Gathers hardware identity and OS version properties from ndroid.os.Build and ndroid.os.Build.VERSION.
- **Decoupling**: Compose UI components remain 100% pure and unaware of Android system classes, consuming only immutable DeviceUiState models.
- **Resilience & Normalization**: Formats raw hardware strings (e.g. capitalized manufacturer names, fallback strings for blank or "unknown" values, SDK release and API integer pairing).

### 6.2 Data Flow Pipeline
```text
Android SDK (Build.MODEL, Build.MANUFACTURER, Build.VERSION)
       │
       ▼
DeviceInfoProvider.getDeviceInfo()
       │
       ▼
DeviceUiState(deviceName, manufacturer, androidVersion)
       │
       ▼
DarpanDashboard (Hoisted State)
       │
       ▼
DeviceCard (Pure Material 3 Presentation)
```

## 7. Sprint S4 Updates (Battery & Storage Inspection)

Sprint S4 connects real Android platform telemetry for the Battery and Storage cards, removing mock state cycles while preserving pure UI state hoisting:

### 7.1 Battery Provider (`battery/BatteryInfoProvider.kt`)
- **Broadcast Interception**: Queries the Android OS sticky intent `Intent.ACTION_BATTERY_CHANGED` via application `Context` without retaining long-lived receiver references or leaking Activity contexts.
- **Metric Derivations**: Computes accurate `levelPercentage` using `BatteryManager.EXTRA_LEVEL` and `BatteryManager.EXTRA_SCALE`.
- **Charging State Mapping**: Normalizes `BatteryManager.EXTRA_STATUS` into human-readable strings (`"Charging"`, `"Discharging"`, `"Full"`, `"Not charging"`) and assigns the `isCharging` boolean flag.
- **Resilience**: Enforces safe bound-checks (`0..100%`) and encapsulates system service exceptions with fallback UI states to prevent application crashes on custom ROMs or emulators.

### 7.2 Storage Provider (`storage/StorageInfoProvider.kt`)
- **Filesystem Inspection**: Queries user-accessible storage partitions via `StatFs` using `Environment.getDataDirectory()`.
- **64-bit Capacity Arithmetic**: Uses `blockCountLong` and `blockSizeLong` (64-bit longs) to avoid integer overflow issues common with large modern storage volumes (>2GB/4GB boundaries).
- **Unit Normalization**: Computes `totalStorageGb`, `usedStorageGb`, and `availableStorageGb` using binary Gigabyte definitions (`1024^3` bytes), guaranteeing non-negative values and safe fraction denominators.

### 7.3 Data Flow Pipeline (Sprint S4 State)
```text
                    Android System
                         │
          ┌──────────────┼──────────────┐
          │              │              │
        Build       Battery APIs      StatFs
          │              │              │
          ▼              ▼              ▼
   DeviceInfoProvider  BatteryInfoProvider  StorageInfoProvider
          │              │              │
          ▼              ▼              ▼
    DeviceUiState   BatteryUiState   StorageUiState
          │              │              │
          └──────────────┼──────────────┘
                         ▼
                  DarpanDashboard
                         │
              ┌──────────┼──────────┐
              ▼          ▼          ▼
          DeviceCard BatteryCard StorageCard
                         │
                         ▼
                    Compose UI
```

## 8. Sprint S5 Updates (Network & Connectivity Inspection)

Sprint S5 replaces the mock network state with live Android platform connectivity reporting using `ConnectivityManager` and `NetworkCapabilities`:

### 8.1 Network Provider (`network/NetworkInfoProvider.kt`)
- **System Service Access**: Queries `Context.CONNECTIVITY_SERVICE` via safe application `Context` (`context.applicationContext`) to prevent leaking Activity references.
- **Active Network Resolution**: Retrieves the active network handle via `connectivityManager.activeNetwork` and inspects `connectivityManager.getNetworkCapabilities(...)`.
- **Capability Inspection**: Checks `NetworkCapabilities.NET_CAPABILITY_INTERNET` for internet connectivity and `NetworkCapabilities.NET_CAPABILITY_VALIDATED` for validated internet access.
- **Transport Identification**: Maps framework transports (`TRANSPORT_WIFI`, `TRANSPORT_CELLULAR`, `TRANSPORT_ETHERNET`, `TRANSPORT_BLUETOOTH`, `TRANSPORT_VPN`) to readable interface names.
- **Resilience & Fallbacks**: Gracefully handles disconnected/airplane mode state (returning `connectionType = "None"` / `statusText = "Disconnected"`) and encapsulates system exceptions.

### 8.2 Permission Architecture
- **Normal Permission**: Declares `android.permission.ACCESS_NETWORK_STATE` in `AndroidManifest.xml`.
- **No Runtime Dialog**: This is an install-time normal permission granted automatically by the OS; no runtime permission requests or complex managers are required.

### 8.3 Data Flow Pipeline (Sprint S5 State)
```text
                    Android System
                         │
          ┌──────────────┼──────────────┬──────────────┐
          │              │              │              │
        Build       Battery APIs      StatFs      Connectivity
          │              │              │              │
          ▼              ▼              ▼              ▼
   DeviceInfoProvider  BatteryInfoProvider  StorageInfoProvider  NetworkInfoProvider
          │              │              │              │
          ▼              ▼              ▼              ▼
    DeviceUiState   BatteryUiState   StorageUiState   NetworkUiState
          │              │              │              │
          └──────────────┼──────────────┴──────────────┘
                         ▼
                  DarpanDashboard
                         │
              ┌──────────┼──────────┬──────────┐
              ▼          ▼          ▼          ▼
          DeviceCard BatteryCard StorageCard NetworkCard
                         │
                         ▼
                    Compose UI
