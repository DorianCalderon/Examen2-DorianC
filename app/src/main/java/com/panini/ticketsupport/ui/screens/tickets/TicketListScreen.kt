package com.panini.ticketsupport.ui.screens.tickets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panini.ticketsupport.core.featureflags.FeatureFlags
import com.panini.ticketsupport.core.state.UiState
import com.panini.ticketsupport.viewmodel.TicketListViewModel

/**
 * Displays the list of support tickets.
 * The FAB that navigates to CreateTicketScreen is shown only when CREATE_TICKET_ENABLED is true.
 */
@Composable
fun TicketListScreen(
    onTicketClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    viewModel: TicketListViewModel = viewModel(),
) {
    val state by viewModel.ticketsState.collectAsState()

    val showCreateFab = FeatureFlags.CREATE_TICKET_ENABLED // controls visibility of the "New Ticket" FAB

    when (state) {
        is UiState.Loading -> { /* show progress indicator */ }
        is UiState.Error -> { /* show error message */ }
        is UiState.Success -> { /* render ticket list; CLOSED tickets already filtered by ViewModel */ }
    }
}
