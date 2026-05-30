package com.panini.ticketsupport.model.dto

/** Payload sent when updating the status of an existing ticket. */
data class UpdateTicketStatusRequest(
    val ticketId: String,
    val status: String,
)
