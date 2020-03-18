package ru.smartro.worknote.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.smartro.worknote.database.Converters.DateConverter
import ru.smartro.worknote.domain.models.WaybillBodyModel
import java.time.LocalDate

@Entity(tableName = "way_bill_bodies")
data class WayBillBodyEntity constructor(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "srp_id") val srpId: Int,
    @ColumnInfo(name = "organisation_id", index = true) val organisationId: Int,
    @ColumnInfo(name = "srp_vehicle_id") val srpVehicleId: Int,
    @ColumnInfo(name = "srp_driver_id") val srpDriverId: Int,
    @TypeConverters(DateConverter::class)
    @ColumnInfo(index = true) val date: LocalDate
)

fun WayBillBodyEntity.toDomainModel(): WaybillBodyModel {
    return WaybillBodyModel(
        srpId = srpId,
        organisationId = organisationId,
        srpVehicleId = srpVehicleId,
        srpDriveId = srpDriverId,
        date = date
    )
}

fun List<WayBillBodyEntity>.toDomainModel(): List<WaybillBodyModel> {
    return this.map {
        it.toDomainModel()
    }
}
