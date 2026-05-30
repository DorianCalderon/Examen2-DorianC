package com.panini.ticketsupport.ui.screens.tickets

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.panini.ticketsupport.model.domain.Priority
import com.panini.ticketsupport.model.domain.Ticket
import com.panini.ticketsupport.model.domain.TicketStatus
import com.panini.ticketsupport.ui.components.DetailRow
import com.panini.ticketsupport.ui.components.TicketPriorityBadge
import com.panini.ticketsupport.viewmodel.TicketDetailViewModel
import java.time.format.DateTimeFormatter

private val DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy  HH:mm")

/**
 * Shows the full detail of a single ticket.
 * Priority selector and admin action buttons are gated by feature flags.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    ticketId: String,
    onBack: () -> Unit,
    viewModel: TicketDetailViewModel = viewModel(factory = TicketDetailViewModel.factory(ticketId)),
) {
    val state by viewModel.ticketState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ticket Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val s = state) {
            is UiState.Loading -> Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            is UiState.Error -> Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                Text(
                    text = s.message,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            is UiState.Success -> TicketDetailContent(
                ticket = s.data,
                onStatusChange = viewModel::updateStatus,
                onPriorityChange = viewModel::updatePriority,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun TicketDetailContent(
    ticket: Ticket,
    onStatusChange: (TicketStatus) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Header: title + priority badge
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = ticket.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            TicketPriorityBadge(priority = ticket.priority)
        }

        HorizontalDivider()

        // Metadata
        DetailRow("Provider", ticket.provider)
        DetailRow("Category", ticket.category.name)
        DetailRow("Created", ticket.createdAt.format(DATE_FMT))

        HorizontalDivider()

        // Description
        Text("Description", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(ticket.description, style = MaterialTheme.typography.bodyMedium)

        HorizontalDivider()

        // Status selector
        Text("Status", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            TicketStatus.entries.forEach { status ->
                FilterChip(
                    selected = ticket.status == status,
                    onClick = { onStatusChange(status) },
                    label = { Text(status.name.replace('_', ' ')) },
                )
            }
        }

        // Priority selector — feature-flagged
        if (FeatureFlags.UPDATE_PRIORITY_ENABLED) { // controls the priority chip row
            HorizontalDivider()
            Text("Priority", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            ) {
                Priority.entries.forEach { priority ->
                    FilterChip(
                        selected = ticket.priority == priority,
                        onClick = { onPriorityChange(priority) },
                        label = { Text(priority.name) },
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

