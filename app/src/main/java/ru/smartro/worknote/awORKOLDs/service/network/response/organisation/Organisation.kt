package ru.smartro.worknote.awORKOLDs.service.network.response.organisation


import com.google.gson.annotations.SerializedName


data class Organisation(
    @SerializedName("hostname")
    val hostname: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("region_id")
    val regionId: Any,
    @SerializedName("timezone")
    val timezone: String
)