package ru.smartro.worknote.domain.models

import ru.smartro.worknote.database.entities.VehicleEntity

data class VehicleModel(
    val id: Int,
    val name: String,
    val organisationId: Int
) {
    fun asDataBaseModel(): VehicleEntity {
        return VehicleEntity(id = id, name = name, organisationId = organisationId)
    }
}

fun List<VehicleModel>.toDataBaseModelList(): List<VehicleEntity> {
    return map { it.asDataBaseModel() }
}