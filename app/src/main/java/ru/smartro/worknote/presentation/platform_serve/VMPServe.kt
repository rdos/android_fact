package ru.smartro.worknote.presentation.platform_serve

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.PlatformEntity

class VMPServe(app: Application) : AViewModel(app) {

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