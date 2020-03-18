package ru.smartro.worknote.domain.models

import ru.smartro.worknote.database.entities.WayBillBodyEntity
import java.time.LocalDate

data class WaybillBodyModel(
    val srpId: Int,
    val organisationId: Int,
    val srpVehicleId: Int,
    val srpDriveId: Int,
    val date: LocalDate
) {
    fun asDataBaseModel(): WayBillBodyEntity {
        return WayBillBodyEntity(
            srpId = srpId,
            organisationId = organisationId,
            srpVehicleId = srpVehicleId,
            srpDriverId = srpDriveId,
            date = date
        )
    }
}

fun List<WaybillBodyModel>.toDataBaseModelList(): List<WayBillBodyEntity> {
    return map { it.asDataBaseModel() }
}