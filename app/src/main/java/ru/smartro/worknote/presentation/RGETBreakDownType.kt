package ru.smartro.worknote.presentation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import ru.smartro.worknote.log.work.RealmRepository
import kotlin.reflect.KClass
import ru.smartro.worknote.App
import ru.smartro.worknote.log.awORKOLDs.service.database.entity.problem.BreakDownReasonEntity
import ru.smartro.worknote.presentation.abs.AGETRequest
import ru.smartro.worknote.presentation.ac.NetObject

class RGETBreakDownType : AGETRequest<BreakDownTypeOutBody>() {
    override fun onGetSRVName(): String {
        return "breakdown_type?page=all"
    }

    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {

    }

    override fun onBefore() {

    }

    override fun onAfter(bodyOut: BreakDownTypeOutBody) {
        val db = RealmRepository(Realm.getDefaultInstance())
        val organisationId = App.getAppParaMS().getOwnerId()
        val entities = bodyOut.data.filter {
            it.attributes.organisationId == organisationId
        }.map {
            BreakDownReasonEntity(it.attributes.id, it.attributes.name)
        }
        db.insertBreakDown(entities)
    }

    override fun onGetResponseClazz(): KClass<BreakDownTypeOutBody> {
        return BreakDownTypeOutBody::class
    }
}


data class BreakDownTypeOutBody(
    @Expose
    @SerializedName("data")
    val data: List<BreakDownTypeOutBodyData>,
    @Expose
    @SerializedName("success")
    val success: Boolean
) : NetObject()

data class BreakDownTypeOutBodyData(
    @Expose
    @SerializedName("attributes")
    val attributes: BreakDownTypeOutBodyDataAttributes,
    @Expose
    @SerializedName("id")
    val id: Int,
    @SerializedName("relationships")
    @Expose
    val relationships: List<Any>,
    @Expose
    @SerializedName("type")
    val type: String
): NetObject()

data class BreakDownTypeOutBodyDataAttributes(
    @SerializedName("created_at")
    @Expose
    val createdAt: String,
    @SerializedName("id")
    @Expose
    val id: Int,
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    @SerializedName("organisation_id")
    val organisationId: Int,
    @SerializedName("trashed")
    @Expose
    val trashed: Boolean
): NetObject()