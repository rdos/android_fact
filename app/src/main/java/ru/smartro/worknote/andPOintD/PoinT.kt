package ru.smartro.worknote.andPOintD

import android.location.Location
import com.yandex.mapkit.geometry.Point
import io.realm.RealmList
import ru.smartro.worknote.*
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.work.ImageEntity

class PoinT(latitude: Double, longitude: Double,
    // TODO:
            private val pointTime: Long?, private val pointAccuracy: Float?
): Point(latitude, longitude) {
    //todo: !r_dos find fun gpSPoinT()
    //  todo: r_dos, use isShowForUser :: e.b.a
    //todo: r_dos, use isShowForUser :: e.b.a
//        private var LocationLAT: DoubleCool = latitude
//        private var LocationLONG: DoubleCool = longitude
//        private var LocationTIME: LongCool = pointTime

//        private var LocationACCURACY = FloatCool("LocationACCURACY", this)


    constructor() : this(Dnull, Dnull, Lnull, Fnull) {
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

    fun inImageEntity(imageBase64: String, isNoLimitPhoto: Boolean): ImageEntity {
        val imageEntity = ImageEntity(imageBase64, MyUtil.timeStamp(),
            PointTOBaseData(), PoinAccuracyTO_SRV(), PointTimeToLastKnowTime_SRV())
        imageEntity.isNoLimitPhoto = isNoLimitPhoto
        return imageEntity
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

    fun isThisPoint(coordLat: Double, coordLong: Double): Boolean {
        //lat=0,000133755 это 15 метров
        val LAT15M = 0.000133755
        val LONG15M = 0.0002232
//        long=0,0002232 это 15 метров
        val minLat = latitude - LAT15M
        val maxLat = latitude + LAT15M
        val minLong = longitude - LONG15M
        val maxLong = longitude + LONG15M

        val res = coordLat in minLat..maxLat && coordLong in minLong..maxLong
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