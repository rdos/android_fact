package ru.smartro.worknote.presentation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import io.realm.RealmList
import ru.smartro.worknote.App
import ru.smartro.worknote.work.work.RealmRepository
import ru.smartro.worknote.log.todo.VehicleEntity
import ru.smartro.worknote.presentation.abs.AGETRequest
import ru.smartro.worknote.presentation.ac.NetObject
import kotlin.reflect.KClass

class RGETVehicle: AGETRequest<VehicleBodyOut>() {
    override fun onGetSRVName(): String {
        return "vehicle"
    }

    override fun onBefore() {

    }

    override fun onAfter(bodyOut: VehicleBodyOut) {
        val db = RealmRepository(Realm.getDefaultInstance())
        val vehicleEntityS = RealmList<VehicleEntity>()
        if(bodyOut.vehicles != null) {
            for (vehicle in bodyOut.vehicles) {
                val vehicleEntity = VehicleEntity()
                vehicleEntity.id = vehicle.id
                vehicleEntity.name = vehicle.name ?: "Имя не задано"
                vehicleEntityS.add(vehicleEntity)
            }
        }

        db.setVehicleEntity(vehicleEntityS)
    }

    override fun onGetResponseClazz(): KClass<VehicleBodyOut> {
        return VehicleBodyOut::class
    }

    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {
        queryParamMap["o"] = App.getAppParaMS().ownerId.toString()
    }
}

data class VehicleBodyOut(
    @Expose
    @SerializedName("data")
    val vehicles: List<VehicleBodyOutVehicle>? = null,
    @Expose
    @SerializedName("success")
    val success: Boolean
) : NetObject()

data class VehicleBodyOutVehicle(
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("name")
    val name: String? = null,
    @Expose
    @SerializedName("oid")
    val oid: Int
) : NetObject()