package ru.smartro.worknote.network.workNote

import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import ru.smartro.worknote.network.BearerToken
import ru.smartro.worknote.network.workNote.requestDTO.WaybillBodyRequest
import ru.smartro.worknote.network.workNote.responseDTO.WaybillBodyDTO

interface WaybillBodyService {

    @POST("/api/waybill/{waybill_id}/import")
    fun list(
        @Body body: WaybillBodyRequest,
        @Path(value = "waybill_id", encoded = true) waybillId: Int,
        @Header("Authorization") token: BearerToken
    ): Deferred<WaybillBodyDTO>
}