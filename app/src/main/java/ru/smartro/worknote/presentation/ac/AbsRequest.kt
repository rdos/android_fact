package ru.smartro.worknote.presentation.ac

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.abs.RequestAI
import ru.smartro.worknote.log.RestConnectionResource
import ru.smartro.worknote.presentation.ANoBodyGET
import ru.smartro.worknote.presentation.RPOSTSynchro
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.reflect.KClass


//Tin Toup
abstract class AbsRequest<TA: NetObject, TB : NetObject>: AbsObject(), RequestAI {

    private var mRESTconnectionMutableLiveData: MutableLiveData<RestConnectionResource<TB>>? = null
    open var isHandledError: Boolean = false

    final override fun getTAGObject(): String {
    //        TODO("Not yet implemented")
        return TAGObj
    }

    private var mResponseString: String? = null
    private var mQueryParamMap: HashMap<String, String> = HashMap()
    private var mGson: Gson? = null
    private val mMediaType: MediaType = "application/json; charset=utf-8".toMediaType()

    protected open fun onGetURL(): String {
        return BuildConfig.URL__SMARTRO
    }

    protected open fun onGetMultipartBody(): MultipartBody? = null
    private fun isMultipartBody() = onGetMultipartBody() != null

    abstract fun onGetSRVName(): String
    abstract fun onGetRequestBodyIn(): TA
    abstract fun onSetQueryParameter(queryParamMap: HashMap<String, String>)
    abstract fun onBefore()
    abstract fun onAfter(bodyOut: TB)
    abstract fun onGetResponseClazz(): KClass<TB>

//    abstract fun onGetResponseClazz(): Class<AuthResponse>

    private fun getGson(): Gson {
        if (mGson == null) {
            val builder = GsonBuilder()
            builder.excludeFieldsWithoutExposeAnnotation()
            mGson = builder.create()
        }
        return mGson!!
    }

    final override fun getOKHTTPRequest(): Request {
        LOG.debug("before")
        var url = this.onGetURL() + this.onGetSRVName()

        onSetQueryParameter(mQueryParamMap)
        if (mQueryParamMap.size > 0) {
            val urlBuilder: HttpUrl.Builder = url.toHttpUrlOrNull()!!.newBuilder()
            for(queryParam in mQueryParamMap) {
                urlBuilder.addQueryParameter(queryParam.key, queryParam.value)
            }
            url = urlBuilder.build().toString()
        }
        LOG.info("url=${url}")

        val headerAuthorization = "Bearer " + App.getAppParaMS().token
        LOG.warn("headerAuthorization=${headerAuthorization}")

        val requestBuilder =  Request.Builder()
        requestBuilder.url(url)
        requestBuilder.addHeader("Authorization", headerAuthorization)

        val netObject = onGetRequestBodyIn()
        if (netObject is ANoBodyGET) {
            return requestBuilder.build()
        }

        val body: RequestBody
        if(isMultipartBody()) {
            body = onGetMultipartBody() as RequestBody
        } else {
            val bodyInStringFormat = getGson().toJson(netObject)
            if (this !is RPOSTSynchro) {
                LOG.debug("bodyInStringFormat=${bodyInStringFormat}")
            }

            if (bodyInStringFormat.isNotNull()) {
                saveJSON(bodyInStringFormat!!, "req-${TAGObj}")
            }
            body = RequestBody.create(mMediaType, bodyInStringFormat)
        }
        requestBuilder.post(body)
        return requestBuilder.build()
    }



    fun onFailure(call: Call, e: IOException, messageShowForUser: String? = null) {
        LOG.error("onFailure", e)
        val message = "onFailure HTTP EXCEPTION ::: ${this::class.java.simpleName} ::: ${e.message}"
        val mMessageShowForUser = messageShowForUser ?: "Произошла ошибка запроса, повторите снова"
        if(isHandledError) {
            App.getAppliCation().sentryCaptureErrorMessage(message)

            mRESTconnectionMutableLiveData?.let {
                LOG.todo("getLiveDate.before")
                it.postValue(RestConnectionResource.Error(Pair(-1, mMessageShowForUser)))
                LOG.info("getLiveDate.after")
            }
        } else {
            App.getAppliCation().sentryCaptureErrorMessage(message, mMessageShowForUser)
        }
    }

    fun getLiveDate() : LiveData<RestConnectionResource<TB>> {
        if (mRESTconnectionMutableLiveData == null) {
            mRESTconnectionMutableLiveData = MutableLiveData()
        }
        return mRESTconnectionMutableLiveData!!
    }

    fun onResponse(call: Call, response: Response) {
        val responseCode = response.code
        mResponseString = response.body?.string()

        LOG.info("response.code=${responseCode}")
        LOG.info("mResponseString=${mResponseString}")

        if (isFindError(response)) {
            val message = "isFindError ::: code: ${responseCode}, body: ${mResponseString}"
            val messageShowForUser = mResponseString ?: "Ошибка запроса: ${responseCode} ${response.message}"
            if(isHandledError) {
                App.getAppliCation().sentryCaptureErrorMessage(message)
                mRESTconnectionMutableLiveData?.let {
                    LOG.info("getLiveDate.before")
                    it.postValue(RestConnectionResource.Error(Pair(responseCode, messageShowForUser)))
                    LOG.info("getLiveDate.after")
                }
            } else {
                App.getAppliCation().sentryCaptureErrorMessage(message, messageShowForUser)
            }
            return
        }

        if (mResponseString.isNotNull()) {
            saveJSON(mResponseString!!, "resp-${TAGObj}")
        }

        val responseObj = getGson().fromJson(mResponseString, this.onGetResponseClazz().java)
        LOG.info("onAfter.before")
        this.onAfter(responseObj)
        LOG.info("onAfter.after")

        mRESTconnectionMutableLiveData?.let {
            LOG.info("getLiveDate.before")
            it.postValue(RestConnectionResource.SuccessData<TB>(responseObj))
            LOG.info("getLiveDate.after")
        }
    }

    private fun isFindError(response: Response): Boolean {
        var result = false
        if (response.code < 300) {
            return result
        }

        result = true
        return result
    }

    private fun saveJSON(bodyInStringFormat: String, p_jsonName: String) {
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
}