package ru.smartro.worknote.workold.ui

import android.app.Application
import ru.smartro.worknote.workold.base.BaseViewModel
import ru.smartro.worknote.work.PlatformEntity

class JournalViewModel(application: Application) : BaseViewModel(application) {

    fun findPlatformsIsServed(): List<PlatformEntity> {
        return baseDat.findPlatformsIsServed()
    }
//
//    fun findWayTask(): WayTaskEntity {
//        return db.findWayTask()
//    }

//    fun findAllPlatforms(): List<PlatformEntity> {
//        return db.findAllPlatforms()
//    }

//    fun findAllContainerInPlatform(platformId: Int): List<ContainerEntity> {
//        return db.findAllContainerInPlatform(platformId)
//    }
//
//    fun findPlatformEntity(platformId: Int): PlatformEntity {
//        return db.findPlatformEntity(platformId)
//    }

}