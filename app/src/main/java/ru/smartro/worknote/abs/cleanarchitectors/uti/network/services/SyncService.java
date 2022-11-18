package ru.smartro.worknote.abs.cleanarchitectors.uti.network.services;

import ru.smartro.worknote.abs.cleanarchitectors.uti.network.model.Payload;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Createdbydmilicic
 */
public interface SyncService {

    /**
     * This endpoint will be used to send new costs created on this device.
     */
    @Headers("Connection: close")
    @POST("/costs")
    Call<Void> uploadData(@Body Payload data);
}