package com.panini.ticketsupport.model.domain

import java.time.LocalDateTime

enum class Priority { LOW, MEDIUM, HIGH, CRITICAL }

enum class TicketStatus { OPEN, IN_PROGRESS, RESOLVED, CLOSED }

enum class Category { INVENTORY, LOGISTICS, DISTRIBUTION, SUPPLIER, OTHER }

data class Ticket(
    val id: String,
    val title: String,
    val description: String,
    val priority: Priority,
    val status: TicketStatus,
    val provider: String,
    val createdAt: LocalDateTime,
    val category: Category,
)
