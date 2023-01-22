package ru.smartro.worknote.ac

import android.location.Location
import com.yandex.mapkit.geometry.Point
import io.realm.RealmList
import ru.smartro.worknote.*
import ru.smartro.worknote.log.todo.ImageInfoEntity

class PoinT(latitude: Double, longitude: Double,
    // TODO:!!pointAccuracy: Float?
            private val pointTime: Long?, private val pointAccuracy: Float?
): Point(latitude, longitude) {

    constructor() : this(Dnull, Dnull, LTIMEnull, Fnull) {
        isFakeGPS = true
    }

    constructor(lat: Double, long: Double) : this(lat, long, null, null)

    constructor(lat: Float, long: Float, time: Long, accuracy: Float) : this(lat.toDouble(), long.toDouble(), time, accuracy)
    constructor(lat: DoubleCool, long: DoubleCool, time: LongCool, accuracy: FloatCool) : this(lat, long, time, accuracy.LET)
    constructor(lat: Double, long: Double, time: Long, accuracy: Double) : this(lat, long, time, accuracy.toFloat())

    fun getTime(): Long {
        return pointTime!!
    }

    fun getAccuracy(): Float {
        return pointAccuracy!!
    }


    fun PointTimeToLastKnowTime_SRV(): Long {
        val res = System.currentTimeMillis() - pointTime!!
        return res
    }

    fun PointTOBaseData(): RealmList<Double> {
        return RealmList(longitude, latitude)
    }

    fun PoinAccuracyTO_SRV(): String {
        val res = pointAccuracy.toString()
        return res
    }

    fun getImageEntity(
        platformId: Int,
        mediaType: String,
        containerId: Int? = null
    ): ImageInfoEntity {
        val timeStamp = App.getAppliCation().timeStampInSec()
        val imageInfo = ImageInfoEntity(
            date = timeStamp,
            createdAt = timeStamp,
            coords = PointTOBaseData(),
            accuracy = PoinAccuracyTO_SRV(),
            lastKnownLocationTime = PointTimeToLastKnowTime_SRV(),
            organisationId = App.getAppParaMS().ownerId,
            platformId = platformId,
            mediaType = mediaType,
            containerId = containerId
        )
        return imageInfo
    }

    //isAddParamS
    fun isSaveGPS(): Boolean {
        val resFalse = false

        if (App.getAppParaMS().iSoldGPSdataSaved()) {
            return resFalse
        }
        if (App.getAppParaMS().isOldGPSbaseDate(pointTime!!)) {
            return true
        }

        return resFalse
    }

    //    todo: смотри fun findPlatformByCoord(
    //если точность меньше либо равно 15 метров используем  констату 15
    //если больше 15> то точность точность без константа:
    // LAT15M/15 * ТОЧНОСТЬ GPS
    // и LONG15M/15 * ТОЧНОСТЬ GPS
    fun isThisPoint(coordLat: Double, coordLong: Double): Boolean {
        //lat=0,000133755 это 15 метров
        val LAT15M = 0.000008917
        val LONG15M = 0.00001488
//        long=0,0002232 это 15 метров
        var koef = 15f
        this.pointAccuracy?.let {
            koef = 15f + it
        }
//        koef = 1500f

        val minLat = coordLat - LAT15M*koef
        val maxLat = coordLat + LAT15M*koef
        val minLong = coordLong - LONG15M*koef
        val maxLong = coordLong + LONG15M*koef

        val res = this.latitude in minLat..maxLat && this.longitude in minLong..maxLong
        return res
    }

    fun showForUser(): String {
        val resTrue = "${this.latitude} , ${this.longitude}"
        return resTrue
    }


    companion object {
        fun fromLocation(finalLoc: Location?): PoinT {
            if(finalLoc != null) {
                return PoinT(finalLoc.latitude, finalLoc.longitude, finalLoc.time, finalLoc.accuracy)
            }
            return PoinT()
        }
    }

    private var isFakeGPS: Boolean = false
    var Location: Location? = null
        get() {
            if (field == null) {
                return Location("")
            }
            return field
        }
        set(value) {field = value}

}