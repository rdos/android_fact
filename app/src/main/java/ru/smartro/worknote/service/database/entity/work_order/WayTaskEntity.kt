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
    var afterMedia: RealmList<String> = RealmList(),
    @SerializedName("before_media")
    var beforeMedia: RealmList<String> = RealmList(),
    @SerializedName("beginned_at")
    var beginnedAt: String? = null,
    @SerializedName("updateAt")
    var updateAt: Long? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("failure_comment")
    var failureComment: String? = null,
    @SerializedName("containers")
    var containers: RealmList<ContainerEntity> = RealmList(),
    @SerializedName("coords")
    var coords: RealmList<Double> = RealmList(),
    @SerializedName("failure_media")
    var failureMedia: RealmList<String> = RealmList(),
    @SerializedName("kgo_media")
    var kgoMedia: RealmList<String> = RealmList(),
    @SerializedName("kgo_volume")
    var kgoVolume : Int? = null,
    @SerializedName("failure_reason_id")
    var failureReasonId: Int? = null,
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
    var failureMedia: RealmList<String> = RealmList(),
    @SerializedName("failure_reason_id")
    var failureReasonId: Int? = null,
    @SerializedName("breakdownReasonId")
    var breakdownReasonId: Int? = null,
    @SerializedName("id")
    var containerId: Int? = null,
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


