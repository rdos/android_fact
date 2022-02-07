package ru.smartro.worknote.work


import android.content.Context
import android.graphics.Color
import android.text.BoringLayout
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import ru.smartro.worknote.*
import ru.smartro.worknote.util.MyUtil.isNotNull
import ru.smartro.worknote.util.MyUtil.toStr
import ru.smartro.worknote.util.StatusEnum
import java.io.Serializable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

open class WorkOrderEntity(
    @PrimaryKey
    var id: Int = Inull,
    var name: String = Snull,
    var platforms: RealmList<PlatformEntity> = RealmList(),
    var start: StartEntity? = null,
//    var unload: UnloadEntity? = null,

    var cnt_platform: Int = Inull,
    var cnt_container: Int = Inull,
    var cnt_platform_status_new: Int = Inull,
    var cnt_platform_status_success: Int = Inull,
    var cnt_platform_status_error: Int = Inull,
    // ErrorS False fail exception
    var cnt_container_status_new: Int = Inull,
    var cnt_container_status_success: Int = Inull,
    var cnt_container_status_error: Int = Inull,

    var start_at: String? = null,
    var update_at: String? = null,
    var progress_at: String? = null,
    var end_at: String? = null,
    ) : Serializable, RealmObject() {

    fun calcInfoStatistics() {
        val platformsCnt = platforms.size + 1
        var platformsStatusNewCnt = 0
        var platformsStatusSuccessCnt = 0
        var platformsStatusErrorCnt = 0

        var containersCnt = 0
        var containersStatusNewCnt = 0
        var containersStatusSuccessCnt = 0
        var containersStatusErrorCnt = 0
        for (platform in platforms) {
            /** статистика для PlatformEntity*/
            if(platform.status == StatusEnum.NEW) {
                platformsStatusNewCnt++
            }
            if(platform.status == StatusEnum.SUCCESS) {
                platformsStatusSuccessCnt++
            }
            if(platform.status == StatusEnum.ERROR) {
                platformsStatusErrorCnt++
            }
            /** статистика для ContainerEntity*/
            platform.containers.forEach {
                containersCnt++
                if(it.status == StatusEnum.NEW) {
                    containersStatusNewCnt++
                }
                if(it.status == StatusEnum.SUCCESS) {
                    containersStatusSuccessCnt++
                }
                if(it.status == StatusEnum.ERROR) {
                    containersStatusErrorCnt++
                }

            }
        }
        this.cnt_platform = platformsCnt
        this.cnt_platform_status_new = platformsStatusNewCnt
        this.cnt_platform_status_success = platformsStatusSuccessCnt
        this.cnt_platform_status_error = platformsStatusErrorCnt
        this.cnt_container = containersCnt
        this.cnt_container_status_new = containersStatusNewCnt
        this.cnt_container_status_success = containersStatusSuccessCnt
        this.cnt_container_status_error = containersStatusErrorCnt
    }
}

open class StartEntity(
    var coords: RealmList<Double> = RealmList(),
    var name: String? = null,
    var id: Int? = null
    ) : Serializable, RealmObject()

open class KGOEntity(
    var volume: Double? = null,
    val media: RealmList<ImageEntity> = RealmList()
    ): Serializable, RealmObject() {
        fun isEmpty(): Boolean {
            val result = volume == null
            return result
        }
        fun isNotEmpty(): Boolean {
            return !isEmpty()
        }
    }

