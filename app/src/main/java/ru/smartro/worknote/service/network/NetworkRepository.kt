package ru.smartro.worknote.service.network

import android.content.Context
import android.util.Log
import androidx.lifecycle.liveData
import com.google.gson.Gson
import io.realm.Realm
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.network.body.AuthBody
import ru.smartro.worknote.service.network.body.ProgressBody
import ru.smartro.worknote.service.network.body.WayListBody
import ru.smartro.worknote.service.network.body.breakdown.BreakdownBody
import ru.smartro.worknote.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.service.network.body.early_complete.EarlyCompleteBody
import ru.smartro.worknote.service.network.body.failure.FailureBody
import ru.smartro.worknote.service.network.body.served.ServiceResultBody
import ru.smartro.worknote.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.service.network.exception.BadRequestException
import ru.smartro.worknote.service.network.response.EmptyResponse
import ru.smartro.worknote.service.network.response.synchronize.SynchronizeResponse
import ru.smartro.worknote.work.RealmRepository
import ru.smartro.worknote.service.database.entity.problem.BreakDownEntity
import ru.smartro.worknote.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.service.network.response.failure_reason.Data


class NetworkRepository(private val context: Context) {
    private val TAG: String = "NetworkRepository--AAA"

    fun auth(model: AuthBody) = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "auth")
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

    fun served(body: ServiceResultBody) = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "served")
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

    fun getVehicle(organisationId: Int) = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "getVehicle.before")
        try {
            val response = RetrofitClient(context)
                .apiService(true).getVehicle(organisationId)
            Log.d(TAG, "getVehicle.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    badRequest(response)
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    Log.d(TAG, "getVehicle.after errorResponse=${errorResponse}")
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    private fun insertBreakDown(data: List<ru.smartro.worknote.service.network.response.breakdown.Data>?) {
        val db = RealmRepository(Realm.getDefaultInstance())
        val entities = data?.filter {
            it.attributes.organisationId == AppPreferences.organisationId
        }?.map {
            BreakDownEntity(it.attributes.id, it.attributes.name)
        }
        db.insertBreakDown(entities!!)
    }

    fun getBreakDownTypes() = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "getBreakDownTypes")
        try {
            val response = RetrofitClient(context)
                .apiService(true).getBreakDownTypes()
            Log.d(TAG, "getBreakDownTypes.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    insertBreakDown(response.body()?.data)
//                    emit(Resource.success(response.body()))
                }
                else -> {
                    badRequest(response)
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    Log.d(TAG, "getBreakDownTypes.after errorResponse=${errorResponse}")
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    private fun insertFailReason(data: List<Data>?) {
        val db = RealmRepository(Realm.getDefaultInstance())

        val entities = data?.filter {
            it.oid == AppPreferences.organisationId
        }!!.map {
            FailReasonEntity(it.id, it.name)
        }
        db.insertFailReason(entities)
    }

    fun getFailReason() = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "getFailReason.before")
        try {
            val response = RetrofitClient(context).apiService(true).getFailReason()
            Log.d(TAG, "getFailReason.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    insertFailReason(response.body()?.data)
//                    emit(Resource.success(response.body()))
                }
                else -> {
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    Log.d(TAG, "getFailReason.after errorResponse=${errorResponse}")
                    badRequest(response)

                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    private fun insertCancelWayReason(data: List<ru.smartro.worknote.service.network.response.cancelation_reason.Data>?) {
        val db = RealmRepository(Realm.getDefaultInstance())

        val entities = data?.filter {
            it.attributes.organisationId == AppPreferences.organisationId
        }!!.map { CancelWayReasonEntity(it.id, it.attributes.name) }

        db.insertCancelWayReason(entities)
    }

    fun getCancelWayReason() = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "getCancelWayReason.before")
        try {
            val response = RetrofitClient(context).apiService(true).getCancelWayReason()
            when {
                response.isSuccessful -> {
                    Log.d(TAG, "getCancelWayReason.after ${response.body().toString()}")
                    insertCancelWayReason(response.body()?.data)
//                    emit(Resource.success(response.body()))
                }
                else -> {
                    badRequest(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "getCancelWayReason.after", ex)
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun getWayList(body: WayListBody) = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "getWayList.before")

        try {
            val response = RetrofitClient(context)
                .apiService(true).getWayList(body)
            when {
                response.isSuccessful -> {
                    Log.d(TAG, "getWayList.after ${response.body().toString()}")
                    emit(Resource.success(response.body()))
                }
                else -> {
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    Log.d(TAG, "getWayList.after errorResponse=${errorResponse}")
                    badRequest(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun sendBreakDown(body: BreakdownBody) = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "sendBreakDownsendBreakDownsendBreakDown")

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

    fun sendFailure(body: FailureBody) = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "sendFailuresendFailuresendFailure")

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
/*
    fun getWayTask(wayId: Int, wayTaskBody: WayTaskBody) = liveData(Dispatchers.IO, TIME_OUT) {
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
    }*/

    fun getWorkOder(organisationId: Int, wayId: Int) = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "getWorkOder.before")

        try {
            val response = RetrofitClient(context)
                .apiService(true).getWorkOrder(organisationId, wayId)
            Log.d(TAG, "getWorkOder.after ${response.body().toString()}")
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

    fun progress(id: Int, body: ProgressBody): Resource<EmptyResponse> {
        Log.i(TAG, "progress.before id=${id} body=${body}")
        try {
            val response = RetrofitClient(context).apiService(true).progress(id, body).execute()
            Log.d(TAG, "progress.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    return Resource.success(response.body())
                }
                else -> {
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    Log.d(TAG, "progress.after errorResponse=${errorResponse}")
                    badRequest(response)
                    return Resource.error("Ошибка ${response.code()}", null)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "progress.after", e)
            return (Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun completeWay(id: Int, body: CompleteWayBody) = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "completeWay.before id=${id}, body=${body}")
        try {
            val response = RetrofitClient(context).apiService(true).complete(id, body)
            Log.d(TAG, "completeWay.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    Log.d(TAG, "completeWay.after errorResponse=${errorResponse}")
                    badRequest(response)
                    emit(Resource.error(errorResponse.message, null))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "completeWay.after", e)
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun earlyComplete(id: Int, body: EarlyCompleteBody) = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "earlyComplete.before id={$id}, body={$body}")
        try {
            val response = RetrofitClient(context).apiService(true).earlyComplete(id, body)
            Log.d(TAG, "earlyComplete.after ${response.body().toString()}")
            val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
            Log.d(TAG, "earlyComplete.after errorResponse=${errorResponse}")
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

    suspend fun synchronizeData(body: SynchronizeBody): Resource<SynchronizeResponse> {
        Log.i(TAG, "synchronizeData.before")
        return try {
            val response = RetrofitClient(context).apiService(true).postSynchro(body)
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

    fun sendLastPlatforms(body: SynchronizeBody) = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "sendLastPlatforms.before")

        try {
            val response = RetrofitClient(context).apiService(true).postSynchro(body)
            Log.d(TAG, "sendLastPlatforms.after ${response.body().toString()}")
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

    fun getOwners() = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "getOwners")

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
    if (response.code() in 400..599) {
        Sentry.setTag("url", response.raw().request.url.encodedPath)
        Sentry.setTag("http_code", response.code().toString())
        Sentry.setTag("host", response.raw().request.url.host)
        Sentry.setTag("user", AppPreferences.userLogin)
        Sentry.captureException(BadRequestException(Gson().toJson(response.errorBody())))
    }
}


