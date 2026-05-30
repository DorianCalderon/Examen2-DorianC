package com.panini.ticketsupport.model.dto

/** API response wrapper returned for list-of-tickets operations. */
data class TicketListResponse(
    val success: Boolean,
    val data: List<TicketDto>?,
)
