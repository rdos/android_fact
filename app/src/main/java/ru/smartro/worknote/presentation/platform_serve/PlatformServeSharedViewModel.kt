package ru.smartro.worknote.presentation.platform_serve

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.smartro.worknote.App.ScreenMode
import ru.smartro.worknote.awORKOLDs.BaseViewModel
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.ServedContainers

data class ClientGroupedContainers(
    var client: String = "",
    var typeGroupedContainers: MutableList<TypeGroupedContainers>
)

data class TypeGroupedContainers(
    var typeName: String = "",
    var containersIds: MutableList<Int>
)

class PlatformServeSharedViewModel(application: Application) : BaseViewModel(application) {

    val mBeforeMediaWasInited: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _platformEntity: MutableLiveData<PlatformEntity> = MutableLiveData(null)
    val mPlatformEntity: LiveData<PlatformEntity>
        get() = _platformEntity

    private val _failReasonS: MutableLiveData<List<String>> = MutableLiveData(emptyList())
    val mFailReasonS: LiveData<List<String>>
        get() = _failReasonS

    private val _sortedContainers: MutableLiveData<List<ClientGroupedContainers>> = MutableLiveData(null)
    val mSortedContainers: LiveData<List<ClientGroupedContainers>>
        get() = _sortedContainers

    val mServedContainers: MutableLiveData<List<ServedContainers>> = MutableLiveData(null)

    val mWasServedExtended: MutableLiveData<Boolean> = MutableLiveData(false)
    val mWasServedSimplified: MutableLiveData<Boolean> = MutableLiveData(false)

//    fun getPlatformEntity(platformId: Int, viewLifecycleOwner = null, next: () -> Any = null): PlatformEntity {
    // viewLifecycleOwner где взять?:R_dos))

    fun getPlatformEntity(platformId: Int): PlatformEntity {
        val response: PlatformEntity = baseDat.getPlatformEntity(platformId)
        _platformEntity.postValue(response)
        val isAnyContainerServedInSimplify = response.containers.any { el -> el.volume != null && el.volume!! > 1.25 }
        if(response.containers.all { el -> el.status == StatusEnum.NEW } || response.servedContainers.isNotEmpty() || isAnyContainerServedInSimplify) {
            val temp = clusterContainers(response.containers.toList())
            if(response.servedContainers.isNotEmpty()) {
                mServedContainers.postValue(response.servedContainers)
            } else if(isAnyContainerServedInSimplify) {
                val result: MutableList<ServedContainers> = mutableListOf()
                temp.forEach { clientGroup ->
                    clientGroup.typeGroupedContainers.forEach { typeGroup ->
                        val container = response.containers.find { el ->
                            el.typeName == typeGroup.typeName
                            && el.client == clientGroup.client
                        }
                        val count = container!!.volume!! * typeGroup.containersIds.size
                        result.add(ServedContainers(typeGroup.typeName, clientGroup.client, count.toInt()))
                    }
                }
                mServedContainers.postValue(result)
            } else {
                val result = countServedContainers(temp)
                Log.d("SHEESH :::", "getPlatformEntity: countServedContainers ${result}")
                mServedContainers.postValue(result)
            }
            _sortedContainers.postValue(temp)
        }
        return response
    }

    private fun countServedContainers(groupedContainers: List<ClientGroupedContainers>): List<ServedContainers> {
        val temp = mutableListOf<ServedContainers>()
        groupedContainers.forEach { clientGroup -> clientGroup.typeGroupedContainers.forEach { typeGroup -> temp.add(ServedContainers(typeGroup.typeName, clientGroup.client, typeGroup.containersIds.size)) } }
        return temp
    }

