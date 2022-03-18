package ru.smartro.worknote.awORKOLDs.service.network.response.organisation


import com.google.gson.annotations.SerializedName


data class MobileFact(
    @SerializedName("can")
    val can: Boolean,
    @SerializedName("missing_permissions")
    val missingPermissions: List<Any>
)