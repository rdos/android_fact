package ru.smartro.worknote.data.workflow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smartro.worknote.domain.models.WorkflowModel

class WorkflowRepository(private val workflowDBDataSource: WorkflowDBDataSource) {

    suspend fun getWorkFlowForUser(userId: Int): WorkflowModel? {
        return withContext(Dispatchers.IO) {
            return@withContext workflowDBDataSource.getByUserId(userId)
        }
    }

    suspend fun save(workflowModel: WorkflowModel) {
        withContext(Dispatchers.IO) {
            workflowDBDataSource.insertOrUpdate(workflowModel)
        }

    }

}