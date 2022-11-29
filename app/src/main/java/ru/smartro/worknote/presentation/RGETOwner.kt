package ru.smartro.worknote.presentation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import io.realm.RealmList
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.LOG
import ru.smartro.worknote.log.work.OrganisationEntity
import ru.smartro.worknote.log.work.RealmRepository
import ru.smartro.worknote.presentation.abs.AGETRequest
import ru.smartro.worknote.presentation.ac.NetObject
import kotlin.reflect.KClass

class RGETOwner : AGETRequest<OwnerBodyOut>() {

    override fun onGetURL(): String {
        return BuildConfig.URL__AUTH
    }

    override fun onGetSRVName(): String {
        return "owner"
    }

    override fun onBefore() {
        LOG.error("DONT USE!!!!!!!!!!")
    }

    override fun onAfter(bodyOut: OwnerBodyOut) {
        val db = RealmRepository(Realm.getDefaultInstance())
        val organisationEntityS = RealmList<OrganisationEntity>()
        if(bodyOut.data != null) {
            for(organisation in bodyOut.data.organisations) {
                val organisationEntity = OrganisationEntity()
                organisationEntity.id = organisation.id
                organisationEntity.name = organisation.name
                organisationEntityS.add(organisationEntity)
            }
        }
        db.setOrganisationEntity(organisationEntityS)
    }

    override fun onGetResponseClazz(): KClass<OwnerBodyOut> {
        return OwnerBodyOut::class
    }

    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {
        LOG.warn("DON'T_USE") //not use
    }
}

data class OwnerBodyOut(
    @Expose
    @SerializedName("data")
    val data: OwnerBodyOutData? = null,
    @Expose
    @SerializedName("success")
    val success: Boolean
): NetObject()

data class OwnerBodyOutData(
    @SerializedName("email")
    val email: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_blocked")
    val isBlocked: Boolean,
    @SerializedName("is_confirmed")
    val isConfirmed: Boolean,
    @SerializedName("is_trashed")
    val isTrashed: Boolean,
    @SerializedName("mobile_fact")
    val mobileFact: OwnerBodyOutDataMobileFact,
    @SerializedName("name")
    val name: String,
    @SerializedName("organisation_ids")
    val organisationIds: List<Int>,
    @Expose
    @SerializedName("organisations")
    val organisations: List<OwnerBodyOutDataOrganisation>,
    @SerializedName("role_srp_ids")
    val roleSrpIds: List<Int>,
    @SerializedName("roles")
    val roles: List<Int>
): NetObject()

data class OwnerBodyOutDataOrganisation(
    @SerializedName("hostname")
    val hostname: String,
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("name")
    val name: String,
    @SerializedName("region_id")
    val regionId: Any,
    @SerializedName("timezone")
    val timezone: String
): NetObject()

data class OwnerBodyOutDataMobileFact(
    @SerializedName("can")
    val can: Boolean,
    @SerializedName("missing_permissions")
    val missingPermissions: List<Any>
): NetObject()