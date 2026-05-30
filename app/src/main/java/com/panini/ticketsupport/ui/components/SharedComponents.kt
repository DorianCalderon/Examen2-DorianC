In com.panini.ticketsupport/viewmodel/, implement the following ViewModels:

1. LoginViewModel.kt
- Simulates login with hardcoded credentials (user: "panini", pass: "2026")
- Exposes UiState<Boolean> via StateFlow
- On success, stores a fake token using TokenManager

2. TicketListViewModel.kt
- Loads tickets from TicketRepository on init
- Exposes StateFlow<UiState<List<Ticket>>>
- Collects TicketEventBus.events in viewModelScope:
* On TicketCreated: adds ticket to current list
* On TicketPriorityUpdated: updates priority and re-sorts list (CRITICAL first)
- Applies FeatureFlags.SHOW_CLOSED_TICKETS to filter list before exposing it

3. TicketDetailViewModel.kt
- Receives ticketId, loads ticket from repository
- Exposes StateFlow<UiState<Ticket>>
- Has a function updateStatus(newStatus) that calls repository and emits no event
(status change is local for now)
- Has a function updatePriority(newPriority) that emits TicketPriorityUpdated to the event bus
only if FeatureFlags.UPDATE_PRIORITY_ENABLED is true

4. CreateTicketViewModel.kt
- Holds form state (title, description, priority, provider, category) as individual StateFlows
- Has a submit() function that validates fields, calls repository,
then emits TicketCreated to event bus
- Exposes a submissionState: StateFlow<UiState<Unit>>
- Respects FeatureFlags.CREATE_TICKET_ENABLED — if false, submissionState emits Error immediately

Use viewModelScope for all coroutines. No Android framework imports beyond ViewModel and viewModelScope.package com.panini.ticketsupport.ui.components

/** Reusable UI composables shared across multiple screens (buttons, cards, loaders, etc.). */
