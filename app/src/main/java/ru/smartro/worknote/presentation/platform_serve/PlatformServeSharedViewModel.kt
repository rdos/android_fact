package ru.smartro.worknote.presentation.platform_serve

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity

data class ActiveGroupedContainers(
    var isActiveToday: Boolean = true,
    var clientGroupedContainers: MutableList<ClientGroupedContainers> = mutableListOf()
)

data class ClientGroupedContainers(
    var client: String = "",
    var typeGroupedContainers: MutableList<TypeGroupedContainers> = mutableListOf()
)

data class TypeGroupedContainers(
    var typeName: String = "",
    var containersIds: MutableList<Int> = mutableListOf()
)

class PlatformServeSharedViewModel(application: Application) : BaseViewModel(application) {

    init {
        Log.d("VM ::: TEST :::", "vm CREATED")
    }

    val wasAskedForPhoto: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _platformEntity: MutableLiveData<PlatformEntity> = MutableLiveData(null)
    val platformEntity: LiveData<PlatformEntity>
        get() = _platformEntity

    private val _sortedContainers: MutableLiveData<List<ActiveGroupedContainers>> = MutableLiveData(null)
    val sortedContainers: LiveData<List<ActiveGroupedContainers>>
        get() = _sortedContainers

    val wasServedExtended: MutableLiveData<Boolean> = MutableLiveData(false)
    val wasServedSimplified: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _screenMode: MutableLiveData<Boolean> = MutableLiveData(false)
    val screenMode: LiveData<Boolean>
        get() = _screenMode

    fun changeScreenMode() {
        _screenMode.postValue(!_screenMode.value!!)
    }

    fun getPlatformEntity(platformId: Int): PlatformEntity {
        val response = baseDat.getPlatformEntity(platformId)
        _platformEntity.postValue(response)
        val temp = clusterContainers(response.containers.toMutableList())
        Log.d("TEST:::", "TEMP RESULT ::: ${temp}")
        _sortedContainers.postValue(temp)
        return response
    }

    //SIMPLIFY
    fun onDecrease(isActiveToday: Boolean, clientGroupId: Int, typeGroupId: Int) {
        Log.d("TEST ::: VM", "VM onDecrease")
    }
    fun onIncrease(isActiveToday: Boolean, clientGroupId: Int, typeGroupId: Int) {
        Log.d("TEST ::: VM", "VM onIncrease")
    }
    fun onAddPhoto(isActiveToday: Boolean, clientGroupId: Int, typeGroupId: Int) {
        Log.d("TEST ::: VM", "VM onAddPhoto")
    }
    //EXTENDED
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

    private fun clusterContainers(containers: List<ContainerEntity>): List<ActiveGroupedContainers> {
        // vlad: CLUSTERING by isActive -> client -> type
        val temp = mutableListOf<ActiveGroupedContainers>()
        containers.forEach { container ->
            val isActiveTemp = temp.find { it.isActiveToday == container.isActiveToday }
            if(isActiveTemp == null) {
                temp.add(ActiveGroupedContainers(
                    container.isActiveToday,
                    mutableListOf(
                        ClientGroupedContainers(
                            container.client ?: "",
                            mutableListOf(TypeGroupedContainers(
                                container.typeName ?: "",
                                mutableListOf(container.containerId?:0)
                            ))
                        )
                    )
                )
                )
            } else {
                val clientTemp =
                    isActiveTemp.clientGroupedContainers
                        .find { it.client == (container.client?: "") }
                val activeIndex = temp.indexOf(isActiveTemp)

                // Список контейнеров, сгруппированных по клиентам
                temp[activeIndex].clientGroupedContainers.apply {
                    if(clientTemp == null) {
                        add(ClientGroupedContainers(
                            container.client?: "",
                            mutableListOf(
                                TypeGroupedContainers(
                                    container.typeName?: "",
                                    mutableListOf(container.containerId?:0)
                                ))
                        ))
                    } else {
                        val clientIndex = indexOf(clientTemp)
                        val typeTemp = clientTemp.typeGroupedContainers
                            .find { it.typeName == (container.typeName?: "") }
                        if(typeTemp == null) {
                            get(clientIndex).typeGroupedContainers.add(TypeGroupedContainers(
                                container.typeName?: "",
                                mutableListOf(container.containerId?:0)
                            ))
                        } else {
                            val typeIndex = get(clientIndex).typeGroupedContainers.indexOf(typeTemp)
                            get(clientIndex).typeGroupedContainers[typeIndex].apply {
                                containersIds.add(container.containerId?:0)
                            }
                        }
                    }
                }
            }
        }
        return temp
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("VM ::TEST :::", "VM IS CLEARED")
    }
}