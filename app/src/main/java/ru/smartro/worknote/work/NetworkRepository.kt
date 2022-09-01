package ru.smartro.worknote.work

import android.content.Context
import android.os.Build
import androidx.lifecycle.liveData
import com.google.gson.Gson
import io.realm.Realm
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import ru.smartro.worknote.*
import ru.smartro.worknote.awORKOLDs.service.database.entity.problem.BreakDownEntity
import ru.smartro.worknote.work.net.CancelWayReasonEntity
import ru.smartro.worknote.awORKOLDs.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.awORKOLDs.service.network.RetrofitClient
import ru.smartro.worknote.awORKOLDs.service.network.body.AuthBody
import ru.smartro.worknote.awORKOLDs.service.network.body.PingBody
import ru.smartro.worknote.awORKOLDs.service.network.body.ProgressBody
import ru.smartro.worknote.awORKOLDs.service.network.body.WayListBody
import ru.smartro.worknote.awORKOLDs.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.work.net.EarlyCompleteBody
import ru.smartro.worknote.awORKOLDs.service.network.body.synchro.SynchronizeBody
//import ru.smartro.worknote.awORKOLDs.service.network.exception.THR
import ru.smartro.worknote.awORKOLDs.service.network.response.EmptyResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.auth.AuthResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.breakdown.BreakDownResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.cancelation_reason.CancelationReasonResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.failure_reason.Data
import ru.smartro.worknote.awORKOLDs.service.network.response.failure_reason.FailureReasonResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.organisation.OrganisationResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.served.ServedResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.synchronize.SynchronizeResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.vehicle.VehicleResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.way_list.WayListResponse


class NetworkRepository(private val context: Context) {
    private val TAG: String = "NetworkRepository--AAA"

    protected fun paramS() =  App.getAppParaMS()
    

