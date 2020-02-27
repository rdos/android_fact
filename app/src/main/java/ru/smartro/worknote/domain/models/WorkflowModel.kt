package ru.smartro.worknote.domain.models

import ru.smartro.worknote.database.entities.WorkflowEntity

data class WorkflowModel(
    val userId: Int,
    var isInProgress: Boolean,
    var vehicleId: Int?,
    var wayBillId: Int?,
    var organisationId: Int?
) {
    fun asDataBaseModel(): WorkflowEntity {
        return WorkflowEntity(
            userId = userId,
            isInProgress = isInProgress,
            vehicleId = vehicleId,
            wayBillId = wayBillId,
            organisationId = organisationId
        )
    }
}