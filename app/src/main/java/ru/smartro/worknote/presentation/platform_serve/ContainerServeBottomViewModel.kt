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

class ContainerServeBottomViewModel(application: Application) : BaseViewModel(application) {

    private val _containerEntity: MutableLiveData<ContainerEntity> = MutableLiveData(null)
    val mContainerEntity: LiveData<ContainerEntity>
        get() = _containerEntity

    fun getContainerEntity(containerId: Int) {
        val response: ContainerEntity = baseDat.getContainerEntity(containerId)
        _containerEntity.postValue(response)
    }

    fun updateContainerVolume(platformId: Int, containerId: Int, volume: Double?) {
        baseDat.updateContainerVolume(platformId, containerId, volume)
        getContainerEntity(platformId)
    }

    fun updateContainerComment(platformId: Int, containerId: Int, comment: String?) {
        baseDat.updateContainerComment(platformId, containerId, comment)
    }
}