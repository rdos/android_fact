package ru.smartro.worknote.awORKOLDs

import com.google.gson.annotations.SerializedName
import okhttp3.Response
import ru.smartro.worknote.App
import ru.smartro.worknote.awORKOLDs.service.NetObject
import ru.smartro.worknote.awORKOLDs.service.network.body.AuthBody
import ru.smartro.worknote.awORKOLDs.service.network.response.auth.AuthResponse

//запрос + бизнесс
class AuthRequest : AbsRequest<AuthResponse>(){
    override fun onGetNetObject(): NetObject {
        val userName = App.getAppParaMS().userName
        val userPass = App.getAppParaMS().userPass
        val result = AuthBody(userName, userPass)
        return result
    }

    override fun onBeforeSend() {

    }

    override fun <T> onAfterSend(responsObject: T) {
        TODO("Not yet implemented")
    }
}

data class AuthResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("success")
    val success: Boolean
)

data class Data(
    @SerializedName("token")
    val token: String
)