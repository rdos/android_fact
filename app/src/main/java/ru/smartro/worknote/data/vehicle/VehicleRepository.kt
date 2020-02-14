package ru.smartro.worknote.data.vehicle

import android.util.SparseArray
import androidx.core.util.set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smartro.worknote.data.NetworkState
import ru.smartro.worknote.data.Result
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.VehicleModel
import ru.smartro.worknote.utils.ExtendedSparseArray

class VehicleRepository(
    private val vehicleDBDataSource: VehicleDBDataSource,
    private val vehicleNetworkDataSource: VehicleNetworkDataSource,
    private val networkState: NetworkState
) {
    private val localCache = LocalCache()
    private val NETWORK_STATE_KEY = "vehicle"


    suspend fun getAllVehiclesByUser(currentUser: UserModel): Result<List<VehicleModel>> {
        return withContext(Dispatchers.IO) {
            return@withContext if (networkState.requestIsNotNeed(NETWORK_STATE_KEY)) {
                val models = getAllCachedByOrganisation(currentUser.currentOrganisationId ?: 0)
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
        val networkResult = vehicleNetworkDataSource.getAllByOrganisation(
            currentUser.currentOrganisationId ?: 0,
            currentUser.token
        )
        return when (networkResult) {
            is Result.Success -> {
                networkState.setRefreshedNowOf(NETWORK_STATE_KEY)
                networkState.isErrorCoolDown = false
                updateLocaclDataSources(networkResult.data)

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
        var models = localCache.getAllByOrganisationId(organisationId)
        if (models.isNotEmpty()) {
            return models
        }
        models = vehicleDBDataSource.getAllByOrganisationId(organisationId)
        localCache.load(models)

        return models
    }

    private fun updateLocaclDataSources(models: List<VehicleModel>) {
        vehicleDBDataSource.insertAll(models)
        localCache.load(models)
    }

    /**
     * section data
     */
    data class CompositeVehicleKey(val organisationId: Int, val vehicleId: Int) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CompositeVehicleKey

            if (organisationId != other.organisationId) return false
            if (vehicleId != other.vehicleId) return false

            return true
        }

        override fun hashCode(): Int {
            var result = organisationId
            result = 31 * result + vehicleId
            return result
        }
    }

    class LocalCache {
        private val store = SparseArray<ExtendedSparseArray<VehicleModel>>()

        fun getAllByOrganisationId(organisationId: Int): List<VehicleModel> {
            return store[organisationId]?.asList() ?: emptyList()
        }

        fun set(organisationId: Int, vehicleId: Int, vehicleModel: VehicleModel) {
            if (store.get(organisationId) == null) {
                store[organisationId] = ExtendedSparseArray()
            }
            store[organisationId][vehicleId] = vehicleModel
        }

        fun load(data: List<VehicleModel>) {
            store.clear()
            data.forEach {
                set(organisationId = it.organisationId, vehicleId = it.id, vehicleModel = it)
            }
        }
    }
}