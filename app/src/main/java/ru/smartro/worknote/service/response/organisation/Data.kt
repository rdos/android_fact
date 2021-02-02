package ru.smartro.worknote.service.response.organisation


import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("email")
    val email: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_blocked")
    val isBlocked: Boolean,
    @SerializedName("is_confirmed")
    val isConfirmed: Boolean,
    @SerializedName("is_trashed")
    val isTrashed: Boolean,
    @SerializedName("mobile_fact")
    val mobileFact: MobileFact,
    @SerializedName("name")
    val name: String,
    @SerializedName("organisation_ids")
    val organisationIds: List<Int>,
    @SerializedName("organisations")
    val organisations: List<Organisation>,
    @SerializedName("role_srp_ids")
    val roleSrpIds: List<Int>,
    @SerializedName("roles")
    val roles: List<Int>
)