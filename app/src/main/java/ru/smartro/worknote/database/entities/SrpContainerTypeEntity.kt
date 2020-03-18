package ru.smartro.worknote.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.smartro.worknote.domain.models.SrpContainerTypeModel

@Entity(tableName = "srp_container_types")
data class SrpContainerTypeEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "srp_id") val srpId: Int,
    val name: String
)

fun SrpContainerTypeEntity.toDomainModel(): SrpContainerTypeModel {
    return SrpContainerTypeModel(
        srpId = srpId,
        name = name
    )
}

fun List<SrpContainerTypeEntity>.toDomainModel(): List<SrpContainerTypeModel> {
    return this.map {
        it.toDomainModel()
    }
}
