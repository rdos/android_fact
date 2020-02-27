package ru.smartro.worknote.data.vehicle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smartro.worknote.data.NetworkState
import ru.smartro.worknote.data.Result
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.VehicleModel

class VehicleRepository(
    private val vehicleDBDataSource: VehicleDBDataSource,
    private val vehicleNetworkDataSource: VehicleNetworkDataSource,
    private val networkState: NetworkState
) {
    private var localCache = listOf<VehicleModel>()
    private var currentOrganisationId: Int? = null
    private val NETWORK_STATE_KEY = "vehicle"


    suspend fun getAllVehiclesByUser(currentUser: UserModel): Result<List<VehicleModel>> {
        return withContext(Dispatchers.IO) {
            return@withContext if (networkState.requestIsNotNeed(NETWORK_STATE_KEY)) {
                val models = getAllCachedByOrganisation(/*currentUser.currentOrganisationId ?:*/ 0)
                if (models.isEmpty()) {
                    getFromNetByUser(currentUser)
                } else {
                    Result.Success(models)
                }
            } else {
                getFromNetByUser(currentUser)
            }
        }

    }

    fun dropAllCD() {
        networkState.isErrorCoolDown = false
        networkState.reset(NETWORK_STATE_KEY)
    }

    private suspend fun getFromNetByUser(currentUser: UserModel): Result<List<VehicleModel>> {
        return when (val networkResult = vehicleNetworkDataSource.getListBy(currentUser)) {
            is Result.Success -> {
                networkState.setRefreshedNowOf(NETWORK_STATE_KEY)
                networkState.isErrorCoolDown = false
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


    private fun getAllCachedByOrganisation(organisationId: Int): List<VehicleModel> {
        return if (currentOrganisationId == organisationId && localCache.isNotEmpty()) {
            localCache
        } else {
            localCache = vehicleDBDataSource.getAllByOrganisationId(organisationId)
            currentOrganisationId = organisationId
            localCache
        }
    }

    private fun updateLocalDataSources(models: List<VehicleModel>) {
        vehicleDBDataSource.insertAll(models)
        localCache = models
    }
}