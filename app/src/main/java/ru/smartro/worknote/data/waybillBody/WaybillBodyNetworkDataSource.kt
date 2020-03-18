package ru.smartro.worknote.data.waybillBody

import android.location.Location
import androidx.core.util.set
import ru.smartro.worknote.data.Result
import ru.smartro.worknote.domain.models.SrpContainerTypeModel
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.complex.SrpContainerWithRelations
import ru.smartro.worknote.domain.models.complex.SrpPlatformWithRelations
import ru.smartro.worknote.domain.models.complex.WaybillWithRelations
import ru.smartro.worknote.domain.models.complex.WorkOrderWithRelations
import ru.smartro.worknote.network.BearerToken
import ru.smartro.worknote.network.workNote.WnNetwork
import ru.smartro.worknote.network.workNote.requestDTO.WaybillBodyRequest
import ru.smartro.worknote.network.workNote.responseDTO.WaybillBodyDTO
import ru.smartro.worknote.utils.ListableSparseArray
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WaybillBodyNetworkDataSource {

    private val containerTypes =
        ListableSparseArray<SrpContainerTypeModel>()

    suspend fun getBy(
        waybillBodyRequest: WaybillBodyRequest,
        userModel: UserModel,
        waybillId: Int
    ): Result<ListResult> {
        val getDeferred =
            WnNetwork.WAY_BILL_BODY_ENTRY_POINT.list(
                waybillBodyRequest,
                waybillId,
                BearerToken(userModel.token)
            )
        return try {
            val waybillBodyResponse = getDeferred.await()
            val wayBillWithRelations = buildWaybillBodyWithRelations(waybillBodyResponse)

            Result.Success(ListResult(containerTypes.asList(), wayBillWithRelations))
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }

    private fun buildWaybillBodyWithRelations(waybillBodyDTO: WaybillBodyDTO): WaybillWithRelations {
        val waybillBodyData = waybillBodyDTO.data.waybill
        val date: LocalDate
        try {
            date = LocalDate.parse(waybillBodyData.date, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            throw Exception("can not pare date ${waybillBodyData.date}", e)
        }
        return WaybillWithRelations(
            srpId = waybillBodyData.srpId,
            date = date,
            srpDriveId = waybillBodyData.srpDriverId,
            srpVehicleId = waybillBodyData.srpVehicleId,
            organisationId = waybillBodyDTO.organisationId,
            workOrders = waybillBodyData.workOrders.map { buildWorkOrder(it) }
        )

    }

    private fun buildWorkOrder(workOrderDto: WaybillBodyDTO.WorkOrder): WorkOrderWithRelations {
        return WorkOrderWithRelations(
            srpId = workOrderDto.srpId,
            platforms = workOrderDto.platformItems.map { buildPlatform(it) }
        )
    }

    private fun buildPlatform(platformDto: WaybillBodyDTO.Platform): SrpPlatformWithRelations {
        return SrpPlatformWithRelations(
            srpId = platformDto.srpId,
            name = platformDto.name,
            kgoNorma = platformDto.kgoNorma,
            address = platformDto.address,
            location = Location("").apply {
                latitude = platformDto.latitude
                longitude = platformDto.longitude
            },
            containers = platformDto.containers.map { buildContainer(it) }
        )
    }

    private fun buildContainer(containerDto: WaybillBodyDTO.Container): SrpContainerWithRelations {
        return SrpContainerWithRelations(
            srpPointDetailsId = containerDto.srpDetailsId,
            invNumber = containerDto.invNumber,
            isActive = containerDto.isActive,
            srpType = buildContainerType(containerDto.srpTypeName, containerDto.srpTypeId)
        )
    }

    private fun buildContainerType(
        srpContainerTypeName: String,
        srpContainerTypeId: Int
    ): SrpContainerTypeModel {
        val type = containerTypes[srpContainerTypeId]
        return if (type == null) {
            val newType = SrpContainerTypeModel(srpContainerTypeId, srpContainerTypeName)
            containerTypes[srpContainerTypeId] = newType
            newType
        } else {
            type
        }
    }

    data class ListResult(
        val allContainerTypes: List<SrpContainerTypeModel>,
        val wayBillWithRelations: WaybillWithRelations
    )
}