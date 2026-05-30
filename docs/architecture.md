# Architecture — Panini Support Ticket System

## Overview

The app follows **MVVM** (Model-View-ViewModel) with a unidirectional data flow:

```
UI (Composable)
    ↕ observes StateFlow / calls functions
ViewModel
    ↕ calls suspend functions
Repository (interface)
    ↕ implemented by
RepositoryImpl  →  MockData  (now)
                →  RetrofitClient / ApiService  (future)
```

---

## Layer Responsibilities

### `model/`
Pure data. No Android dependencies.
- `domain/` — app-internal data classes (`Ticket`, enums). Used everywhere above the data layer.
- `dto/` — API wire types (all fields are `String?`). Used only inside `RepositoryImpl` and `ApiService`. Mapped to domain via `TicketDto.toDomain()`.

### `data/`
Data access. No UI or ViewModel dependencies.
- `repository/TicketRepository` — interface that defines what the app can do with tickets. Returns domain models only.
- `repository/TicketRepositoryImpl` — the only class that knows whether data comes from mock or network.
- `remote/ApiService` — Retrofit interface. Declares HTTP endpoints, returns DTOs.
- `remote/network/RetrofitClient` — singleton OkHttp + Retrofit instance. `TokenManager` holds the session JWT.
- `mock/MockTicketData` — hardcoded list used until the backend exists.

### `viewmodel/`
Business logic and state. No UI imports.
- Exposes `StateFlow<UiState<T>>` to the UI. Never exposes raw data or DTOs.
- Calls `Repository` suspend functions inside `viewModelScope`.
- Reads `FeatureFlags` to guard operations before they reach the repository.
- Emits / collects events via `TicketEventBus`.

### `ui/`
Display only. No business logic.
- Composables observe `StateFlow` via `collectAsState()`.
- User actions call ViewModel functions — no direct data manipulation.
- `ui/components/` holds stateless, reusable composables (badges, chips, dropdowns).

### `core/`
Cross-cutting infrastructure shared by all layers.
- `state/UiState` — typed result wrapper used by every ViewModel.
- `events/` — app-wide reactive event bus.
- `featureflags/` — compile-time feature toggles.

---

## Event Bus

`TicketEventBus` allows one screen's ViewModel to notify another without them sharing
a ViewModel instance or requiring a full data reload.

```
TicketDetailViewModel.updatePriority()
    → repository.updatePriority()       (updates local store)
    → TicketEventBus.emit(
          TicketPriorityUpdated(ticketId, newPriority)
      )

TicketListViewModel.observeEvents()     (collecting in viewModelScope)
    ← receives TicketPriorityUpdated
    → patches the in-memory list + re-sorts
    → emits new UiState.Success(updatedList)

TicketListScreen
    ← collectAsState() recomposes automatically
```

**Key design choices:**
- `replay = 0` — late collectors get nothing. A stale priority update should not replay
  when the list screen reappears; it will reload from the repository on its own.
- `extraBufferCapacity = 0` — emitter suspends if no collector is active. This is
  intentional for cross-screen sync events that are only meaningful when the target
  screen is in the back stack.
- Events carry only IDs and changed values, never full objects, to avoid stale-reference issues.

**Current events:**

| Event | Emitted by | Collected by |
|---|---|---|
| `TicketCreated(ticket)` | `CreateTicketViewModel.submit()` | `TicketListViewModel` |
| `TicketPriorityUpdated(id, priority)` | `TicketDetailViewModel.updatePriority()` | `TicketListViewModel` |

**Adding a new event:**
1. Add a `data class` subtype to `TicketEvent`.
2. Emit it from the appropriate ViewModel via `TicketEventBus.emit(...)`.
3. Add a `when` branch in the target ViewModel's `observeEvents()`.

---

## Feature Flags

Defined in `core/featureflags/FeatureFlags.kt` as compile-time `const val` booleans.

| Flag | Default | Controls |
|---|---|---|
| `CREATE_TICKET_ENABLED` | `true` | FAB visibility in list; form rendering; ViewModel submit guard |
| `UPDATE_PRIORITY_ENABLED` | `true` | Priority chip row in detail; ViewModel update guard |
| `SHOW_CLOSED_TICKETS` | `false` | Filter applied to ticket list before emitting to UI |
| `ADMIN_ACTIONS_ENABLED` | `false` | Admin button section in detail screen |

Each flag is enforced at **two levels**:
- **ViewModel** — returns early before any repository call.
- **Composable** — hides the UI element so the action is never reachable.

**Adding a new flag:**
```kotlin
// In FeatureFlags.kt
const val MY_NEW_FEATURE = false

// In the ViewModel
fun doSomething() {
    if (!FeatureFlags.MY_NEW_FEATURE) return
    ...
}

// In the Composable
if (FeatureFlags.MY_NEW_FEATURE) {
    MyFeatureButton(...)
}
```

**Future: Firebase Remote Config**

Replace the `object` body with remote values fetched at app start:
```kotlin
object FeatureFlags {
    var CREATE_TICKET_ENABLED: Boolean = remoteConfig.getBoolean("create_ticket_enabled")
    ...
}
```
No other code needs to change because all consumers read from `FeatureFlags` by reference.

---

## Swapping Mock for Real Retrofit

The repository interface is the seam. Nothing above `TicketRepositoryImpl` changes.

**`TicketRepositoryImpl` — before (mock):**
```kotlin
override suspend fun getTickets(): List<Ticket> {
    // TODO: replace with RetrofitClient.apiService.getTickets().data?.map { it.toDomain() }
    return store.toList()
}
```

**`TicketRepositoryImpl` — after (real):**
```kotlin
override suspend fun getTickets(): List<Ticket> {
    return RetrofitClient.apiService.getTickets().data?.map { it.toDomain() } ?: emptyList()
}
```

Every method in `TicketRepositoryImpl` has an identical TODO comment. The Retrofit
infrastructure (`RetrofitClient`, `ApiService`, `AuthInterceptor`, `TokenManager`, DTOs,
`toDomain()` mappers) is already fully implemented and requires no changes.
