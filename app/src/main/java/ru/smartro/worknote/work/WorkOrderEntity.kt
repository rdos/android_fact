package ru.smartro.worknote.work


import android.content.Context
import android.graphics.Color
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
import java.text.SimpleDateFormat
import java.util.*

const val THIS_IS_ERROR = "это ошибка?"
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
            /** статистика для */
            platformsCnt++
            when(platform.getStatusPlatform()) {
                StatusEnum.NEW -> platformsStatusNewCnt++
                StatusEnum.SUCCESS -> platformsStatusSuccessCnt++
                StatusEnum.ERROR -> platformsStatusErrorCnt++
                StatusEnum.UNFINISHED -> platformsStatusNewCnt++
                StatusEnum.PARTIAL_PROBLEMS -> platformsStatusPartialProblemsCnt++
            }
            /** статистика для ContainerEntity*/
            platform.containerS!!.forEach {
                containersCnt++
                val containerStatus = it.getStatusContainer()
                LOG.info("containerStatus=${containerStatus}")
                when(containerStatus) {
                    StatusEnum.NEW -> containersStatusNewCnt++
                    StatusEnum.SUCCESS -> containersStatusSuccessCnt++
                    StatusEnum.ERROR -> containersStatusErrorCnt++
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
        
        fun mapMedia(data: List<String>): RealmList<ImageEntity> {
            var idx = 0L
            return data.mapTo(RealmList()) {
                idx++
                ImageEntity(
                    image = it, date = idx,
                    coords = RealmList()
                )
            }
        }


        private fun mapPlatforms(data: List<Platform_know1>, workorderId: Int, database: RealmRepository): RealmList<PlatformEntity> {
            val result = data.mapTo(RealmList()) {
                LOG.info("PLATFORM MAP::: failureMediaSize ${it.failureMedia.size} failureReason ${it.failureReasonId}")
                val platformEntity = PlatformEntity(
                    platformId = it.id,
                    address = it.address,
                    coordSOriginal = RealmList(it.coords[0], it.coords[1]),
                    coordLat = it.coords[0],
                    coordLong = it.coords[1],
                    name = it.name,
                    srpId = it.srpId,
                    icon = it.icon,
                    // volumePickup
                    orderTimeStart = it.orderStartTime,
                    orderTimeEnd = it.orderEndTime,
                    orderTimeWarning = it.orderWarningTime,
                    orderTimeAlert = it.orderAlertTime,
                    failureReasonId = it.failureReasonId, /*breakdownReasonId = it.breakdownReasonId,*/
                    // failureComment
                    status = it.status, //!r_dos
                    beginnedAt = it.beginnedAt,
                    updateAt = it.updateAt,
                    finishedAt = it.finishedAt,
                    workOrderId = workorderId,
                    // events
                    // isWorkOrderProgress
                    // isWorkOrderComplete
                    // pickupMedia
                    /** volumeKGO = null,*/
                )
                platformEntity.containerS = platformEntity.mapContainers(it.coNTaiNeRKnow1s, database)

                val platformMediaEntity = database.loadPlatformMediaEntity(platformEntity)
                val afterMedia = mapMedia(it.afterMedia)
                platformEntity.addAfterMedia(afterMedia)
                platformMediaEntity.afterMedia.addAll(afterMedia)
                val beforeMedia = mapMedia(it.beforeMedia)
                platformEntity.addAfterMedia(beforeMedia)
                platformMediaEntity.beforeMedia.addAll(beforeMedia)
                val failureMedia = mapMedia(it.failureMedia)
                platformEntity.addFailureMedia(failureMedia)
                platformMediaEntity.failureMedia.addAll(failureMedia)

                it.kgo_served?.let {
                    it.volume?.let {
                        platformEntity.setServedKGOVolume(it.toStr())
                    }
                    it.media?.let {
                        val kgoServedMedia = mapMedia(it)
                        platformEntity.addServerKGOMedia(kgoServedMedia)
                        platformMediaEntity.kgoServedMedia.addAll(kgoServedMedia)
                    }
                }

                it.kgo_remaining?.let {
                    it.volume?.let {
                        platformEntity.setRemainingKGOVolume(it.toStr())
                    }
                    it.media?.let {
                        val kgoRemainingMedia = mapMedia(it)
                        platformEntity.addRemainingKGOMedia(kgoRemainingMedia)
                        platformMediaEntity.kgoRemainingMedia.addAll(kgoRemainingMedia)
                    }
                }

                platformEntity.networkStatus = platformEntity.isNotNewPlatform()
                platformEntity
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
        fun map(woRKoRDeRknow1List: List<WoRKoRDeR_know1>, database: RealmRepository): RealmList<WorkOrderEntity> {
            val res = RealmList<WorkOrderEntity>()
                for (woRKoRDeRknow1 in woRKoRDeRknow1List) {
                    try {
                        val workOrder = WorkOrderEntity(
                            id = woRKoRDeRknow1.id,
                            start_at = MyUtil.currentTime(),
                            name = woRKoRDeRknow1.name,
                            waste_type_id = woRKoRDeRknow1.waste_type?.id,
                            waste_type_name = woRKoRDeRknow1.waste_type?.name,
                            waste_type_color = woRKoRDeRknow1.waste_type?.color?.hex,
                            platforms = mapPlatforms(woRKoRDeRknow1.platformKnow1s, woRKoRDeRknow1.id, database),
                            start = mapStart(woRKoRDeRknow1.STaRTknow1)
                        )
                        res.add(workOrder)
                    } catch (eXthr: Exception) {
                        LOG.error("eXthr", eXthr)
                    }
                }
            LOG.debug("res=${res.size}")
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
    @Expose
    var volume: Double? = null,
    @Expose
    var media: RealmList<ImageEntity> = RealmList()
    ): Serializable, RealmObject() {
        fun isEmpty(): Boolean {
            val result = volume == null
            return result
        }

        fun isNotEmpty(): Boolean {
            return !isEmpty()
        }

    }

open class AppEventEntity(
    @Expose
    var event: String = Snull,
    @Expose
    var counter: String = Snull
): Serializable, RealmObject()

open class PlatformMediaEntity(
    @PrimaryKey
    var platformId: Int = Inull,
    var beforeMedia: RealmList<ImageEntity> = RealmList(),
    var kgoServedMedia: RealmList<ImageEntity> = RealmList(),
    var kgoRemainingMedia: RealmList<ImageEntity> = RealmList(),
    var pickupMedia: RealmList<ImageEntity> = RealmList(),
    var failureMedia: RealmList<ImageEntity> = RealmList(),
    var afterMedia: RealmList<ImageEntity> = RealmList(),
    var workOrderId: Int = Inull,
    var dev_info: String? = null
): Serializable, RealmObject() {

    fun getBeforeMediaSize(): Int {
        return beforeMedia.size
    }


    companion object {
        // TODO: !!!
        fun createEmpty(): PlatformMediaEntity {
            val result = PlatformMediaEntity(dev_info=THIS_IS_ERROR)
            return result
        }
    }
}

open class PlatformEntity(
    @Expose
    @SerializedName("id")
    @PrimaryKey
    var platformId: Int = Inull,
    @Expose
    @SerializedName("address")
    var address: String? = null,

    @Expose
    @SerializedName("containers")
    var containerS: RealmList<ContainerEntity> = RealmList(),
    @Expose
    @SerializedName("coords")
    var coordSOriginal: RealmList<Double> = RealmList(),

    var coordLat: Double = Dnull,
    var coordLong: Double = Dnull,

    @Expose
    @SerializedName("name")
    var name: String? = null,
    @Expose
    @SerializedName("srp_id")
    var srpId: Int? = null,
    @Expose
    @SerializedName("icon")
    var icon: String? = null,
    @Expose
    @SerializedName("pickup_volume")
    var volumePickup: Double? = null,
    @Expose
    @SerializedName("order_start_time")
    var orderTimeStart: String? = null,
    @Expose
    @SerializedName("order_end_time")
    var orderTimeEnd: String? = null,
    @Expose
    @SerializedName("order_warning_time")
    var orderTimeWarning: String? = null,
    @Expose
    @SerializedName("order_alert_time")
    var orderTimeAlert: String? = null,

    @Expose
    @SerializedName("failure_reason_id")
    var failureReasonId: Int = 0,
    @Expose
    @SerializedName("failure_comment")
    var failureComment: String? = null,

    @Expose
    @SerializedName("status")
    var status: String? = Snull,
    @Expose
    @SerializedName("beginned_at")
    var beginnedAt: String? = null,
    @Expose
    @SerializedName("updated_at")
    var updateAt: Long = 0,
    @Expose
    @SerializedName("network_status")
    var networkStatus: Boolean = false,
    @Expose
    @SerializedName("finished_at")
    var finishedAt: String? = null,
    // TODO: Нам так приходит это поле же?..
    @Expose
    @SerializedName("workOrderId")
    var workOrderId: Int = Inull,

    @Expose
    var events: RealmList<AppEventEntity> = RealmList(),

    var isWorkOrderProgress: Boolean = false,
    var isWorkOrderComplete: Boolean = false,

    @Expose
    @SerializedName("after_media")
    var afterMedia: RealmList<ImageEntity> = RealmList(),
    @Expose
    @SerializedName("before_media")
    var beforeMedia: RealmList<ImageEntity> = RealmList(),
    @Expose
    @SerializedName("pickup_media")
    var pickupMedia: RealmList<ImageEntity> = RealmList(),
    @Expose
    @SerializedName("failure_media")
    var failureMedia: RealmList<ImageEntity> = RealmList(),
    @Expose
    @SerializedName("kgo_remaining")
    var kgoRemaining: KGOEntity? = null,
    @Expose
    @SerializedName("kgo_served")
    var kgoServed: KGOEntity? = null,

) : Serializable, RealmObject() {

    fun getBeforeMediaSize() = this.beforeMedia.size
    fun getAfterMediaSize() = afterMedia.size
    fun getFailureMediaSize() = this.failureMedia.size
    fun getPickupMediaSize() = this.pickupMedia.size
    fun getRemainingKGOMediaSize(): Int {
        var result = 0
        if (this.kgoRemaining == null) {
            LOG.trace("this.kgoRemaining == null, result=${result}")
            return result
        }
        result = this.kgoRemaining!!.media.size
        return result
    }
    fun getServedKGOMediaSize(): Int {
        var result = 0
        if (this.kgoServed == null) {
            LOG.trace("this.kgoRemaining == null, result=${result}")
            return result
        }
        result = this.kgoServed!!.media.size
        return result
    }

    fun isTypoMiB(): Boolean = this.icon == "Bath"

    fun getStatusPlatform(): String {
        val _beforeMediaSize = this.getBeforeMediaSize()
        val _afterMediaSize = this.getAfterMediaSize()
        val _failureMediaSize = this.getFailureMediaSize()

        val filteredContainers = this.containerS.filter {
                el -> el.isActiveToday
        }

        val containerStatuses = filteredContainers.map { it.getStatusContainer() }

        val isAllErrorContainers = containerStatuses.all {
            it == StatusEnum.ERROR
        }

        if(isAllErrorContainers || _failureMediaSize > 0)
            return StatusEnum.ERROR

        val isAllSuccessContainers = containerStatuses.all {
            it == StatusEnum.SUCCESS
        }

        if(isAllSuccessContainers && _afterMediaSize != 0)
            return StatusEnum.SUCCESS

        val hasUnservedContainers = containerStatuses.any {
            it == StatusEnum.NEW
        }

        if(!hasUnservedContainers && _afterMediaSize != 0)
            return StatusEnum.PARTIAL_PROBLEMS

        if(hasUnservedContainers && _beforeMediaSize != 0)
            return StatusEnum.UNFINISHED

        return StatusEnum.NEW
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
        this.containerS.forEach{ containerEntity ->
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
            LOG.warn(this.orderTimeWarning!!)
            LOG.warn(minutes.toString())
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

    fun isPickupNotEmpty(): Boolean {
        return this.volumePickup != null
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

        this.kgoServed?.media = RealmList()
        for(image in imageS) {
            val imageEntity = ImageEntity.createEmpty(image)
            this.kgoServed?.media?.add(imageEntity)
        }
    }

    fun addRemainingKGOMedia(imageS: List<ImageEntity>) {
        initRemainingKGOEntity()
        this.kgoRemaining?.media = RealmList()
        for(image in imageS) {
            val imageEntity = ImageEntity.createEmpty(image)
            this.kgoRemaining?.media?.add(imageEntity)
        }
    }

    fun setServedKGOVolume(kgoVolume: String) {
        initServedKGOEntity()
        var kgoVolumeDouble: Double = Dnull
        try {
            kgoVolumeDouble = kgoVolume.toDouble()
        } catch (ex: Exception) {
            LOG.error("setServedKGOVolume", ex)
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
            LOG.error("ex", ex)
        }
        this.kgoRemaining?.let{
            it.volume = kgoVolumeDouble
        }
    }

    fun getServeMode(): String? {
        val nonIntegerVolumes = listOf(0.25, 0.5, 0.75, 1.25)
        val hasContainerFailureOrNonIntegerVolume = this.containerS.any {
            it.failureReasonId != 0 || nonIntegerVolumes.contains(it.volume)
        }
        val isPlatformError = this.getFailureMediaSize() != 0 || this.failureReasonId != 0
        val isKgoServed = this.kgoServed?.volume != null
        val isPickedUp = this.volumePickup != null

        if(hasContainerFailureOrNonIntegerVolume || isPlatformError || isKgoServed || isPickedUp ) {
            return ServeMode.PServeF
        }

        if(this.containerS.any { it.volume != null && it.volume!! > 1.25 }) {
            return ServeMode.PServeGroupByContainersF
        }

        return null
    }

    fun addBeforeMedia(imageS: List<ImageEntity>) {
        this.beforeMedia = RealmList()
        for(image in imageS) {
            val imageEntity = ImageEntity.createEmpty(image)
            this.beforeMedia.add(imageEntity)
        }
    }

    fun addPickupMedia(imageS: List<ImageEntity>) {
        this.pickupMedia = RealmList()
        for(image in imageS) {
            val imageEntity = ImageEntity.createEmpty(image)
            this.pickupMedia.add(imageEntity)
        }
    }

    fun addAfterMedia(imageS: List<ImageEntity>) {
        this.afterMedia = RealmList()
        for(image in imageS) {
            val imageEntity = ImageEntity.createEmpty(image)
            this.afterMedia.add(imageEntity)
        }
    }

    fun addFailureMedia(imageS: List<ImageEntity>) {
        this.failureMedia = RealmList()
        for(image in imageS) {
            val imageEntity = ImageEntity.createEmpty(image)
            this.failureMedia.add(imageEntity)
        }
    }

    // TODO: 29.10.2021 ! it.volume = 0.0 ??Error возраст.
    fun mapContainers(containerSRV: List<CoNTaiNeR_know1>, database: RealmRepository): RealmList<ContainerEntity> {
        val result = containerSRV.mapTo(RealmList()) {
            LOG.trace("containerSRV.id=${it.id}")
            val containerEntity = ContainerEntity(
                containerId = it.id,
                platformId = this.platformId,
                number = it.number,
                volume = it.volume,
                isActiveToday = it.isActiveToday,
                status = it.status,
                client = it.client,
                contacts = it.contacts,
                typeId = it.typeId,
                typeName = it.typeName,
                constructiveVolume = it.constructiveVolume,
                failureReasonId = it.failureReasonId,
//                breakdownReasonId = ,
//                comment
                failureMedia = WorkOrderEntity.mapMedia(it.failureMedia),
//                breakdownMedia
//                breakdownComment
                workOrderId = this.workOrderId
            )
            val containerMediaEntity = database.loadContainerMediaEntity(containerEntity)
            val failureMedia = WorkOrderEntity.mapMedia(it.failureMedia)
            containerEntity.addFailureMedia(afterMedia)
            containerMediaEntity.failureMedia.addAll(failureMedia)

            containerEntity
        }
        LOG.debug("result=${result.size}")
        return result
    }

    companion object {
        // TODO: !!!
        fun createEmpty(): PlatformEntity {
            val result = PlatformEntity(platformId = Inull, address = THIS_IS_ERROR)
            return result
        }

        fun toSRV(platforms: List<PlatformEntity>, db: RealmRepository): List<PlatformEntity> {
            for(platform in platforms) {
                LOG.debug("platform.platformId=${platform.platformId}")
                val platformMediaEntity = db.getPlatformMediaEntity(platform)
                platform.beforeMedia = platformMediaEntity.beforeMedia
                platform.kgoServed?.let {
                    it.media = platformMediaEntity.kgoServedMedia
                }
                platform.kgoRemaining?.let {
                    it.media = platformMediaEntity.kgoRemainingMedia
                }
                platform.failureMedia = platformMediaEntity.failureMedia
                platform.pickupMedia = platformMediaEntity.pickupMedia
                platform.afterMedia = platformMediaEntity.afterMedia

                for (container in platform.containerS) {
                    LOG.debug("container.containerId=${container.containerId}")
                    val containerMediaEntity = db.getContainerMediaEntity(container)
                    container.failureMedia = containerMediaEntity.failureMedia
                    container.breakdownMedia = containerMediaEntity.breakdownMedia
                }
            }

            return platforms
        }

        object ServeMode {
            const val PServeF = "PServeF"
            const val PServeGroupByContainersF = "PServeGroupByContainersF"
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

enum class ConfigName(val displayName: String) {
    BOOT_CNT("BOOT_CNT"),
    Snull(ru.smartro.worknote.Snull),
    RUNAPP_CNT("RUNAPP_CNT"),
    SWIPE_CNT("SWIPE_CNT"),
    AIRPLANE_MODE_ON_CNT("AIRPLANE_MODE_ON_CNT"),
    AIRPLANE_MODE_OFF_CNT("AIRPLANE_MODE_OFF_CNT"),
    NOINTERNET_CNT("NOINTERNET_CNT"),
    MAPACTDESTROY_CNT("MAPACTDESTROY_CNT"),
    USER_WORK_SERVE_MODE_CODENAME("USER_WORK_SERVE_MODE_CODENAME"),
}

open class ConfigEntity(
    @PrimaryKey
    private var name: String = Snull,
    var value: String = Snull,
    var isAppEvent: Boolean = false
) : RealmObject() {

    fun toLong(): Long {
        return this.value.toLong()
    }

    fun cntPlusOne() {
        if (this.value == Snull) {
            this.value = "0"
        }
        this.value = (this.toLong() + 1).toString()
    }

//    @Required
//    var status: String = TaskStatus.Open.name
    var configName: ConfigName
        get() {
            return try {
                ConfigName.valueOf(name)
            } catch (e: IllegalArgumentException) {
                ConfigName.Snull
            }
        }
        set(value) {
            this.name = value.displayName.uppercase()
        }
}

open class ContainerMediaEntity(
//    var platformEntity: PlatformEntity? = null,
//    var containerEntity: ContainerEntity? = null,
    @PrimaryKey
    var containerId: Int = Inull,
    var platformId: Int = Inull,
    var breakdownMedia: RealmList<ImageEntity> = RealmList(),
    var failureMedia: RealmList<ImageEntity> = RealmList(),
    var workOrderId: Int = Inull,
    var dev_info: String? = null
): Serializable, RealmObject() {

    fun getBreakdownMediaSize(): Int {
        return this.breakdownMedia.size
    }

    fun getFailureMediaSize(): Int {
        return this.failureMedia.size
    }

//    fun getPlatform(): PlatformEntity {
//        return this.platformEntity!!
//    }

    companion object {
        // TODO: !!!
        fun createEmpty(): ContainerMediaEntity {
            val result = ContainerMediaEntity(dev_info=THIS_IS_ERROR)
            return result
        }
    }
}

open class ContainerEntity(
    @Expose
    @SerializedName("id")
    @PrimaryKey
    var containerId: Int = Inull,
    var platformId: Int = Inull,
    var platformEntity: PlatformEntity? = null,
    @Expose
    @SerializedName("number")
    var number: String? = null,
    @Expose
    @SerializedName("volume")
    var volume: Double? = null,
    @Expose
    @SerializedName("is_active_today")
    var isActiveToday: Boolean = false,
    @Expose
    @SerializedName("status")
    var status: String? = null,
    @Expose
    @SerializedName("client")
    var client: String? = null,
    @Expose
    @SerializedName("contacts")
    var contacts: String? = null,
    @Expose
    @SerializedName("type_id")
    var typeId: Int? = null,
    @Expose
    @SerializedName("type_name")
    var typeName: String? = null,
    @Expose
    @SerializedName("constructive_volume")
    var constructiveVolume: Double? = null,
    @Expose
    @SerializedName("failure_reason_id")
    var failureReasonId: Int = 0,
    @Expose
    @SerializedName("breakdown_reason_id")
    var breakdownReasonId: Int? = null,
    @Expose
    @SerializedName("comment")
    var comment: String? = null,
    @Expose
    @SerializedName("failure_media")
    var failureMedia: RealmList<ImageEntity> = RealmList(),
    @Expose
    @SerializedName("breakdown_media")
    var breakdownMedia: RealmList<ImageEntity> = RealmList(),
    @Expose
    @SerializedName("breakdown_comment")
    var breakdownComment: String? = null,
    var isWorkOrderProgress: Boolean = false,
    var isWorkOrderComplete: Boolean = false,
    var workOrderId: Int = Inull
    ) : Serializable, RealmObject() {

    fun getStatusContainer(): String {
        if (this.failureReasonId != 0) {
            return StatusEnum.ERROR
        }
        if(this.volume != null) {
            return StatusEnum.SUCCESS
        }
        if (!this.isActiveToday) {
            return StatusEnum.SUCCESS
        }

        return StatusEnum.NEW
    }

    private fun getFailureMediaSize(): Int {
        var res = 0
        if (this.failureReasonId <= 0) {
            return res
        }
        res = this.failureMedia.size

        return res
    }


    private fun getBreakdownMediaSize(): Int {
        var res = 0
        if (this.breakdownReasonId == null ) {
            return res
        }
        res = this.breakdownMedia.size

        return res
    }

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
        if (this.getStatusContainer() == StatusEnum.ERROR) {
            return Color.RED
        }
        return ContextCompat.getColor(context, R.color.colorAccent)
    }

    fun isFailureNotEmpty(): Boolean {
        return getFailureMediaSize() > 0
    }

    fun isBreakdownNotEmpty(): Boolean {
        return getBreakdownMediaSize() > 0
    }

    fun addFailureMedia(imageS: List<ImageEntity>) {
        this.failureMedia = RealmList()
        for(image in imageS) {
            val imageEntity = ImageEntity.createEmpty(image)
            this.failureMedia.add(imageEntity)
        }
    }

    fun addBreakdown(imageS: List<ImageEntity>) {
        this.breakdownMedia = RealmList()
        for(image in imageS) {
            val imageEntity = ImageEntity.createEmpty(image)
            this.breakdownMedia.add(imageEntity)
        }
    }

    companion object {
        // TODO: !!!
        fun createEmpty(): ContainerEntity {
            val result = ContainerEntity(containerId = Inull, client = THIS_IS_ERROR, number = THIS_IS_ERROR)
            return result
        }
    }
}

// TODO: //ContainerGroupClient
open class ContainerGROUPClientEntity(
    var platformId: Int = Inull,
    var client: String? = null ,

    var containers: RealmList<ContainerEntity> = RealmList(),
) : Serializable, RealmObject() {
// TODO: //GroBy

    // TODO: !!r_dos!!!
    fun addClient(container: ContainerEntity) {
        this.client = container.client
    }

    fun getClientForUser(): String {
        // TODO: VT!!!)(
        var result = "Клиент не указан"
        if (client == "Клиент не указан") {
            result = "Клиент не указан (2"
            return result
        }
        if (client == null) {
            return result
        }
        result = client!!
        return result
    }

    companion object {
        // TODO: !!!
        fun createEmpty(): ContainerGROUPClientEntity {
            val result = ContainerGROUPClientEntity(platformId = Inull, client = THIS_IS_ERROR)
            return result
        }
    }
}
// TODO: //GroBy
open class ContainerGROUPClientTypeEntity(
    // TODO: //GroBy
    var platformId: Int = Inull,
    var client: String? = null,
    var typeId: Int? = null,
    var typeName: String? = null,
    var containers: RealmList<ContainerEntity> = RealmList(),
//    var serveCNT: Int = 0
) : Serializable, RealmObject() {
    fun getTypeCount(): String {
        val result = containers.size.toString()
        LOG.trace("result=${result}")
        return result
    }

    fun getTypetForUser(): String {
        // TODO: VT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        var result = "Тип не указан"
        if (typeName == "Тип не указан") {
            result = "Тип не указан (2"
            return result
        }
        if (typeName == null) {
            return result
        }
        result = typeName!!
        return result
    }

    fun getServeCNT(): Int {
        return containers.sumOf {
            if(it.volume != null)
                it.volume!!
            else
                0.0
        }.toInt()
    }

    companion object {
        // TODO: !!!
        fun createEmpty(): ContainerGROUPClientTypeEntity {
            val result = ContainerGROUPClientTypeEntity(platformId = Inull, client = THIS_IS_ERROR, typeName = THIS_IS_ERROR)
            return result
        }
    }
}


open class ImageEntity(
    @Expose
    var image: String? = null,
    @Expose
    var date: Long? = null,
    @Expose
    var coords: RealmList<Double> = RealmList(),
    @Expose
    var accuracy: String? = null,
    @Expose
    var lastKnownLocationTime: Long? =null,
    @Expose
    var md5: String = Snull
) : Serializable, RealmObject() {

    companion object {
        // TODO: !!!
        fun createEmpty(imageEntity: ImageEntity? = null): ImageEntity {
            val result = ImageEntity()
            if (imageEntity == null) {
                return result
            }
            result.coords = imageEntity.coords
            result.md5 = imageEntity.md5
            result.date = imageEntity.date
            result.accuracy = imageEntity.accuracy
            result.lastKnownLocationTime = imageEntity.lastKnownLocationTime
            return result
        }
    }
}