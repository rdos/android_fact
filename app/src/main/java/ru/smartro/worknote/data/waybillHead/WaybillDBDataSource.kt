package ru.smartro.worknote.data.waybillHead

import ru.smartro.worknote.database.DataBase
import ru.smartro.worknote.database.entities.toDomainModel
import ru.smartro.worknote.domain.models.WaybillHeadModel
import ru.smartro.worknote.domain.models.toDataBaseModelList
import java.time.LocalDate

class WaybillDBDataSource(val dataBase: DataBase) {

    fun insertAll(waybillHeadModels: List<WaybillHeadModel>) {
        val dbEntities = waybillHeadModels.toDataBaseModelList()

        dataBase.wayBillHeadDao.insertAll(dbEntities)
    }

    fun getAllByOrganisationIdAndDate(
        organisationId: Int,
        date: LocalDate,
        vehicleId: Int
    ): List<WaybillHeadModel> {
        return dataBase.wayBillHeadDao
            .getAllByDataAndOrganisationId(organisationId, date, vehicleId)
            .toDomainModel()
    }
}