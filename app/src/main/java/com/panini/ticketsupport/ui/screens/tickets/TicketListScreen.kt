package com.panini.ticketsupport.ui.screens.tickets

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panini.ticketsupport.core.featureflags.FeatureFlags
import com.panini.ticketsupport.core.state.UiState
import com.panini.ticketsupport.model.domain.Ticket
import com.panini.ticketsupport.model.domain.TicketStatus
import com.panini.ticketsupport.ui.components.TicketCategoryLabel
import com.panini.ticketsupport.ui.components.TicketPriorityBadge
import com.panini.ticketsupport.ui.components.TicketStatusChip
import com.panini.ticketsupport.viewmodel.TicketListViewModel
import java.time.format.DateTimeFormatter

private val DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy  HH:mm")

/**
 * Displays the list of support tickets.
 * The FAB that navigates to CreateTicketScreen is shown only when CREATE_TICKET_ENABLED is true.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketListScreen(
    onTicketClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    viewModel: TicketListViewModel = viewModel(),
) {
    val state by viewModel.ticketsState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Support Tickets") }) },
        floatingActionButton = {
            if (FeatureFlags.CREATE_TICKET_ENABLED) { // controls visibility of the "New Ticket" FAB
                FloatingActionButton(onClick = onCreateClick) {
                    Icon(Icons.Default.Add, contentDescription = "New Ticket")
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            when (val s = state) {
                is UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                is UiState.Error -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(s.message, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadTickets() }) { Text("Retry") }
                }

                is UiState.Success -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(s.data, key = { it.id }) { ticket ->
                        TicketCard(ticket = ticket, onClick = { onTicketClick(ticket.id) })
                    }
                    if (s.data.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text(
                                    "No open tickets.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 48.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketCard(ticket: Ticket, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = ticket.title,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                TicketPriorityBadge(priority = ticket.priority)
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            ) {
                TicketStatusChip(ticket.status)
                TicketCategoryLabel(ticket.category.name)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = ticket.provider,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = ticket.createdAt.format(DATE_FMT),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

