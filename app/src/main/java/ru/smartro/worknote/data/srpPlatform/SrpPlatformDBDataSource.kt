package ru.smartro.worknote.data.srpPlatform

import ru.smartro.worknote.data.model.PlatformWithContainerCount
import ru.smartro.worknote.database.DataBase

class SrpPlatformDBDataSource(
    private val dataBase: DataBase
) {

    fun getPlatformsWithContainerCount(workOrderId: Int): List<PlatformWithContainerCount> {
        return dataBase.srpPlatformDao.getWithContainerCount(workOrderId)
    }
}