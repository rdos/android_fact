package ru.smartro.worknote.service.network

import android.content.Context
import androidx.lifecycle.liveData
import com.google.gson.Gson
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.network.body.AuthBody
import ru.smartro.worknote.service.network.body.ProgressBody
import ru.smartro.worknote.service.network.body.WayListBody
import ru.smartro.worknote.service.network.body.WayTaskBody
import ru.smartro.worknote.service.network.body.breakdown.BreakdownBody
import ru.smartro.worknote.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.service.network.body.early_complete.EarlyCompleteBody
import ru.smartro.worknote.service.network.body.failure.FailureBody
import ru.smartro.worknote.service.network.body.served.ServiceResultBody
import ru.smartro.worknote.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.service.network.exeption.BadRequestException
import ru.smartro.worknote.service.network.response.EmptyResponse

class NetworkRepository(private val context: Context) {
    fun auth(model: AuthBody) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(false).auth(model)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    response.raw().request.url
                    badRequest(response)
                    emit(Resource.error("Неверный логин или пароль", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun served(body: ServiceResultBody) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(true).served(body)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    badRequest(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun getVehicle(organisationId: Int) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context)
                .apiService(true).getVehicle(organisationId)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    badRequest(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun getBreakDownTypes() = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context)
                .apiService(true).getBreakDownTypes()
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    badRequest(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun getFailReason() = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(true).getFailReason()
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    badRequest(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun getWayList(body: WayListBody) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context)
                .apiService(true).getWayList(body)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    badRequest(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun sendBreakDown(body: BreakdownBody) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(true).sendBreakDown(body)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body(), "${response.code()}"))
                }
                else -> {
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun sendFailure(body: FailureBody) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(true).sendFailure(body)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body(), "${response.code()}"))
                }
                else -> {
                    badRequest(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun getWayTask(wayId: Int, wayTaskBody: WayTaskBody) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context)
                .apiService(true).getWayTask(wayId, wayTaskBody)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    badRequest(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun progress(id: Int, body: ProgressBody) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context)
                .apiService(true).progress(id, body)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    badRequest(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun completeWay(id: Int, body: CompleteWayBody) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(true).complete(id, body)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    badRequest(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun earlyComplete(id: Int, body: EarlyCompleteBody) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(true).earlyComplete(id, body)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    badRequest(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun getCancelWayReason() = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(true).getCancelWayReason()
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    badRequest(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    suspend fun synchronizeData(body : SynchronizeBody): Resource<EmptyResponse> {
        return try {
            val response = RetrofitClient(context).apiService(true).synchro(body)
            when {
                response.isSuccessful -> {
                    Resource.success(response.body())
                }
                else -> {
                    badRequest(response)
                    Resource.error("Ошибка ${response.code()}", null)
                }
            }
        } catch (e: Exception) {
            Resource.network("Проблемы с подключением интернета", null)
        }
    }

    fun getOwners() = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitClient(context).apiService(false).getOwners()
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    emit(Resource.error("Ошибка ${response.code()}", null))
                    badRequest(response)
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

}

private fun <T> badRequest(response: Response<T>) {
    if (response.code() in 400..500) {
        Sentry.setTag("url", response.raw().request.url.encodedPath)
        Sentry.setTag("http_code", response.code().toString())
        Sentry.setTag("host", response.raw().request.url.host)
        Sentry.setTag("user", AppPreferences.userLogin)
        Sentry.captureException(BadRequestException(Gson().toJson(response.errorBody())))
    }
}


