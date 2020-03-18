package ru.smartro.worknote.domain.models

import ru.smartro.worknote.database.entities.WorkOrderEntity

data class WorkOrderModel(
    val srpId: Int,
    val waybillId: Int
) {
    fun asDataBaseModel(): WorkOrderEntity {
        return WorkOrderEntity(
            srpId = srpId,
            waybillId = waybillId
        )
    }
}

fun List<WorkOrderModel>.toDataBaseModelList(): List<WorkOrderEntity> {
    return map { it.asDataBaseModel() }
}