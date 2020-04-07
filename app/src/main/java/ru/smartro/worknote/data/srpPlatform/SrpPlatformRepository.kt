package ru.smartro.worknote.data.srpPlatform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smartro.worknote.data.model.PlatformWithContainerCount
import ru.smartro.worknote.domain.models.complex.SrpPlatformWithRelations

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

    suspend fun getPlatformWithRelations(platformId: Int): SrpPlatformWithRelations {
        return withContext(Dispatchers.IO) {
            return@withContext srpPlatformDBDataSource.getPlatformWithContainers(platformId)
        }
    }
}