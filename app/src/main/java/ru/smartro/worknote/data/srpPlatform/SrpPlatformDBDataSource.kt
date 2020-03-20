package ru.smartro.worknote.data.srpPlatform

import ru.smartro.worknote.database.DataBase
import ru.smartro.worknote.ui.workFlow.showSrpPlatform.PlatformToShow

class SrpPlatformDBDataSource(
    private val dataBase: DataBase
) {

    fun getPlatformsToView(workOrderId: Int): List<PlatformToShow> {
        return dataBase.srpPlatformDao.getWithContainerCount(workOrderId)
    }
}