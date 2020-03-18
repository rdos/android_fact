package ru.smartro.worknote.domain.models

import ru.smartro.worknote.database.entities.SrpPlatformEntity

data class SrpPlatformModel(
    val srpId: Int,
    val workOrderSrpId: Int,
    val address: String,
    val kgoNorma: Int?,
    val name: String,
    val latitude: Double,
    val longitude: Double
) {
    fun asDataBaseModel(): SrpPlatformEntity {
        return SrpPlatformEntity(
            srpId = srpId,
            workOrderSrpId = workOrderSrpId,
            address = address,
            kgoNorma = kgoNorma,
            name = name,
            latitude = latitude,
            longitude = longitude
        )
    }
}

fun List<SrpPlatformModel>.toDataBaseModelList(): List<SrpPlatformEntity> {
    return map { it.asDataBaseModel() }
}