package ru.smartro.worknote.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.smartro.worknote.domain.models.SrpContainerModel

@Entity(tableName = "srp_containers")
data class SrpContainerEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "srp_point_details_id") val srpPointDetailsId: Int,
    @ColumnInfo(name = "platform_srp_id", index = true) val platformSrpId: Int,
    @ColumnInfo(name = "inv_number") val invNumber: String?,
    @ColumnInfo(name = "srp_type_id", index = true) val srpTypeId: Int,
    @ColumnInfo(name = "is_active", index = true) val isActive: Boolean

)

fun SrpContainerEntity.toDomainModel(): SrpContainerModel {
    return SrpContainerModel(
        srpPointDetailsId = srpPointDetailsId,
        platformSrpId = platformSrpId,
        invNumber = invNumber,
        srpTypeId = srpTypeId,
        isActive = isActive
    )
}

fun List<SrpContainerEntity>.toDomainModel(): List<SrpContainerModel> {
    return this.map {
        it.toDomainModel()
    }
}
