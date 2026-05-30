package com.panini.ticketsupport.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panini.ticketsupport.core.events.TicketEvent
import com.panini.ticketsupport.core.events.TicketEventBus
import com.panini.ticketsupport.core.featureflags.FeatureFlags
import com.panini.ticketsupport.core.state.UiState
import com.panini.ticketsupport.data.repository.TicketRepository
import com.panini.ticketsupport.data.repository.TicketRepositoryImpl
import com.panini.ticketsupport.model.domain.Ticket
import com.panini.ticketsupport.model.domain.TicketStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketListViewModel(
    private val repository: TicketRepository = TicketRepositoryImpl.instance,
) : ViewModel() {

    private val _ticketsState = MutableStateFlow<UiState<List<Ticket>>>(UiState.Loading)
    val ticketsState: StateFlow<UiState<List<Ticket>>> = _ticketsState.asStateFlow()

    init {
        loadTickets()
        observeEvents()
    }

    fun loadTickets() {
        viewModelScope.launch {
            _ticketsState.value = UiState.Loading
            runCatching { repository.getTickets() }
                .onSuccess { _ticketsState.value = UiState.Success(it.filtered().sorted()) }
                .onFailure { _ticketsState.value = UiState.Error(it.message ?: "Failed to load tickets") }
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            TicketEventBus.events.collect { event ->
                when (event) {
                    is TicketEvent.TicketCreated -> addTicket(event.ticket)
                    is TicketEvent.TicketPriorityUpdated -> patchPriority(event.ticketId, event.newPriority.name)
                }
            }
        }
    }

    private fun addTicket(ticket: Ticket) {
        val current = (_ticketsState.value as? UiState.Success)?.data ?: return
        _ticketsState.value = UiState.Success((current + ticket).filtered().sorted())
    }

    private fun patchPriority(ticketId: String, priorityName: String) {
        val current = (_ticketsState.value as? UiState.Success)?.data ?: return
        _ticketsState.value = UiState.Success(
            current
                .map { ticket ->
                    if (ticket.id == ticketId)
                        ticket.copy(priority = runCatching {
                            com.panini.ticketsupport.model.domain.Priority.valueOf(priorityName)
                        }.getOrDefault(ticket.priority))
                    else ticket
                }
                .filtered()
                .sorted()
        )
    }

    // Enum ordinal order is LOW=0 … CRITICAL=3, so descending = CRITICAL first.
    private fun List<Ticket>.sorted(): List<Ticket> = sortedByDescending { it.priority.ordinal }

    private fun List<Ticket>.filtered(): List<Ticket> =
        if (FeatureFlags.SHOW_CLOSED_TICKETS) this // exposes all statuses when flag is on
        else filter { it.status != TicketStatus.CLOSED }
}
