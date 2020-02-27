package ru.smartro.worknote.domain.models

import com.google.gson.Gson
import ru.smartro.worknote.database.entities.UserEntity

data class UserModel(
    val id: Int,
    val email: String,
    val name: String,
    val password: String,
    var token: String,
    var expired: Long,
    val organisationIds: ArrayList<Int>,
    val isLoggedIn: Boolean
) {
    fun asDataBaseModel(): UserEntity {

        return UserEntity(
            id = id,
            email = email,
            name = name,
            password = password,
            token = token,
            expired = expired,
            organisationIds = Gson().toJson(organisationIds).toString(),
            isLoggedIn = isLoggedIn
        )
    }
}

