package ru.smartro.worknote.data.srpPlatform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smartro.worknote.data.model.PlatformWithContainerCount

class SrpPlatformRepository(
    private val srpPlatformDBDataSource: SrpPlatformDBDataSource
) {
    suspend fun getPlatformsWithContainerCount(
        workOrderId: Int
    ): List<PlatformWithContainerCount> {
        return withContext(Dispatchers.IO) {
            return@withContext srpPlatformDBDataSource.getPlatformsWithContainerCount(workOrderId = workOrderId)
        }
    }
}