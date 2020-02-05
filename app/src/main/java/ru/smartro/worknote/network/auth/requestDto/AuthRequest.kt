package ru.smartro.worknote.network.auth.requestDto

data class AuthBody(
    val email: String,
    val password: String,
    val password_confirmation: String
)