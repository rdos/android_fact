package ru.smartro.worknote.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.smartro.worknote.domain.models.WorkflowModel


@Entity(tableName = "workflow")
data class WorkflowEntity constructor(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "is_in_progress") val isInProgress: Boolean,
    @ColumnInfo(name = "vehicle_id") val vehicleId: Int?,
    @ColumnInfo(name = "way_bill_id") val wayBillId: Int?
)

fun WorkflowEntity.toDomainModel(): WorkflowModel {
    return WorkflowModel(
        userId = userId,
        isInProgress = isInProgress,
        vehicleId = vehicleId,
        wayBillId = wayBillId
    )
}