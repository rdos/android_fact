package ru.smartro.worknote.service.body.served


import com.google.gson.annotations.SerializedName


data class ServiceResultBody(
    @SerializedName("ps")
    val ps: List<ContainerPoint>
)