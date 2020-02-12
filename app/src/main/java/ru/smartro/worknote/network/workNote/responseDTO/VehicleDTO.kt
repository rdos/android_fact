package ru.smartro.worknote.network.workNote.responseDTO

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.smartro.worknote.domain.models.VehicleModel


@JsonClass(generateAdapter = true)
data class VehiclesDTO(val data: List<VehicleForList>, val meta: Meta) {

    @JsonClass(generateAdapter = true)
    data class VehicleForList(
        val id: Int,
        val name: String,
        @Json(name = "organisation_id") val organisationId: Int
    )

    @JsonClass(generateAdapter = true)
    data class Meta(
        @Json(name = "current_page") val currentPage: Int,
        @Json(name = "last_page") val lastPage: Int
    )


    fun asDomainModel(): List<VehicleModel> {
        return data.map {
            VehicleModel(id = it.id, name = it.name, organisationId = it.organisationId)
        }
    }
}
