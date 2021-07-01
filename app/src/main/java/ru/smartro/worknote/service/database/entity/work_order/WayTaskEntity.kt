package ru.smartro.worknote.service.database.entity.work_order


import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class WayTaskEntity(
    @PrimaryKey
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("accounting")
    var accounting: Int? = null,
    @SerializedName("beginned_at")
    var beginnedAt: String? = null,
    @SerializedName("finished_at")
    var finishedAt: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("platforms")
    var platforms: RealmList<PlatformEntity> = RealmList(),
    @SerializedName("start")
    var start: StartEntity? = null,
    @SerializedName("unload")
    var unload: UnloadEntity? = null
) : Serializable, RealmObject()

open class StartEntity(
    var coords: RealmList<Double> = RealmList(),
    var name: String? = null,
    var id: Int? = null
) : Serializable, RealmObject()

open class PlatformEntity(
    @SerializedName("address")
    var address: String? = null,
    @SerializedName("after_media")
    var afterMedia: RealmList<ImageEntity> = RealmList(),
    @SerializedName("before_media")
    var beforeMedia: RealmList<ImageEntity> = RealmList(),
    @SerializedName("beginned_at")
    var beginnedAt: String? = null,
    @SerializedName("updateAt")
    var updateAt: Long = 0,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("network_status")
    var networkStatus: Boolean? = false,
    @SerializedName("failure_comment")
    var failureComment: String? = null,
    @SerializedName("containers")
    var containers: RealmList<ContainerEntity> = RealmList(),
    @SerializedName("coords")
    var coords: RealmList<Double> = RealmList(),
    @SerializedName("failure_media")
    var failureMedia: RealmList<ImageEntity> = RealmList(),
    @SerializedName("kgo_media")
    var kgoMedia: RealmList<ImageEntity> = RealmList(),
    @SerializedName("kgo_volume")
    var kgoVolume : Double = 0.0,
    @SerializedName("failure_reason_id")
    var failureReasonId: Int? = null,
/*    @SerializedName("breakdown_reason_id")
    var breakdownReasonId: Int? = null,*/
    @SerializedName("finished_at")
    var finishedAt: String? = null,
    @SerializedName("id")
    var platformId: Int? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("srp_id")
    var srpId: Int? = null
) : Serializable, RealmObject()

open class UnloadEntity(
    var coords: RealmList<Double> = RealmList(),
    var name: String? = null,
    var id: Int? = null
) : Serializable, RealmObject()

open class ContainerEntity(
    @SerializedName("client")
    var client: String? = null,
    @SerializedName("contacts")
    var contacts: String? = null,
    @SerializedName("failure_media")
    var failureMedia: RealmList<ImageEntity> = RealmList(),
    @SerializedName("failure_reason_id")
    var failureReasonId: Int? = null,
/*    @SerializedName("breakdown_reason_id")
    var breakdownReasonId: Int? = null,*/
    @SerializedName("id")
    var containerId: Int? = null,
    @SerializedName("constructive_volume")
    var constructiveVolume: Double? = null,
    @SerializedName("type_name")
    var typeName: String? = null,
    @SerializedName("is_active_today")
    var isActiveToday: Boolean? = null,
    @SerializedName("number")
    var number: String? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("type_id")
    var typeId: Int? = null,
    @SerializedName("volume")
    var volume: Double? = null,
    @SerializedName("failure_comment")
    var failureComment: String? = null,
    @SerializedName("comment")
    var comment: String? = null
) : Serializable, RealmObject()

open class ImageEntity(
    var image: String? = null,
    var date: Long? = null
) : Serializable, RealmObject()


