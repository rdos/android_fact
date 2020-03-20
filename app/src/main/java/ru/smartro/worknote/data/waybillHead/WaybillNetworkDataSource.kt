package ru.smartro.worknote.data.waybillHead

import ru.smartro.worknote.data.Result
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.WaybillHeadModel
import ru.smartro.worknote.network.BearerToken
import ru.smartro.worknote.network.workNote.WnNetwork
import ru.smartro.worknote.network.workNote.requestDTO.WaybillHeadRequest
import java.time.LocalDate

class WaybillNetworkDataSource {

    suspend fun getListBy(
        waybillHeadRequest: WaybillHeadRequest,
        userModel: UserModel,
        date: LocalDate
    ): Result<List<WaybillHeadModel>> {
        val getDeferred =
            WnNetwork.WAY_BILL_ENTRY_POINT.list(waybillHeadRequest, BearerToken(userModel.token))
        return try {
            val waybillResponse = getDeferred.await()

            Result.Success(waybillResponse.asDomainModel(date, waybillHeadRequest.vehicleId))
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }
}