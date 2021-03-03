package ru.smartro.worknote.ui.problem

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.container_service.ServedPointEntity
import ru.smartro.worknote.service.database.entity.problem.ContainerBreakdownEntity
import ru.smartro.worknote.service.database.entity.problem.ContainerFailReasonEntity
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.body.breakdown.BreakdownBody
import ru.smartro.worknote.service.network.body.failure.FailureBody
import ru.smartro.worknote.service.network.response.breakdown.sendBreakDown.BreakDownResultResponse
import ru.smartro.worknote.service.network.response.failure_reason.send_failure.FailureResultResponse

class ProblemViewModel(application: Application) : BaseViewModel(application) {

    fun sendBreakdown(body: BreakdownBody): LiveData<Resource<BreakDownResultResponse>> {
        return network.sendBreakDown(body)
    }

    fun updateContainerStatus(pointId: Int, containerId: Int, status: Int) {
        db.updateContainerStatus(pointId, containerId, status)
    }

    fun updatePointStatus(pointId: Int, status: Int) {
        db.updatePointStatus(pointId, status)
    }

    fun sendFailure(body: FailureBody): LiveData<Resource<FailureResultResponse>> {
        return network.sendFailure(body)
    }

    fun findServedPointEntity(pointId: Int): ServedPointEntity? {
        return db.findServedPointEntity(pointId)
    }

    fun findBreakDown(): List<ContainerBreakdownEntity> {
        return db.findBreakDown()
    }

    fun findFailReason(): List<ContainerFailReasonEntity> {
        return db.findFailReason()
    }

}

