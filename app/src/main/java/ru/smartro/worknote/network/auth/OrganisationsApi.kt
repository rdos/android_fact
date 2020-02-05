package ru.smartro.worknote.network.auth

import kotlinx.coroutines.Deferred

import retrofit2.http.GET
import retrofit2.http.Header
import ru.smartro.worknote.network.BearerToken

import ru.smartro.worknote.network.auth.responseDto.Organisations


interface OrganisationsService {
    @GET("api/organisation")
    fun index(@Header("Authorization") token: BearerToken): Deferred<Organisations>

}