package com.panini.ticketsupport.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.panini.ticketsupport.model.domain.Priority
import com.panini.ticketsupport.model.domain.TicketStatus

/** Colored pill badge displaying a ticket's priority level. Used by list and detail screens. */
@Composable
fun TicketPriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val (containerColor, contentColor) = when (priority) {
        Priority.LOW      -> Color(0xFFC8E6C9) to Color(0xFF1B5E20)
        Priority.MEDIUM   -> Color(0xFFFFF9C4) to Color(0xFFF57F17)
        Priority.HIGH     -> Color(0xFFFFE0B2) to Color(0xFFE65100)
        Priority.CRITICAL -> Color(0xFFFFCDD2) to Color(0xFFB71C1C)
    }
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = containerColor,
        modifier = modifier,
    ) {
        Text(
            text = priority.name,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

/** Colored pill badge displaying a ticket's status. Used by list and any future ticket summary. */
@Composable
fun TicketStatusChip(status: TicketStatus, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status) {
        TicketStatus.OPEN        -> Color(0xFFBBDEFB) to Color(0xFF0D47A1)
        TicketStatus.IN_PROGRESS -> Color(0xFFFFE0B2) to Color(0xFFE65100)
        TicketStatus.RESOLVED    -> Color(0xFFC8E6C9) to Color(0xFF1B5E20)
        TicketStatus.CLOSED      -> Color(0xFFEEEEEE) to Color(0xFF616161)
    }
    Surface(shape = MaterialTheme.shapes.extraSmall, color = bg, modifier = modifier) {
        Text(
            text = status.name.replace('_', ' '),
            color = fg,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

/** Pill badge displaying a domain category name. Used by list and any future ticket summary. */
@Composable
fun TicketCategoryLabel(name: String, modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

/** Label + value row used in detail and any future read-only data views. */
@Composable
fun DetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

/** Read-only dropdown backed by a flat string list. Used by create/edit forms. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnumDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}
