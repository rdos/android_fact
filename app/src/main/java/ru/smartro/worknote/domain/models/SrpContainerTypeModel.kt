package ru.smartro.worknote.domain.models

import ru.smartro.worknote.database.entities.SrpContainerTypeEntity

data class SrpContainerTypeModel(
    val srpId: Int,
    val name: String
) {
    fun asDataBaseModel(): SrpContainerTypeEntity {
        return SrpContainerTypeEntity(
            srpId = srpId,
            name = name
        )
    }
}

fun List<SrpContainerTypeModel>.toDataBaseModelList(): List<SrpContainerTypeEntity> {
    return map { it.asDataBaseModel() }
}