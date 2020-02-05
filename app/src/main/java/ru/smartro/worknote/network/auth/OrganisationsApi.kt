package ru.smartro.worknote.network.auth

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import ru.smartro.worknote.network.BearerToken
import ru.smartro.worknote.network.auth.responseDto.Organisation
import ru.smartro.worknote.network.auth.responseDto.Organisations


interface OrganisationsService {
    @GET("api/organisation")
    fun index(@Header("Authorization") token: BearerToken): Deferred<Organisations>


    @GET("api/organisation/{id}")
    fun getOrganisation(@Header("Authorization") token: BearerToken, @Path("id") id: Int): Deferred<Organisation>

}