open class PlatformEntity(
    var workOrderId: Int = Inull,
    var isWorkOrderProgress: Boolean = false,
    var isWorkOrderComplete: Boolean = false,
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

    @SerializedName("kgo_remaining")
    var remainingKGO: KGOEntity? = null,
    @SerializedName("kgo_served")
    var servedKGO: KGOEntity? = null,

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
        if (this.isStartServe()) {
            return R.drawable.ic_serving
        }
//        if (this.beginnedAt.isNullOrEmpty() && this.status == StatusEnum.NEW) {
//            // время прошло = красный
//            if (isOrderTimeAlert()) {
//                return getIconFromStatus(StatusEnum.ERROR)
//            }
//            //осталось меньше часа оранжевый
//            if (isOrderTimeWarning()) {
//                return getIconFromStatus(StatusEnum.UNFINISHED)
//            }
//        }
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

    fun isStartServe(): Boolean {
        return this.beginnedAt.isNotNull() && this.status == StatusEnum.NEW
    }

    fun isStartServeVolume(): Boolean {
        var result = false
        if (!this.isStartServe()) {
            return result
        }
        this.containers.forEach { container ->
            if (container.volume != null) {
                result = true
                return@forEach
            }
        }
        return result
    }


    private fun initServedKGOEntity() {
        if (this.servedKGO == null ) {
            this.servedKGO = createKGOEntity(Realm.getDefaultInstance())
        }
    }

    private fun initRemainingKGOEntity() {
        if (this.remainingKGO == null ) {
            this.remainingKGO = createKGOEntity(Realm.getDefaultInstance())
        }
    }

    fun getServedKGOMediaSize(): Int {
        var res = 0
        if (this.servedKGO != null ) {
//            this.servedKGO = createServedKGO(Realm.getDefaultInstance())
            res = this.servedKGO!!.media.size
        }
        return res
    }

    fun getRemainingKGOMediaSize(): Int {
            var res = 0
            if (this.remainingKGO != null ) {
//            this.servedKGO = createServedKGO(Realm.getDefaultInstance())
                res = this.remainingKGO!!.media.size
            }
            return res
        }

    fun createKGOEntity(realm: Realm): KGOEntity {
//        this.servedKGO = Realm.getDefaultInstance().createObject(Real)
        val result = realm.createObject(KGOEntity::class.java)
        return result
    }


    fun getServedKGOVolume(): String {
        var res = ""
        if (this.servedKGO != null ) {
            res = this.servedKGO!!.volume.toStr()
        }
        return res
    }

    fun getRemainingKGOVolume(): String {
        var res = ""
        if (this.remainingKGO != null ) {
            res = this.remainingKGO!!.volume.toStr()
        }
        return res
    }

    fun isServedKGONotEmpty(): Boolean {
        return getServedKGOMediaSize() > 0
    }

    fun isRemainingKGONotEmpty(): Boolean {
        return getRemainingKGOMediaSize() > 0
    }

    fun addServerKGOMedia(imageEntity: ImageEntity) {
        initServedKGOEntity()
        this.servedKGO?.let{
            it.media.add(imageEntity)
        }
    }

    fun addRemainingKGOMedia(imageEntity: ImageEntity) {
        initRemainingKGOEntity()
        this.remainingKGO?.let{
            it.media.add(imageEntity)
        }
    }

    fun setServedKGOVolume(kgoVolume: String) {
        initServedKGOEntity()
        var kgoVolumeDouble: Double = Dnull
        try {
            kgoVolumeDouble = kgoVolume.toDouble()
        } catch (ex: Exception) {
            Log.e("TMP", "setRemainingKGOVolume", ex)
        }
        this.servedKGO?.let{
            it.volume = kgoVolumeDouble
        }
    }

    fun setRemainingKGOVolume(kgoVolume: String) {
        initRemainingKGOEntity()
        var kgoVolumeDouble: Double = Dnull
        try {
            kgoVolumeDouble = kgoVolume.toDouble()
        } catch (ex: Exception) {
            Log.e("TMP", "setRemainingKGOVolume", ex)
        }
        this.remainingKGO?.let{
            it.volume = kgoVolumeDouble
        }
    }


//    fun createServedKGO(realm: Realm): KGOEntity {
//        val result = realm.createObject(KGOEntity::class.java)
//        return result
//    }
}



open class UnloadEntity(
    var coords: RealmList<Double> = RealmList(),
    var name: String? = null,
    var id: Int? = null
) : Serializable, RealmObject()

open class ContainerEntity(
    var workOrderId: Int = Inull,
    var isWorkOrderProgress: Boolean = false,
    var isWorkOrderComplete: Boolean = false,
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
    var comment: String? = null,
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


