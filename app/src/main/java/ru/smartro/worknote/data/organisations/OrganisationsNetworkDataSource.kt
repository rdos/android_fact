package ru.smartro.worknote.data.organisations

import ru.smartro.worknote.data.Result
import ru.smartro.worknote.domain.models.OrganisationModel
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.network.BearerToken
import ru.smartro.worknote.network.auth.AuthNetwork

class OrganisationsNetworkDataSource {
    suspend fun getByUser(userModel: UserModel): Result<List<OrganisationModel>> {
        val getDeferred = AuthNetwork.ORGANISATIONS_ENTRY_POINT.index(BearerToken(userModel.token))

        return try {
            val organisationsResponse =  getDeferred.await()
            Result.Success(organisationsResponse.asDomainModel())
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }

}