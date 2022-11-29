package ru.smartro.worknote.presentation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.log.awORKOLDs.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.log.work.RealmRepository
import ru.smartro.worknote.presentation.abs.AGETRequest
import ru.smartro.worknote.presentation.ac.NetObject
import kotlin.reflect.KClass

class RGETFailureReason: AGETRequest<FailureReasonBodyOut>() {
    override fun onGetSRVName(): String {
        return "failure_reason?page=all"
    }

    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {

    }

    override fun onBefore() {

    }

    override fun onAfter(bodyOut: FailureReasonBodyOut) {
        val db = RealmRepository(Realm.getDefaultInstance())
        val organisationId = App.getAppParaMS().getOwnerId()
        val entities = mutableListOf<FailReasonEntity>()

        if(bodyOut.data != null) {
            bodyOut.data.forEach {
                if(it.oid == organisationId)
                    entities.add(FailReasonEntity(it.id, it.name))
            }
        }

        LOG.debug("TEST:::" + entities.joinToString { it.problem.toString() })

        db.insertFailReason(entities)
    }

    override fun onGetResponseClazz(): KClass<FailureReasonBodyOut> {
        return FailureReasonBodyOut::class
    }
}

data class FailureReasonBodyOut(
    @Expose
    @SerializedName("data")
    val data: List<FailureReasonBodyOutData>? = null,
    @Expose
    @SerializedName("success")
    val success: Boolean
) : NetObject()

data class FailureReasonBodyOutData(
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    @SerializedName("oid")
    val oid: Int
): NetObject()