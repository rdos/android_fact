package ru.smartro.worknote.domain.models.complex

import android.location.Location
import ru.smartro.worknote.domain.models.SrpPlatformModel

class SrpPlatformWithRelations(
    val srpId: Int,
    val address: String,
    val kgoNorma: Int?,
    val name: String,
    val location: Location,
    val containers: List<SrpContainerWithRelations>
) {
    fun toDomainModel(workOrderId: Int): SrpPlatformModel {
        return SrpPlatformModel(
            srpId = srpId,
            address = address,
            kgoNorma = kgoNorma,
            name = name,
            latitude = location.latitude,
            longitude = location.longitude,
            workOrderSrpId = workOrderId
        )
    }
}