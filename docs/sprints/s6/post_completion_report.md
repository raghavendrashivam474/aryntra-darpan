# Sprint S6 — Post-Completion Report
## Local Persistence & Snapshot Architecture

| Field | Value |
|---|---|
| **Sprint** | S6 |
| **Version** | `v0.6.0` |
| **Baseline** | `v0.5.0` |
| **Branch** | `main` |
| **Status** | ✅ Completed |

---

## 1. Problem Statement

Prior to S6, Aryntra Darpan could observe the device and render live telemetry across four subsystems (Device, Battery, Storage, Network). However, the snapshot history introduced in S1 existed only as an in-memory `mutableStateListOf` inside the Compose dashboard. Every application restart destroyed all historical observations.

**S6's mandate:** Make Darpan capable of persisting point-in-time device snapshots to the phone's private local database so that observation history survives application restarts — without introducing any cloud dependency, external API, or unnecessary architectural complexity.

---

## 2. Architectural Decisions

### 2.1 Hybrid Dual-Path State Model

The central design decision in S6 is the strict separation between **live state** and **historical state**:

```
              LIVE PATH (Hot State — RAM)
Providers ──────────► Compose State ──► Dashboard UI
                         │
                         │  (capture on refresh)
                         ▼
                   DeviceSnapshot
                         │
                         ▼
              PERSISTENCE PATH (Cold State — Disk)
                   SnapshotStore
                         │
                      Room DAO
                         │
                    SQLite (darpan.db)
                         │
               App-Private Local Storage
```

**Rationale:** Making Room the source of truth for live dashboard rendering would introduce unnecessary disk I/O latency on every UI recomposition. The live path remains pure RAM. The persistence path operates asynchronously on `Dispatchers.IO` and never blocks the UI thread.

### 2.2 Persistence Boundary Interface

Rather than coupling the dashboard directly to Room DAOs, S6 introduces a narrow abstraction:

```kotlin
interface SnapshotStore {
    suspend fun save(snapshot: DeviceSnapshot): Long
    suspend fun getRecent(limit: Int = 10): List<DeviceSnapshot>
    suspend fun getAll(): List<DeviceSnapshot>
    suspend fun getCount(): Int
    suspend fun clear()
}
```

**Rationale:** This keeps the persistence surface area minimal and testable. The rest of the application does not know or care whether the underlying implementation is Room, raw SQLite, or a future alternative. No generic repository framework, DI container, or use-case layer was introduced — the boundary is intentionally narrow.

### 2.3 Domain Model Evolution

The S1 learning model `DeviceSnapshot(timestamp, sampleLabel, sequenceNumber)` carried zero actual telemetry data. S6 evolves this into a complete point-in-time observation capturing all four subsystem states:

- **Device:** deviceName, manufacturer, androidVersion
- **Battery:** batteryLevel, isBatteryCharging, batteryChargingStatus
- **Storage:** totalStorageGb, usedStorageGb, availableStorageGb
- **Network:** networkConnectionType, isNetworkConnected, networkStatusText

The domain model uses safe default values for all fields, ensuring backward compatibility with existing call sites.

### 2.4 Entity-Domain Separation

The Room entity (`SnapshotEntity`) is a separate class from the domain model (`DeviceSnapshot`), connected by explicit `fromDomain()` and `toDomain()` mapping functions.

**Rationale:** This prevents Compose-specific or database-specific concerns from leaking into each other. Future UI changes will not force database schema migrations, and future schema changes will not break the UI layer.

### 2.5 Crash Isolation

All `RoomSnapshotStore` methods wrap database operations in `try/catch` blocks with `Log.e` reporting. If persistence fails for any reason (corrupt database, disk full, initialization error), the live telemetry pipeline continues functioning normally.

**Rationale:** A persistence failure should degrade gracefully (empty history) rather than crash the entire application.

---

## 3. What Was Built

### 3.1 New Files

| File | Purpose |
|---|---|
| `snapshot/DeviceSnapshot.kt` | Immutable domain model for full device observations |
| `snapshot/SnapshotStore.kt` | Persistence boundary interface |
| `persistence/SnapshotEntity.kt` | Room `@Entity` with bidirectional domain mapping |
| `persistence/SnapshotDao.kt` | Room `@Dao` with insert, query, count, and clear |
| `persistence/DarpanDatabase.kt` | Thread-safe Room singleton (`darpan.db`) |
| `persistence/RoomSnapshotStore.kt` | Production `SnapshotStore` implementation |
| `test/.../SnapshotMappingTest.kt` | Unit tests for entity-domain mapping |
| `docs/sprints/s6/post_completion_report.md` | This report |

### 3.2 Modified Files

| File | Change |
|---|---|
| `gradle/libs.versions.toml` | Added Room `2.6.1` and KSP `2.0.21-1.0.28` |
| `app/build.gradle.kts` | Added KSP plugin and Room dependencies |
| `ui/DarpanDashboard.kt` | Integrated `SnapshotStore`, `LaunchedEffect` history loading, async persistence on refresh |
| `ui/DashboardComponents.kt` | Updated snapshot import, refined history card UI with telemetry details and count badge |
| `MainActivity.kt` | Removed S1 learning `DeviceSnapshot` data class (replaced by domain model) |
| `README.md` | Updated overview, tech stack, project structure, roadmap |
| `docs/architecture.md` | Added Section 9 documenting S6 persistence architecture |

### 3.3 What Was NOT Changed

