# Panini FIFA World Cup 2026 — Support Ticket System (PoC)

Android proof-of-concept for a distributor support ticket management system built for
Panini's FIFA World Cup 2026 album distribution network.

---

## Description

Allows distributors and support agents to open, track, and update tickets related to
album distribution issues: supplier delays, inventory shortages, missing shipments,
wrong sticker packs, logistics problems, etc.

The backend does not exist yet. All data is served from in-memory mock objects. The
networking layer (Retrofit + OkHttp) is fully wired and ready to connect to a real API
by swapping one implementation class.

---

## How to Run

1. Clone the repository.
2. Open in **Android Studio Hedgehog** or later.
3. Sync Gradle (`File → Sync Project with Gradle Files`).
4. Run on an emulator or physical device with **API 26+**.
5. Log in with:
   - Username: `panini`
   - Password: `2026`

No network connection is required. All data is loaded from `MockTicketData.kt`.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 1.9+ |
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| Navigation | Jetpack Navigation Compose |
| Networking | Retrofit 2 + OkHttp (mock, not yet active) |
| Serialization | Gson |
| Async | Kotlin Coroutines + Flow |
| Event bus | `SharedFlow` (no third-party library) |
| Feature flags | Compile-time constants (Remote Config ready) |
| DI | None — `companion object` singletons |

---

## Package Structure

```
com.panini.ticketsupport/
├── core/
│   ├── events/          # TicketEventBus (SharedFlow), TicketEvent sealed class
│   ├── featureflags/    # FeatureFlags object (compile-time toggles)
│   └── state/           # UiState<T> sealed class (Loading / Success / Error)
├── data/
│   ├── mock/            # MockTicketData — hardcoded Ticket list
│   ├── remote/          # ApiService (Retrofit interface)
│   │   └── network/     # RetrofitClient, TokenManager, AuthInterceptor
│   └── repository/      # TicketRepository interface + TicketRepositoryImpl
├── model/
│   ├── domain/          # Ticket, Priority, TicketStatus, Category, User
│   └── dto/             # TicketDto, CreateTicketRequest, UpdateStatusRequest, etc.
├── navigation/          # AppNavGraph, NavigationRoutes
├── ui/
│   ├── components/      # Shared composables (badge, chip, dropdown, detail row)
│   ├── screens/
│   │   ├── login/       # LoginScreen
│   │   └── tickets/     # TicketListScreen, TicketDetailScreen, CreateTicketScreen
│   └── theme/           # Material3 theme
└── viewmodel/           # LoginViewModel, TicketListViewModel,
                         # TicketDetailViewModel, CreateTicketViewModel
```

---

## Replacing Mock Data with Real API Calls

All mock logic is isolated in `TicketRepositoryImpl.kt`. Every method contains a
`// TODO:` comment with the exact Retrofit call to substitute:

```kotlin
// Current (mock):
override suspend fun getTickets(): List<Ticket> {
    // TODO: replace with RetrofitClient.apiService.getTickets().data?.map { it.toDomain() }
    return store.toList()
}

// Future (real):
override suspend fun getTickets(): List<Ticket> {
    return RetrofitClient.apiService.getTickets().data?.map { it.toDomain() } ?: emptyList()
}
```

Steps to go live:
1. Replace each method body in `TicketRepositoryImpl` with the TODO Retrofit call.
2. Write `TokenManager.token` with the real JWT returned by `POST /auth/login`.
3. Update `BASE_URL` in `RetrofitClient` if the production URL differs.
4. Remove `MockTicketData.kt` if no longer needed for previews.

See `docs/architecture.md` for the full architectural overview.  
See `contracts/tickets-api.yaml` for the OpenAPI contract the backend must satisfy.
