package ru.smartro.worknote.data

import android.util.Log
import org.json.JSONObject
import retrofit2.HttpException
import ru.smartro.worknote.data.model.LoggedInUserToken
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.network.BearerToken
import ru.smartro.worknote.network.auth.AuthNetwork
import ru.smartro.worknote.network.auth.requestDto.AuthBody
import ru.smartro.worknote.network.auth.responseDto.OwnerData
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class NetworkLoginDataSource {

    suspend fun login(username: String, password: String): Result<LoggedInUserToken> {
        val authDeferred = AuthNetwork.LOGIN_ENTRY_POINT.auth(AuthBody(username, password, password))
        var result: Result<LoggedInUserToken>

        try {
            val loginResponseData = authDeferred.await()
            result = Result.Success(LoggedInUserToken(loginResponseData.data.token))
        } catch (e: HttpException) {
            try {
                val json = e.response()?.errorBody()?.string()
                val jsonParsedBody = JSONObject((json ?: ""))
                result = Result.Error(e, jsonParsedBody.getString("message"))
            } catch (e: Throwable) {
                Log.e("login json", e.stackTrace.toString())
                result = Result.Error(e)
            }
        } catch (e: IOException) {
            result = Result.Error(e)
        } catch (e: Throwable) {
            Log.e("login", e.stackTrace.toString())
            result = Result.Error(e)
        }
        return result
    }

    suspend fun getOwner(token: String): Result<OwnerData> {
        val ownerDeferred = AuthNetwork.LOGIN_ENTRY_POINT.getOwner(BearerToken(token))
        val result: Result<OwnerData>
        result = try {
            val ownerData = ownerDeferred.await()
            Result.Success(ownerData)
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: Throwable) {
            Log.e("owner", e.stackTrace.toString())
            Result.Error(e)
        }
        return result
    }

    suspend fun refreshToken(userModel: UserModel): Result<LoggedInUserToken> {
        val authDeferred = AuthNetwork.LOGIN_ENTRY_POINT.refresh(BearerToken(userModel.token))
        var result: Result<LoggedInUserToken>
        try {
            val refreshResponseData = authDeferred.await()
            result = Result.Success(LoggedInUserToken(refreshResponseData.data.token))
        } catch (e: HttpException) {
            try {
                val json = e.response()?.errorBody()?.string()
                val jsonParsedBody = JSONObject((json ?: ""))
                result = Result.Error(e, jsonParsedBody.getString("message"))
            } catch (e: Throwable) {
                Log.e("refresh json", e.stackTrace.toString())
                result = Result.Error(e)
            }
        } catch (e: IOException) {
            result = Result.Error(e)
        } catch (e: Throwable) {
            Log.e("refresh", e.stackTrace.toString())
            result = Result.Error(e)
        }
        return result
    }

    fun logout() {
        // TODO: revoke authentication
    }
}

