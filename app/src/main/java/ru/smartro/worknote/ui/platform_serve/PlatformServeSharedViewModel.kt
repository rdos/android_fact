package ru.smartro.worknote.ui.platform_serve

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.smartro.worknote.Inull
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity

class PlatformServeSharedViewModel(application: Application) : BaseViewModel(application) {

    private val _platformEntity: MutableLiveData<PlatformEntity> = MutableLiveData(null)
    val platformEntity: LiveData<PlatformEntity>
        get() = _platformEntity

    private val _screenMode: MutableLiveData<Boolean> = MutableLiveData(false)
    val screenMode: LiveData<Boolean>
        get() = _screenMode

    fun changeScreenMode() {
        _screenMode.postValue(!_screenMode.value!!)
    }

    fun getPlatformEntity(platformId: Int): PlatformEntity {
        val response = baseDat.getPlatformEntity(platformId)
        _platformEntity.postValue(response)
        return response
    }

    fun updateContainerVolume(platformId: Int, containerId: Int, volume: Double?) {
        baseDat.updateContainerVolume(platformId, containerId, volume)
    }

    fun updateContainerComment(platformId: Int, containerId: Int, comment: String?) {
        baseDat.updateContainerComment(platformId, containerId, comment)
    }

    fun updateSelectionVolume(platformId: Int, volume: Double?) {
        baseDat.updateSelectionVolume(platformId, volume)
    }

    fun clearContainerVolume(platformId: Int, containerId: Int) {
        baseDat.clearContainerVolume(platformId, containerId)
    }

    fun updatePlatformStatusSuccess(platformId: Int) {
        baseDat.updatePlatformStatusSuccess(platformId)
    }

    fun updatePlatformStatusUnfinished() {
        if(platformEntity.value != null && platformEntity.value!!.platformId != null) {
            baseDat.updatePlatformStatusUnfinished(platformEntity.value!!.platformId!!)
        }
    }

    fun updatePlatformStatusUnfinished(platformId: Int) {
        baseDat.updatePlatformStatusUnfinished(platformId)
    }

    fun removePlatformMedia(photoFor: Int, image: ImageEntity, platformId: Int) {
        baseDat.removePlatformMedia(photoFor, image, platformId)
    }

    fun removeContainerMedia(photoFor: Int,platformId: Int, containerId: Int, imageBase64: ImageEntity) {
        baseDat.removeContainerMedia(photoFor, platformId, containerId, imageBase64)
    }

    fun updatePlatformKGO(platformId: Int, kgoVolume: String, isServedKGO: Boolean) {
        baseDat.updatePlatformKGO(platformId, kgoVolume, isServedKGO)
    }


}