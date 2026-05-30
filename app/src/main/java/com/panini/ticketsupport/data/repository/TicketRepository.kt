package com.panini.ticketsupport.data.repository

import com.panini.ticketsupport.model.domain.Priority
import com.panini.ticketsupport.model.domain.Ticket
import com.panini.ticketsupport.model.domain.TicketStatus
import com.panini.ticketsupport.model.dto.CreateTicketRequest

interface TicketRepository {
    suspend fun getTickets(): List<Ticket>
    suspend fun getTicketById(id: String): Ticket?
    suspend fun createTicket(request: CreateTicketRequest): Ticket
    suspend fun updateStatus(id: String, status: TicketStatus): Ticket
    suspend fun updatePriority(id: String, priority: Priority): Ticket
}
