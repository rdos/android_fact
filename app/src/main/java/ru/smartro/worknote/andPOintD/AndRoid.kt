package ru.smartro.worknote.andPOintD

import android.content.Context
import android.location.Criteria
import android.location.LocationManager
import com.yandex.mapkit.geometry.Point
import ru.smartro.worknote.*

//AndRoid.PoinT лучше/е?
/**
private var GPSpoinT: AndRoid.PoinT? = null
get() {
field?.let {
return field
}
return getAppParaMS().getAlwaysGPS()
}
set(value) { field = value }
 */
class AndRoid {

    companion object {
        private var mInstance: ru.smartro.worknote.andPOintD.AndRoid? = null
        //todo: getService(id here)
        fun getService(): LocationManager {
            if (ru.smartro.worknote.andPOintD.AndRoid.Companion.mInstance == null) {
                ru.smartro.worknote.andPOintD.AndRoid.Companion.mInstance = ru.smartro.worknote.andPOintD.AndRoid()
            }
            return ru.smartro.worknote.andPOintD.AndRoid.Companion.mInstance!!.getLocationService()

        }

        fun getCriterial(): Criteria {
            val criteria = Criteria()
            criteria.powerRequirement = Criteria.POWER_HIGH
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.isSpeedRequired = false
            criteria.isAltitudeRequired = false
            criteria.isBearingRequired = false
//            criteria.isCostAllowed = false
            return criteria
        }

        fun getProviderName(): String {
            return ru.smartro.worknote.andPOintD.AndRoid.Companion.getService()
                .getBestProvider(ru.smartro.worknote.andPOintD.AndRoid.Companion.getCriterial(), true)?: LocationManager.NETWORK_PROVIDER
        }

        fun getProviderNameS(): MutableList<String> {
            return ru.smartro.worknote.andPOintD.AndRoid.Companion.getService().allProviders
        }

    }

    fun gpSPoinT(): Point {
        return Point()
    }


    private var mLocationManagerSystem: LocationManager? = null

    fun getLocationService(): LocationManager {
        //todo: ПЕРЕключатель.ключ.вкл.ON_выкл-OFF
//        toggle
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



}