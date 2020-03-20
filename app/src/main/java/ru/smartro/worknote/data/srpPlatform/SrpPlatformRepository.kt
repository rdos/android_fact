package ru.smartro.worknote.data.srpPlatform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smartro.worknote.ui.workFlow.showSrpPlatform.PlatformToShow

class SrpPlatformRepository(
    private val srpPlatformDBDataSource: SrpPlatformDBDataSource
) {
    suspend fun getPlatformsWithContainerCount(
        workOrderId: Int
    ): List<PlatformToShow> {
        return withContext(Dispatchers.IO) {
            return@withContext srpPlatformDBDataSource.getPlatformsToView(workOrderId = workOrderId)
        }
    }
}