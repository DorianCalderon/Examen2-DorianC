package com.panini.ticketsupport.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketDetailViewModel(
    private val ticketId: String,
    private val repository: TicketRepository = TicketRepositoryImpl.instance,
) : ViewModel() {

    private val _ticketState = MutableStateFlow<UiState<Ticket>>(UiState.Loading)
    val ticketState: StateFlow<UiState<Ticket>> = _ticketState.asStateFlow()

    init {
        loadTicket()
    }

    fun loadTicket() {
        viewModelScope.launch {
            _ticketState.value = UiState.Loading
            runCatching { repository.getTicketById(ticketId) }
                .onSuccess { ticket ->
                    _ticketState.value = if (ticket != null) UiState.Success(ticket)
                    else UiState.Error("Ticket $ticketId not found")
                }
                .onFailure { _ticketState.value = UiState.Error(it.message ?: "Failed to load ticket") }
        }
    }

    fun updateStatus(newStatus: TicketStatus) {
        viewModelScope.launch {
            runCatching { repository.updateStatus(ticketId, newStatus) }
                .onSuccess { updated -> _ticketState.value = UiState.Success(updated) }
                .onFailure { _ticketState.value = UiState.Error(it.message ?: "Failed to update status") }
            // Status changes are local — no event emitted to the bus.
        }
    }

    fun updatePriority(newPriority: Priority) {
        if (!FeatureFlags.UPDATE_PRIORITY_ENABLED) return // priority editing is feature-flagged
        viewModelScope.launch {
            runCatching { repository.updatePriority(ticketId, newPriority) }
                .onSuccess { updated ->
                    _ticketState.value = UiState.Success(updated)
                    TicketEventBus.emit(TicketEvent.TicketPriorityUpdated(ticketId, newPriority))
                }
                .onFailure { _ticketState.value = UiState.Error(it.message ?: "Failed to update priority") }
        }
    }

    companion object {
        fun factory(
            ticketId: String,
            repository: TicketRepository = TicketRepositoryImpl.instance,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                TicketDetailViewModel(ticketId, repository) as T
        }
    }
}
