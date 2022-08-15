package ru.smartro.worknote.presentation.platform_serve

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import ru.smartro.worknote.App.ScreenMode
import ru.smartro.worknote.andPOintD.BaseViewModel
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.ServedContainers

class PServeViewModel(application: Application) : BaseViewModel(application) {

    private val _platformEntity: MutableLiveData<PlatformEntity> = MutableLiveData(null)
    val mPlatformEntity: LiveData<PlatformEntity>
        get() = _platformEntity

    fun getPlatformEntity(platformId: Int) {
        _platformEntity.postValue(baseDat.getPlatformEntity(platformId))
    }

    fun updateVolumePickup(platformId: Int, volume: Double?) {
        baseDat.updateVolumePickup(platformId, volume)
        getPlatformEntity(platformId)
    }

    fun updatePlatformStatusUnfinished() {
        baseDat.updatePlatformStatusUnfinished(mPlatformEntity.value!!.platformId!!)
        getPlatformEntity(mPlatformEntity.value!!.platformId!!)
    }

}