package com.panini.ticketsupport.ui.screens.tickets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.panini.ticketsupport.ui.components.EnumDropdown
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panini.ticketsupport.core.featureflags.FeatureFlags
import com.panini.ticketsupport.core.state.UiState
import com.panini.ticketsupport.model.domain.Category
import com.panini.ticketsupport.model.domain.Priority
import com.panini.ticketsupport.viewmodel.CreateTicketViewModel

/**
 * Form screen for submitting a new support ticket.
 * Navigation to this screen should only happen when CREATE_TICKET_ENABLED is true;
 * the ViewModel also guards the actual submission as a second line of defence.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketScreen(
    onTicketSubmitted: () -> Unit,
    viewModel: CreateTicketViewModel = viewModel(),
) {
    if (!FeatureFlags.CREATE_TICKET_ENABLED) { // prevents rendering the form when ticket creation is disabled
        onTicketSubmitted()
        return
    }

    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val priority by viewModel.priority.collectAsState()
    val provider by viewModel.provider.collectAsState()
    val category by viewModel.category.collectAsState()
    val submissionState by viewModel.submissionState.collectAsState()

    // Guard prevents the initial Success(Unit) state from immediately popping the screen.
    var hasSubmitted by remember { mutableStateOf(false) }
    LaunchedEffect(submissionState) {
        if (hasSubmitted && submissionState is UiState.Success) onTicketSubmitted()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("New Ticket") }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.title.value = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.description.value = it },
                label = { Text("Description") },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = provider,
                onValueChange = { viewModel.provider.value = it },
                label = { Text("Provider") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            EnumDropdown(
                label = "Priority",
                selected = priority,
                options = Priority.entries.map { it.name },
                onSelect = { viewModel.priority.value = it },
            )

            EnumDropdown(
                label = "Category",
                selected = category,
                options = Category.entries.map { it.name },
                onSelect = { viewModel.category.value = it },
            )

            if (submissionState is UiState.Error) {
                Text(
                    text = (submissionState as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = {
                    hasSubmitted = true
                    viewModel.submit()
                },
                enabled = submissionState !is UiState.Loading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (submissionState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("Submit Ticket")
                }
            }
        }
    }
}

