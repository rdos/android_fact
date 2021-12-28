package ru.smartro.worknote.service.database.entity.work_order


import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.w3c.dom.Text
import ru.smartro.worknote.*
import ru.smartro.worknote.util.MyUtil.isNotNull
import ru.smartro.worknote.util.StatusEnum
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.days

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
    var volumeKGO: Double? = null,
    @SerializedName("kgo_is_takeaway")
    var isTakeawayKGO: Boolean = true,
    @SerializedName("pickup_volume")
    var volumePickup: Double? = null,
    @SerializedName("pickup_media")
    var pickupMedia: RealmList<ImageEntity> = RealmList(),
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
    var srpId: Int? = null,
    @SerializedName("icon")
    var icon: String? = null,
    @SerializedName("order_start_time")
    var orderTimeStart: String? = null,
    @SerializedName("order_end_time")
    var orderTimeEnd: String? = null,
    @SerializedName("order_warning_time")
    var orderTimeWarning: String? = null,
    @SerializedName("order_alert_time")
    var orderTimeAlert: String? = null,
) : Serializable, RealmObject() {

    fun getIconDrawableResId(): Int {
        if (this.beginnedAt.isNotNull() && this.status == StatusEnum.NEW) {
            return R.drawable.ic_serving
        }
        if (this.beginnedAt.isNullOrEmpty() && this.status == StatusEnum.NEW) {
            // время прошло = красный
            if (isOrderTimeAlert()) {
                return getIconFromStatus(StatusEnum.ERROR)
            }
            //осталось меньше часа оранжевый
            if (isOrderTimeWarning()) {
                return getIconFromStatus(StatusEnum.UNFINISHED)
            }
        }
        return getIconFromStatus(this.status)
    }

    private fun getIconFromStatus(p_status: String?): Int {
        return when (this.icon) {
            "bunker" ->
                when (p_status) {
                    StatusEnum.NEW -> R.drawable.ic_bunker_blue
                    StatusEnum.SUCCESS -> R.drawable.ic_bunker_green
                    StatusEnum.ERROR -> R.drawable.ic_bunker_red
                    else -> R.drawable.ic_bunker_orange
                }
            "bag" ->
                when (p_status) {
                    StatusEnum.NEW -> R.drawable.ic_bag_blue
                    StatusEnum.SUCCESS -> R.drawable.ic_bag_green
                    StatusEnum.ERROR -> R.drawable.ic_bag_red
                    else -> R.drawable.ic_bag_orange
                }
            "bulk" ->
                when (p_status) {
                    StatusEnum.NEW -> R.drawable.ic_bulk_blue
                    StatusEnum.SUCCESS -> R.drawable.ic_bulk_green
                    StatusEnum.ERROR -> R.drawable.ic_bulk_red
                    else -> R.drawable.ic_bulk_orange
                }
            "euro" ->
                when (p_status) {
                    StatusEnum.NEW -> R.drawable.ic_euro_blue
                    StatusEnum.SUCCESS ->  R.drawable.ic_euro_green
                    StatusEnum.ERROR -> R.drawable.ic_euro_red
                    else -> R.drawable.ic_euro_orange
                }
            "metal" ->
                when (p_status) {
                    StatusEnum.NEW -> R.drawable.ic_metal_blue
                    StatusEnum.SUCCESS -> R.drawable.ic_metal_green
                    StatusEnum.ERROR -> R.drawable.ic_metal_red
                    else -> R.drawable.ic_metal_orange
                }
            else ->
                //many
                when (p_status) {
                    StatusEnum.NEW -> R.drawable.ic_many_blue
                    StatusEnum.SUCCESS -> R.drawable.ic_many_green
                    StatusEnum.ERROR -> R.drawable.ic_many_red
                    else -> R.drawable.ic_many_orange
                }
        }
    }

    fun getContactsInfo(): String {
        var result = ""
        this.containers.forEach{ containerEntity ->
            if (!containerEntity.client.isNullOrEmpty()) {
                result += containerEntity.client + " "
            }
            if (!containerEntity.contacts.isNullOrEmpty()) {
                result += containerEntity.contacts
            }
            if (!containerEntity.client.isNullOrEmpty() || !containerEntity.contacts.isNullOrEmpty()) {
                result += "\n"
            }
        }
        return result
    }

    fun getOrderTime(): String {
        var result = Snull
        this.orderTimeStart?.let {
            result = "${this.orderTimeStart}—${this.orderTimeEnd}"
        }
        return result
    }

    fun getOrderTimeForMaps(): String {
        var result = Snull
        if (this.beginnedAt.isNullOrEmpty() && this.status == StatusEnum.NEW) {
            this.orderTimeStart?.let {
                result = "до ${this.orderTimeEnd}"
            }
        }
        return result
    }

    fun getOrderTimeColor(context: Context): Int {
        if (isOrderTimeWarning() && isNotOrderTimeAlert()) {
            return ContextCompat.getColor(context, R.color.orange)
        }
        if (isOrderTimeAlert()) {
            return ContextCompat.getColor(context, R.color.dark_red)
        }
        return Color.GRAY
    }


    fun isOrderTimeWarning(): Boolean {
        var result = false
        this.orderTimeWarning?.let {
            val orderEndTime: Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(this.orderTimeWarning)
            val today = getDeviceDateTime()
            val diff: Long = orderEndTime.time - today.time
            val minutes = diff / (1000 * 60)
            Log.d("AAAA", this.orderTimeWarning!!)
            Log.d("AAAA", minutes.toString())
            if (minutes < 0) {
                result = true
            }
        }
        return result
    }

    fun isNotOrderTimeAlert(): Boolean {
        return !isOrderTimeAlert()
    }

    private fun isOrderTimeAlert(): Boolean {
        var result = false
        this.orderTimeAlert?.let {
            val orderEndTime: Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(this.orderTimeAlert)
            val today = getDeviceDateTime()
            val diff: Long = orderEndTime.time - today.time
            val minutes = diff / (1000 * 60)
            if (minutes < 0) {
                result = true
            }
        }
        return result
    }
}

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
    var isActiveToday: Boolean = false,
    @SerializedName("number")
    var number: String? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("type_id")
    var typeId: Int? = null,
    @SerializedName("volume")
    var volume: Double? = null,
    @SerializedName("comment")
    var comment: String? = null
) : Serializable, RealmObject() {

    fun convertVolumeToPercent() : Double {
        var result = -111.1

        result =  when (this.volume) {
            0.00 -> 0.0
            0.25 -> 25.0
            0.50 -> 50.0
            0.75 -> 75.0
            1.00 -> 100.0
            1.25 -> 125.0
            else -> 100.0
            }
        return result
    }

    // TODO: 23.12.2021
    fun getVolumeInPercent(): Double {
        if ((!this.isActiveToday) && this.volume == null) {
            return 0.0
        }
        return this.convertVolumeToPercent()
    }

    fun getVolumePercentColor(context: Context): Int {
        if (!this.isActiveToday) {
            return Color.GRAY
        }
        if (this.status == StatusEnum.ERROR) {
            return Color.RED
        }
        return ContextCompat.getColor(context, R.color.colorAccent)
    }
}

open class ImageEntity(
    var image: String? = null,
    var date: Long? = null,
    var coords: RealmList<Double> = RealmList()
) : Serializable, RealmObject()


