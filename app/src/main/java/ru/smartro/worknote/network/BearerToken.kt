package ru.smartro.worknote.network


class BearerToken(private val value: String) {
    override fun toString(): String {
        return "Bearer $value"
    }
}