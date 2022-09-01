package ru.smartro.worknote.awORKOLDs.service.network

import retrofit2.Response
import retrofit2.http.*
import ru.smartro.worknote.awORKOLDs.service.network.body.AuthBody
import ru.smartro.worknote.awORKOLDs.service.network.body.PingBody
import ru.smartro.worknote.awORKOLDs.service.network.body.ProgressBody
import ru.smartro.worknote.awORKOLDs.service.network.body.WayListBody
import ru.smartro.worknote.awORKOLDs.service.network.body.breakdown.BreakdownBody
import ru.smartro.worknote.awORKOLDs.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.work.net.EarlyCompleteBody
import ru.smartro.worknote.awORKOLDs.service.network.body.failure.FailureBody
import ru.smartro.worknote.awORKOLDs.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.awORKOLDs.service.network.response.EmptyResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.auth.AuthResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.breakdown.BreakDownResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.breakdown.sendBreakDown.BreakDownResultResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.cancelation_reason.CancelationReasonResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.failure_reason.FailureReasonResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.failure_reason.send_failure.FailureResultResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.organisation.OrganisationResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.served.ServedResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.synchronize.SynchronizeResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.vehicle.VehicleResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.way_list.WayListResponse
import ru.smartro.worknote.work.AppStartUpBody
import ru.smartro.worknote.work.AppStartUpResponse
import ru.smartro.worknote.work.RPCBody
import ru.smartro.worknote.work.WorkOrderResponse_know1

interface ApiService {

    @POST("login")
    suspend fun auth(@Body model: AuthBody): Response<AuthResponse>

    @GET("owner")
    suspend fun getOwners(): Response<OrganisationResponse>

    @GET("vehicle")
    suspend fun getVehicle(@Query("o") organisationId: Int): Response<VehicleResponse>

    @GET("breakdown_type?page=all")
    suspend fun getBreakDownTypes(): Response<BreakDownResponse>

    @GET("failure_reason?page=all")
    suspend fun getFailReason(): Response<FailureReasonResponse>

    @POST("waybill")
    suspend fun getWayList(@Body body: WayListBody): Response<WayListResponse>

//    @POST("breakdown")
//    suspend fun sendBreakDown(@Body body: BreakdownBody): Response<BreakDownResultResponse>
//
//    @POST("failure")
//    suspend fun sendFailure(@Body body: FailureBody): Response<FailureResultResponse>

    @POST("workorder/{id}/progress")
    suspend fun progress(@Path("id") id: Int, @Body time: ProgressBody): Response<ServedResponse>

    //find   vs.networkDat.completeWay(-11, body)
    @POST("workorder/{id}/complete")
    suspend fun complete(@Path("id") id: Int, @Body time: CompleteWayBody): Response<EmptyResponse>

    @GET("work_order_cancelation_reason")
    suspend fun getCancelWayReason(): Response<CancelationReasonResponse>

    //см  vs.networkDat.earlyComplete(-111, body)
    @POST("workorder/{id}/early_complete")
    suspend fun earlyComplete(@Path("id") id: Int, @Body body: EarlyCompleteBody): Response<EmptyResponse>

    @POST("synchro")
    suspend fun postSynchro(@Body time: SynchronizeBody): Response<SynchronizeResponse>

    @POST("rpc")
    suspend fun ping(@Body pingBody: PingBody): Response<PingBody>

    @POST("rpc")
    suspend fun sendAppStartUp(@Body rpcBody: RPCBody<AppStartUpBody>): Response<RPCBody<AppStartUpResponse>>

    // NEXT STEP
//    @POST("rpc")
//    suspend fun sendAppEvent(@Body rpcBody: RPCBody<AppEventBody>): Response<RPCBody<AppEventResponse>>

    @POST("synchro/{o_id}/{w_id}")
    suspend fun getWorkOrder(@Path("o_id") organisationId: Int, @Path("w_id") waybillId: Int): Response<WorkOrderResponse_know1>

}