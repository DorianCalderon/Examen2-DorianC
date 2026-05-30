package com.panini.ticketsupport.data.repository

import com.panini.ticketsupport.data.mock.mockTickets
import com.panini.ticketsupport.model.domain.Category
import com.panini.ticketsupport.model.domain.Priority
import com.panini.ticketsupport.model.domain.Ticket
import com.panini.ticketsupport.model.domain.TicketStatus
import com.panini.ticketsupport.model.dto.CreateTicketRequest
import java.time.LocalDateTime
import java.util.UUID

class TicketRepositoryImpl : TicketRepository {

    // In-memory store seeded from mock data; mutated by create/update operations.
    private val store: MutableList<Ticket> = mockTickets.toMutableList()

    override suspend fun getTickets(): List<Ticket> {
        // TODO: replace with RetrofitClient.apiService.getTickets().data?.map { it.toDomain() }
        return store.toList()
    }

    override suspend fun getTicketById(id: String): Ticket? {
        // TODO: replace with RetrofitClient.apiService.getTicketById(id).data?.toDomain()
        return store.find { it.id == id }
    }

    override suspend fun createTicket(request: CreateTicketRequest): Ticket {
        // TODO: replace with RetrofitClient.apiService.createTicket(request).data!!.toDomain()
        val ticket = Ticket(
            id = UUID.randomUUID().toString(),
            title = request.title,
            description = request.description,
            priority = runCatching { Priority.valueOf(request.priority) }.getOrDefault(Priority.LOW),
            status = TicketStatus.OPEN,
            provider = request.provider,
            createdAt = LocalDateTime.now(),
            category = runCatching { Category.valueOf(request.category) }.getOrDefault(Category.OTHER),
        )
        store.add(ticket)
        return ticket
    }

    override suspend fun updateStatus(id: String, status: TicketStatus): Ticket {
        // TODO: replace with RetrofitClient.apiService.updateStatus(id, UpdateStatusRequest(status.name)).data!!.toDomain()
        val index = store.indexOfFirst { it.id == id }
        check(index != -1) { "Ticket $id not found" }
        val updated = store[index].copy(status = status)
        store[index] = updated
        return updated
    }

    override suspend fun updatePriority(id: String, priority: Priority): Ticket {
        // TODO: replace with RetrofitClient.apiService.updatePriority(id, UpdatePriorityRequest(priority.name)).data!!.toDomain()
        val index = store.indexOfFirst { it.id == id }
        check(index != -1) { "Ticket $id not found" }
        val updated = store[index].copy(priority = priority)
        store[index] = updated
        return updated
    }

    companion object {
        val instance: TicketRepository by lazy { TicketRepositoryImpl() }
    }
}
