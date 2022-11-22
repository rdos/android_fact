package ru.smartro.worknote.awORKOLDs

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
import ru.smartro.worknote.awORKOLDs.service.NetObject
import ru.smartro.worknote.awORKOLDs.util.RESTconnection
import java.io.IOException
import kotlin.reflect.KClass


//Tin Toup
abstract class AbsRequest<TA:NetObject, TB : NetObject>: AbsObject(), RequestAI {
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
        if (netObject is NoBody) {
            return requestBuilder.build()
        }

        val bodyInStringFormat = getGson().toJson(netObject)
        LOG.debug("bodyInStringFormat=${bodyInStringFormat}")
        val body: RequestBody = RequestBody.create(mMediaType, bodyInStringFormat)
        requestBuilder.post(body)
        return requestBuilder.build()
    }



    override fun onFailure(call: Call, e: IOException) {
        LOG.error("onResponse", e)

    }


    fun getLiveDate() : LiveData<RESTconnection> {
        if (mRESTconnectionMutableLiveData == null) {
            mRESTconnectionMutableLiveData = MutableLiveData()
        }
        return mRESTconnectionMutableLiveData!!
    }

    private var mRESTconnectionMutableLiveData: MutableLiveData<RESTconnection>? = null
    override fun onResponse(call: Call, response: Response) {
        LOG.info("onResponse")
        if(response.code == 401) {
//            val charset = Charsets.UTF_8
//val byteArray = "Hello".toByteArray(charset)
//println(byteArray.contentToString()) // [72, 101, 108, 108, 111]
//println(byteArray.toString(charset)) // Hello
            return
        }
//        TypeToken.get(onGetResponseClazz())
        val connectionREVERS = RESTconnection()
        connectionREVERS.isSent = true
        mResponseString = response.body?.string()

        LOG.debug("TEST ::::: RESP BODY : ${mResponseString}")
//        val test = getGson().fromJson(mResponseString, (NetObject() as TB)::class.java)

        this::class.java.methods.forEach{
            if (it.name == "onAfter"){
                if (it.genericParameterTypes.size > 0) {
                    LOG.debug("${it.name}")
                }
                val param: Class<*> = it.parameterTypes.get(0)

                LOG.debug("${param::javaClass.name}")
            }

        }

        val responseObj = getGson().fromJson(mResponseString, this.onGetResponseClazz().java)
        this.onAfter(responseObj)
        mRESTconnectionMutableLiveData?.postValue(connectionREVERS)
    }
}