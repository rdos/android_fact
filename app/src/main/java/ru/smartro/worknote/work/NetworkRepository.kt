package ru.smartro.worknote.awORKOLDs.service.network

import android.content.Context
import android.util.Log
import androidx.lifecycle.liveData
import com.google.gson.Gson
import io.realm.Realm
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import ru.smartro.worknote.App
import ru.smartro.worknote.TIME_OUT
import ru.smartro.worknote.awORKOLDs.service.database.entity.problem.BreakDownEntity
import ru.smartro.worknote.work.net.CancelWayReasonEntity
import ru.smartro.worknote.awORKOLDs.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.awORKOLDs.service.network.body.AuthBody
import ru.smartro.worknote.awORKOLDs.service.network.body.PingBody
import ru.smartro.worknote.awORKOLDs.service.network.body.ProgressBody
import ru.smartro.worknote.awORKOLDs.service.network.body.WayListBody
import ru.smartro.worknote.awORKOLDs.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.work.net.EarlyCompleteBody
import ru.smartro.worknote.awORKOLDs.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.awORKOLDs.service.network.exception.BadRequestException
import ru.smartro.worknote.awORKOLDs.service.network.response.EmptyResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.failure_reason.Data
import ru.smartro.worknote.awORKOLDs.service.network.response.synchronize.SynchronizeResponse
import ru.smartro.worknote.work.RealmRepository


class NetworkRepository(private val context: Context) {
    private val TAG: String = "NetworkRepository--AAA"

    protected fun paramS() =  App.getAppParaMS()

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

    private fun insertBreakDown(data: List<ru.smartro.worknote.awORKOLDs.service.network.response.breakdown.Data>?) {
        val db = RealmRepository(Realm.getDefaultInstance())
        val entities = data?.filter {
            it.attributes.organisationId == paramS().ownerId
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
            it.oid == paramS().ownerId
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

    private fun insertCancelWayReason(data: List<ru.smartro.worknote.awORKOLDs.service.network.response.cancelation_reason.Data>?) {
        val db = RealmRepository(Realm.getDefaultInstance())

        val entities = data?.filter {
            it.attributes.organisationId == paramS().ownerId
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


    fun getWorkOrder(organisationId: Int, wayId: Int) = liveData(Dispatchers.IO, TIME_OUT) {
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

    fun progress(id: Int, body: ProgressBody) = liveData(Dispatchers.IO, TIME_OUT) {
        Log.i(TAG, "progress.before id=${id} body=${body}")
        try {
            val response = RetrofitClient(context).apiService(true).progress(id, body)
            Log.d(TAG, "progress.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    Log.d(TAG, "progress.after errorResponse=${errorResponse}")
                    badRequest(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "progress.after", e)
            emit(Resource.network("Проблемы с подключением интернета", null))
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

    suspend fun postSynchro(body: SynchronizeBody): Resource<SynchronizeResponse> {
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
        } catch (ex: Exception) {
            Log.e(TAG, "synchronizeData", ex)
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

    suspend fun ping(pingBody: PingBody): Resource<PingBody> {
        Log.i(TAG, "test_ping.before")
        return try {
            val response = RetrofitClient(context).testApiService().ping(pingBody)
            when {
                response.isSuccessful -> {
                    Resource.success(response.body())
                }
                else -> {
                    badRequest(response)
                    Resource.error("Ошибка ${response.code()}", null)
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "test_ping", ex)
            Resource.network("Проблемы с подключением интернета", null)
        }
    }


}

data class Resource<out T>(val status: Status, val data: T?, val msg: String?) {
    companion object {
        fun <T> success(data: T?, msg: String = ""): Resource<T> {
            return Resource(Status.SUCCESS, data, msg)
        }
        fun <T> error(msg: String = "", data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }
        fun <T> network(msg: String = "", data: T? = null): Resource<T> {
            return Resource(Status.NETWORK, data, msg)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    NETWORK,
}


private fun <T> badRequest(response: Response<T>) {
    if (response.code() in 400..599) {
        val urlName = response.raw().request.url.encodedPath
        Sentry.setTag("url_name", urlName)
        Sentry.setTag("http_code", response.code().toString())
        Sentry.setTag("url_host_name", response.raw().request.url.host)

//        Sentry.setTag("user", AppPreferences.BoTlogin)
        // TODO: replace  BadRequestException for post  @POST("synchro")
//        Sentry.captureException(BadRequestException(Gson().toJson(response.errorBody())))
        Sentry.captureException(BadRequestException(urlName))
    }
}


