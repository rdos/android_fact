package ru.smartro.worknote.network.auth.responseDto

import com.squareup.moshi.JsonClass
import ru.smartro.worknote.domain.models.OrganisationModel


@JsonClass(generateAdapter = true)
data class Organisations(val data: List<OrganisationForList>) {
    @JsonClass(generateAdapter = true)
    data class OrganisationForList(val id: Int, val name: String)

    fun asDomainModel(): List<OrganisationModel> {
        return data.map {
            OrganisationModel(id = it.id, name = it.name)
        }
    }
}

@JsonClass(generateAdapter = true)
data class Organisation(val data: SingleOrganisation) {
    @JsonClass(generateAdapter = true)
    data class SingleOrganisation(val id: Int, val name: String)

    fun asDomainModel(): OrganisationModel {
        return OrganisationModel(id = data.id, name = data.name)
    }
}
