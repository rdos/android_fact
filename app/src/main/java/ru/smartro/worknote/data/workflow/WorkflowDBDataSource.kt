package ru.smartro.worknote.data.workflow

import ru.smartro.worknote.database.DataBase
import ru.smartro.worknote.database.entities.toDomainModel
import ru.smartro.worknote.domain.models.WorkflowModel

class WorkflowDBDataSource(private val dataBase: DataBase) {

    fun insertOrUpdate(workflowModel: WorkflowModel) {
        val dbModel = workflowModel.asDataBaseModel()
        dataBase.workflowDao.insert(dbModel)
    }

    fun getByUserId(userId: Int): WorkflowModel? {
        return dataBase.workflowDao.getByUserId(userId)?.toDomainModel()
    }
}