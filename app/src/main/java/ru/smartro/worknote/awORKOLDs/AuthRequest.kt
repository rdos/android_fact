package ru.smartro.worknote.awORKOLDs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ru.smartro.worknote.App
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.LOG
import ru.smartro.worknote.awORKOLDs.service.NetObject
import kotlin.reflect.KClass

//запрос + бизнесс
class AuthRequest: POSTRequestA<AuthBodyIn, AuthBodyOut>() {

    override fun onGetURL(): String {
        return BuildConfig.URL__AUTH
    }

    override fun onGetRequestBodyIn(): AuthBodyIn {
        val userName = App.getAppParaMS().userName
        val userPass = App.getAppParaMS().userPass
        val result = AuthBodyIn(userName, userPass)
        return result
    }

    override fun onBefore() {
        LOG.debug("before")
    }

    override fun onAfter(bodyOut: AuthBodyOut) {
        LOG.debug("after")
        App.getAppParaMS().token = bodyOut.data.token
    }

    override fun onGetSRVName(): String {
       return "login"
    }

    override fun onGetResponseClazz(): KClass<AuthBodyOut> {
        return AuthBodyOut::class
    }

    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {
        LOG.warn("DON'T_USE") //not use
    }

}
data class AuthBodyIn(
    @Expose
    @SerializedName("email")
    val email: String,
    @Expose
    @SerializedName("password")
    val password: String
) : NetObject()

data class AuthBodyOut(
    @Expose
    @SerializedName("data")
    val data: AuthBodyOutData,
    @Expose
    @SerializedName("success")
    val success: Boolean
) : NetObject()

data class AuthBodyOutData(
    @Expose
    @SerializedName("token")
    val token: String
) : NetObject()
