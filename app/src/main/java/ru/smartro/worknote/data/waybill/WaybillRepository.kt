package ru.smartro.worknote.data.waybill

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smartro.worknote.data.NetworkState
import ru.smartro.worknote.data.Result
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.WaybillHeadModel
import ru.smartro.worknote.network.workNote.requestDTO.WaybillHeadRequest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WaybillRepository(
    private val waybillNetworkDataSource: WaybillNetworkDataSource,
    private val waybillDBDataSource: WaybillDBDataSource,
    private val networkState: NetworkState
) {

    data class WaybillCriteria(
        val date: LocalDate,
        val VehicleId: Int,
        val organisationId: Int,
        val user: UserModel
    )

    private var localCache = listOf<WaybillHeadModel>()
    private var currentOrganisationId: Int? = null
    private var currentDate: LocalDate? = null

    private val NETWORK_STATE_KEY = "way_bill_head"


    suspend fun getAllWaybillsByCriteria(criteria: WaybillCriteria): Result<List<WaybillHeadModel>> {
        return withContext(Dispatchers.IO) {
            return@withContext if (networkState.requestIsNotNeed(NETWORK_STATE_KEY)) {
                val models = getAllCachedByCriteria(criteria)
                if (models.isEmpty()) {
                    getFromNetByCriteria(criteria)
                } else {
                    Result.Success(models)
                }
            } else {
                getFromNetByCriteria(criteria)
            }
        }
    }

    fun dropAllCD() {
        networkState.isErrorCoolDown = false
        networkState.reset(NETWORK_STATE_KEY)
    }

    private suspend fun getFromNetByCriteria(criteria: WaybillCriteria): Result<List<WaybillHeadModel>> {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        return when (val networkResult = waybillNetworkDataSource.getListBy(
            WaybillHeadRequest(
                criteria.date.format(formatter),
                criteria.VehicleId,
                criteria.organisationId
            ),
            criteria.user,
            criteria.date
        )) {
            is Result.Success -> {
                networkState.setRefreshedNowOf(NETWORK_STATE_KEY)
                networkState.isErrorCoolDown = false
                currentOrganisationId = criteria.organisationId
                updateLocalDataSources(networkResult.data, criteria.organisationId, criteria.date)

                networkResult
            }
            is Result.Error -> when {
                networkResult.isIOError -> {
                    networkState.isErrorCoolDown = true

                    Result.Success(emptyList())
                }
                networkResult.isAuthError -> networkResult
                else -> networkResult
            }
        }
    }


    private fun getAllCachedByCriteria(criteria: WaybillCriteria): List<WaybillHeadModel> {

        return if (currentOrganisationId == criteria.organisationId
            && currentDate?.equals(criteria.date) == true
            && localCache.isNotEmpty()
        ) {
            localCache
        } else {
            localCache = waybillDBDataSource.getAllByOrganisationIdAndDate(
                criteria.organisationId,
                criteria.date
            )
            currentOrganisationId = criteria.organisationId
            currentDate = criteria.date
            localCache
        }
    }

    private fun updateLocalDataSources(
        models: List<WaybillHeadModel>,
        organisationId: Int,
        date: LocalDate
    ) {
        waybillDBDataSource.insertAll(models)
        localCache = models
        currentOrganisationId = organisationId
        currentDate = date
    }
}