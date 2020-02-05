package ru.smartro.worknote.data

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.smartro.worknote.data.model.LoggedInUserToken
import ru.smartro.worknote.database.entities.asDomainModel
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.network.auth.responseDto.OwnerData
import ru.smartro.worknote.network.auth.responseDto.asDomainModel
import ru.smartro.worknote.utils.TimeConsts.ONE_MINUTE
import ru.smartro.worknote.utils.TimeConsts.TOKEN_HALF_LIFE
import ru.smartro.worknote.utils.TimeConsts.TOKEN_LIFE_TIME
import java.io.IOException

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(
    val dataSourceNetwork: NetworkLoginDataSource,
    val dbLoginDataSource: DbLoginDataSource
) {

    // in-memory cache of the loggedInUser object
    var userToken: LoggedInUserToken? = null
        private set

    var currentUser: UserModel? = null

    private var lastRefresh = 0L

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        userToken = null
    }

    suspend fun logout(userHolder: MutableLiveData<UserModel?>) {
        userToken = null
        withContext(Dispatchers.IO) {
            dbLoginDataSource.logOutAll()
            userHolder.postValue(null)
            currentUser = null
        }
    }

    suspend fun login(username: String, password: String): Result<UserModel> {
        // handle login

        return when (val tokenResult = dataSourceNetwork.login(username, password)) {
            is Result.Success -> {
                val userModelResult = getUserModelFromOwner(tokenResult.data.token, password)
                if (userModelResult is Result.Success) {
                    withContext(Dispatchers.IO) {
                        dbLoginDataSource.insertOrUpdateUser(userModelResult.data)
                        dbLoginDataSource.logOutAll()
                        dbLoginDataSource.login(userModelResult.data.id)
                        currentUser = userModelResult.data
                        lastRefresh = System.currentTimeMillis()
                    }
                }
                userModelResult
            }
            is Result.Error -> {
                tokenResult
            }
        }

    }

    suspend fun checkRefreshUser(userModel: UserModel): Result<UserModel> {
        val refreshedUserModel: UserModel
        when (val result = refreshTokenIfNeed(userModel)) {
            is Result.Error -> return result
            is Result.Success ->  {refreshedUserModel = result.data}
        }
       
        if ((System.currentTimeMillis() - ONE_MINUTE) < lastRefresh) {
            return Result.Success(refreshedUserModel)
        }

        val userModelRefreshDataResult = getUserModelFromOwner(refreshedUserModel.token, refreshedUserModel.password)

        if (userModelRefreshDataResult is Result.Success) {
            userModelRefreshDataResult.data.expired = refreshedUserModel.expired
            userModelRefreshDataResult.data.token = refreshedUserModel.token
            userModelRefreshDataResult.data.currentOrganisationId =
                refreshedUserModel.currentOrganisationId
            dbLoginDataSource.insertOrUpdateUser(userModelRefreshDataResult.data)
            dbLoginDataSource.logOutAll()
            dbLoginDataSource.login(userModelRefreshDataResult.data.id)
            currentUser = userModelRefreshDataResult.data
            lastRefresh = System.currentTimeMillis()

            return userModelRefreshDataResult
        } else if (userModelRefreshDataResult is Result.Error) {
            when (userModelRefreshDataResult.exception) {
                is IOException -> return Result.Success(refreshedUserModel)
                is HttpException -> {
                    return refreshTokenByCreditionalis(refreshedUserModel)
                }
            }
        }

        return Result.Error(Exception())
    }

    private suspend fun refreshTokenIfNeed(userModel: UserModel): Result<UserModel> {
        if (userModel.expired > System.currentTimeMillis() + TOKEN_HALF_LIFE) {
            return refreshToken(userModel)
        }
        return Result.Success(userModel)
    }

    private suspend fun refreshToken(userModel: UserModel): Result<UserModel> {
        when (val result = refreshTokenByToken(userModel)) {
            is Result.Success -> return result
            is Result.Error -> {
                when (result.exception) {
                    is IOException -> return Result.Success(userModel)
                }
            }
        }
        when (val result = refreshTokenByCreditionalis(userModel)) {
            is Result.Success -> return result
            is Result.Error -> when (result.exception) {
                is IOException -> return Result.Success(userModel)
                is HttpException -> return result
            }
        }
        return Result.Error(Exception("base refresh"))

    }

    private suspend fun refreshTokenByCreditionalis(userModel: UserModel): Result<UserModel> {
        return handleRefreshResult(
            dataSourceNetwork.login(userModel.email, userModel.password),
            userModel
        )
    }

    private suspend fun refreshTokenByToken(userModel: UserModel): Result<UserModel> {
        return handleRefreshResult(
            dataSourceNetwork.refreshToken(userModel),
            userModel
        )
    }

    private fun handleRefreshResult(
        iOResult: Result<LoggedInUserToken>,
        userModel: UserModel
    ): Result<UserModel> {
        if (iOResult is Result.Success) {
            userModel.expired = System.currentTimeMillis() + TOKEN_LIFE_TIME
            userModel.token = iOResult.data.token
            lastRefresh = System.currentTimeMillis()
            dbLoginDataSource.insertOrUpdateUser(userModel)

            return Result.Success(userModel)
        } else if (iOResult is Result.Error) {
            when (iOResult.exception) {
                is IOException -> return Result.Success(userModel)
                is HttpException -> return Result.Error(iOResult.exception)
            }
        }
        return Result.Error(Exception("error refresh token"))
    }


    suspend fun getLoggedInUser(userHolder: MutableLiveData<UserModel?>) {
        withContext(Dispatchers.IO) {
            val userModel = currentUser ?: dbLoginDataSource.getLoggedIn()?.asDomainModel()

            if (userModel === null) {
                userHolder.postValue(null)
                currentUser = null

                return@withContext
            }
            currentUser = when (checkRefreshUser(userModel)) {
                is Result.Success -> {
                    userHolder.postValue(userModel)
                    userModel
                }
                is Result.Error -> {
                    userHolder.postValue(null)
                    null
                }
            }
        }
    }

    fun setCurrentOrganisation(userId: Int, organisationId: Int) {
        dbLoginDataSource.updateCurrentOrganisation(userId, organisationId)
        if (currentUser?.id == userId) {
            currentUser?.currentOrganisationId = organisationId
        }
    }

    private suspend fun getUserModelFromOwner(
        token: String,
        password: String
    ): Result<UserModel> {

        return when (val ownerResult = dataSourceNetwork.getOwner(token)) {
            is Result.Success -> {
                makeUserModelResult(ownerResult.data, password, token)
            }
            is Result.Error -> {
                Result.Error(ownerResult.exception)
            }
        }
    }

    private fun makeUserModelResult(
        ownerData: OwnerData,
        password: String,
        token: String
    ): Result<UserModel> {
        return Result.Success(
            ownerData.asDomainModel(
                password,
                token,
                System.currentTimeMillis() + TOKEN_LIFE_TIME,
                false
            )
        )
    }
}
