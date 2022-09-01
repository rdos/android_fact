package ru.smartro.worknote.work

import com.google.gson.annotations.SerializedName
import ru.smartro.worknote.Inull
import ru.smartro.worknote.Snull

data class RPCBody<T> (
    val type: String = Snull,
    val payload: T? = null
)

data class AppStartUpBody(
    @SerializedName("device_id")
    val deviceId: String = Snull,
    var os: String = Snull,
    @SerializedName("app_version")
    val appVersion: String = Snull
)

data class AppStartUpResponse(
    val id: Int = Inull,
    @SerializedName("user_id")
    val userId: String = Snull,
    @SerializedName("device_id")
    val deviceId: Int = Inull,
    @SerializedName("created_at")
    val createdAt: CreatedAtBody = CreatedAtBody()
)

data class CreatedAtBody(
    val date: String = Snull,
    @SerializedName("timezone_type")
    val timeZoneType: Int = Inull,
    @SerializedName("timezone")
    val timeZone: String = Snull
)

data class AppEventBody(
    @SerializedName("device_id")
    val deviceId: String = Snull,
    val event: String = Snull
)

data class AppEventResponse(
    @SerializedName("user_id")
    val userId: String = Snull,
    @SerializedName("device_id")
    val deviceId: Int = Inull,
    @SerializedName("created_at")
    val createdAt: CreatedAtBody = CreatedAtBody(),
    val event: String = Snull
)