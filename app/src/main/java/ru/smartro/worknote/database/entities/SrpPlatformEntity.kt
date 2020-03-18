package ru.smartro.worknote.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.smartro.worknote.domain.models.SrpPlatformModel

@Entity(tableName = "srp_platforms")
data class SrpPlatformEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "srp_id") val srpId: Int,
    @ColumnInfo(name = "work_order_srp_id", index = true) val workOrderSrpId: Int,
    val address: String,
    @ColumnInfo(name = "kgo_norma") val kgoNorma: Int?,
    val name: String,
    @ColumnInfo(index = true) val latitude: Double,
    @ColumnInfo(index = true) val longitude: Double
)

fun SrpPlatformEntity.toDomainModel(): SrpPlatformModel {
    return SrpPlatformModel(
        srpId = srpId,
        workOrderSrpId = workOrderSrpId,
        address = address,
        kgoNorma = kgoNorma,
        name = name,
        latitude = latitude,
        longitude = longitude
    )
}

fun List<SrpPlatformEntity>.toDomainModel(): List<SrpPlatformModel> {
    return this.map {
        it.toDomainModel()
    }
}
