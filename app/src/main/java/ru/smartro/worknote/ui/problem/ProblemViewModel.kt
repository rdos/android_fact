package ru.smartro.worknote.ui.problem

import android.app.Application
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.util.ProblemEnum

class ProblemViewModel(application: Application) : BaseViewModel(application) {


    fun findPlatformEntity(platformId: Int): PlatformEntity {
        return baseDat.findPlatformEntity(platformId)
    }

    fun findContainerEntity(containerId: Int): ContainerEntity {
        return baseDat.findContainerEntity(containerId)
    }

    fun updateContainerProblem(platformId: Int, containerId: Int, problemComment: String, problemType: ProblemEnum, problem: String, failProblem: String?) {
        baseDat.updateContainerProblem(platformId, containerId, problemComment, problemType, problem, failProblem)
    }

    fun updatePlatformProblem(platformId: Int, problemComment: String, problemType: ProblemEnum, problem: String, failProblem: String?) {
        baseDat.updatePlatformProblem(platformId, problemComment, problemType, problem, failProblem)
    }

    fun findBreakDown(): List<String> {
        return baseDat.findAllBreakDown()
    }

    fun findFailReason(): List<String> {
        return baseDat.findAllFailReason()
    }

}

