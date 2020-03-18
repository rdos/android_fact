package ru.smartro.worknote.data.waybillBody

import ru.smartro.worknote.database.entities.SrpContainerEntity
import ru.smartro.worknote.database.entities.SrpPlatformEntity
import ru.smartro.worknote.database.entities.WayBillBodyEntity
import ru.smartro.worknote.database.entities.WorkOrderEntity
import ru.smartro.worknote.domain.models.complex.SrpContainerWithRelations
import ru.smartro.worknote.domain.models.complex.SrpPlatformWithRelations
import ru.smartro.worknote.domain.models.complex.WaybillWithRelations
import ru.smartro.worknote.domain.models.complex.WorkOrderWithRelations

class DbWaybillWIthRelationsConverter {

    fun makePlatformEntity(
        platformWithRelations: SrpPlatformWithRelations,
        workOrderId: Int
    ): SrpPlatformEntity {
        return SrpPlatformEntity(
            srpId = platformWithRelations.srpId,
            workOrderSrpId = workOrderId,
            address = platformWithRelations.address,
            kgoNorma = platformWithRelations.kgoNorma,
            name = platformWithRelations.name,
            longitude = platformWithRelations.location.longitude,
            latitude = platformWithRelations.location.latitude
        )
    }

    fun makeWaybillBodyEntity(waybillWithRelations: WaybillWithRelations): WayBillBodyEntity {
        return WayBillBodyEntity(
            srpId = waybillWithRelations.srpId,
            organisationId = waybillWithRelations.organisationId,
            srpVehicleId = waybillWithRelations.srpVehicleId,
            date = waybillWithRelations.date,
            srpDriverId = waybillWithRelations.srpDriveId
        )
    }

    fun makeWorkOrderEntity(
        workOrderWithRelations: WorkOrderWithRelations,
        wayBillId: Int
    ): WorkOrderEntity {
        return WorkOrderEntity(
            srpId = workOrderWithRelations.srpId,
            waybillId = wayBillId
        )
    }

    fun makeContainerEntity(
        srpContainerWithRelations: SrpContainerWithRelations,
        srpPlatformId: Int
    ): SrpContainerEntity {
        return SrpContainerEntity(
            srpPointDetailsId = srpContainerWithRelations.srpPointDetailsId,
            platformSrpId = srpPlatformId,
            isActive = srpContainerWithRelations.isActive,
            invNumber = srpContainerWithRelations.invNumber,
            srpTypeId = srpContainerWithRelations.srpType.srpId
        )
    }


}