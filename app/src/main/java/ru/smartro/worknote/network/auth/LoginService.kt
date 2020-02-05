package ru.smartro.worknote.network.auth

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.network.BearerToken
import ru.smartro.worknote.network.auth.requestDto.AuthBody
import ru.smartro.worknote.network.auth.responseDto.LoginData
import ru.smartro.worknote.network.auth.responseDto.OwnerData

private const val BASE_URL = BuildConfig.AUTH_URL

interface LoginService {
    @POST("api/login")
    fun auth(@Body authBody: AuthBody): Deferred<LoginData>

    @POST("api/refresh")
    fun refresh(@Header("Authorization") token: BearerToken): Deferred<LoginData>

    @GET("api/owner")
    fun getOwner(@Header("Authorization") token: BearerToken): Deferred<OwnerData>
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


object AuthNetwork {

    private val clientBuilder = OkHttpClient.Builder().addInterceptor {
        val request = it.request()
            .newBuilder()
            .addHeader("accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()
        it.proceed(request)
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .client(clientBuilder.build())
        .build()

    val LOGIN_ENTRY_POINT: LoginService by lazy {
        retrofit.create(
            LoginService::class.java
        )
    }

    val ORGANISATIONS_ENTRY_POINT: OrganisationsService by lazy {
        retrofit.create(OrganisationsService::class.java)
    }
}