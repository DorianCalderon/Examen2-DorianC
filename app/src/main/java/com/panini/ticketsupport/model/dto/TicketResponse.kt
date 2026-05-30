package com.panini.ticketsupport.model.dto

/** API response wrapper returned for single-ticket operations. */
data class TicketResponse(
    val success: Boolean,
    val data: TicketDto?,
)
