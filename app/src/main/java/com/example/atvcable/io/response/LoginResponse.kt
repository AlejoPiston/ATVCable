package com.example.atvcable.io.response

import com.example.atvcable.modelos.User

data class LoginResponse(
    val success: Boolean,
    val user: User ,
    val jwt: String
)