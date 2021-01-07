package ru.smartro.worknote.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.smartro.worknote.service.body.AuthBody
import ru.smartro.worknote.service.response.auth.AuthResponse
import ru.smartro.worknote.service.response.owner.OwnerResponse

interface ApiService {

    @POST("login")
    suspend fun auth(@Body model: AuthBody): Response<AuthResponse>

    @GET("owner")
    suspend fun getOwners(): Response<OwnerResponse>

    /* @Multipart
     @POST("card/file/{id}")
     suspend fun sendImage(
         @Part image: MultipartBody.Part,
         @Part("taskId") taskId: Int,
         @Part("taskTypeId") taskTypeId: Int,
         @Path("id") cardId: Int
     ): Response<String>*/

}