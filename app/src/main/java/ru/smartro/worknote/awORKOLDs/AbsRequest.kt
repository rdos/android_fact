package ru.smartro.worknote.awORKOLDs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
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
    private var mGson: Gson? = null
    private val URL = BuildConfig.URL__AUTH
    private val mMediaType: MediaType = "application/json; charset=utf-8".toMediaType()

    abstract fun onGetRequestBodyIn(): TA
    abstract fun onBefore()
    abstract fun onAfter(bodyOut: TB)
    abstract fun onGetResponseClazz(): KClass<TB>

    abstract fun onGetSRVName(): String
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
        val netObject = onGetRequestBodyIn()
//        val authBodyIn = AuthBodyIn(email="asd", password = "asdf")
        val bodyInStringFormat = getGson().toJson(netObject)
        LOG.info("my body::${bodyInStringFormat}")
        val body: RequestBody = RequestBody.create(mMediaType, bodyInStringFormat)

        val url = URL + this.onGetSRVName()
        LOG.warn("my url::${url}")
        val headerAuthorization = "Bearer " + App.getAppParaMS().token
        val request: Request = Request.Builder()
            .url(url)
            .addHeader("Authorization", headerAuthorization)
            .post(body)
            .build()
        return request
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