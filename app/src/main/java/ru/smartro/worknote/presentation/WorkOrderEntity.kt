package ru.smartro.worknote.log.todo


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
import ru.smartro.worknote.App.Companion.PhotoTypeMapping.AFTER_MEDIA
import ru.smartro.worknote.App.Companion.PhotoTypeMapping.BEFORE_MEDIA
import ru.smartro.worknote.App.Companion.PhotoTypeMapping.BREAKDOWN_MEDIA
import ru.smartro.worknote.App.Companion.PhotoTypeMapping.FAILURE_MEDIA
import ru.smartro.worknote.App.Companion.PhotoTypeMapping.KGO_REMAINING_MEDIA
import ru.smartro.worknote.App.Companion.PhotoTypeMapping.KGO_SERVED_MEDIA
import ru.smartro.worknote.BuildConfig.URL__SMARTRO_PING
import ru.smartro.worknote.presentation.*
import ru.smartro.worknote.work.work.RealmRepository
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
    var start: StartWorkOrderEntity? = null,
    var unload: UnloadWorkOrderEntity? = null,

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
    var failure_id: Int? = null,
    var finished_at: Long? = null,
    var unload_type: Int? = null,
    var unload_value: Double? = null
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
            platform.containerS.forEach {
                containersCnt++
                val containerStatus = it.getStatusContainer()
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

    fun getEarlyCompleteBodyIn(): EarlyCompleteBodyIn {
        var result = EarlyCompleteBodyIn(Inull, Lnull, Inull, Dnull)
        if (this.failure_id == null) {
            return result
        }
        if (this.finished_at == null) {
            return result
        }
        if (this.unload_type == null) {
            return result
        }
        if (this.unload_value == null) {
            return result
        }
        result = EarlyCompleteBodyIn(this.failure_id!!, this.finished_at!!, this.unload_type!!, this.unload_value!!)
        return result
    }

    fun getCompleteBodyIn(): CompleteWayBodyIn {
        var result = CompleteWayBodyIn(Lnull, Inull, Dnull)
        if (this.finished_at == null) {
            return result
        }
        if (this.unload_type == null) {
            return result
        }
        if (this.unload_value == null) {
            return result
        }
        result = CompleteWayBodyIn(this.finished_at!!, this.unload_type!!, this.unload_value!!)
        return result
    }

    companion object {
        
        fun mapMedia(
            data: List<SynchroOidWidOutBodyDataWorkorderMedia>,
            organisationId: Int? = null,
            platformId: Int? = null,
            mediaType: String? = null,
            containerId: Int? = null,
        ): RealmList<ImageInfoEntity> {
            var idx = 0L
            return data.mapTo(RealmList()) {
                idx++
                // TODO: WTF???
                val imageInfo = ImageInfoEntity(
                    date = idx,
                    coords = RealmList(),
                    url = URL__SMARTRO_PING + "file/" + it.link,
                    md5 = it.hash,
                    organisationId = organisationId,
                    platformId = platformId,
                    mediaType = mediaType
                )

                if(containerId != null)
                    imageInfo.containerId = containerId

                imageInfo
            }
        }


        private fun mapPlatforms(data: List<SynchroOidWidOutBodyDataWorkorderPlatform>, workorderId: Int, database: RealmRepository): RealmList<PlatformEntity> {
            val result = data.mapTo(RealmList()) {
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

                    needCleanup = it.cleanup,

                    /** volumeKGO = null,*/

                    /** volumeKGO = null,*/
                    /** volumeKGO = null,*/
                    /** volumeKGO = null,*/
                )
                platformEntity.containerS = platformEntity.mapContainers(
                    it.coNTaiNeRKnow1s,
                    App.getAppParaMS().getOwnerId(),
                    platformEntity.platformId
                )

                val organisationId = App.getAppParaMS().getOwnerId()
                val platformId = it.id

                val afterMedia = mapMedia(it.afterMedia, organisationId, platformId, AFTER_MEDIA)
                platformEntity.addAfterMedia(afterMedia)
                val beforeMedia = mapMedia(it.beforeMedia, organisationId, platformId, BEFORE_MEDIA)
                platformEntity.addAfterMedia(beforeMedia)
                if(it.failureMedia != null) {
                    val failureMedia = mapMedia(it.failureMedia, organisationId, platformId, FAILURE_MEDIA)
                    platformEntity.addFailureMedia(failureMedia)
                }

                it.kgo_served?.let {
                    it.volume?.let {
                        platformEntity.setServedKGOVolume(it.toStr())
                    }
                    it.media?.let {
                        val kgoServedMedia = mapMedia(it, organisationId, platformId, KGO_SERVED_MEDIA)
                        platformEntity.addServerKGOMedia(kgoServedMedia)
                    }
                }

                it.kgo_remaining?.let {
                    it.volume?.let {
                        platformEntity.setRemainingKGOVolume(it.toStr())
                    }
                    it.media?.let {
                        val kgoRemainingMedia = mapMedia(it, organisationId, platformId, KGO_REMAINING_MEDIA)
                        platformEntity.addRemainingKGOMedia(kgoRemainingMedia)
                    }
                }

                platformEntity.networkStatus = platformEntity.isNotNewPlatform()
                platformEntity
            }
            return result
        }

        private fun mapStart(data: SynchroOidWidOutBodyDataWorkorderStart?, workorderId: Int): StartWorkOrderEntity? {
            var result: StartWorkOrderEntity? = null
            if (data != null) {
                result = StartWorkOrderEntity(
                    coords = RealmList(data.coords[0], data.coords[1]),
                    name = data.name,
                    id = data.id,
                    workOrderId = workorderId
                )
            }
            return result
        }

        private fun mapUnload(data: SynchroOidWidOutBodyDataWorkorderUnload?, workorderId: Int): UnloadWorkOrderEntity? {
            var result: UnloadWorkOrderEntity? = null
            if (data != null) {
                result = UnloadWorkOrderEntity(
                    coords = RealmList(data.coords[0], data.coords[1]),
                    name = data.name,
                    id = data.id,
                    workOrderId = workorderId
                )
            }
            return result
        }


        /**public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapTo(destination: C, transform: (T) -> R): C {        */
        fun map(woRKoRDeRknow1List: List<SynchroOidWidOutBodyDataWorkorder>, database: RealmRepository): RealmList<WorkOrderEntity> {
            val res = RealmList<WorkOrderEntity>()
                for (woRKoRDeRknow1 in woRKoRDeRknow1List) {
                    try {
                        val workOrder = WorkOrderEntity(
                            id = woRKoRDeRknow1.id,
                            start_at = App.getAppliCation().currentTime(),
                            name = woRKoRDeRknow1.name,
                            waste_type_id = woRKoRDeRknow1.waste_type?.id,
                            waste_type_name = woRKoRDeRknow1.waste_type?.name,
                            waste_type_color = woRKoRDeRknow1.waste_type?.color?.hex,
                            platforms = mapPlatforms(woRKoRDeRknow1.platformKnow1s, woRKoRDeRknow1.id, database),
                            start = mapStart(woRKoRDeRknow1.start, woRKoRDeRknow1.id),
                            unload = mapUnload(woRKoRDeRknow1.uNLoaDknow1, woRKoRDeRknow1.id)
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

open class KGOEntity(
    @Expose
    var volume: Double? = null,
    @Expose
    var media: RealmList<ImageInfoEntity> = RealmList()
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

open class PlatformVoiceCommentEntity(
    @PrimaryKey
    var platformId: Int = Inull,
    @Expose
    @SerializedName("byte_array")
    var voiceByteArray: ByteArray? = null,
    var platformEntity: PlatformEntity? = null,
    var workOrderEntity: WorkOrderEntity? = null,
    var updateAd: String? = null,  //todo:))это компромис между updated_at и update_at
    var createAd: String = App.getAppliCation().currentTime(),
    var dev_info: String? = null
): Serializable, RealmObject() {

    companion object {
        fun createEmpty(): PlatformVoiceCommentEntity {
            val result = PlatformVoiceCommentEntity(dev_info= THIS_IS_ERROR)
            return result
        }
    }
}


open class RegionEntity(
    @Expose
    @SerializedName("id")
    @PrimaryKey
    var id: Int = Inull,
    @Expose
    var name: String = Snull,
    @Expose
    var showName: String? = null,
    @Expose
    var size: String = Snull,
    @Expose
    var cities: RealmList<String> = RealmList()
) : Serializable, RealmObject()  {

    fun showForUser(): String {
        return showName ?: name
    }
}

open class OrganisationEntity(
    @PrimaryKey
    var id: Int = Inull,
    var name: String = Snull,
    var dev_info: String? = null

): RealmObject() {
    companion object {
        // TODO: !!!
        fun createEmpty(): OrganisationEntity {
            val result = OrganisationEntity(dev_info= THIS_IS_ERROR)
            return result
        }
    }
}

open class VehicleEntity(
    @PrimaryKey
    var id: Int = Inull,
    var name: String = Snull,
    var organisationId: Int = Inull
): RealmObject()


open class WaybillEntity(
    @PrimaryKey
    var id: Int = Inull,
    var number: String = Snull,

    var organisationId: Int = Inull,
    var vehicleId: Int = Inull,

    ): RealmObject()

object StatusEnum {
    const val NEW = "new"
    const val SUCCESS = "success"
    const val ERROR = "error"
    const val UNFINISHED = "unfinished"
    const val PARTIAL_PROBLEMS = "partial_problems"
    const val NOT_ACTIVE="not_active"
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
    var srpId: Long? = null,
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

    @SerializedName("voice_comment")
    var platformVoiceCommentEntity: PlatformVoiceCommentEntity? = null,

    @Expose
    var voiceComment: String? = null,

    @Expose
    var comment: String? = null,

    @Expose
    @SerializedName("status")
    var status: String? = Snull,

    @Expose
    @SerializedName("unload")
    var unloadEntity: PlatformUnloadEntity? = null,
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

    @Expose
    var needCleanup: Boolean = false,

    var isWorkOrderProgress: Boolean = false,
    var isWorkOrderComplete: Boolean = false,

    @Expose
    @SerializedName("after_media")
    var afterMedia: RealmList<ImageInfoEntity> = RealmList(),
    @Expose
    @SerializedName("before_media")
    var beforeMedia: RealmList<ImageInfoEntity> = RealmList(),
    @Expose
    @SerializedName("pickup_media")
    var pickupMedia: RealmList<ImageInfoEntity> = RealmList(),
    @Expose
    @SerializedName("failure_media")
    var failureMedia: RealmList<ImageInfoEntity> = RealmList(),
    @Expose
    @SerializedName("kgo_remaining")
    var kgoRemaining: KGOEntity? = null,
    @Expose
    @SerializedName("kgo_served")
    var kgoServed: KGOEntity? = null,

    ) : Serializable, RealmObject() {

    fun getBeforeMediaSize() = this.beforeMedia.size
    fun getAfterMediaSize() = afterMedia.size
    fun getFailureMediaSize() = this.failureMedia?.size ?: 0
    fun getPickupMediaSize() = this.pickupMedia.size

    fun getRemainingKGOMediaSize(): Int {
        val result = this.getRemainingKGOMedia().size
        return result
    }

    fun getRemainingKGOMedia(): RealmList<ImageInfoEntity> {
        var result = RealmList<ImageInfoEntity>()
        if (this.kgoRemaining == null) {
            LOG.trace("if (this.kgoServed == null) { result.size=${result}")
            return result
        }
        result = this.kgoRemaining!!.media
        LOG.trace("result.size=${result.size}")
        return result
    }

    fun getServedKGOMediaSize(): Int {
        val result = this.getServedKGOMedia().size
        return result
    }

    fun getServedKGOMedia(): RealmList<ImageInfoEntity> {
        var result = RealmList<ImageInfoEntity>()
        if (this.kgoServed == null) {
            LOG.trace("if (this.kgoServed == null) { result.size=${result}")
            return result
        }
        result = this.kgoServed!!.media
        LOG.trace("result.size=${result.size}")
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

    fun addServerKGOMedia(imageS: List<ImageInfoEntity>) {
        initServedKGOEntity()

        this.kgoServed?.media = RealmList()
        for(image in imageS) {
            this.kgoServed?.media?.add(image)
        }
    }

    fun addRemainingKGOMedia(imageS: List<ImageInfoEntity>) {
        initRemainingKGOEntity()
        this.kgoRemaining?.media = RealmList()
        for(image in imageS) {
            this.kgoRemaining?.media?.add(image)
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

    fun addBeforeMedia(imageS: List<ImageInfoEntity>) {
        this.beforeMedia = RealmList()
        for(image in imageS) {
            this.beforeMedia.add(image)
        }
    }

    fun addPickupMedia(imageS: List<ImageInfoEntity>) {
        this.pickupMedia = RealmList()
        for(image in imageS) {
            this.pickupMedia.add(image)
        }
    }

    fun addAfterMedia(imageS: List<ImageInfoEntity>) {
        this.afterMedia = RealmList()
        for(image in imageS) {
            this.afterMedia.add(image)
        }
    }

    fun addFailureMedia(imageS: List<ImageInfoEntity>) {
        this.failureMedia = RealmList()
        for(image in imageS) {
            this.failureMedia?.add(image)
        }
    }

    // TODO: 29.10.2021 ! it.volume = 0.0 ??Error возраст.
    fun mapContainers(
        containerSRV: List<SynchroOidWidOutBodyDataWorkorderPlatformContainer>,
        organisationId: Int,
        platformId: Int
    ): RealmList<ContainerEntity> {
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
//                breakdownMedia
//                breakdownComment
                workOrderId = this.workOrderId
            )
            if(it.failureMedia != null) {
                val failureMedia = WorkOrderEntity.mapMedia(it.failureMedia, organisationId, platformId, FAILURE_MEDIA, it.id)
                containerEntity.addFailureMedia(failureMedia)
            }

            if(it.breakdownMedia != null) {
                val breakdownMedia = WorkOrderEntity.mapMedia(it.breakdownMedia, organisationId, platformId, BREAKDOWN_MEDIA, it.id)
                containerEntity.addBreakdownMedia(breakdownMedia)
            }

            containerEntity
        }
        LOG.debug("result=${result.size}")
        return result
    }

    //wtf?Upload
    fun ploadUnloadEntity(): PlatformUnloadEntity {
        val result = PlatformUnloadEntity.createEmpty()
        result.workOrderId = this.workOrderId
        result.platformId = this.platformId
        this.unloadEntity = result
        return  result
    }

    companion object {
        // TODO: !!!
        fun createEmpty(): PlatformEntity {
            val result = PlatformEntity(platformId = Inull, address = THIS_IS_ERROR)
            return result
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

open class StartWorkOrderEntity(
    var coords: RealmList<Double> = RealmList(),
    var name: String? = null,
    var id: Int? = null,
    var workOrderId: Int = Inull
) : Serializable, RealmObject()

open class UnloadWorkOrderEntity(
    var coords: RealmList<Double> = RealmList(),
    var name: String? = null,
    var id: Int? = null,
    var workOrderId: Int = Inull,
    var dev_info: String? = null
) : RealmObject() {
    companion object {
        // TODO: !!!
        fun createEmpty(): UnloadWorkOrderEntity {
            val result = UnloadWorkOrderEntity(dev_info= THIS_IS_ERROR)
            return result
        }
    }
}


open class PlatformUnloadEntity(
    @PrimaryKey
    var platformId: Int = Inull,
    @SerializedName("before_media")
    @Expose
    var beforeMedia: RealmList<ImageInfoEntity> = RealmList(),
    @SerializedName("after_media")
    @Expose
    var afterMedia: RealmList<ImageInfoEntity> = RealmList(),
    @SerializedName("before_value")
    @Expose
    var beforeValue: Float? = null,
    @SerializedName("after_value")
    @Expose
    var afterValue: Float? = null,
    @SerializedName("ticket_value")
    @Expose
    var ticketValue: Float? = null,
    var workOrderId: Int = Inull,

    var dev_info: String? = null
): RealmObject() {
    companion object {
        // TODO: !!!
        fun createEmpty(): PlatformUnloadEntity {
            val result = PlatformUnloadEntity(dev_info= THIS_IS_ERROR)
            return result
        }
    }
}

enum class ConfigName(val displayName: String) {
    Snull(ru.smartro.worknote.Snull),
    BOOT_CNT("BOOT_CNT"),
    RUNAPP_CNT("RUNAPP_CNT"),
    SWIPE_CNT("SWIPE_CNT"),
    AIRPLANE_MODE_ON_CNT("AIRPLANE_MODE_ON_CNT"),
    AIRPLANE_MODE_OFF_CNT("AIRPLANE_MODE_OFF_CNT"),
    NOINTERNET_CNT("NOINTERNET_CNT"),
    MAPACTDESTROY_CNT("MAPACTDESTROY_CNT"),
    USER_WORK_SERVE_MODE_CODENAME("USER_WORK_SERVE_MODE_CODENAME"),
    AAPP__IS_MODE__UNLOAD("IS_MODE__UNLOAD"),
    AAPP__LAST_SYNCHROTIME_IN_SEC("APP_PARAM__LAST_SYNCHROTIME_IN_SEC"),
    AAPP__LAST_PLATFORM_ID("AAPP__LAST_PLATFORM_ID")
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
    var failureMedia: RealmList<ImageInfoEntity> = RealmList(),
    @Expose
    @SerializedName("breakdown_media")
    var breakdownMedia: RealmList<ImageInfoEntity> = RealmList(),
    @Expose
    @SerializedName("breakdown_comment")
    var breakdownComment: String? = null,
    var isWorkOrderProgress: Boolean = false,
    var isWorkOrderComplete: Boolean = false,
    var workOrderId: Int = Inull
    ) : Serializable, RealmObject() {

    fun getStatusContainer(): String {
        if (!this.isActiveToday) {
            return StatusEnum.NOT_ACTIVE
        }
        if (this.failureReasonId != 0) {
            return StatusEnum.ERROR
        }
        if(this.volume != null) {
            return StatusEnum.SUCCESS
        }

        return StatusEnum.NEW
    }

    private fun getFailureMediaSize(): Int {
        var res = 0
        if (this.failureReasonId <= 0) {
            return res
        }
        res = this.failureMedia?.size ?: 0

        return res
    }

    private fun getBreakdownMediaSize(): Int {
        var res = 0
        if (this.breakdownReasonId == null ) {
            return res
        }
        res = this.breakdownMedia?.size ?: 0

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

    fun addFailureMedia(imageS: List<ImageInfoEntity>) {
        this.failureMedia = RealmList()
        for(image in imageS) {
            this.failureMedia?.add(image)
        }
    }

    fun addBreakdownMedia(imageS: List<ImageInfoEntity>) {
        this.breakdownMedia = RealmList()
        for(image in imageS) {
            this.breakdownMedia?.add(image)
        }
    }

    fun addBreakdown(imageS: List<ImageInfoEntity>) {
        this.breakdownMedia = RealmList()
        for(image in imageS) {
            this.breakdownMedia?.add(image)
        }
    }

    fun getIconFromStatus(): Int {
        val currentStatus = this.getStatusContainer()
        return when (currentStatus) {
            StatusEnum.NEW -> R.drawable.ic_euro_blue
            StatusEnum.SUCCESS ->  R.drawable.ic_euro_green
            StatusEnum.ERROR -> R.drawable.ic_euro_red
            else -> R.drawable.ic_euro_gray
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
    var client: String? = null,

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


open class ImageInfoEntity(
    @Expose
    var md5: String = Snull,
    @Expose
    @SerializedName("updated_at")
    var createdAt: Long = 0,
    @Expose
    var date: Long? = null,
    @Expose
    var coords: RealmList<Double> = RealmList(),
    @Expose
    var accuracy: String? = null,
    @Expose
    var lastKnownLocationTime: Long? = null,

    var url: String? = null,
    var organisationId: Int? = null,
    var platformId: Int? = null,
    var containerId: Int? = null,
    var mediaType: String? = null,
    var synchroAttempt: Long = Lnull,
    var synchroTime: Long = Lnull

) : Serializable, RealmObject() {

    fun isContainer() = this.containerId != null

    init {
        LOG.debug("CREATED:::: ${md5} ${createdAt} ${date} ${coords} ${accuracy} ${lastKnownLocationTime}")
    }

    companion object {
//        // TODO: !!!
//        fun createEmpty(imageInfo: ImageInfoEntity? = null): ImageInfoEntity {
//            val result = ImageInfoEntity()
//            if (imageInfo == null) {
//                return result
//            }
//            result.coords = imageInfo.coords
//            result.md5 = imageInfo.md5
//            result.date = imageInfo.date
//            result.accuracy = imageInfo.accuracy
//            result.lastKnownLocationTime = imageInfo.lastKnownLocationTime
//            return result
//        }
    }
}

open class CancelWayReasonEntity(
    @PrimaryKey
    var id: Int = 0,
    var problem: String? = null
) : RealmObject()

open class FailReasonEntity(
    @PrimaryKey
    var id: Int = 0,
    var problem: String? = null
) : RealmObject()
open class BreakDownReasonEntity(
    @PrimaryKey
    var id: Int = 0,
    var problem: String? = null
) : RealmObject()
