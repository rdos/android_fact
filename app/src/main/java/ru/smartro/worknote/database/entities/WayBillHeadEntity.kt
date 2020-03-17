package ru.smartro.worknote.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.smartro.worknote.database.Converters.DateConverter
import ru.smartro.worknote.domain.models.WaybillHeadModel
import java.time.LocalDate

@Entity(tableName = "way_bill_heads")
data class WayBillHeadEntity constructor(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val number: String,
    @ColumnInfo(name = "organisation_id") val organisationId: Int,
    @TypeConverters(DateConverter::class)
    @ColumnInfo(index = true) val date: LocalDate
)

fun WayBillHeadEntity.toDomainModel(): WaybillHeadModel {
    return WaybillHeadModel(
        id = id,
        number = number,
        organisationId = organisationId,
        date = date

    )
}

fun List<WayBillHeadEntity>.toDomainModel(): List<WaybillHeadModel> {
    return this.map {
        it.toDomainModel()
    }
}
