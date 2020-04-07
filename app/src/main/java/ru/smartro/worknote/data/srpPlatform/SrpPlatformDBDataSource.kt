package ru.smartro.worknote.data.srpPlatform

import android.location.Location
import ru.smartro.worknote.data.model.PlatformWithContainerCount
import ru.smartro.worknote.database.DataBase
import ru.smartro.worknote.database.entities.toDomainModel
import ru.smartro.worknote.domain.models.SrpContainerTypeModel
import ru.smartro.worknote.domain.models.complex.SrpContainerWithRelations
import ru.smartro.worknote.domain.models.complex.SrpPlatformWithRelations

class SrpPlatformDBDataSource(
    private val dataBase: DataBase
) {

    fun getPlatformsWithContainerCount(workOrderId: Int): List<PlatformWithContainerCount> {
        return dataBase.srpPlatformDao.getWithContainerCount(workOrderId)
    }

    fun getPlatformWithContainers(platformId: Int): SrpPlatformWithRelations {
        val platformEntity = dataBase.srpPlatformDao.get(platformId)
        val containers = dataBase.srpContainerDao.getByPlatform(platformId)
        val containersWithRelations = containers.map {
            val containerType = dataBase.srpContainerTypeDao.get(it.srpTypeId)

            SrpContainerWithRelations(
                srpPointDetailsId = it.srpPointDetailsId,
                invNumber = it.invNumber,
                isActive = it.isActive,
                srpType = containerType.toDomainModel()
            )
        }

        return SrpPlatformWithRelations(
            srpId = platformEntity.srpId,
            address = platformEntity.address,
            name = platformEntity.name,
            kgoNorma = platformEntity.kgoNorma,
            location = Location("").apply {
                latitude = platformEntity.latitude
                longitude = platformEntity.longitude
            },
            containers = containersWithRelations
        )
    }
}