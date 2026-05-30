package com.panini.ticketsupport.model.dto

data class LoginResponse(
    val token: String,
    val userId: String,
)
