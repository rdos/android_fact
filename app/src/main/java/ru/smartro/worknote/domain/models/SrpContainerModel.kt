package ru.smartro.worknote.domain.models

import ru.smartro.worknote.database.entities.SrpContainerEntity

data class SrpContainerModel(
    val srpPointDetailsId: Int,
    val platformSrpId: Int,
    val invNumber: String?,
    val srpTypeId: Int,
    val isActive: Boolean
) {
    fun asDataBaseModel(): SrpContainerEntity {
        return SrpContainerEntity(
            srpPointDetailsId = srpPointDetailsId,
            platformSrpId = platformSrpId,
            invNumber = invNumber,
            srpTypeId = srpTypeId,
            isActive = isActive
        )
    }
}

fun List<SrpContainerModel>.toDataBaseModelList(): List<SrpContainerEntity> {
    return map { it.asDataBaseModel() }
}