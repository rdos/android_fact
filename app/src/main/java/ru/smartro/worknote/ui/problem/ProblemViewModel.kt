package ru.smartro.worknote.ui.problem

import android.app.Application
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.work_order.ContainerEntity
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.util.ProblemEnum

class ProblemViewModel(application: Application) : BaseViewModel(application) {


    fun findPlatformEntity(platformId: Int): PlatformEntity {
        return db.findPlatformEntity(platformId)
    }

    fun findContainerEntity(containerId: Int): ContainerEntity {
        return db.findContainerEntity(containerId)
    }

    fun updateContainerProblem(platformId: Int, containerId: Int, problemComment: String, problemType: ProblemEnum, problem: String, failProblem: String?) {
        db.updateContainerProblem(platformId, containerId, problemComment, problemType, problem, failProblem)
    }

    fun updatePlatformProblem(platformId: Int, problemComment: String, problemType: ProblemEnum, problem: String, failProblem: String?) {
        db.updatePlatformProblem(platformId, problemComment, problemType, problem, failProblem)
    }

    fun findBreakDown(): List<String> {
        return db.findAllBreakDown()
    }

    fun findFailReason(): List<String> {
        return db.findAllFailReason()
    }

}

