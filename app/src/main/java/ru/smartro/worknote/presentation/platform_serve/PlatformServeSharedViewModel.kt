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
    val id: Int = 0,
    var client: String = "",
    var groupedContainers: MutableList<TypeGroupedContainers> = mutableListOf()
)

data class TypeGroupedContainers(
    var id: Int = 0,
    var typeName: String = "",
    var containers: MutableList<ContainerEntity> = mutableListOf()
)

class PlatformServeSharedViewModel(application: Application) : BaseViewModel(application) {

    val wasAskedForPhoto: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _platformEntity: MutableLiveData<PlatformEntity> = MutableLiveData(null)
    val platformEntity: LiveData<PlatformEntity>
        get() = _platformEntity

    private val _sortedContainers: MutableLiveData<List<ActiveGroupedContainers>> = MutableLiveData(null)
    val sortedContainers: LiveData<List<ActiveGroupedContainers>>
        get() = _sortedContainers

    private val _screenMode: MutableLiveData<Boolean> = MutableLiveData(false)
    val screenMode: LiveData<Boolean>
        get() = _screenMode

    fun changeScreenMode() {
        _screenMode.postValue(!_screenMode.value!!)
    }

    fun getPlatformEntity(platformId: Int): PlatformEntity {
        val response = baseDat.getPlatformEntity(platformId)
        _platformEntity.postValue(response)
        val temp = clusterContainers()
        _sortedContainers.postValue(temp)
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

    private fun clusterContainers(containers: List<ContainerEntity>) {
        // vlad: CLUSTERING by isActive -> client -> type
        val temp = mutableListOf<ActiveGroupedContainers>()
        containers.forEach { container ->
            val isActiveTemp = temp.find { it.isActiveToday == container.isActiveToday }
            if(isActiveTemp == null) {
                temp.add(ActiveGroupedContainers(
                    container.isActiveToday,
                    mutableListOf(
                        ClientGroupedContainers(
                            0,
                            container.client ?: "",
                            mutableListOf(TypeGroupedContainers(
                                0,
                                container.typeName ?: "",
                                mutableListOf(container)
                            ))
                        )
                    )
                ))
            } else {
                val clientTemp =
                    isActiveTemp.clientGroupedContainers
                        .find { it.client == (container.client?: "") }
                val activeIndex = temp.indexOf(isActiveTemp)

                // Список контейнеров, сгруппированных по клиентам
                temp[activeIndex].clientGroupedContainers.apply {
                    if(clientTemp == null) {
                        add(ClientGroupedContainers(
                            0,
                            container.client?: "",
                            mutableListOf(
                                TypeGroupedContainers(
                                    0,
                                    container.typeName?: "",
                                    mutableListOf(container)
                                ))
                        ))
                    } else {
                        val clientIndex = indexOf(clientTemp)
                        val typeTemp = clientTemp.groupedContainers
                            .find { it.typeName == (container.typeName?: "") }
                        if(typeTemp == null) {
                            get(clientIndex).groupedContainers.add(TypeGroupedContainers(
                                get(clientIndex).groupedContainers.size,
                                container.typeName?: "",
                                mutableListOf(container)
                            ))
                        } else {
                            val typeIndex = get(clientIndex).groupedContainers.indexOf(typeTemp)
                            get(clientIndex).groupedContainers[typeIndex].apply {
                                containers.add(container)
                            }
                        }
                    }
                }
            }
        }
    }
}