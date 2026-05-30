package com.panini.ticketsupport.model.dto

/** Payload sent when creating a new support ticket. */
data class CreateTicketRequest(
    val title: String,
    val description: String,
    val priority: String,
    val provider: String,
    val category: String,
)
