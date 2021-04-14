package ru.smartro.worknote.ui.ProblemActivity

import android.app.Application
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.problem.BreakDownEntity
import ru.smartro.worknote.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.service.database.entity.way_task.PlatformEntity
import ru.smartro.worknote.util.ProblemEnum

class ProblemViewModel(application: Application) : BaseViewModel(application) {



    fun findPlatformEntity(platformId: Int): PlatformEntity? {
        return db.findPlatformEntity(platformId)
    }

    fun updateContainerProblem (platformId: Int, containerId: Int, problemComment: String, problemType: ProblemEnum, problem: String, failProblem: String?){
        db.updateContainerProblem(platformId, containerId, problemComment, problemType, problem, failProblem)
    }

    fun updatePlatformProblem (platformId: Int, problemComment: String, problemType: ProblemEnum, problem: String, failProblem: String?){
        db.updatePlatformProblem(platformId, problemComment, problemType, problem, failProblem)
    }

    fun findBreakDown(): List<BreakDownEntity> {
        return db.findAllBreakDown()
    }

    fun findFailReason(): List<FailReasonEntity> {
        return db.findAllFailReason()
    }

}

