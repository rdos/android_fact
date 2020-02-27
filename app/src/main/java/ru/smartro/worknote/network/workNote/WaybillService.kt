package ru.smartro.worknote.network.workNote

import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import ru.smartro.worknote.network.BearerToken
import ru.smartro.worknote.network.workNote.requestDTO.WaybillHeadRequest
import ru.smartro.worknote.network.workNote.responseDTO.WaybillHeadDTO

interface WaybillService {

    @POST("/api/waybill/list")
    fun list(
        @Body body: WaybillHeadRequest,
        @Header("Authorization") token: BearerToken
    ): Deferred<WaybillHeadDTO>
}