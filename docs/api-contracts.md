# API Contracts

Full contract: [`/contracts/tickets-api.yaml`](/contracts/tickets-api.yaml) (OpenAPI 3.0.3)

---

## Endpoints

### Authentication

| Method | Path | Auth required |
|---|---|---|
| `POST` | `/auth/login` | No |

**Request:** `{ username: String, password: String }`  
**Response:** `{ token: String, userId: String }`  
**App flow:** `LoginViewModel.login()` → writes `token` to `TokenManager` → `AuthInterceptor` injects it on all subsequent requests.

---

### Tickets

All ticket endpoints require `Authorization: Bearer <token>`.

| Method | Path | App flow |
|---|---|---|
| `GET` | `/tickets` | `TicketListViewModel.loadTickets()` |
| `GET` | `/tickets/{id}` | `TicketDetailViewModel.loadTicket()` |
| `POST` | `/tickets` | `CreateTicketViewModel.submit()` |
| `PATCH` | `/tickets/{id}/status` | `TicketDetailViewModel.updateStatus()` |
| `PATCH` | `/tickets/{id}/priority` | `TicketDetailViewModel.updatePriority()` |

---

## Shared Response Shape

All ticket endpoints return:
```json
{
  "success": true,
  "data": { ...TicketDto... }
}
```
List endpoint wraps `data` as an array. On error, `success: false` and `data` is absent; an
`error: { code, message }` object is present instead.

---

## Ticket Fields

| Field | Type | Notes |
|---|---|---|
| `id` | `String (uuid)` | Server-assigned |
| `title` | `String` | Max 120 chars |
| `description` | `String` | Max 2000 chars |
| `priority` | `LOW \| MEDIUM \| HIGH \| CRITICAL` | |
| `status` | `OPEN \| IN_PROGRESS \| RESOLVED \| CLOSED` | |
| `provider` | `String` | Distributor or supplier name |
| `createdAt` | `String (ISO-8601)` | Server-assigned, mapped to `LocalDateTime` |
| `category` | `INVENTORY \| LOGISTICS \| DISTRIBUTION \| SUPPLIER \| OTHER` | |

Enum values are transmitted as plain uppercase strings. `TicketDto.toDomain()` converts
them to Kotlin enums with a safe fallback on unknown values.

---

## Create Request Fields

`POST /tickets` body (`CreateTicketRequest`):

| Field | Required |
|---|---|
| `title` | Yes |
| `description` | Yes |
| `priority` | Yes |
| `provider` | Yes |
| `category` | Yes |

`id`, `status`, and `createdAt` are **not sent** — they are server-assigned.

---

## HTTP Status Codes

| Code | Meaning |
|---|---|
| `200` | Success |
| `400` | Validation error (bad field value or missing required field) |
| `401` | Missing or expired JWT |
| `500` | Unexpected server error |

---

## Contract ↔ App Mapping

```
contracts/tickets-api.yaml
    └── ApiService.kt          (Retrofit interface, mirrors endpoints 1:1)
        └── TicketRepository   (interface, domain-typed)
            └── TicketRepositoryImpl  (calls ApiService, maps DTOs to domain)
                └── ViewModels (consume domain models only)
```

The YAML is the source of truth for the backend team. `ApiService.kt` is the source of
truth for the Android team. Both must stay in sync when endpoints change.
