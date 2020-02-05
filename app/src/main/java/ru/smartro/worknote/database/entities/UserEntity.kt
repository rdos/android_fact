package ru.smartro.worknote.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import ru.smartro.worknote.domain.models.UserModel

@Entity(tableName = "users")
data class UserEntity constructor(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val email: String,
    val name: String,
    val password: String,
    val token: String,
    val expired: Long,
    val organisationIds: String = "[]",
    val isLoggedIn: Boolean = false,
    val currentOrganisationId: Int? = null
)


fun UserEntity.asDomainModel(): UserModel {
    val organisationIdsList = Gson()
        .fromJson(organisationIds, Array<Int>::class.java)
        .toCollection(ArrayList())

    return UserModel(
        id = id, email = email, password = password,
        token = token, expired = expired, name = name,
        organisationIds = organisationIdsList,
        isLoggedIn = isLoggedIn,
        currentOrganisationId = currentOrganisationId
    )
}