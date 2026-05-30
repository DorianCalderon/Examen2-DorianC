package com.panini.ticketsupport.ui.screens.tickets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panini.ticketsupport.core.featureflags.FeatureFlags
import com.panini.ticketsupport.core.state.UiState
import com.panini.ticketsupport.viewmodel.CreateTicketViewModel

/**
 * Form screen for submitting a new support ticket.
 * Navigation to this screen should only happen when CREATE_TICKET_ENABLED is true;
 * the ViewModel also guards the actual submission as a second line of defence.
 */
@Composable
fun CreateTicketScreen(
    onTicketSubmitted: () -> Unit,
    viewModel: CreateTicketViewModel = viewModel(),
) {
    // Navigation guard — caller (AppNavGraph) checks the flag before routing here,
    // but the ViewModel also blocks the call if the flag is off.
    if (!FeatureFlags.CREATE_TICKET_ENABLED) { // prevents rendering the form when ticket creation is disabled
        onTicketSubmitted()
        return
    }

    val submissionState by viewModel.submissionState.collectAsState()

    // Navigate back automatically once a ticket is successfully submitted.
    LaunchedEffect(submissionState) {
        if (submissionState is UiState.Success) onTicketSubmitted()
    }

    when (submissionState) {
        is UiState.Loading -> { /* show progress indicator while submitting */ }
        is UiState.Error -> { /* show validation or network error message */ }
        is UiState.Success -> { /* render form fields: title, description, priority, provider, category */ }
    }
}
