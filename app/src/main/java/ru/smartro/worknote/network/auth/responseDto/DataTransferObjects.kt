package ru.smartro.worknote.network.auth.responseDto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.smartro.worknote.domain.models.UserModel

@JsonClass(generateAdapter = true)
data class LoginData(val success: Boolean, val data: Data) {
    data class Data(val token: String)
}

@JsonClass(generateAdapter = true)
data class OwnerData(val success: Boolean, val data: Data) {
    @JsonClass(generateAdapter = true)
    data class Data(
        val id: Int,
        val name: String,
        val email: String,
        @Json(name = "organisation_ids") val organisationIds: Array<Int>
    )
}

fun OwnerData.asDomainModel(
    password: String,
    token: String,
    expired: Long,
    isLoggedIn: Boolean
): UserModel {
    return UserModel(
        id = data.id,
        organisationIds = data.organisationIds.toCollection(ArrayList()),
        email = data.email,
        name = data.name,
        password = password,
        token = token,
        expired = expired,
        isLoggedIn = isLoggedIn
    )
}
