package com.panini.ticketsupport.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.panini.ticketsupport.model.domain.Priority

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
