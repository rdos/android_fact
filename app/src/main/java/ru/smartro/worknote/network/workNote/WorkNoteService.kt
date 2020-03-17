package ru.smartro.worknote.network.workNote

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.smartro.worknote.BuildConfig


private const val BASE_URL = BuildConfig.WN_URL


private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

object WnNetwork {

    private val logInterceptor by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC

        return@lazy interceptor
    }

    private val clientBuilder = OkHttpClient.Builder().addInterceptor {
        val request = it.request()
            .newBuilder()
            .addHeader("accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()
        it.proceed(request)
    }.addInterceptor(interceptor = logInterceptor)

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .client(clientBuilder.build())
        .build()

    val VEHICLE_ENTRY_POINT: VehicleService by lazy {
        retrofit.create(
            VehicleService::class.java
        )
    }

    val WAY_BILL_ENTRY_POINT: WaybillService by lazy {
        retrofit.create(
            WaybillService::class.java
        )
    }

}