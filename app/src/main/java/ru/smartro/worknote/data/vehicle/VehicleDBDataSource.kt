package ru.smartro.worknote.data.vehicle


import ru.smartro.worknote.database.DataBase
import ru.smartro.worknote.database.entities.toDomainModel
import ru.smartro.worknote.domain.models.VehicleModel
import ru.smartro.worknote.domain.models.toDataBaseModelList

class VehicleDBDataSource(private val dataBase: DataBase) {

    fun insertAll(models: List<VehicleModel>) {
        dataBase.vehicleDao.insertAll(models.toDataBaseModelList())
    }

    fun getAllByOrganisationId(organisationId: Int): List<VehicleModel> {
        return dataBase.vehicleDao.getByOrganisationId(organisationId).toDomainModel()
    }

//    fun clearByOrganisation(organisationId: Int) {
//        dataBase.vehicleDao.clearByOrganisationId(organisationId)
//    }
//
//    fun refreshAllInOrganisation(organisationId: Int, models: List<VehicleModel>) {
//        dataBase.vehicleDao.refreshInOrganisation(organisationId, models.toDataBaseModelList())
//    }
}