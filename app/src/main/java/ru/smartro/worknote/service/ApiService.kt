package ru.smartro.worknote.service

import retrofit2.Response
import retrofit2.http.*
import ru.smartro.worknote.service.body.AuthBody
import ru.smartro.worknote.service.body.ProgressBody
import ru.smartro.worknote.service.body.WayListBody
import ru.smartro.worknote.service.body.WayTaskBody
import ru.smartro.worknote.service.body.served.ServiceResultBody
import ru.smartro.worknote.service.response.EmptyResponse
import ru.smartro.worknote.service.response.auth.AuthResponse
import ru.smartro.worknote.service.response.organisation.OrganisationResponse
import ru.smartro.worknote.service.response.served.ServedResponse
import ru.smartro.worknote.service.response.vehicle.VehicleResponse
import ru.smartro.worknote.service.response.way_list.WayListResponse
import ru.smartro.worknote.service.response.way_task.WayTaskResponse

interface ApiService {

    @POST("login")
    suspend fun auth(@Body model: AuthBody): Response<AuthResponse>

    @GET("owner")
    suspend fun getOwners(): Response<OrganisationResponse>

    @GET("vehicle")
    suspend fun getVehicle(@Query("o") organisationId: Int): Response<VehicleResponse>

    @POST("waybill")
    suspend fun getWayList(@Body body: WayListBody): Response<WayListResponse>

    @POST("waybill/{id}")
    suspend fun getWayTask(@Path("id") wayId: Int, @Body wayTaskBody: WayTaskBody): Response<WayTaskResponse>

    @POST("served")
    suspend fun served(@Body body: ServiceResultBody): Response<ServedResponse>

    @POST("workorder/{id}/progress")
    suspend fun progress(@Path("id") id: Int, @Body time: ProgressBody): Response<EmptyResponse>

    @POST("workorder/{id}/progress")
    suspend fun complete(@Path("id") id: Int, @Body time: ProgressBody): Response<EmptyResponse>

    /* @Multipart
     @POST("card/file/{id}")
     suspend fun sendImage(
         @Part image: MultipartBody.Part,
         @Part("taskId") taskId: Int,
         @Part("taskTypeId") taskTypeId: Int,
         @Path("id") cardId: Int
     ): Response<String>*/

}