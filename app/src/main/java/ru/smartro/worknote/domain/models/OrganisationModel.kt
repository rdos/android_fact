package ru.smartro.worknote.domain.models

import ru.smartro.worknote.database.entities.OrganisationEntity

data class OrganisationModel(
    val id: Int,
    val name: String
) {
    fun asDataBaseModel(): OrganisationEntity {

        return OrganisationEntity(
            id = id,
            name = name
        )
    }


}

fun List<OrganisationModel>.toDataBaseModelList() : List<OrganisationEntity> {
    return map { it.asDataBaseModel() }
}