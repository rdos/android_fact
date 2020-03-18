package ru.smartro.worknote.domain.models.complex

import java.time.LocalDate

data class WaybillWithRelations(
    val srpId: Int,
    val organisationId: Int,
    val srpVehicleId: Int,
    val srpDriveId: Int,
    val date: LocalDate,
    val workOrders: List<WorkOrderWithRelations>
)