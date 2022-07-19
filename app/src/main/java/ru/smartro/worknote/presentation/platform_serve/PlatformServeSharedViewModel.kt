package ru.smartro.worknote.presentation.platform_serve

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity

data class ClientGroupedContainers(
    var client: String = "",
    var typeGroupedContainers: MutableList<TypeGroupedContainers>
)

data class TypeGroupedContainers(
    var typeName: String = "",
    var containersIds: MutableList<Int>
)

data class ServedContainers(
    var clientGroupIndex: Int,
    var typeGroupIndex: Int,
    var count: Int = 0,
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

    val mServedContainers: MutableLiveData<List<ServedContainers>> = MutableLiveData(listOf())

    val mWasServedExtended: MutableLiveData<Boolean> = MutableLiveData(false)
    val mWasServedSimplified: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _screenMode: MutableLiveData<Boolean> = MutableLiveData(false)
    val mScreenMode: LiveData<Boolean>
        get() = _screenMode

    fun changeScreenMode() {
        _screenMode.postValue(!_screenMode.value!!)
    }

//    fun getPlatformEntity(platformId: Int, viewLifecycleOwner = null, next: () -> Any = null): PlatformEntity {
    // viewLifecycleOwner где взять?:R_dos))

    fun getPlatformEntity(platformId: Int): PlatformEntity {
        val response = baseDat.getPlatformEntity(platformId)
        _platformEntity.postValue(response)
        if(response.containers.all { el -> el.status == StatusEnum.NEW }) {
            val temp = clusterContainers(response.containers.toList())
            _sortedContainers.postValue(temp)
        }
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

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // SIMPLIFY SERVING
    fun onDecrease(clientGroupIndex: Int, typeGroupIndex: Int) {
        mWasServedSimplified.postValue(true)
        val containersToServe = mSortedContainers.value?.
            get(clientGroupIndex)?.typeGroupedContainers?.
            get(typeGroupIndex)?.containersIds

        val servedContainers = mServedContainers.value?.find { it.clientGroupIndex == clientGroupIndex && it.typeGroupIndex == typeGroupIndex }

        if(containersToServe != null && servedContainers != null) {
            val decreasedCount = servedContainers.count - 1
            val indOfServedContainers = mServedContainers.value!!.indexOf(servedContainers)
            if(decreasedCount >= 0) {
                val temp = mServedContainers.value!!.toMutableList()
                temp[indOfServedContainers].count = decreasedCount
                mServedContainers.postValue(temp)
            }
        }
    }

    fun onIncrease(clientGroupIndex: Int, typeGroupIndex: Int) {
        mWasServedSimplified.postValue(true)
        val containersToServe = mSortedContainers.value?.
        get(clientGroupIndex)?.typeGroupedContainers?.
        get(typeGroupIndex)?.containersIds

        val servedContainers = mServedContainers.value?.find { it.clientGroupIndex == clientGroupIndex && it.typeGroupIndex == typeGroupIndex }

        if(containersToServe != null && servedContainers != null) {
            val indOfServedContainers = mServedContainers.value!!.indexOf(servedContainers)
            val temp = mServedContainers.value!!.toMutableList()
            temp[indOfServedContainers].count = servedContainers.count + 1
            mServedContainers.postValue(temp)
        }
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // EXTENDED SERVING
    fun updateContainerVolume(platformId: Int, containerId: Int, volume: Double?) {
        mWasServedExtended.postValue(true)
        baseDat.updateContainerVolume(platformId, containerId, volume)
        getPlatformEntity(platformId)
    }

    fun updateContainerComment(platformId: Int, containerId: Int, comment: String?) {
        mWasServedExtended.postValue(true)
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
        mWasServedExtended.postValue(true)
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

    fun updatePlatformKGO(platformId: Int?, kgoVolume: String, isServedKGO: Boolean) {
        if (platformId == null) {
            return
        }
//        mWasServedExtended.postValue(true)
        baseDat.updatePlatformKGO(platformId, kgoVolume, isServedKGO)
//        getPlatformEntity(platformId)
    }

    // !!!!!!!!!!!!!!!!!!!
    // FINISH
    fun updatePlatformStatusSuccess(platformId: Int) {
        if(mWasServedSimplified.value == true) {
            mServedContainers.value!!.forEach { el ->
                val containers = _sortedContainers.value!![el.clientGroupIndex].typeGroupedContainers[el.typeGroupIndex].containersIds
                val newVolume = el.count.toDouble() / containers.size
                containers.forEach { cont ->
                    updateContainerVolume(platformId, cont, newVolume)
                }
            }
        }
        baseDat.updatePlatformStatusSuccess(platformId)
    }

    private fun clusterContainers(containers: List<ContainerEntity>): List<ClientGroupedContainers> {
        // vlad: CLUSTERING by isActive -> client -> type
        val temp = mutableListOf<ClientGroupedContainers>()
        val tempServed = mutableListOf<ServedContainers>()
        
        containers.filter { it.isActiveToday }.forEach { el ->
            val clientName = el.client ?: ""
            val typeName = el.typeName ?: ""

            val clientGroup = temp.find { it.client == el.client }
            if(clientGroup != null) {
                val typeGroup = clientGroup.typeGroupedContainers.find { it.typeName == el.typeName }
                val indOfClientGroup = temp.indexOf(clientGroup)
                if(typeGroup != null) {
                    // TODO VLAD CONTAINER ID -1
                    typeGroup.containersIds.add(el.containerId ?: -1)
                    val indOfTypeGroup = temp[indOfClientGroup].typeGroupedContainers.indexOf(typeGroup)
                    tempServed.find {
                        it.clientGroupIndex == indOfClientGroup && it.typeGroupIndex == indOfTypeGroup
                    }?.apply {
                        count += 1
                    }
                } else {
                    val newTypeGroup = TypeGroupedContainers(
                        typeName,
                        mutableListOf(el.containerId ?: -1)
                    )

                    clientGroup.typeGroupedContainers.add(newTypeGroup)
                    val indOfTypeGroup = temp[indOfClientGroup].typeGroupedContainers.indexOf(newTypeGroup)
                    tempServed.add(ServedContainers(indOfClientGroup, indOfTypeGroup, 1))
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

                val indOfClientGroup = temp.indexOf(newClientGroup)

                tempServed.add(ServedContainers(indOfClientGroup, 0, 1))
            }
        }
        mServedContainers.postValue(tempServed)
        return temp
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("VM ::TEST :::", "VM IS CLEARED")
    }
}