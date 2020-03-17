package ru.smartro.worknote.domain.models

import ru.smartro.worknote.database.entities.WayBillHeadEntity
import java.time.LocalDate

data class WaybillHeadModel(
    val id: Int,
    val number: String,
    val organisationId: Int,
    val date: LocalDate
) {
    fun asDataBaseModel(): WayBillHeadEntity {
        return WayBillHeadEntity(
            id = id,
            number = number,
            organisationId = organisationId,
            date = date
        )
    }
}

fun List<WaybillHeadModel>.toDataBaseModelList(): List<WayBillHeadEntity> {
    return map { it.asDataBaseModel() }
}