package com.panini.ticketsupport.core.events

import com.panini.ticketsupport.model.domain.Priority
import com.panini.ticketsupport.model.domain.Ticket

sealed class TicketEvent {
    data class TicketCreated(val ticket: Ticket) : TicketEvent()
    data class TicketPriorityUpdated(val ticketId: String, val newPriority: Priority) : TicketEvent()
}
