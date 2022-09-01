package ru.smartro.worknote.awORKOLDs.service.network

import android.content.Context
import com.google.gson.GsonBuilder
import io.sentry.android.okhttp.SentryOkHttpInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.smartro.worknote.App
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.LOG
import ru.smartro.worknote.TIME_OUT
import java.util.concurrent.TimeUnit
import ru.smartro.worknote.awORKOLDs.service.network.body.synchro.SynchronizeBody

class RetrofitClient(context: Context) {
    


    private val authInterceptor = Interceptor { chain ->
        val newUrl = chain.request().url
            .newBuilder()
            .build()

        val newRequest = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer " + App.getAppParaMS().token)
            .url(newUrl)
            .build()
        chain.proceed(newRequest)
    }

    private var httpLoggingInterceptor = run {
        val httpLoggingInterceptor1 = HttpLoggingInterceptor { message -> LOG.warn( message) }
        httpLoggingInterceptor1.apply {
            httpLoggingInterceptor1.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val client =
        OkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
//            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(SentryOkHttpInterceptor())
//            .authenticator(TokenAuthenticator(context))
            .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .build()

    private fun retrofit(baseUrl: String) =
        Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun testApiService(): ApiService {
        return retrofit(BuildConfig.URL__SMARTRO).create(ApiService::class.java)
    }

    fun apiService(isWorkNote: Boolean): ApiService {
        // переключатель для разных API
        return if (isWorkNote)
            retrofit(BuildConfig.URL__SMARTRO + "fact/").create(ApiService::class.java)
        else
            retrofit(BuildConfig.URL__AUTH).create(ApiService::class.java)
    }
}