Per the S6 brief's backward compatibility mandate:

- ❌ `DeviceInfoProvider.kt` — untouched
- ❌ `BatteryInfoProvider.kt` — untouched
- ❌ `StorageInfoProvider.kt` — untouched
- ❌ `NetworkInfoProvider.kt` — untouched
- ❌ `AndroidManifest.xml` — untouched (no new permissions required)
- ❌ No ViewModel introduced (no architectural need emerged)
- ❌ No DI framework (Hilt/Koin) introduced
- ❌ No generic repository pattern introduced
- ❌ No cloud, Firebase, or external synchronization

---

## 4. Dependencies Added

| Dependency | Version | Purpose |
|---|---|---|
| `androidx.room:room-runtime` | 2.6.1 | SQLite abstraction layer |
| `androidx.room:room-ktx` | 2.6.1 | Coroutine support for Room DAOs |
| `androidx.room:room-compiler` | 2.6.1 | KSP annotation processor |
| `com.google.devtools.ksp` | 2.0.21-1.0.28 | Kotlin Symbol Processing |

No other dependencies were introduced.

---

## 5. Runtime Behavior

### 5.1 Application Launch

```
User opens Darpan
       ↓
Providers collect current state → RAM → Dashboard renders immediately
       ↓
LaunchedEffect queries SnapshotStore.getRecent(10)
       ↓
If history exists → populate from SQLite
If empty → capture initial baseline snapshot → persist
```

### 5.2 Refresh Action

```
User taps Refresh
       ↓
1. Re-query all four providers (synchronous, RAM)
2. Update Compose state (instant UI update)
3. Construct full DeviceSnapshot with all telemetry
4. Add to in-memory history list
5. Launch coroutine → SnapshotStore.save() on Dispatchers.IO
```

The UI never waits for disk I/O.

### 5.3 Application Restart

```
User closes and reopens Darpan
       ↓
LaunchedEffect loads persisted snapshots from SQLite
       ↓
Previous observations restored in history card
```

---

## 6. Testing

### 6.1 Unit Tests

**`SnapshotMappingTest`** (4 test cases, all passing):
- `domainToEntity_mapsAllFieldsAccurately` — verifies all 16 fields map correctly from domain to entity
- `entityToDomain_mapsAllFieldsAccurately` — verifies reverse mapping
- `bidirectionalMapping_preservesEquality` — verifies `domain → entity → domain` round-trip equality
- `defaultConstructor_hasSafeFallbackValues` — verifies safe defaults for partial construction

**Regression:** All 10 existing S0–S5 provider tests pass with zero modifications.

### 6.2 Physical Device Verification

| Step | Action | Result |
|---|---|---|
| 1 | Install S6 debug build on physical device | ✅ Success |
| 2 | Launch Darpan, observe live telemetry | ✅ All four cards render real data |
| 3 | Verify initial baseline snapshot in history | ✅ `#1 • Initial System Baseline` with `1 saved` badge |
| 4 | Tap Refresh 3 times | ✅ Snapshots `#2`, `#3`, `#4` appear instantly |
| 5 | Force-stop app (`adb shell am force-stop`) | ✅ Process killed |
| 6 | Relaunch app | ✅ Previous snapshots restored from SQLite |
| 7 | Tap Refresh after restart | ✅ New snapshot appended to existing history |

---

## 7. UI Changes

S6 includes proportional UI refinement without major redesign:

- **Snapshot history card** renamed from "Recent Snapshots" to "Persistent History"
- **Count badge** showing `N saved` in the card header
- **Snapshot rows** now display captured telemetry: `82% • 57GB used • Wi-Fi`
- **Empty state** preserved for first-run experience
- **Visual styling** consistent with existing Material 3 dark/light theme

---

## 8. Known Limitations & Future Work

| Item | Sprint |
|---|---|
| History filtering and search | S7 |
| Detailed snapshot drill-down view | S7 |
| Lifecycle-aware background observation | S8/S9 |
| Analytics and trend aggregation | S10 |
| Database migration strategy | When schema evolves |
| History size management / pruning | S7+ |

---

## 9. Definition of Done

| Criterion | Status |
|---|---|
| Hybrid RAM + persistent architecture implemented | ✅ |
| Persistence boundary exists (`SnapshotStore`) | ✅ |
| Room backed by SQLite | ✅ |
| Database is local/app-private | ✅ |
| No cloud/external persistence | ✅ |
| No unnecessary architecture explosion | ✅ |
| Snapshots can be persisted | ✅ |
| Recent snapshots can be retrieved | ✅ |
| History survives app restart | ✅ |
| Dashboard remains responsive | ✅ |
| Refresh collects fresh telemetry | ✅ |
| S0–S5 behavior intact | ✅ |
| Persistence failure doesn't crash live telemetry | ✅ |
| Empty history handled | ✅ |
| Unit tests pass | ✅ |
| Regression tests pass | ✅ |
| Build succeeds | ✅ |
| Physical device verification completed | ✅ |
| Documentation updated | ✅ |
| Atomic commits ready | ✅ |

---

## 10. Summary

S6 transforms Darpan from a transient observation tool into a persistent device journal. The hybrid architecture ensures that live telemetry remains instant and reliable while historical observations accumulate safely in local SQLite. The implementation is minimal, well-tested, and introduces no unnecessary complexity — providing a stable foundation for S7's richer history experience and S8–S10's advanced capabilities.