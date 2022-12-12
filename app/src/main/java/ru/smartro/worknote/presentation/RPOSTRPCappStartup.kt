package ru.smartro.worknote.presentation

import android.os.Build
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ru.smartro.worknote.*
import ru.smartro.worknote.presentation.ac.RPCBody
import ru.smartro.worknote.presentation.ac.AbsRequest
import kotlin.reflect.KClass

class RPCappStartup : AbsRequest<TestRPCtartupIn, TestRPCtartupIOUt>() {
    override fun onGetSRVName(): String {
        return "rpc"
    }

    override fun onGetRequestBodyIn(): TestRPCtartupIn {
        val appStartUpBody = RPCappStartupBodyIn(
            deviceId = App.getAppliCation().getDeviceId(),
            appVersion = BuildConfig.VERSION_NAME
        )

        appStartUpBody.os = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Build.VERSION.CODENAME
        } else {
            Build.VERSION.SDK_INT.toString()
        }

        val rpcBody = TestRPCtartupIn()
        rpcBody.type = "app_startup"
        rpcBody.payload = appStartUpBody
        return rpcBody
    }

    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {
//        TODO("Not yet implemented")
    }

    override fun onBefore() {

    }

    override fun onAfter(bodyOut: TestRPCtartupIOUt) {
        LOG.error("bodyOut.type=${bodyOut.type}")
    }

    override fun onGetResponseClazz(): KClass<TestRPCtartupIOUt> {
        return TestRPCtartupIOUt::class
    }
}

data class RPCappStartupBodyIn(
    @Expose
    @SerializedName("device_id")
    val deviceId: String = Snull,
    @Expose
    var os: String = Snull,
    @Expose
    @SerializedName("app_version")
    val appVersion: String = Snull
)

class TestRPCtartupIn: RPCBody<RPCappStartupBodyIn>()

data class RPCappStartupBodyOut(
    @Expose
    val id: Int = Inull,
    @Expose
    @SerializedName("user_id")
    val userId: String = Snull,
    @Expose
    @SerializedName("device_id")
    val deviceId: Int = Inull,
    @Expose
    @SerializedName("created_at")
    val createdAt: RPCappStartupBodyOutCreatedAt = RPCappStartupBodyOutCreatedAt()
)

class TestRPCtartupIOUt: RPCBody<RPCappStartupBodyOut>()

data class RPCappStartupBodyOutCreatedAt(
    @Expose
    val date: String = Snull,
    @Expose
    @SerializedName("timezone_type")
    val timeZoneType: Int = Inull,
    @Expose
    @SerializedName("timezone")
    val timeZone: String = Snull
)