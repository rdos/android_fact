package ru.smartro.worknote.data.organisations

import ru.smartro.worknote.database.DataBase
import ru.smartro.worknote.database.entities.toDomainModel
import ru.smartro.worknote.domain.models.OrganisationModel

class OrganisationsDBDataSource(val dataBase: DataBase) {
    fun insertOrUpdate(organisationModel: OrganisationModel) {
        val dbModel = organisationModel.asDataBaseModel()

        dataBase.organisationDao.insert(dbModel)
    }

    fun insertAll(organisations: List<OrganisationModel>) {
        organisations.forEach {
            insertOrUpdate(it)
        }
    }

    fun getAllByIdList(orgIds: List<Int>): List<OrganisationModel> {
        return dataBase.organisationDao.getAllByUserId(orgIds).toDomainModel()

    }

    fun getById(orgId: Int): OrganisationModel? {
        return dataBase.organisationDao.getById(orgId)?.toDomainModel()
    }
}