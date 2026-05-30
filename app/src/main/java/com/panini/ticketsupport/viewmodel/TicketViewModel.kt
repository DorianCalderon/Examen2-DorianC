package com.panini.ticketsupport.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panini.ticketsupport.core.events.TicketEvent
import com.panini.ticketsupport.core.events.TicketEventBus
import com.panini.ticketsupport.core.featureflags.FeatureFlags
import com.panini.ticketsupport.core.state.UiState
import com.panini.ticketsupport.data.repository.TicketRepository
import com.panini.ticketsupport.data.repository.TicketRepositoryImpl
import com.panini.ticketsupport.model.domain.Priority
import com.panini.ticketsupport.model.domain.Ticket
import com.panini.ticketsupport.model.domain.TicketStatus
import com.panini.ticketsupport.model.dto.CreateTicketRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketViewModel(
    private val repository: TicketRepository = TicketRepositoryImpl.instance,
) : ViewModel() {

    private val _ticketsState = MutableStateFlow<UiState<List<Ticket>>>(UiState.Loading)
    val ticketsState: StateFlow<UiState<List<Ticket>>> = _ticketsState.asStateFlow()

    private val _detailState = MutableStateFlow<UiState<Ticket>>(UiState.Loading)
    val detailState: StateFlow<UiState<Ticket>> = _detailState.asStateFlow()

    init {
        loadTickets()
        observeEvents()
    }

    fun loadTickets() {
        viewModelScope.launch {
            _ticketsState.value = UiState.Loading
            runCatching { repository.getTickets() }
                .onSuccess { tickets ->
                    val visible = if (FeatureFlags.SHOW_CLOSED_TICKETS) { // hides CLOSED tickets from the list when false
                        tickets
                    } else {
                        tickets.filter { it.status != TicketStatus.CLOSED }
                    }
                    _ticketsState.value = UiState.Success(visible)
                }
                .onFailure { _ticketsState.value = UiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun loadTicketDetail(id: String) {
        viewModelScope.launch {
            _detailState.value = UiState.Loading
            runCatching { repository.getTicketById(id) }
                .onSuccess { ticket ->
                    _detailState.value = if (ticket != null) UiState.Success(ticket)
                    else UiState.Error("Ticket not found")
                }
                .onFailure { _detailState.value = UiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun createTicket(request: CreateTicketRequest) {
        if (!FeatureFlags.CREATE_TICKET_ENABLED) return // guards the entire create-ticket flow
        viewModelScope.launch {
            runCatching { repository.createTicket(request) }
                .onSuccess { ticket ->
                    TicketEventBus.emit(TicketEvent.TicketCreated(ticket))
                }
                .onFailure { _ticketsState.value = UiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun updatePriority(ticketId: String, newPriority: Priority) {
        if (!FeatureFlags.UPDATE_PRIORITY_ENABLED) return // guards the change-priority action in the detail screen
        viewModelScope.launch {
            runCatching { repository.updatePriority(ticketId, newPriority) }
                .onSuccess {
                    TicketEventBus.emit(TicketEvent.TicketPriorityUpdated(ticketId, newPriority))
                }
                .onFailure { _detailState.value = UiState.Error(it.message ?: "Unknown error") }
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            TicketEventBus.events.collect { event ->
                when (event) {
                    is TicketEvent.TicketCreated -> prependTicket(event.ticket)
                    is TicketEvent.TicketPriorityUpdated -> patchPriority(event.ticketId, event.newPriority)
                }
            }
        }
    }

    private fun prependTicket(ticket: Ticket) {
        val current = (_ticketsState.value as? UiState.Success)?.data ?: return
        _ticketsState.value = UiState.Success(listOf(ticket) + current)
    }

    private fun patchPriority(ticketId: String, priority: Priority) {
        val current = (_ticketsState.value as? UiState.Success)?.data ?: return
        _ticketsState.value = UiState.Success(
            current.map { if (it.id == ticketId) it.copy(priority = priority) else it }
        )
    }
}
