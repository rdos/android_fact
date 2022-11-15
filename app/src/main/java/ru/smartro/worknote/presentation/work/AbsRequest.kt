package ru.smartro.worknote.presentation.work

import io.sentry.android.okhttp.SentryOkHttpInterceptor
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import ru.smartro.worknote.App
import ru.smartro.worknote.TIME_OUT
import ru.smartro.worknote.abs.AbsObject
import java.io.IOException
import java.util.concurrent.TimeUnit

abstract class AbsRequest() : AbsObject(), Callback {


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

    fun send() {
//        val body: RequestBody = RequestBody.create(PostExample.JSON, json)
        val body = onBefore().toString().toRequestBody()
        val request: Request = Request.Builder()
            .url("")
            .addHeader("Authorization", "Bearer " + App.getAppParaMS().token)
            .post(body)
            .build()
        client.newCall(request).enqueue(this)
    }

    override fun onFailure(call: Call, e: IOException) {
        call
    }

    override fun onResponse(call: Call, response: Response) {
        onAfter(response.body.toString())
    }

    abstract fun onAfter(body: String?)
    abstract fun onBefore()
}
