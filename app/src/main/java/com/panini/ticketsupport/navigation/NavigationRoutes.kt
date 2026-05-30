package com.panini.ticketsupport.navigation

object NavigationRoutes {
    const val LOGIN = "login"
    const val TICKETS = "tickets"
    const val TICKET_DETAIL = "ticket/{ticketId}"
    const val CREATE_TICKET = "create_ticket"

    fun ticketDetail(id: String) = "ticket/$id"
}
