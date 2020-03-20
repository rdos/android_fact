package ru.smartro.worknote.data.waybillBody

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smartro.worknote.data.NetworkState
import ru.smartro.worknote.data.Result
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.WorkOrderModel
import ru.smartro.worknote.domain.models.complex.WaybillWithRelations
import ru.smartro.worknote.domain.models.complex.asDomainModel
import ru.smartro.worknote.network.workNote.requestDTO.WaybillBodyRequest
import timber.log.Timber

class WaybillBodyRepository(
    private val waybillNetworkDataSource: WaybillBodyNetworkDataSource,
    private val waybillDBDataSource: WaybillBodyDBDataSource,
    private val networkState: NetworkState
) {

    private val NETWORK_STATE_KEY = "way_bill_body"

    suspend fun refresh(
        user: UserModel,
        organisationId: Int,
        waybillId: Int
    ): Result<WaybillWithRelations> {
            val waybillWithRelationsResult =
                waybillNetworkDataSource.getBy(WaybillBodyRequest(organisationId), user, waybillId)
        return when (waybillWithRelationsResult) {
            is Result.Error -> {
                Timber.e(waybillWithRelationsResult.exception)
                Result.Error(waybillWithRelationsResult.exception)
            }
            is Result.Success -> {
                waybillDBDataSource.insert(waybillWithRelationsResult.data.wayBillWithRelations)

                Result.Success(waybillWithRelationsResult.data.wayBillWithRelations)
            }
        }
    }

    suspend fun getWorkOrders(
        user: UserModel,
        organisationId: Int,
        waybillId: Int
    ): Result<List<WorkOrderModel>> {
        return withContext(Dispatchers.IO) {
            if (networkState.requestIsNotNeed(NETWORK_STATE_KEY)) {
                val models = waybillDBDataSource.getWorkOrders(waybillId)
                if (models.isEmpty()) {
                    when (val resultWitRelations = refresh(user, organisationId, waybillId)) {
                        is Result.Success -> return@withContext Result.Success(
                            resultWitRelations.data.workOrders.asDomainModel(
                                waybillId
                            )
                        )
                        is Result.Error -> return@withContext Result.Error(resultWitRelations.exception)
                    }
                } else {
                    return@withContext Result.Success(models)
                }
            } else {
                when (val resultWitRelations = refresh(user, organisationId, waybillId)) {
                    is Result.Success -> return@withContext Result.Success(
                        resultWitRelations.data.workOrders.asDomainModel(
                            waybillId
                        )
                    )
                    is Result.Error -> {
                        if (resultWitRelations.isAuthError) {
                            return@withContext Result.Error(resultWitRelations.exception)
                        } else {
                            return@withContext Result.Success(
                                waybillDBDataSource.getWorkOrders(
                                    waybillId
                                )
                            )
                        }
                    }

                }
            }
        }
    }

    fun dropAllCD() {
        networkState.isErrorCoolDown = false
        networkState.reset(NETWORK_STATE_KEY)
    }

}