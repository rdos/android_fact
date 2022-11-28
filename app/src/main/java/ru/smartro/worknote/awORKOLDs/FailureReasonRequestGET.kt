package ru.smartro.worknote.awORKOLDs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.awORKOLDs.service.NetObject
import ru.smartro.worknote.awORKOLDs.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.presentation.work.RealmRepository
import kotlin.reflect.KClass

class FailureReasonRequestGET: GETRequestA<FailureReasonBodyOut>() {
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