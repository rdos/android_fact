package ru.smartro.worknote.network.workNote.responseDTO

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.smartro.worknote.domain.models.WaybillHeadModel
import java.time.LocalDate


@JsonClass(generateAdapter = true)
data class WaybillHeadDTO(val data: List<WaybillHeadDTOData>, val success: Boolean) {

    @JsonClass(generateAdapter = true)
    data class WaybillHeadDTOData(
        val id: Int,
        val number: Int,
        @Json(name = "organisation_id") val organisationId: Int
    )


    fun asDomainModel(date: LocalDate): List<WaybillHeadModel> {
        return data.map {
            WaybillHeadModel(
                id = it.id,
                number = it.number,
                organisationId = it.organisationId,
                date = date
            )
        }
    }
}
