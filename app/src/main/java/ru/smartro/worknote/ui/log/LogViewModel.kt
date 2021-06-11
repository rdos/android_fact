package ru.smartro.worknote.ui.log

import android.app.Application
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.work_order.ContainerEntity
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.service.database.entity.work_order.WayTaskEntity

class LogViewModel(application: Application) : BaseViewModel(application) {

    fun findContainerProgress(): List<Int> {
        return db.findContainerProgress()
    }

    fun findPlatformProgress(): List<Int> {
        return db.findPlatformProgress()
    }

    fun findWayTask(): WayTaskEntity {
        return db.findWayTask()
    }

    fun findAllPlatforms(): List<PlatformEntity> {
        return db.findAllPlatforms()
    }

    fun findAllContainerInPlatform(platformId: Int): List<ContainerEntity> {
        return db.findAllContainerInPlatform(platformId)
    }

    fun findPlatformEntity(platformId: Int): PlatformEntity {
        return db.findPlatformEntity(platformId)
    }

}