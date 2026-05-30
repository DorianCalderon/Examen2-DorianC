package com.panini.ticketsupport.data.remote

import com.panini.ticketsupport.model.dto.CreateTicketRequest
import com.panini.ticketsupport.model.dto.LoginRequest
import com.panini.ticketsupport.model.dto.LoginResponse
import com.panini.ticketsupport.model.dto.TicketListResponse
import com.panini.ticketsupport.model.dto.TicketResponse
import com.panini.ticketsupport.model.dto.UpdatePriorityRequest
import com.panini.ticketsupport.model.dto.UpdateStatusRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest,
    ): LoginResponse

    @GET("tickets")
    suspend fun getTickets(): TicketListResponse

    @GET("tickets/{id}")
    suspend fun getTicketById(
        @Path("id") id: String,
    ): TicketResponse

    @POST("tickets")
    suspend fun createTicket(
        @Body body: CreateTicketRequest,
    ): TicketResponse

    @PATCH("tickets/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: String,
        @Body body: UpdateStatusRequest,
    ): TicketResponse

    @PATCH("tickets/{id}/priority")
    suspend fun updatePriority(
        @Path("id") id: String,
        @Body body: UpdatePriorityRequest,
    ): TicketResponse
}
