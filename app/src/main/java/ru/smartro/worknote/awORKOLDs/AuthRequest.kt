package ru.smartro.worknote.awORKOLDs

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import ru.smartro.worknote.App
import ru.smartro.worknote.awORKOLDs.service.NetObject
import ru.smartro.worknote.awORKOLDs.util.RESTconnection
import ru.smartro.worknote.awORKOLDs.util.THR

//запрос + бизнесс
class AuthRequest : AbsRequest(){
    override fun onGetNetObject(): NetObject {
        val userName = App.getAppParaMS().userName
        val userPass = App.getAppParaMS().userPass
        val result = AuthBody(userName, userPass)
        return result
    }

    override fun onBeforeSend() {
//        TODO("Not yet implemented")
    }

    override fun onAfterSend(connectionREVERS: RESTconnection) {
//        TODO("Not yet implemented")

    }


}
class AuthBody(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
) : NetObject()

data class AuthResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("success")
    val success: Boolean
): NetObject()

data class Data(
    @SerializedName("token")
    val token: String
)
