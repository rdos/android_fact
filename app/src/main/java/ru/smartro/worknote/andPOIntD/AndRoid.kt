package ru.smartro.worknote.andPOIntD

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import com.yandex.mapkit.geometry.Point
import io.realm.RealmList
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.FloatCool
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.work.ImageEntity

class AndRoid {

    companion object {
        private var mInstance: AndRoid? = null
        //todo: getService(id here)
        fun getService(): LocationManager {
            if (mInstance == null) {
                mInstance = AndRoid()
            }
            return mInstance!!.getLocationService()

        }

        fun getCriterial(): Criteria {
            val criteria = Criteria()
            criteria.powerRequirement = Criteria.POWER_HIGH
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.isSpeedRequired = false
            criteria.isAltitudeRequired = false
            criteria.isBearingRequired = false
            criteria.isCostAllowed = false
            return criteria
        }

        fun getProviderName(): String? {
            return getService().getBestProvider(getCriterial(), true)
        }

        fun getProviderNameS(): MutableList<String> {
            return getService().allProviders
        }

    }

    fun gpSPoinT(): Point {
        return Point()
    }


    private var mLocationManagerSystem: LocationManager? = null

    fun getLocationService(): LocationManager {
        //todo: ПЕРЕключатель.ключ.вкл.ON_выкл-OFF
        if (mLocationManagerSystem == null) {
            mLocationManagerSystem = App.getAppliCation().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        return mLocationManagerSystem!!

    }


//        fun gpSPoinT(): Point {
//            GPSpoinT?.let {
//                return GPSpoinT!!
//            }
//            return LocationManagerUtils.getLastKnownLocation()!!.position
//        }


    public class PoinT(
        latitude: Double, longitude: Double
        , private val pointTime: Long
        , private val pointAccuracy: Float
    ): Point(latitude, longitude) {

        constructor(lat: Float, long: Float, time: Long, accuracy: Float) : this(lat.toDouble(), long.toDouble(), time, accuracy)
        constructor(lat: DoubleCool, long: DoubleCool, time: LongCool, accuracy: FloatCool) : this(lat, long, time, accuracy.VAL)
        constructor(lat: Double, long: Double, time: Long, accuracy: Double) : this(lat, long, time, accuracy.toFloat())

        fun PointToListDouble(): List<Double> {
            return listOf(latitude, longitude)
        }
        
        fun PointTimeToLastKnowTime_SRV(): Long {
            return System.currentTimeMillis() - pointTime
        }

        fun PointTOBaseDate(): RealmList<Double> {
           return RealmList(latitude, longitude)
        }

        fun PoinAccuracyTO_SRV(): String {
            return pointAccuracy.toString()
        }

        fun inImageEntity(imageBase64: String): ImageEntity {
            val imageEntity = ImageEntity(imageBase64, MyUtil.timeStamp(),
                PointTOBaseDate(), PoinAccuracyTO_SRV(), PointTimeToLastKnowTime_SRV())
            return imageEntity
        }

        //isAddParamS
        fun isSaveGPS(): Boolean {
            val resFalse = false

            if (App.getAppParaMS().isLastGPSSaved()) {
                return resFalse
            }
            if (App.getAppParaMS().isLastGPS(pointTime)) {
                return true
            }

           return resFalse
        }

        var Location: Location? = null
            get() {
                if (field == null) {
                    return Location("")
                }
               return field
            }
            set(value) {field = value}

    }
}