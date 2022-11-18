package ru.smartro.worknote.awORKOLDs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.sentry.android.okhttp.SentryOkHttpInterceptor
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.smartro.worknote.App
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.LOG
import ru.smartro.worknote.TIME_OUT
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.awORKOLDs.service.NetObject
import ru.smartro.worknote.awORKOLDs.util.RESTconnection
import java.io.IOException
import java.util.concurrent.TimeUnit

abstract class AbsRequest: AbsObject(), Callback {
    private var mResponseString: String? = null
    private var mGson: Gson? = null
    private val URL = BuildConfig.URL__AUTH
    private val mMediaType: MediaType = "application/json; charset=utf-8".toMediaType()
    abstract fun onGetNetObject(): NetObject
    abstract fun onBeforeSend()
    abstract fun onAfterSend(connectionREVERS: RESTconnection)
//    abstract fun onGetResponseClazz(): Class<AuthResponse>

    private fun getGson(): Gson {
        if (mGson == null) {
            val builder = GsonBuilder()
            builder.excludeFieldsWithoutExposeAnnotation()
            mGson = builder.create()
        }
        return mGson!!
    }
    fun getOKHTTPRequest(): Request {

        val netObject = onGetNetObject()
        val bodyInStringFormat = getGson().toJson(netObject)

        val body: RequestBody = RequestBody.create(mMediaType, bodyInStringFormat)


        val request: Request = Request.Builder()
            .url(URL)
            .addHeader("Authorization", "Bearer " + App.getAppParaMS().token)
            .post(body)
            .build()
        /**НТР*/return request
    }

    override fun onFailure(call: Call, e: IOException) {
      LOG.warn("onFailure")
    }


    fun getLiveDate() : LiveData<RESTconnection?> {
        if (mRESTconnectionMutableLiveData == null) {
            mRESTconnectionMutableLiveData = MutableLiveData(null)
        }
        return mRESTconnectionMutableLiveData!!
    }
    private var mRESTconnectionMutableLiveData: MutableLiveData<RESTconnection?>? = null
    override fun onResponse(call: Call, response: Response) {
        LOG.info("onFailure")
        if(response.code == 401) {
            return
        }
//        TypeToken.get(onGetResponseClazz())
        val connectionREVERS = RESTconnection()
        connectionREVERS.isSent = true
        mResponseString = response.body.toString()

        this.onAfterSend(connectionREVERS)
        mRESTconnectionMutableLiveData?.postValue(connectionREVERS)
    }

    protected fun toClassObject(clazz: Class<NetObject>): NetObject {
        val result = getGson().fromJson(mResponseString, clazz)
        return result
    }

}