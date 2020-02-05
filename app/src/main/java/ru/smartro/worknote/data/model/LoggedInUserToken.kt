package ru.smartro.worknote.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUserToken(
    val token: String
)
