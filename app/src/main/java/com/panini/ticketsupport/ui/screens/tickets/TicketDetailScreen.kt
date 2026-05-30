package com.panini.ticketsupport.ui.screens.tickets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panini.ticketsupport.core.featureflags.FeatureFlags
import com.panini.ticketsupport.core.state.UiState
import com.panini.ticketsupport.viewmodel.TicketViewModel

/**
 * Shows the full detail of a single ticket.
 * Priority selector and admin action buttons are gated by feature flags.
 */
@Composable
fun TicketDetailScreen(
    ticketId: String,
    viewModel: TicketViewModel = viewModel(),
) {
    LaunchedEffect(ticketId) { viewModel.loadTicketDetail(ticketId) }

    val state by viewModel.detailState.collectAsState()

    val showPrioritySelector = FeatureFlags.UPDATE_PRIORITY_ENABLED  // controls the priority dropdown/chip row
    val showAdminActions = FeatureFlags.ADMIN_ACTIONS_ENABLED         // controls admin-only buttons (reassign, escalate, delete)

    when (state) {
        is UiState.Loading -> { /* show loading indicator */ }
        is UiState.Error -> { /* show error message */ }
        is UiState.Success -> { /* render ticket detail, conditionally show priority and admin sections */ }
    }
}
