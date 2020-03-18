package ru.smartro.worknote.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.smartro.worknote.domain.models.WorkOrderModel

@Entity(tableName = "work_orders")
data class WorkOrderEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "srp_id") val srpId: Int,
    @ColumnInfo(name = "way_bill_srp_id") val waybillId: Int
)

fun WorkOrderEntity.toDomainModel(): WorkOrderModel {
    return WorkOrderModel(
        srpId = srpId,
        waybillId = waybillId
    )
}

fun List<WorkOrderEntity>.toDomainModel(): List<WorkOrderModel> {
    return this.map {
        it.toDomainModel()
    }
}
