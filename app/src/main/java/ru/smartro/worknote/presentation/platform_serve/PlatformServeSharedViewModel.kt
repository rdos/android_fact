package ru.smartro.worknote.presentation.platform_serve

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.smartro.worknote.App.ScreenMode
import ru.smartro.worknote.andPOintD.BaseViewModel
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.ServedContainers

class PlatformServeSharedViewModel(application: Application) : BaseViewModel(application) {

    private val _platformEntity: MutableLiveData<PlatformEntity> = MutableLiveData(null)
    val mPlatformEntity: LiveData<PlatformEntity>
        get() = _platformEntity

    private val _failReasonS: MutableLiveData<List<String>> = MutableLiveData(emptyList())
    val mFailReasonS: LiveData<List<String>>
        get() = _failReasonS

    fun getPlatformEntity(platformId: Int): PlatformEntity {
        val response: PlatformEntity = baseDat.getPlatformEntity(platformId)
        _platformEntity.postValue(response)
        return response
    }

    fun getFailReasonS(): List<String> {
        var result = _failReasonS.value!!
        if (result.isEmpty()) {
            result = baseDat.findAllFailReason()
        }
        _failReasonS.postValue(result)
        return result
    }

    fun updateVolumePickup(platformId: Int, volume: Double?) {
        baseDat.updateVolumePickup(platformId, volume)
        getPlatformEntity(platformId)
    }

    fun updatePlatformKGO(platformId: Int, kgoVolume: String, isServedKGO: Boolean) {
        baseDat.updatePlatformKGO(platformId, kgoVolume, isServedKGO)
        getPlatformEntity(platformId)
    }

    fun updatePlatformStatusSuccess(platformId: Int) {
        baseDat.updatePlatformStatusSuccess(platformId)
    }
}