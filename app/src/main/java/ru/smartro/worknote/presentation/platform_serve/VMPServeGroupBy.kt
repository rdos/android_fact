package ru.smartro.worknote.presentation.platform_serve

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.work.ContainerEntity
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

class PServeByTypesViewModel(app: Application) : AViewModel(app) {

    private val _platformEntity: MutableLiveData<PlatformEntity> = MutableLiveData(null)
    val mPlatformEntity: LiveData<PlatformEntity>
        get() = _platformEntity

    private val _sortedContainers: MutableLiveData<List<ClientGroupedContainers>> = MutableLiveData(null)
    val mSortedContainers: LiveData<List<ClientGroupedContainers>>
        get() = _sortedContainers

    val mServedContainers: MutableLiveData<List<ServedContainers>> = MutableLiveData(null)

    fun getPlatformEntity(platformId: Int) {
        val response: PlatformEntity = baseDat.getPlatformEntity(platformId)
        val temp = clusterContainers(response.containers.toList())
        if(response.servedContainers.isNotEmpty()) {
            mServedContainers.postValue(response.servedContainers)
        } else if(response.containers.any { el -> el.volume != null }) {
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
            mServedContainers.postValue(result)
        }
        _sortedContainers.postValue(temp)
        _platformEntity.postValue(response)
    }

    private fun countServedContainers(groupedContainers: List<ClientGroupedContainers>): List<ServedContainers> {
        val temp = mutableListOf<ServedContainers>()
        groupedContainers.forEach { clientGroup ->
            clientGroup.typeGroupedContainers.forEach { typeGroup ->
                temp.add(ServedContainers(typeGroup.typeName, clientGroup.client, 0))
            }
        }
        return temp
    }

    fun onDecrease(clientName: String, typeName: String) {
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
        val servedContainers = mServedContainers.value!!.toMutableList()
        val servedCluster = servedContainers.find { el -> el.client == clientName && el.typeName == typeName }
        val newCount = servedCluster!!.servedCount + 1

        val clusterIndex = servedContainers.indexOf(servedCluster)
        servedContainers[clusterIndex].servedCount = newCount

        baseDat.updatePlatformServedContainers(_platformEntity.value!!.platformId!!, servedContainers)
        mServedContainers.postValue(servedContainers)
    }

    fun updatePlatformStatusUnfinished() {
        baseDat.updatePlatformStatusUnfinished(mPlatformEntity.value!!.platformId!!)
        getPlatformEntity(mPlatformEntity.value!!.platformId!!)
    }

    fun updatePlatformStatusSuccess(platformId: Int) {
        mServedContainers.value?.let {
            if (_sortedContainers.value != null ) {
                it.forEach { el ->
                    val containers = _sortedContainers.value!!.find { sorted -> sorted.client == el.client }!!.typeGroupedContainers.find { typed -> typed.typeName == el.typeName }!!.containersIds
                    val newVolume = el.servedCount.toDouble() / containers.size
                    containers.forEach { cont ->
                        baseDat.updateContainerVolume(platformId, cont, newVolume)
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

            val clientGroup = temp.find { it.client == clientName }
            if(clientGroup != null) {
                val typeGroup = clientGroup.typeGroupedContainers.find { it.typeName == typeName }
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

}