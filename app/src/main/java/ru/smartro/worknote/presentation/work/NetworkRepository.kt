package ru.smartro.worknote.presentation.work

//import ru.smartro.worknote.awORKOLDs.service.network.exception.THR

import android.content.Context
import android.os.Build
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import okhttp3.Callback
import ru.smartro.worknote.App
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.LOG
import ru.smartro.worknote.TIME_OUT
import ru.smartro.worknote.awORKOLDs.PostExample
import ru.smartro.worknote.awORKOLDs.RetrofitClient
import ru.smartro.worknote.awORKOLDs.service.database.entity.problem.BreakDownReasonEntity
import ru.smartro.worknote.awORKOLDs.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.awORKOLDs.service.network.body.PingBody
import ru.smartro.worknote.awORKOLDs.service.network.body.ProgressBody
import ru.smartro.worknote.awORKOLDs.service.network.body.WayListBody
import ru.smartro.worknote.awORKOLDs.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.awORKOLDs.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.awORKOLDs.EmptyResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.failure_reason.Data
import ru.smartro.worknote.awORKOLDs.service.network.response.synchronize.SynchronizeResponse
import ru.smartro.worknote.awORKOLDs.util.THR
import ru.smartro.worknote.presentation.work.net.CancelWayReasonEntity
import ru.smartro.worknote.presentation.work.net.EarlyCompleteBody
import java.io.File
import java.io.FileOutputStream


class NetworkRepository(private val context: Context) {
    private val TAG: String = "NetworkRepository--AAA"

    protected fun paramS() =  App.getAppParaMS()
    

//    fun auth(model: AuthBody) = liveData(Dispatchers.IO, TIME_OUT) {
//        LOG.info( "auth")
//        try {
//            val response = RetrofitClient(context).apiService(false).auth(model)
//            when {
//                response.isSuccessful -> {
//                    emit(Resource.success(response.body()))
//                }
//                else -> {
//                    THR.BadRequestLogin(response)
//                    emit(Resource.error("Неверный логин или пароль", null))
//                }
//            }
//        } catch (e: Exception) {
//            emit(Resource.network("Проблемы с подключением интернета", null))
//        }
//    }

//    suspend fun getVehicle(organisationId: Int) =
//        RetrofitClient(context).apiService(true).getVehicle(organisationId)
//    fun getVehicleOld(organisationId: Int) = liveData(Dispatchers.IO, TIME_OUT) {
//        LOG.info( "getVehicle.before")
//        try {
//
//            val response = RetrofitClient(context)
//
//                .apiService(true).getVehicle(organisationId)
//            LOG.debug("getVehicle.after ${response.body()}")
//            when {
//                response.isSuccessful -> {
//                    emit(Resource.success(response.body()))
//                }
//                else -> {
//                    THR.BadRequestVehicle(response)
//                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
//                    LOG.debug("getVehicle.after errorResponse=${errorResponse}")
//                    emit(Resource.error("Ошибка ${response.code()}", null))
//                }
//            }
//        } catch (e: Exception) {
//            emit(Resource.network("Проблемы с подключением интернета", null))
//        }
//    }


    private fun insertBreakDown(data: List<ru.smartro.worknote.awORKOLDs.service.network.response.breakdown.Data>?) {
        val db = RealmRepository(Realm.getDefaultInstance())
        val entities = data?.filter {
            it.attributes.organisationId == paramS().ownerId
        }?.map {
            BreakDownReasonEntity(it.attributes.id, it.attributes.name)
        }
        db.insertBreakDown(entities!!)
    }

