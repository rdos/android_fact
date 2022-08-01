package ru.smartro.worknote.work


import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import ru.smartro.worknote.*
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.awORKOLDs.util.MyUtil.toStr
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import java.io.Serializable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


open class WorkOrderEntity(
    @PrimaryKey
    var id: Int = Inull,
    var name: String = Snull,

    var platforms: RealmList<PlatformEntity> = RealmList(),
    var waste_type_id: Int? = null,
    var waste_type_name: String? = null,
    var waste_type_color: String? = null,
    var start: StartEntity? = null,
//    var unload: UnloadEntity? = null,

    var cnt_platform: Int = Inull,
    var cnt_container: Int = Inull,
    var cnt_platform_status_new: Int = Inull,
    var cnt_platform_status_success: Int = Inull,
    var cnt_platform_status_error: Int = Inull,
    var cnt_platform_status_partial_problems: Int = Inull,
    // ErrorS False fail exception
    var cnt_container_status_new: Int = Inull,
    var cnt_container_status_success: Int = Inull,
    var cnt_container_status_error: Int = Inull,

    var start_at: String? = null,
    var update_at: String? = null,
    var progress_at: String? = null,
    var end_at: String? = null,
    var isShowForUser: Boolean = true,

    ) : Serializable, RealmObject() {

    fun calcInfoStatistics() {
        var platformsCnt = 0
        var platformsStatusNewCnt = 0
        var platformsStatusSuccessCnt = 0
        var platformsStatusErrorCnt = 0
        var platformsStatusPartialProblemsCnt = 0

        var containersCnt = 0
        var containersStatusNewCnt = 0
        var containersStatusSuccessCnt = 0
        var containersStatusErrorCnt = 0
        for (platform in platforms) {
            /** статистика для PlatformEntity*/
            platformsCnt++
            when(platform.getStatusPlatform()) {
                StatusEnum.NEW -> platformsStatusNewCnt++
                StatusEnum.SUCCESS -> platformsStatusSuccessCnt++
                StatusEnum.ERROR -> platformsStatusErrorCnt++
                StatusEnum.UNFINISHED -> platformsStatusNewCnt++
                StatusEnum.PARTIAL_PROBLEMS -> platformsStatusPartialProblemsCnt++
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
        this.cnt_platform_status_partial_problems = platformsStatusPartialProblemsCnt
        this.cnt_container = containersCnt
        this.cnt_container_status_new = containersStatusNewCnt
        this.cnt_container_status_success = containersStatusSuccessCnt
        this.cnt_container_status_error = containersStatusErrorCnt
    }

    fun cntPlatformProgress(): Int {
        return cnt_platform - cnt_platform_status_new
    }
    fun cntContainerProgress(): Int {
        return cnt_container - cnt_container_status_new
    }

    fun isComplete(): Boolean {
        return this.cntPlatformProgress() == this.cnt_platform
    }

    companion object {
        private fun mapMedia(data: List<String>): RealmList<ImageEntity> {
            return data.mapTo(RealmList()) {
                ImageEntity(
                    image = it, date = 0,
                    coords = RealmList()
                )
            }
        }

        // TODO: 29.10.2021 ! it.volume = 0.0 ??Error возраст.
        private fun mapContainers(list: List<CoNTaiNeR_know1>, workorderId: Int): RealmList<ContainerEntity> {
            return list.mapTo(RealmList()) {
//                var volumeReal : Double? = null
//                if (it.volume >  0) {
//                    Log.e(TAG ,"mapContainers.it.volume >  0")
//                    volumeReal = it.volume
//                    Log.e(TAG ,"mapContainers.volumeReal = ${volumeReal}")
//                }
                ContainerEntity(
                    workOrderId = workorderId,
                    client = it.client,
                    contacts = it.contacts,
                    failureMedia = mapMedia(it.failureMedia),
                    failureReasonId = it.failureReasonId,
                    containerId = it.id,
                    isActiveToday = it.isActiveToday,/* breakdownReasonId = it.breakdownReasonId,*/
                    number = it.number,
                    status = it.status,
                    typeId = it.typeId,
                    constructiveVolume = it.constructiveVolume,
                    typeName = it.typeName,
                    volume = it.volume
                )
            }
        }

        private fun mapPlatforms(data: List<Platform_know1>, workorderId: Int): RealmList<PlatformEntity> {
            val result = data.mapTo(RealmList()) {
                val platform = PlatformEntity(
                    workOrderId = workorderId,
                    address = it.address,
                    afterMedia = mapMedia(it.afterMedia),
                    beforeMedia = mapMedia(it.beforeMedia),
                    beginnedAt = it.beginnedAt,
                    containers = mapContainers(it.coNTaiNeRKnow1s, workorderId),
                    coordSOriginal = RealmList(it.coords[0], it.coords[1]),
                    coordLat = it.coords[0],
                    coordLong = it.coords[1],
                    failureMedia = mapMedia(it.failureMedia),
                    failureReasonId = it.failureReasonId, /*breakdownReasonId = it.breakdownReasonId,*/
                    finishedAt = it.finishedAt,
                    platformId = it.id,
                    name = it.name,
                    updateAt = 0,
                    srpId = it.srpId,
                    status = it.status, //!r_dos
                    /** volumeKGO = null,*/ icon = it.icon,
                    orderTimeEnd = it.orderEndTime,
                    orderTimeStart = it.orderStartTime,
                    orderTimeAlert = it.orderAlertTime,
                    orderTimeWarning = it.orderWarningTime,
                    kgoServed = KGOEntity().copyKGOEntity(it.kgo_served),
                    kgoRemaining = KGOEntity().copyKGOEntity(it.kgo_remaining)
                )
                platform.networkStatus = platform.isNotNewPlatform()
                platform
            }
            return result
        }

        private fun mapStart(data: STaRT_know1?): StartEntity? {
            var result: StartEntity? = null
            if (data != null) {
                result = StartEntity(
                    coords = RealmList(data.coords[0], data.coords[1]),
                    name = data.name,
                    id = data.id
                )
            }
            return result
        }

        /**public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapTo(destination: C, transform: (T) -> R): C {        */
        fun map(woRKoRDeRknow1List: List<WoRKoRDeR_know1>): RealmList<WorkOrderEntity> {
            val res = RealmList<WorkOrderEntity>()
            try {
                for (woRKoRDeRknow1 in woRKoRDeRknow1List) {
                    val workOrder = WorkOrderEntity(
                        id = woRKoRDeRknow1.id,
                        start_at = MyUtil.currentTime(),
                        name = woRKoRDeRknow1.name,
                        waste_type_id = woRKoRDeRknow1.waste_type?.id,
                        waste_type_name = woRKoRDeRknow1.waste_type?.name,
                        waste_type_color = woRKoRDeRknow1.waste_type?.color?.hex,
                        platforms = mapPlatforms(woRKoRDeRknow1.platformKnow1s, woRKoRDeRknow1.id),
                        start = mapStart(woRKoRDeRknow1.STaRTknow1)
                    )
                    res.add(workOrder)
                }
            } catch (eXthr: Exception) {
                Log.e("TAGS", "map", eXthr)
            }
            return res
        }
    }
}

open class StartEntity(
    var coords: RealmList<Double> = RealmList(),
    var name: String? = null,
    var id: Int? = null
    ) : Serializable, RealmObject()

open class KGOEntity(
    var volume: Double? = null,
    var media: RealmList<ImageEntity> = RealmList()
    ): Serializable, RealmObject() {
        fun isEmpty(): Boolean {
            val result = volume == null
            return result
        }

        fun isNotEmpty(): Boolean {
            return !isEmpty()
        }

        fun copyKGOEntity(kgoentityKnow100: KGOEntity_know100?): KGOEntity? {
            fun mapMedia(data: List<String>): RealmList<ImageEntity> {
                return data.mapTo(RealmList()) { ImageEntity(image = it, date = 0,
                    coords = RealmList())}
            }
            var result: KGOEntity? = null
            kgoentityKnow100?.let {
                this.volume = kgoentityKnow100.volume
                kgoentityKnow100.media?.let {
                    this.media = mapMedia(kgoentityKnow100.media!!)
                }

                result = this
            }
            return result
        }

    }

open class ServedContainers(
    var typeName: String = Snull,
    var client: String = Snull,
    var servedCount: Int = Inull
): Serializable, RealmObject()

open class PlatformEntity(
    var workOrderId: Int = Inull,
    var isWorkOrderProgress: Boolean = false,
    var isWorkOrderComplete: Boolean = false,
    // TODO:::Vlad: will be removed
    var afterMediaSize: Int = 0,
    // TODO:::Vlad: will be removed
    var beforeMediaSize: Int = 0,
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
    var networkStatus: Boolean = false,
    @SerializedName("failure_comment")
    var failureComment: String? = null,
    @SerializedName("containers")
    var containers: RealmList<ContainerEntity> = RealmList(),
    @SerializedName("coords")
    var coordSOriginal: RealmList<Double> = RealmList(),
    var coordLat: Double = Dnull,
    var coordLong: Double = Dnull,
    @SerializedName("failure_media")
    var failureMedia: RealmList<ImageEntity> = RealmList(),

    @SerializedName("kgo_remaining")
    var kgoRemaining: KGOEntity? = null,
    @SerializedName("kgo_served")
    var kgoServed: KGOEntity? = null,

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

    @Expose
    var servedContainers: RealmList<ServedContainers> = RealmList()

) : Serializable, RealmObject() {

    fun isTypoMiB(): Boolean = this.icon == "Bath"

    fun getStatusPlatform(): String {
        val filteredContainers = this.containers.filter { el -> el.isActiveToday }
        val hasUnservedContainers = filteredContainers.any { el -> el.status == StatusEnum.NEW  }
        val isAllSuccess = filteredContainers.all { el -> el.status == StatusEnum.SUCCESS }
        val isAllError = filteredContainers.all { el -> el.status == StatusEnum.ERROR }

        val _beforeMediaSize = if(this.beforeMedia.size == 0) this.beforeMediaSize else this.beforeMedia.size
        val _afterMediaSize = if(this.afterMedia.size == 0) this.afterMediaSize else this.afterMedia.size

        val result = when {
            isAllError -> StatusEnum.ERROR
            _beforeMediaSize == 0 && _afterMediaSize == 0 -> StatusEnum.NEW
            _beforeMediaSize != 0 && hasUnservedContainers -> StatusEnum.UNFINISHED
            isAllSuccess -> StatusEnum.SUCCESS
            filteredContainers.all { el -> el.status != StatusEnum.NEW } -> StatusEnum.PARTIAL_PROBLEMS
            else -> {
                return StatusEnum.UNFINISHED
            }
        }
        return result
    }

    private fun isNewPlatform(): Boolean {
        val res = this.getStatusPlatform() == StatusEnum.NEW
        return res
    }
    fun isNotNewPlatform(): Boolean {
        val result = !this.isNewPlatform()
        return result
    }

    fun getIconFromStatus(): Int {
        val currentStatus = this.getStatusPlatform()
        if(currentStatus == StatusEnum.UNFINISHED) return R.drawable.ic_serving
        return when (this.icon) {
            "bunker" ->
                when (currentStatus) {
                    StatusEnum.NEW -> R.drawable.ic_bunker_blue
                    StatusEnum.SUCCESS -> R.drawable.ic_bunker_green
                    StatusEnum.ERROR -> R.drawable.ic_bunker_red
                    else -> R.drawable.ic_bunker_orange
                }
            "bag" ->
                when (currentStatus) {
                    StatusEnum.NEW -> R.drawable.ic_bag_blue
                    StatusEnum.SUCCESS -> R.drawable.ic_bag_green
                    StatusEnum.ERROR -> R.drawable.ic_bag_red
                    else -> R.drawable.ic_bag_orange
                }
            "bulk" ->
                when (currentStatus) {
                    StatusEnum.NEW -> R.drawable.ic_bulk_blue
                    StatusEnum.SUCCESS -> R.drawable.ic_bulk_green
                    StatusEnum.ERROR -> R.drawable.ic_bulk_red
                    else -> R.drawable.ic_bulk_orange
                }
            "euro" ->
                when (currentStatus) {
                    StatusEnum.NEW -> R.drawable.ic_euro_blue
                    StatusEnum.SUCCESS ->  R.drawable.ic_euro_green
                    StatusEnum.ERROR -> R.drawable.ic_euro_red
                    else -> R.drawable.ic_euro_orange
                }
            "metal" ->
                when (currentStatus) {
                    StatusEnum.NEW -> R.drawable.ic_metal_blue
                    StatusEnum.SUCCESS -> R.drawable.ic_metal_green
                    StatusEnum.ERROR -> R.drawable.ic_metal_red
                    else -> R.drawable.ic_metal_orange
                }
            "Bath" ->
                when (currentStatus) {
                    StatusEnum.NEW -> R.drawable.ic_two_sync_b
                    StatusEnum.SUCCESS -> R.drawable.ic_two_sync_g
                    StatusEnum.ERROR -> R.drawable.ic_two_sync_red
                    else -> R.drawable.ic_two_sync_orange
                }
            else ->
                //many
                when (currentStatus) {
                    StatusEnum.NEW -> R.drawable.ic_many_blue
                    StatusEnum.SUCCESS -> R.drawable.ic_many_green
                    StatusEnum.ERROR -> R.drawable.ic_many_red
                    else -> R.drawable.ic_many_orange
                }
        }
    }

    fun getInactiveIcon(): Int {
        return when (this.icon) {
            "bunker" -> R.drawable.ic_bunker_gray
            "bag" -> R.drawable.ic_bag_gray
            "bulk" -> R.drawable.ic_bulk_gray
            "euro" -> R.drawable.ic_euro_gray
            "metal" -> R.drawable.ic_metal_gray
            "Bath" -> R.drawable.ic_two_sync_gray
            else -> R.drawable.ic_many_gray
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
        if (this.beginnedAt.isNullOrEmpty() && this.getStatusPlatform() == StatusEnum.NEW) {
            this.orderTimeStart?.let {
                result = "до ${this.orderTimeEnd}"
            }
        }
        return result
    }

    fun getOrderTimeColor(context: Context): Int {
        if (isOrderTimeWarning() && isNotOrderTimeAlert()) {
            return ContextCompat.getColor(context, R.color.dark_orange)
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

    private fun initServedKGOEntity() {
        if (this.kgoServed == null ) {
            this.kgoServed = createKGOEntity(Realm.getDefaultInstance())
        }
    }

    private fun initRemainingKGOEntity() {
        if (this.kgoRemaining == null ) {
            this.kgoRemaining = createKGOEntity(Realm.getDefaultInstance())
        }
    }

    fun getServedKGOMediaSize(): Int {
        var res = 0
        if (this.kgoServed != null ) {
//            this.servedKGO = createServedKGO(Realm.getDefaultInstance())
            res = this.kgoServed!!.media.size
        }
        return res
    }

    fun isPickupNotEmpty(): Boolean {
        return this.volumePickup != null
    }

    fun getPickupMediaSize(): Int {
        var res = 0
        if (this.pickupMedia.isNotEmpty()) {
//            this.servedKGO = createServedKGO(Realm.getDefaultInstance())
            res = this.pickupMedia.size
        }
        return res
    }

    fun getRemainingKGOMediaSize(): Int {
            var res = 0
            if (this.kgoRemaining != null ) {
//            this.servedKGO = createServedKGO(Realm.getDefaultInstance())
                res = this.kgoRemaining!!.media.size
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
        if (this.kgoServed != null ) {
            res = this.kgoServed!!.volume.toStr()
        }
        return res
    }

    fun getRemainingKGOVolume(): String {
        var res = ""
        if (this.kgoRemaining != null ) {
            res = this.kgoRemaining!!.volume.toStr()
        }
        return res
    }

    fun isServedKGONotEmpty(): Boolean {
        return getServedKGOMediaSize() > 0
    }

    fun isRemainingKGONotEmpty(): Boolean {
        return getRemainingKGOMediaSize() > 0
    }

    fun addServerKGOMedia(imageS: List<ImageEntity>) {
        initServedKGOEntity()
        this.kgoServed?.let{
            it.media.addAll(imageS)
        }
    }

    fun addRemainingKGOMedia(imageS: List<ImageEntity>) {
        initRemainingKGOEntity()
        this.kgoRemaining?.let{
            it.media.addAll(imageS)
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
        this.kgoServed?.let{
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
        this.kgoRemaining?.let{
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
    @SerializedName("breakdown_media")
    var breakdownMedia: RealmList<ImageEntity> = RealmList(),
    @SerializedName("breakdown_reason_id")
    var breakdownReasonId: Int? = null,
    @SerializedName("breakdown_comment")
    var breakdownComment: String? = null,
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
        if(this.volume == null) {
            return 100.0
        } else {
            return (this.volume!! * 100).toInt().toDouble()
        }
    }

    // TODO: 23.12.2021
    fun getVolumeInPercent(): Double {
        if ((!this.isActiveToday) && this.volume == null) {
            return 0.0
        }
        return this.convertVolumeToPercent()
    }

    fun getVolumePercentColor(context: Context): Int {
        if (!this.isActiveToday && this.volume == null) {
            return Color.GRAY
        }
        if (this.status == StatusEnum.ERROR) {
            return Color.RED
        }
        return ContextCompat.getColor(context, R.color.colorAccent)
    }

    private fun getFailureMediaSize(): Int {
        var res = 0
        if (this.failureReasonId != null ) {
            res = this.failureMedia.size
        }
        return res
    }

    fun isFailureNotEmpty(): Boolean {
        return getFailureMediaSize() > 0
    }

    private fun getBreakdownMediaSize(): Int {
        var res = 0
        if (this.breakdownReasonId != null ) {
            res = this.breakdownMedia.size
        }
        return res
    }

    fun isBreakdownNotEmpty(): Boolean {
        return getBreakdownMediaSize() > 0
    }
}

open class ImageEntity(
    var image: String? = null,
    var date: Long? = null,
    var coords: RealmList<Double> = RealmList(),
    var accuracy: String? = null,
    var lastKnownLocationTime: Long? =null,
    var isNoLimitPhoto: Boolean = false,
    // 0 = vertical, 1 = horizontal
    var origOrient: Int = 0
) : Serializable, RealmObject() {

}


