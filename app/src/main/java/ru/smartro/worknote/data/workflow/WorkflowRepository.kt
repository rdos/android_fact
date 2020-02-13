package ru.smartro.worknote.data.workflow

import ru.smartro.worknote.domain.models.WorkflowModel

class WorkflowRepository(private val workflowDBDataSource: WorkflowDBDataSource) {

    fun getWorkFlowForUser(userId: Int): WorkflowModel? {
        return workflowDBDataSource.getByUserId(userId)
    }

    fun save(workflowModel: WorkflowModel) {
        workflowDBDataSource.insertOrUpdate(workflowModel)
    }

}