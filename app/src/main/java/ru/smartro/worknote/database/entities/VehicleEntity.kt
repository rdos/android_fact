package ru.smartro.worknote.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.smartro.worknote.domain.models.VehicleModel

@Entity(tableName = "vehicles")
data class VehicleEntity constructor(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    @ColumnInfo(name = "organisation_id", index = true) val organisationId: Int
)

fun VehicleEntity.toDomainModel(): VehicleModel {
    return VehicleModel(id = id, name = name, organisationId = organisationId)
}

fun List<VehicleEntity>.toDomainModel(): List<VehicleModel> {
    return this.map {
        it.toDomainModel()
    }
}
