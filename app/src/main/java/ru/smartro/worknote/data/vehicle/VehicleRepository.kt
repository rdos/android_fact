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
    private var localCache = mutableListOf<VehicleModel>()
    private var currentOrganisationId: Int? = null
    private val NETWORK_STATE_KEY = "vehicle"


    suspend fun getAllVehiclesByUser(
        currentUser: UserModel,
        organisationId: Int
    ): Result<List<VehicleModel>> {
        return withContext(Dispatchers.IO) {
            return@withContext if (networkState.requestIsNotNeed(NETWORK_STATE_KEY)) {
                val models = getAllCachedByOrganisation(organisationId)
                if (models.isEmpty()) {
                    getFromNetByUser(currentUser, organisationId)
                } else {
                    Result.Success(models)
                }
            } else {
                getFromNetByUser(currentUser, organisationId)
            }
        }
    }

    suspend fun getVehicle(
        currentUser: UserModel,
        organisationId: Int,
        id: Int
    ): Result<VehicleModel> {
        when (val result = getAllVehiclesByUser(currentUser, organisationId)) {
            is Result.Error -> return result
            is Result.Success -> {
                val vehicle: VehicleModel? = result.data.find {
                    return@find it.id == id
                }
                return Result.Success(
                    vehicle ?: VehicleModel(
                        id,
                        "not find",
                        organisationId
                    )
                ) //todo придумать что то полутше
            }
        }
    }

    fun dropAllCD() {
        networkState.isErrorCoolDown = false
        networkState.reset(NETWORK_STATE_KEY)
    }

    private suspend fun getFromNetByUser(
        currentUser: UserModel,
        organisationId: Int
    ): Result<List<VehicleModel>> {
        return when (val networkResult = vehicleNetworkDataSource.getListBy(
            VehicleNetworkDataSource.ParamsGetList(currentUser, organisationId)
        )) {
            is Result.Success -> {
                networkState.setRefreshedNowOf(NETWORK_STATE_KEY)
                networkState.isErrorCoolDown = false
                updateLocalDataSources(networkResult.data)

                networkResult
            }
            is Result.Error -> when {
                networkResult.isIOError -> {
                    networkState.isErrorCoolDown = true
                    Result.Success(getAllCachedByOrganisation(organisationId))
                }
                networkResult.isAuthError -> networkResult
                else -> networkResult
            }
        }
    }


    private fun getCachedByOrganisation(organisationId: Int, id: Int): VehicleModel? {
        var result: VehicleModel?
        if (currentOrganisationId == organisationId && localCache.isNotEmpty()) {
            result = getFromCacheById(id)
            if (result != null) {
                return result
            }
        }
        result = vehicleDBDataSource.get(id)
        if (result != null) {
            localCache.add(result)
        }
        currentOrganisationId = organisationId

        return result

    }

    private fun getFromCacheById(id: Int): VehicleModel? {
        return localCache.find {
            it.id == id
        }
    }

    private fun getAllCachedByOrganisation(organisationId: Int): List<VehicleModel> {
        return if (currentOrganisationId == organisationId && localCache.isNotEmpty()) {
            localCache
        } else {
            localCache.clear()
            localCache.addAll(vehicleDBDataSource.getAllByOrganisationId(organisationId))
            currentOrganisationId = organisationId
            localCache
        }
    }

    private fun updateLocalDataSources(models: List<VehicleModel>) {
        vehicleDBDataSource.insertAll(models)
        localCache.clear()
        localCache.addAll(models)
    }
}