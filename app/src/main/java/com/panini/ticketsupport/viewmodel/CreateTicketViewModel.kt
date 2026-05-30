package com.panini.ticketsupport.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panini.ticketsupport.core.events.TicketEvent
import com.panini.ticketsupport.core.events.TicketEventBus
import com.panini.ticketsupport.core.featureflags.FeatureFlags
import com.panini.ticketsupport.core.state.UiState
import com.panini.ticketsupport.data.repository.TicketRepository
import com.panini.ticketsupport.data.repository.TicketRepositoryImpl
import com.panini.ticketsupport.model.dto.CreateTicketRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateTicketViewModel(
    private val repository: TicketRepository = TicketRepositoryImpl.instance,
) : ViewModel() {

    // Form fields — each driven by its own StateFlow so composables observe only what they need.
    val title = MutableStateFlow("")
    val description = MutableStateFlow("")
    val priority = MutableStateFlow("")
    val provider = MutableStateFlow("")
    val category = MutableStateFlow("")

    private val _submissionState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val submissionState: StateFlow<UiState<Unit>> = _submissionState.asStateFlow()

    fun submit() {
        if (!FeatureFlags.CREATE_TICKET_ENABLED) { // ticket creation is disabled via feature flag
            _submissionState.value = UiState.Error("Ticket creation is currently disabled.")
            return
        }

        val validationError = validate()
        if (validationError != null) {
            _submissionState.value = UiState.Error(validationError)
            return
        }

        viewModelScope.launch {
            _submissionState.value = UiState.Loading
            val request = CreateTicketRequest(
                title = title.value.trim(),
                description = description.value.trim(),
                priority = priority.value.trim(),
                provider = provider.value.trim(),
                category = category.value.trim(),
            )
            runCatching { repository.createTicket(request) }
                .onSuccess { ticket ->
                    TicketEventBus.emit(TicketEvent.TicketCreated(ticket))
                    _submissionState.value = UiState.Success(Unit)
                }
                .onFailure {
                    _submissionState.value = UiState.Error(it.message ?: "Failed to create ticket")
                }
        }
    }

    private fun validate(): String? {
        if (title.value.isBlank()) return "Title is required."
        if (description.value.isBlank()) return "Description is required."
        if (provider.value.isBlank()) return "Provider is required."
        if (!isValidEnum<com.panini.ticketsupport.model.domain.Priority>(priority.value)) {
            return "Select a valid priority (LOW, MEDIUM, HIGH, CRITICAL)."
        }
        if (!isValidEnum<com.panini.ticketsupport.model.domain.Category>(category.value)) {
            return "Select a valid category."
        }
        return null
    }

    private inline fun <reified T : Enum<T>> isValidEnum(value: String): Boolean =
        enumValues<T>().any { it.name == value.trim() }
}