    fun getBreakDownTypes() = liveData(Dispatchers.IO, TIME_OUT) {
        LOG.info("getBreakDownTypes")
        try {
            val response = RetrofitClient(context)
                .apiService(true).getBreakDownTypes()
            LOG.debug("getBreakDownTypes.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    LOG.debug("getBreakDownTypes.after SUCCESSFUL")
                    insertBreakDown(response.body()?.data)
//                    emit(Resource.success(response.body()))
                }
                else -> {
                    THR.BadRequestBreakdown_type(response)
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    LOG.debug("getBreakDownTypes.after errorResponse=${errorResponse}")
                    emit(Resource.error("Ошибка ${response.code()}", null))
                }
            }
        } catch (e: Exception) {
            LOG.debug("getBreakDownTypes.after EXCEPTION")
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
            LOG.debug("getFailReason.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    insertFailReason(response.body()?.data)
//                    emit(Resource.success(response.body()))
                }
                else -> {
                    THR.BadRequestFailure_reason(response)
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    LOG.debug("getFailReason.after errorResponse=${errorResponse}")
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
                    LOG.debug("getCancelWayReason.after ${response.body().toString()}")
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
            LOG.debug("progress.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    THR.BadRequestProgress(response)
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    LOG.debug("progress.after errorResponse=${errorResponse}")

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
            LOG.debug("completeWay.after ${response.body().toString()}")
            when {
                response.isSuccessful -> {
                    emit(Resource.success(response.body()))
                }
                else -> {
                    THR.BadRequestWorkorder__id__complete(response)
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
                    LOG.debug("completeWay.after errorResponse=${errorResponse}")

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
            LOG.debug("earlyComplete.after ${response.body().toString()}")
            val errorResponse = Gson().fromJson(response.errorBody()?.string(), EmptyResponse::class.java)
            LOG.debug("earlyComplete.after errorResponse=${errorResponse}")
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

    fun saveJSON(bodyInStringFormat: String, p_jsonName: String) {
        fun getOutputDirectory(platformUuid: String, containerUuid: String?): File {
            var dirPath = App.getAppliCation().dataDir.absolutePath
            if(containerUuid == null) {
                dirPath = dirPath + File.separator + platformUuid
            } else {
                dirPath = dirPath + File.separator + platformUuid + File.separator + containerUuid
            }

            val file = File(dirPath)
            if (!file.exists()) file.mkdirs()
            return file
        }
        val file: File = File(getOutputDirectory("saveJSON", null), "${p_jsonName}.json")
        try {
            file.delete()
        } catch (ex: Exception) {
            LOG.error("file.delete()", ex)
        }

        //This point and below is responsible for the write operation

        //This point and below is responsible for the write operation
        var outputStream: FileOutputStream? = null
        try {

            file.createNewFile()
            //second argument of FileOutputStream constructor indicates whether
            //to append or create new file if one exists
            outputStream = FileOutputStream(file, true)
            outputStream.write(bodyInStringFormat.toByteArray())
            outputStream.flush()
            outputStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun sendLastPlatforms(body: SynchronizeBody, callBack: Callback) {
        LOG.info( "sendLastPlatforms.before")

        val builder = GsonBuilder()
        builder.excludeFieldsWithoutExposeAnnotation()
        val gson = builder.create()
        val bodyInStringFormat = gson.toJson(body)
        saveJSON(bodyInStringFormat, "sendLastPlatforms")
        
        val example = PostExample()
        val URL = BuildConfig.URL__SMARTRO + "fact/synchro"
        example.post(URL, bodyInStringFormat, callBack)
    }


    suspend fun postSynchro(body: SynchronizeBody): Resource<SynchronizeResponse> {
        LOG.info( "synchronizeData.before")

        val builder = GsonBuilder()
        builder.excludeFieldsWithoutExposeAnnotation()
        val gson = builder.create()
        val bodyInStringFormat = gson.toJson(body)
        saveJSON(bodyInStringFormat, "postSynchro2")

        var result: Resource<SynchronizeResponse> = Resource.success(null)

        try {
            val example = PostExample()
            val URL = BuildConfig.URL__SMARTRO + "fact/synchro"
            val response = example.post(URL, bodyInStringFormat)
            val str = "${response.body!!.string()}"
            LOG.info("str=${str}}")
            if (response.isSuccessful) {
                val synchronizeResponse = gson.fromJson(str, SynchronizeResponse::class.java)
                result = Resource.success(synchronizeResponse)
            } else {
                THR.BadRequestPOSTsynchroOKHTTP(response)
                result = Resource.error("Ошибка ${response.code}", null)
            }

//            val response2 = RetrofitClient(context).apiService(true).postSynchro(body)
//            if (response2.isSuccessful) {
////                val synchronizeResponse = gson.fromJson(response2.body(), SynchronizeResponse::class.java)
//                Resource.success(response2)
//            } else {
//                THR.BadRequestPOSTsynchro(response2)
//                Resource.error("Ошибка ${response2.code()}", null)
//            }

        } catch (ex: Exception) {
            LOG.error("postSynchro", ex)
            result = Resource.network("Проблемы с подключением интернета", null)
        }
        LOG.info("postSynchro.result=${result.status}")
        return result
    }

//    suspend fun getOwners() = RetrofitClient(context).apiService(false).getOwners()
//    fun getOwnersOld() = liveData(Dispatchers.IO, TIME_OUT) {
//        LOG.info( "getOwners")
//
//        try {
//            val response = RetrofitClient(context).apiService(false).getOwners()
//            when {
//                response.isSuccessful -> {
//                    emit(Resource.success(response.body()))
//                }
//                else -> {
//                    THR.BadRequestOwner(response)
//                    emit(Resource.error("Ошибка ${response.code()}", null))
//
//                }
//            }
//        } catch (e: Exception) {
//            emit(Resource.network("Проблемы с подключением интернета", null))
//        }
//    }

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

