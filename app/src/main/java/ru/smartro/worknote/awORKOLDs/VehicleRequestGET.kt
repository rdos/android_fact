package ru.smartro.worknote.awORKOLDs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import io.realm.RealmList
import ru.smartro.worknote.App
import ru.smartro.worknote.awORKOLDs.service.NetObject
import ru.smartro.worknote.presentation.work.RealmRepository
import ru.smartro.worknote.presentation.work.VehicleEntity
import kotlin.reflect.KClass

class VehicleRequestGET: GETRequestA<VehicleBodyOut>() {
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