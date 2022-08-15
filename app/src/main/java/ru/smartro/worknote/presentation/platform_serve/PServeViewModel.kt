package ru.smartro.worknote.presentation.platform_serve

import android.app.Application
import android.os.Handler
import android.os.Looper
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

    private val _containerEntity: MutableLiveData<ContainerEntity> = MutableLiveData(null)
    val mContainerEntity: LiveData<ContainerEntity>
        get() = _containerEntity

    fun getContainerEntity(containerId: Int) {
        val response: ContainerEntity = baseDat.getContainerEntity(containerId)
        _containerEntity.postValue(response)
    }

    fun updateContainerVolume(platformId: Int, containerId: Int, volume: Double?) {
        baseDat.updateContainerVolume(platformId, containerId, volume)
        getContainerEntity(containerId)
        getPlatformEntity(platformId)
    }

    fun updateContainerComment(platformId: Int, containerId: Int, comment: String?) {
        baseDat.updateContainerComment(platformId, containerId, comment)
    }

    fun getPlatformEntity(platformId: Int) {
        Log.d("TEST::::", "GET PLATFORM ${platformId}")
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