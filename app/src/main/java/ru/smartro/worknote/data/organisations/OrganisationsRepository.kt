package ru.smartro.worknote.data.organisations

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smartro.worknote.data.Result
import ru.smartro.worknote.domain.models.OrganisationModel
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.utils.TimeConsts.FIVE_MINUTES
import java.io.IOException

class OrganisationsRepository(
    private val organisationsNetworkDataSource: OrganisationsNetworkDataSource,
    private val organisationsDBDataSource: OrganisationsDBDataSource
) {
    private var _organisations: Map<Int, OrganisationModel> = HashMap()

    private var lastRefresh: Long = 0L

    suspend fun refresh(currentUserModel: UserModel): Result<List<OrganisationModel>> {
        return when (val organisationsFromNetResult = organisationsNetworkDataSource.getByUser(currentUserModel)) {
            is Result.Success -> {
                lastRefresh = System.currentTimeMillis()
                _organisations = organisationsFromNetResult.data.map { it.id to it }.toMap()
                organisationsDBDataSource.insertAll(_organisations.values.toList())
                Result.Success(_organisations.values.toList())
            }
            is Result.Error -> handleNetworkGetError(currentUserModel, organisationsFromNetResult)
        }
    }


    private fun handleNetworkGetError(
        currentUserModel: UserModel,
        organisationsFromNetResult: Result.Error
    ):
            Result<List<OrganisationModel>> {
        return when (organisationsFromNetResult.exception) { //get from local db if no connect
            is IOException -> {
                val organisations = organisationsDBDataSource
                    .getAllByIdList(currentUserModel.organisationIds)
                _organisations = organisations.map { it.id to it }.toMap()

                Result.Success(_organisations.values.toList())
            }
            else -> Result.Error(organisationsFromNetResult.exception)
        }
    }

    suspend fun getOrganisations(currentUserModel: UserModel): Result<List<OrganisationModel>> {
        val now = System.currentTimeMillis()
        if (now - lastRefresh > FIVE_MINUTES) {
            return refresh(currentUserModel)
        }
        return Result.Success(_organisations.values.toList())
    }

    suspend fun getOrganisation(id: Int, userModel: UserModel): Result<OrganisationModel>? {
        return withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            if (now - lastRefresh > FIVE_MINUTES) {
                return@withContext when (val organisation = _organisations[id]
                    ?: organisationsDBDataSource.getById(id)) {
                    null -> {
                        Result.Error(Exception("Organisation not find"))
                    }
                    else -> Result.Success(organisation)
                }
            }

            return@withContext when (val result =
                organisationsNetworkDataSource.getById(id, userModel = userModel)) {
                is Result.Success -> {
                    lastRefresh = now
                    organisationsDBDataSource.insertOrUpdate(result.data)
                    result
                }
                is Result.Error -> when (result.exception) {
                    is IOException -> {
                        val organisation =
                            _organisations[id] ?: organisationsDBDataSource.getById(id)
                        if (organisation == null) {
                            Result.Error(Exception("Organisation not find"))
                        } else {
                            Result.Success(organisation)
                        }
                    }
                    else -> result
                }

            }
        }

    }

}