package ru.smartro.worknote.presentation.ac

import io.sentry.Sentry
import retrofit2.Response
import ru.smartro.worknote.App
import ru.smartro.worknote.log.awORKOLDs.service.network.response.served.ServedResponse

sealed class THR(code: Int) : Throwable(code.toString()) {
    //    abstract val message: String
    fun <T> sentToSentry(response: Response<T>){
        if (response.code() in 400..599) {
            val urlName = response.raw().request.url.encodedPath
            Sentry.setTag("url_name", urlName)
            Sentry.setTag("http_code", response.code().toString())
            Sentry.setTag("url_host_name", response.raw().request.url.host)

            Sentry.setTag("user", App.getAppParaMS().userName)
            // TODO: replace  BadRequestException for post  @POST("synchro")
//        Sentry.captureException(BadRequestException(Gson().toJson(response.errorBody())))
            Sentry.captureException(this)
        }
    }

    fun  sentToSentry(response: okhttp3.Response){
        if (response.code in 400..599) {
            val urlName = response.request.url.encodedPath
            Sentry.setTag("url_name", urlName)
            Sentry.setTag("http_code", response.code.toString())
            Sentry.setTag("url_host_name", response.request.url.host)

            Sentry.setTag("user", App.getAppParaMS().userName)
            // TODO: replace  BadRequestException for post  @POST("synchro")
//        Sentry.captureException(BadRequestException(Gson().toJson(response.errorBody())))
            Sentry.captureException(this)
        }
    }

    fun sentToSentryOKHTTP(response: okhttp3.Response){
        if (response.isSuccessful) {
            val urlName = response.request.url.encodedPath
            Sentry.setTag("url_name", urlName)
            Sentry.setTag("http_code", response.code.toString())
            Sentry.setTag("url_host_name", response.request.url.host)

            Sentry.setTag("user", App.getAppParaMS().userName)
            // TODO: replace  BadRequestException for post  @POST("synchro")
//        Sentry.captureException(BadRequestException(Gson().toJson(response.errorBody())))
            Sentry.captureException(this)
        }
    }

//    class BadRequestLogin(response: Response<AuthResponse>) : THR(response.code()) {
//        //        override val message = 70.0
//        init {
//            sentToSentry(response)
//        }
//
//    }

//    class BadRequestOwner(response: Response<OrganisationResponse>) : THR(response.code()) {
//
//        init {
//            sentToSentry(response)
//        }
//
//    }

//    class BadRequestVehicle(response: Response<VehicleResponse>) : THR(response.code()) {
//
//        init {
//            sentToSentry(response)
//        }
//
//    }
//    class BadRequestBreakdown_type(response: Response<BreakDownResponse>) : THR(response.code()) {
//
//        init {
//            sentToSentry(response)
//        }
//
//    }
//    class BadRequestFailure_reason(response: Response<FailureReasonResponse>) : THR(response.code()) {
//
//        init {
//            sentToSentry(response)
//        }
//
//    }

//    class BadRequestWaybill(response: Response<WayListResponse>) : THR(response.code()) {
//
//        init {
//            sentToSentry(response)
//        }
//
//    }
    //    class breakdown(response: String) : THR(response.code()) {
//
//        init {
//            sentToSentry(response)
//        }
//
//    }
//    class failure(response: String) : THR(response.code()) {
//
//        init {
//            sentToSentry(response)
//        }
//
//    }
    class BadRequestProgress(response: Response<ServedResponse>) : THR(response.code()) {

        init {
            sentToSentry(response)
        }

    }


//    class BadRequestWork_order_cancelation_reason(response: Response<CancelationReasonResponse>) : THR(response.code()) {
//
//        init {
//            sentToSentry(response)
//        }
//
//    }
//    class BadRequestWorkorder__id__early_complete(response: Response<EarlyCompleteBodyOut>) : THR(response.code()) {
//
//        init {
//            sentToSentry(response)
//        }
//
//    }
//
//    class BadRequestPOSTsynchro(response: Response<SynchronizeResponse>) : THR(response.code()) {
//
//        init {
//            sentToSentry(response)
//        }
//
//    }

    class BadRequestPOSTsynchroOKHTTP(response: okhttp3.Response) : THR(response.code) {

        init {
            sentToSentryOKHTTP(response)
        }

    }

//    class BadRequestPing(response: Response<PingBody>) : THR(response.code()) {
//        init {
//            sentToSentry(response)
//        }
//
//    }



//    class BadRequestAppStartUp(response: Response<RPCBody<AppStartUpResponse>>) : THR(response.code()) {
//
//        init {
//            sentToSentry(response)
//        }
//
//    }

//    //    BadRequestSynchro__o_id__w_id
//    class BadRequestSynchro__o_id__w_id(response: Response<WorkOrderResponse_know1>) : THR(response.code()) {
//        init {
//            sentToSentry(response)
//        }
//
//    }

}


