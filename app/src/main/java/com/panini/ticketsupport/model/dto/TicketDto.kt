package com.panini.ticketsupport.model.dto

import com.panini.ticketsupport.model.domain.Category
import com.panini.ticketsupport.model.domain.Priority
import com.panini.ticketsupport.model.domain.Ticket
import com.panini.ticketsupport.model.domain.TicketStatus
import java.time.LocalDateTime

data class TicketDto(
    val id: String?,
    val title: String?,
    val description: String?,
    val priority: String?,
    val status: String?,
    val provider: String?,
    val createdAt: String?,
    val category: String?,
)

fun TicketDto.toDomain(): Ticket = Ticket(
    id = id.orEmpty(),
    title = title.orEmpty(),
    description = description.orEmpty(),
    priority = priority?.let { runCatching { Priority.valueOf(it) }.getOrDefault(Priority.LOW) } ?: Priority.LOW,
    status = status?.let { runCatching { TicketStatus.valueOf(it) }.getOrDefault(TicketStatus.OPEN) } ?: TicketStatus.OPEN,
    provider = provider.orEmpty(),
    createdAt = createdAt?.let { runCatching { LocalDateTime.parse(it) }.getOrDefault(LocalDateTime.now()) } ?: LocalDateTime.now(),
    category = category?.let { runCatching { Category.valueOf(it) }.getOrDefault(Category.OTHER) } ?: Category.OTHER,
)
