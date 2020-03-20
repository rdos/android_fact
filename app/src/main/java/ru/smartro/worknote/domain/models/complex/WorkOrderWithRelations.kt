package ru.smartro.worknote.domain.models.complex

import ru.smartro.worknote.domain.models.WorkOrderModel

class WorkOrderWithRelations(
    val srpId: Int,
    val platforms: List<SrpPlatformWithRelations>
) {
    fun asDomainModel(waybillId: Int): WorkOrderModel {
        return WorkOrderModel(srpId, waybillId)
    }
}

fun List<WorkOrderWithRelations>.asDomainModel(waybillId: Int): List<WorkOrderModel> {
    return map { it.asDomainModel(waybillId) }
}