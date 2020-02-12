package ru.smartro.worknote.network.workNote

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import ru.smartro.worknote.network.BearerToken
import ru.smartro.worknote.network.workNote.responseDTO.VehiclesDTO

interface VehicleService {

    @GET("/api/common/vehicle")
    fun index(
        @Query("page") page: Int = 1,
        @Query("o") organisationId: Int,
        @Header("Authorization") token: BearerToken
    ): Deferred<VehiclesDTO>
}