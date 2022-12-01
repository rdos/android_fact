package ru.smartro.worknote.presentation.ac

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import ru.smartro.worknote.App
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.LOG
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.abs.RequestAI
import ru.smartro.worknote.log.RESTconnection
import ru.smartro.worknote.presentation.ANoBodyGET
import java.io.IOException
import kotlin.reflect.KClass


//Tin Toup
abstract class AbsRequest<TA: NetObject, TB : NetObject>: AbsObject(), RequestAI {
    private var mRESTconnectionMutableLiveData: MutableLiveData<RESTconnection>? = null
    private var mRESTconnection: RESTconnection? = null


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

        val bodyInStringFormat = getGson().toJson(netObject)
        LOG.debug("bodyInStringFormat=${bodyInStringFormat}")
        val body: RequestBody = RequestBody.create(mMediaType, bodyInStringFormat)
        requestBuilder.post(body)
        return requestBuilder.build()
    }



    fun onFailure(call: Call, e: IOException, messageShowForUser: String? = null) {
        LOG.error("onResponse", e)
        val message = "onFailure HTTP EXCEPTION ::: ${this::class.java.simpleName} ::: ${e.message}"
        App.getAppliCation().sentryCaptureErrorMessage(message, messageShowForUser)
    }


    protected open fun getRESTconnection(): RESTconnection {
        LOG.warn("DON'T_USE")
        if (mRESTconnection == null) {
            mRESTconnection = RESTconnection()
        }
        return mRESTconnection!!
    }

    fun getLiveDate() : LiveData<RESTconnection> {
        if (mRESTconnectionMutableLiveData == null) {
            mRESTconnectionMutableLiveData = MutableLiveData()
        }
        return mRESTconnectionMutableLiveData!!
    }


    fun onResponse(call: Call, response: Response) {
        LOG.info("response.code=${response.code}")
        if (isFindError(response)) {
            return
        }

        val connectionREVERS = this.getRESTconnection()
        connectionREVERS.isSent = true
        mResponseString = response.body?.string()
        LOG.info("mResponseString=${mResponseString}")


        val responseObj = getGson().fromJson(mResponseString, this.onGetResponseClazz().java)
        LOG.info("onAfter.before")
        this.onAfter(responseObj)
        LOG.info("onAfter.after")


        mRESTconnectionMutableLiveData?.let {
            LOG.info("getLiveDate.before")
            it.postValue(connectionREVERS)
            LOG.info("getLiveDate.after")
        }
    }

    private fun isFindError(response: Response): Boolean {
        var result = false
        if (response.code < 300) {
            return result
        }

        val messageShowForUser = "Ошибка запроса: ${response.code} ::: ${this::class.java.simpleName}"
        val message = "HTTP ${response.code} ::: ${this::class.java.simpleName}"
        App.getAppliCation().sentryCaptureErrorMessage(message, messageShowForUser)

        LOG.error("isFindError")
        result = true
        return result
    }

}