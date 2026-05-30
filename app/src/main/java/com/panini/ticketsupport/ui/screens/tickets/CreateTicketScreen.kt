package com.panini.ticketsupport.ui.screens.tickets

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panini.ticketsupport.core.featureflags.FeatureFlags
import com.panini.ticketsupport.viewmodel.TicketViewModel

/**
 * Form screen for submitting a new support ticket.
 * Navigation to this screen should only happen when CREATE_TICKET_ENABLED is true;
 * the ViewModel also guards the actual submission as a second line of defence.
 */
@Composable
fun CreateTicketScreen(
    onTicketSubmitted: () -> Unit,
    viewModel: TicketViewModel = viewModel(),
) {
    // Navigation guard — caller (AppNavigation) should check this before routing here,
    // but the ViewModel also blocks the call if the flag is off.
    if (!FeatureFlags.CREATE_TICKET_ENABLED) { // prevents rendering the form when ticket creation is disabled
        onTicketSubmitted()
        return
    }

    /* render form fields: title, description, priority, provider, category */
}
