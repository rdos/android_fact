package ru.smartro.worknote.awORKOLDs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.sentry.android.okhttp.SentryOkHttpInterceptor
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import ru.smartro.worknote.App
import ru.smartro.worknote.App.Companion.getAppParaMS
import ru.smartro.worknote.LOG
import ru.smartro.worknote.TIME_OUT
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostExample : Callback {

//    private val authInterceptor = Interceptor { chain ->
//        val newUrl = chain.request().url
//            .newBuilder()
//            .build()
//
//        val newRequest = chain.request()
//            .newBuilder()
//            .addHeader("Authorization", "Bearer " + App.getAppParaMS().token)
//            .url(newUrl)
//            .build()
//        chain.proceed(newRequest)
//    }

    private var httpLoggingInterceptor = run {
        val httpLoggingInterceptor1 = HttpLoggingInterceptor { message ->
            if(message.contains("image"))
                LOG.warn(message.replace("\"image\":\".*?\",".toRegex(), ""))
            else
                LOG.warn(message)
        }
        httpLoggingInterceptor1.apply {
            httpLoggingInterceptor1.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val client =
        OkHttpClient().newBuilder()
//            .addInterceptor(authInterceptor)
//            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(SentryOkHttpInterceptor())
//            .authenticator(TokenAuthenticator(context))
            .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .build()

    private val _ownersList: MutableLiveData<Response> = MutableLiveData(null)
    val mOwnersList: LiveData<Response>
        get() = _ownersList

    @Throws(IOException::class)
    fun post(url: String?, json: String): Response {
        val body: RequestBody = RequestBody.create(JSON, json)
        val request: Request = Request.Builder()
            .url(url!!)
            .addHeader("Authorization", "Bearer " + getAppParaMS().token)
            .post(body)
            .build()
        val result = client.newCall(request).execute()
        return result
    }

    fun bowlingJson(player1: String, player2: String): String {
        return ("{'winCondition':'HIGH_SCORE',"
                + "'name':'Bowling',"
                + "'round':4,"
                + "'lastSaved':1367702411696,"
                + "'dateStarted':1367702378785,"
                + "'players':["
                + "{'name':'" + player1 + "','history':[10,8,6,7,8],'color':-13388315,'total':39},"
                + "{'name':'" + player2 + "','history':[6,10,5,10,10],'color':-48060,'total':41}"
                + "]}")
    }

    companion object {
        val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val example = PostExample()
            val json = example.bowlingJson("Jesse", "Jake")
            val response = example.post("http://www.roundsapp.com/post", json)
//            println(response.body.toString())
        }
    }

    override fun onFailure(call: Call, e: IOException) {
        TODO("Not yet implemented")
    }

    override fun onResponse(call: Call, response: Response) {
        _ownersList.postValue(response)
    }
}