    fun auth(model: AuthBody) = liveData(Dispatchers.IO, TIME_OUT) {
        LOG.info( "auth")
        try {
            val response = RetrofitClient(context).apiService(false).auth(model)
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    THR.BadRequestLogin(response)
                    emit(Resource.error("Неверный логин или пароль", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    suspend fun getVehicle(organisationId: Int) =
        RetrofitClient(context).apiService(true).getVehicle(organisationId)
    fun getVehicleOld(organisationId: Int) = liveData(Dispatchers.IO, TIME_OUT) {
        LOG.info( "getVehicle.before")
        try {

            val response = RetrofitClient(context)

                .apiService(true).getVehicle(organisationId)
            log("getVehicle.after ${response.body()}")
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    THR.BadRequestVehicle(response)
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    log("getVehicle.after errorResponse=${errorResponse}")
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
        LOG.info( "getBreakDownTypes")
        try {
            val response = RetrofitClient(context)
                .apiService(true).getBreakDownTypes()
            log("getBreakDownTypes.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    insertBreakDown(response.body()?.data)
//                    emit(Resource.success(response.body()))
                }
                else -> {
                    THR.BadRequestBreakdown_type(response)
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    log("getBreakDownTypes.after errorResponse=${errorResponse}")
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
        LOG.info( "getFailReason.before")
        try {
            val response = RetrofitClient(context).apiService(true).getFailReason()
            log("getFailReason.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    insertFailReason(response.body()?.data)
//                    emit(Resource.success(response.body()))
                }
                else -> {
                    THR.BadRequestFailure_reason(response)
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    log("getFailReason.after errorResponse=${errorResponse}")
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
        LOG.info( "getCancelWayReason.before")
        try {
            val response = RetrofitClient(context).apiService(true).getCancelWayReason()
            when {
                response.isSuccessful -> {
                    log("getCancelWayReason.after ${response.body().toString()}")
                    insertCancelWayReason(response.body()?.data)
//                    emit(Resource.success(response.body()))
                }
                else -> {
                    THR.BadRequestWork_order_cancelation_reason(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (ex: Exception) {
            LOG.error("getCancelWayReason", ex)
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    suspend fun getWayList(body: WayListBody) = RetrofitClient(context).apiService(true).getWayList(body)


    suspend fun getWorkOrder(organisationId: Int, wayId: Int) =
        RetrofitClient(context).apiService(true).getWorkOrder(organisationId, wayId)


    fun progress(id: Int, body: ProgressBody) = liveData(Dispatchers.IO, TIME_OUT) {
        LOG.info( "progress.before id=${id} body=${body}")
        try {
            val response = RetrofitClient(context).apiService(true).progress(id, body)
            log("progress.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    THR.BadRequestProgress(response)
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    log("progress.after errorResponse=${errorResponse}")

                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            LOG.error("progress", e)
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun completeWay(id: Int, body: CompleteWayBody) = liveData(Dispatchers.IO, TIME_OUT) {
        LOG.info( "completeWay.before id=${id}, body=${body}")
        try {
            val response = RetrofitClient(context).apiService(true).complete(id, body)
            log("completeWay.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    THR.BadRequestWorkorder__id__complete(response)
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    log("completeWay.after errorResponse=${errorResponse}")

                    emit(Resource.error(errorResponse.message, null))
                }
            }
        } catch (e: Exception) {
            LOG.error("completeWay", e)
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    fun earlyComplete(id: Int, body: EarlyCompleteBody) = liveData(Dispatchers.IO, TIME_OUT) {
        LOG.info( "earlyComplete.before id={$id}, body={$body}")
        try {
            val response = RetrofitClient(context).apiService(true).earlyComplete(id, body)
            log("earlyComplete.after ${response.body().toString()}")
            val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
            log("earlyComplete.after errorResponse=${errorResponse}")
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    THR.BadRequestWorkorder__id__early_complete(response)
//                    App.getAppliCation().toast(result.msg)
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    suspend fun postSynchro(body: SynchronizeBody): Resource<SynchronizeResponse> {
        LOG.info( "synchronizeData.before")
        return try {
            val response = RetrofitClient(context).apiService(true).postSynchro(body)
            when {
                response.isSuccessful -> {
                    Resource.success(response.body())
                }
                else -> {
                    THR.BadRequestPOSTsynchro(response)
                    Resource.error("Ошибка ${response.code()}", null)
                }
            }
        } catch (ex: Exception) {
            LOG.error("postSynchro", ex)
            Resource.network("Проблемы с подключением интернета", null)
        }
    }

    fun sendLastPlatforms(body: SynchronizeBody) = liveData(Dispatchers.IO, TIME_OUT) {
        LOG.info( "sendLastPlatforms.before")

        try {
            val response = RetrofitClient(context).apiService(true).postSynchro(body)
            log("sendLastPlatforms.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    THR.BadRequestPOSTsynchro(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))

                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    suspend fun getOwners() = RetrofitClient(context).apiService(false).getOwners()
    fun getOwnersOld() = liveData(Dispatchers.IO, TIME_OUT) {
        LOG.info( "getOwners")

        try {
            val response = RetrofitClient(context).apiService(false).getOwners()
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    THR.BadRequestOwner(response)
                    emit(Resource.error("Ошибка ${response.code()}", null))

                }
            }
        } catch (e: Exception) {
            emit(Resource.network("Проблемы с подключением интернета", null))
        }
    }

    suspend fun ping(pingBody: PingBody): Resource<PingBody> {
        LOG.info( "test_ping.before")
        return try {
            val response = RetrofitClient(context).testApiService().ping(pingBody)
            when {
                response.isSuccessful -> {
                    Resource.success(response.body())
                }
                else -> {
                    THR.BadRequestPing(response)
                    Resource.error("Ошибка ${response.code()}", null)
                }
            }
        } catch (ex: Exception) {
//            LoG.error( ex)
            Resource.network("Проблемы с подключением интернета", null)
        }
    }


    suspend fun sendAppStartUp(): Resource<AppStartUpResponse> {
        val appStartUpBody = AppStartUpBody(
            deviceId = App.getAppliCation().getDeviceId(),
            appVersion = BuildConfig.VERSION_NAME
        )

        appStartUpBody.os = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Build.VERSION.CODENAME
        } else {
            Build.VERSION.SDK_INT.toString()
        }

        val rpcBody = RPCBody("app_startup", appStartUpBody)

        LOG.info("RPCBODY: ${rpcBody}")

        return try {
            val response = RetrofitClient(context).testApiService().sendAppStartUp(rpcBody)
            LOG.info("RESPONSE.isSuccessful: ${response.isSuccessful}, responseBody: ${response.body()}")
            when {
                response.isSuccessful -> {
                    Resource.success(response.body()?.payload)
                }
                else -> {
                    THR.BadRequestAppStartUp(response)
                    Resource.error("Ошибка ${response.code()}", null)
                }
            }
        } catch (ex: Exception) {
//            LoG.error( ex)
            Resource.network("Проблемы с подключением интернета", null)
        }
    }

    suspend fun sendAppEvent(rpcBody: RPCBody<AppEventBody>) = RetrofitClient(context).testApiService().sendAppEvent(rpcBody)

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

sealed class THR(code: Int) : Throwable(code.toString()) {
    //    abstract val message: String
    fun <T> sentToSentry(response: Response<T>){
        if (response.code() in 400..599) {
            val urlName = response.raw().request.url.encodedPath
            Sentry.setTag("url_name", urlName)
            Sentry.setTag("http_code", response.code().toString())
            Sentry.setTag("url_host_name", response.raw().request.url.host)

            Sentry.setTag("user", App.getAppParaMS().userName)
            // TODO: replace  BadRequestException for post  @POST("synchro")
//        Sentry.captureException(BadRequestException(Gson().toJson(response.errorBody())))
            Sentry.captureException(this)
        }
    }


    class BadRequestLogin(response: Response<AuthResponse>) : THR(response.code()) {
        //        override val message = 70.0
        init {
            sentToSentry(response)
        }

    }

    class BadRequestOwner(response: Response<OrganisationResponse>) : THR(response.code()) {
        
        init {
            sentToSentry(response)
        }

    }

    class BadRequestVehicle(response: Response<VehicleResponse>) : THR(response.code()) {
        
        init {
            sentToSentry(response)
        }

    }
    class BadRequestBreakdown_type(response: Response<BreakDownResponse>) : THR(response.code()) {
        
        init {
            sentToSentry(response)
        }

    }
    class BadRequestFailure_reason(response: Response<FailureReasonResponse>) : THR(response.code()) {
        
        init {
            sentToSentry(response)
        }

    }

    class BadRequestWaybill(response: Response<WayListResponse>) : THR(response.code()) {
        
        init {
            sentToSentry(response)
        }

    }
//    class breakdown(response: String) : THR(response.code()) {
//
//        init {
//            sentToSentry(response)
//        }
//
//    }
//    class failure(response: String) : THR(response.code()) {
//
//        init {
//            sentToSentry(response)
//        }
//
//    }
    class BadRequestProgress(response: Response<ServedResponse>) : THR(response.code()) {
        
        init {
            sentToSentry(response)
        }

    }
    class BadRequestWorkorder__id__complete(response: Response<EmptyResponse>) : THR(response.code()) {
        
        init {
            sentToSentry(response)
        }

    }

    class BadRequestWork_order_cancelation_reason(response: Response<CancelationReasonResponse>) : THR(response.code()) {
        
        init {
            sentToSentry(response)
        }

    }
    class BadRequestWorkorder__id__early_complete(response: Response<EmptyResponse>) : THR(response.code()) {
        
        init {
            sentToSentry(response)
        }

    }
    class BadRequestPOSTsynchro(response: Response<SynchronizeResponse>) : THR(response.code()) {
        
        init {
            sentToSentry(response)
        }

    }

    class BadRequestPing(response: Response<PingBody>) : THR(response.code()) {
        
        init {
            sentToSentry(response)
        }

    }

    class BadRequestAppStartUp(response: Response<RPCBody<AppStartUpResponse>>) : THR(response.code()) {

        init {
            sentToSentry(response)
        }

    }

    class BadRequestAppEvent(response: Response<RPCBody<AppEventResponse>>) : THR(response.code()) {

        init {
            sentToSentry(response)
        }

    }

//    BadRequestSynchro__o_id__w_id
    class BadRequestSynchro__o_id__w_id(response: Response<WorkOrderResponse_know1>) : THR(response.code()) {

        init {
            sentToSentry(response)
        }

    }

}


