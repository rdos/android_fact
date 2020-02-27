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

    private val localCache = HashMap<LocalDate, MutableList<WaybillHeadModel>>()
    private var currentOrganisationId: Int? = null
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
                updateLocalDataSources(networkResult.data)

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
            && localCache[criteria.date]?.isNotEmpty() == true
        ) {
            localCache[criteria.date] ?: mutableListOf()
        } else {
            localCache[criteria.date] = waybillDBDataSource.getAllByOrganisationIdAndDate(
                criteria.organisationId,
                criteria.date
            ) as MutableList<WaybillHeadModel>

            currentOrganisationId = criteria.organisationId
            localCache[criteria.date] ?: mutableListOf()
        }
    }

    private fun updateLocalDataSources(models: List<WaybillHeadModel>) {
        waybillDBDataSource.insertAll(models)
        models.forEach {
            if (localCache[it.date] === null) {
                localCache[it.date] = mutableListOf()
            }
            localCache[it.date]?.add(it)
        }


    }
}