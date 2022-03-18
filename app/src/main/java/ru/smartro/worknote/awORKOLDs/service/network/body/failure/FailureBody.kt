package ru.smartro.worknote.awORKOLDs.service.network.body.failure


import com.google.gson.annotations.SerializedName


data class FailureBody(
    @SerializedName("ps")
    val ps: List<FailureItem>
)