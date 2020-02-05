package ru.smartro.worknote.data.organisations

import ru.smartro.worknote.data.Result
import ru.smartro.worknote.domain.models.OrganisationModel
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.utils.TimeConsts.FIVE_MINUTES
import java.io.IOException

class OrganisationsRepository(
    private val organisationsNetworkDataSource: OrganisationsNetworkDataSource,
    private val organisationsDBDataSource: OrganisationsDBDataSource
) {
    private var _organisations : List<OrganisationModel> = emptyList()

    private var lastRefresh: Long = 0L

    suspend fun refresh(currentUserModel: UserModel): Result<List<OrganisationModel>> {
        return when (val organisationsFromNetResult = organisationsNetworkDataSource.getByUser(currentUserModel)) {
            is Result.Success -> {
                lastRefresh = System.currentTimeMillis()
                _organisations = organisationsFromNetResult.data
                organisationsDBDataSource.insertAll(_organisations)
                Result.Success(_organisations)
            }
            is Result.Error -> hundleNetworkGetError(currentUserModel, organisationsFromNetResult)
        }
    }


    private fun hundleNetworkGetError(
        currentUserModel: UserModel,
        organisationsFromNetResult: Result.Error
    ):
            Result<List<OrganisationModel>> {
        return when (organisationsFromNetResult.exception) { //get from local db if no connect
            is IOException -> {
                val organisations = organisationsDBDataSource
                    .getAllByIdList(currentUserModel.organisationIds)
                _organisations = organisations

                Result.Success(_organisations)
            }
            else -> Result.Error(organisationsFromNetResult.exception)
        }
    }

    suspend fun getOrganisations(currentUserModel: UserModel): Result<List<OrganisationModel>> {
        val now = System.currentTimeMillis()
        if (now - lastRefresh > FIVE_MINUTES) {
            return refresh(currentUserModel)
        }
        return Result.Success(_organisations.toList())
    }
}