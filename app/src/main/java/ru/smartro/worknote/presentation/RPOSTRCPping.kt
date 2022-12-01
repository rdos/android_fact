package ru.smartro.worknote.log.todo

import com.google.gson.annotations.Expose
import ru.smartro.worknote.App
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.presentation.ac.AbsRequest
import ru.smartro.worknote.presentation.ac.NetObject
import kotlin.reflect.KClass

class RCPping : AbsRequest<PingBody, PingBody>() {
    override fun onGetSRVName(): String {
        return "rpc"
    }

    override fun onGetRequestBodyIn(): PingBody {
        val result = PingBody("ping")
        return result
    }

    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {

    }

    override fun onBefore() {

    }

    override fun onAfter(bodyOut: PingBody) {
        val message = bodyOut.payload?.message
        if(message != null)
            App.getAppliCation().showAlertNotification(message)
        else {
//                    LoG.error("Ping EMPTY MESSAGE ${pingResponse.data}")
        }
    }

    override fun onGetResponseClazz(): KClass<PingBody> {
        return PingBody::class
    }

    override fun onGetURL(): String {
        return BuildConfig.URL__SMARTRO_PING
    }
}

data class PingBody(
    @Expose
    val type: String,
    @Expose
    val error: PingBodyError? = null,
    @Expose
    val payload: PingBodyPayload? = null
) : NetObject()

data class PingBodyError(
    @Expose
    val message: String? = null
): NetObject()

data class PingBodyPayload(
    @Expose
    val message: String? = null
): NetObject()
