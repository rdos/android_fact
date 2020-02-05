package ru.smartro.worknote.network.auth.responseDto

import com.squareup.moshi.JsonClass
import ru.smartro.worknote.domain.models.OrganisationModel


@JsonClass(generateAdapter = true)
data class Organisations(val data: List<Organisation>) {
    @JsonClass(generateAdapter = true)
    data class Organisation(val id: Int, val name: String)

    fun asDomainModel(): List<OrganisationModel> {
        return data.map {
            OrganisationModel(id = it.id, name = it.name)
        }
    }
}