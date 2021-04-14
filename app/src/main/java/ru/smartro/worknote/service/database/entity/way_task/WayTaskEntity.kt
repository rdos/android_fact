package ru.smartro.worknote.service.database.entity.way_task


import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class WayTaskEntity(
    @PrimaryKey
    var id: Int? = null,
    var accounting: Int? = null,
    var begunAt: String? = null,
    var finishedAt: String? = null,
    var name: String? = null,
    var platfroms: RealmList<PlatformEntity>? = null,
    var start: StartPlatformEntity? = null,
    var unload: UnloadPlatformEntity? = null,
    var updatedAt: Long? = null
) : Serializable, RealmObject()

open class StartPlatformEntity(
    var lat : Double? = null,
    var lon : Double? = null,
    var name: String? = null
) : Serializable, RealmObject()

open class PlatformEntity(
    var platformId: Int? = null,
    var address: String? = null,
    var lat: Double? = null,
    var lon: Double? = null,
    var name: String? = null,
    var containers: RealmList<ContainerEntity>? = null,
    @SerializedName("srp_id")
    var srpId: Int? = null,
    var status: Int = 0,
    @SerializedName("after_media")
    var mediaAfter: RealmList<String>? = RealmList(),

    @SerializedName("before_media")
    var mediaBefore: RealmList<String>? = RealmList(),

    @SerializedName("platform_failure_media")
    var mediaPlatformProblem: RealmList<String>? = RealmList(),

    @SerializedName("container_failure_media")
    var mediaContainerProblem: RealmList<String>? = RealmList(),

    @SerializedName("breakdown_id")
    var breakdownReasonId: Int? = null,

    @SerializedName("failure_reason_id")
    var failureReasonId: Int? = null,

    @SerializedName("problem_comment")
    var problemComment: String? = null,

    var updatedAt: Long? = null,
    @SerializedName("organisation_id")
    var organisationId: Int? = null,

    @SerializedName("wo_id")
    var woId: Int? = null
) : Serializable, RealmObject()

open class UnloadPlatformEntity(
    var lat : Double? = null,
    var lon : Double? = null,
    var name: String? = null
) : Serializable, RealmObject()

open class ContainerEntity(
    @SerializedName("container_id")
    var containerId: Int? = null,
    var client: String? = null,
    var contacts: String? = null,
    @SerializedName("is_active")
    var isActive: Int? = null,
    var number: String? = null,
    var type_id: Int? = null,
    var comment: String? = null,
    @SerializedName("server_volume")
    var volume: Double? = null,
    @SerializedName("breakdown_id")
    var breakdownReasonId: Int? = null,
    @SerializedName("failure_reason_id")
    var failureReasonId: Int? = null,
    @SerializedName("problem_comment")
    var problemComment: String? = null,
    var status: Int = 0
) : Serializable, RealmObject()

open class ImageEntity(
    var imagePath : String? = null,
    var imageBase64 : String? = null
)

