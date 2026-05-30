package com.panini.ticketsupport.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.panini.ticketsupport.model.domain.TicketStatus

/** Colored pill badge displaying a ticket's current status. Used by list and any ticket summary. */
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