    fun getFailReasonS(): List<String> {
        var result = _failReasonS.value!!
        if (result.isEmpty()) {
            result = baseDat.findAllFailReason()
        }
        _failReasonS.postValue(result)
        return result
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // SIMPLIFY SERVING



    fun onDecrease(clientName: String, typeName: String) {
        Log.d("SHEESH ::: VM_ON_DEC", "client: ${clientName}, type: ${typeName}")
        mWasServedSimplified.postValue(true)
        val servedContainers = mServedContainers.value!!.toMutableList()
        val servedCluster = servedContainers.find { el -> el.client == clientName && el.typeName == typeName }
        val newCount = servedCluster!!.servedCount - 1

        if(newCount >= 0) {
            val clusterIndex = servedContainers.indexOf(servedCluster)
            servedContainers[clusterIndex].servedCount = newCount

            baseDat.updatePlatformServedContainers(_platformEntity.value!!.platformId!!, servedContainers)
            mServedContainers.postValue(servedContainers)
        }

    }

    fun onIncrease(clientName: String, typeName: String) {
        Log.d("SHEESH ::: VM_ON_INC", "client: ${clientName}, type: ${typeName}")
        mWasServedSimplified.postValue(true)
        val servedContainers = mServedContainers.value!!.toMutableList()
        val servedCluster = servedContainers.find { el -> el.client == clientName && el.typeName == typeName }
        val newCount = servedCluster!!.servedCount + 1

        val clusterIndex = servedContainers.indexOf(servedCluster)
        servedContainers[clusterIndex].servedCount = newCount

        baseDat.updatePlatformServedContainers(_platformEntity.value!!.platformId!!, servedContainers)
        mServedContainers.postValue(servedContainers)
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // EXTENDED SERVING
    fun updateContainerVolume(platformId: Int, containerId: Int, volume: Double?) {
        mWasServedExtended.postValue(true)
        baseDat.updateContainerVolume(platformId, containerId, volume)
        getPlatformEntity(platformId)
    }

    fun updateContainerComment(platformId: Int, containerId: Int, comment: String?) {
        baseDat.updateContainerComment(platformId, containerId, comment)
    }

    fun updateVolumePickup(platformId: Int, volume: Double?) {
        mWasServedExtended.postValue(true)
        baseDat.updateVolumePickup(platformId, volume)
        getPlatformEntity(platformId)
    }

    fun clearContainerVolume(platformId: Int, containerId: Int) {
        mWasServedExtended.postValue(true)
        baseDat.clearContainerVolume(platformId, containerId)
        getPlatformEntity(platformId)
    }

    fun updatePlatformStatusUnfinished() {
//        mWasServedExtended.postValue(true)
        if(mPlatformEntity.value != null && mPlatformEntity.value!!.platformId != null) {
            baseDat.updatePlatformStatusUnfinished(mPlatformEntity.value!!.platformId!!)
        }
    }

    fun updatePlatformStatusUnfinished(platformId: Int) {
        mWasServedExtended.postValue(true)
        baseDat.updatePlatformStatusUnfinished(platformId)
        getPlatformEntity(platformId)
    }

    fun removePlatformMedia(photoFor: Int, image: ImageEntity, platformId: Int) {
        mWasServedExtended.postValue(true)
        baseDat.removePlatformMedia(photoFor, image, platformId)
        getPlatformEntity(platformId)
    }

    fun removeContainerMedia(photoFor: Int,platformId: Int, containerId: Int, imageBase64: ImageEntity) {
//        mWasServedExtended.postValue(true)
//        baseDat.removeContainerMedia(photoFor, platformId, containerId, imageBase64)
//        getPlatformEntity(platformId)
    }

    fun updatePlatformKGO(platformId: Int, kgoVolume: String, isServedKGO: Boolean) {
        mWasServedExtended.postValue(true)
        baseDat.updatePlatformKGO(platformId, kgoVolume, isServedKGO)
        getPlatformEntity(platformId)
    }

    // !!!!!!!!!!!!!!!!!!!
    // FINISH
    fun updatePlatformStatusSuccess(platformId: Int) {
        // если упрощенный режим
        if(params.lastScreenMode == ScreenMode.SIMPLIFY) {
            mServedContainers.value?.let {
                if (_sortedContainers.value != null ) {
                    it.forEach { el ->
                        val containers = _sortedContainers.value!!.find { sorted -> sorted.client == el.client }!!.typeGroupedContainers.find { typed -> typed.typeName == el.typeName }!!.containersIds
                        val newVolume = el.servedCount.toDouble() / containers.size
                        containers.forEach { cont ->
                            updateContainerVolume(platformId, cont, newVolume)
                        }
                    }
                }

            }

        }
        baseDat.updatePlatformStatusSuccess(platformId)
    }

    private fun clusterContainers(containers: List<ContainerEntity>): List<ClientGroupedContainers> {
        // vlad: CLUSTERING by isActive -> client -> type
        val temp = mutableListOf<ClientGroupedContainers>()
        
        containers.filter { it.isActiveToday }.forEach { el ->
            val clientName = el.client ?: ""
            val typeName = el.typeName ?: ""

            val clientGroup = temp.find { it.client == el.client }
            if(clientGroup != null) {
                val typeGroup = clientGroup.typeGroupedContainers.find { it.typeName == el.typeName }
                if(typeGroup != null) {
                    // TODO VLAD CONTAINER ID -1
                    typeGroup.containersIds.add(el.containerId ?: -1)
                } else {
                    val newTypeGroup = TypeGroupedContainers(
                        typeName,
                        mutableListOf(el.containerId ?: -1)
                    )
                    clientGroup.typeGroupedContainers.add(newTypeGroup)
                }
            } else {
                val newTypeGroup = TypeGroupedContainers(
                    typeName,
                    mutableListOf(el.containerId ?: -1)
                )
                val newClientGroup = ClientGroupedContainers(
                    clientName,
                    mutableListOf(newTypeGroup)
                )
                temp.add(newClientGroup)
            }
        }
        return temp
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("VM ::TEST :::", "VM IS CLEARED")
    }
}