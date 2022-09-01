package ru.smartro.worknote.presentation.checklist.workorder

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import ru.smartro.worknote.LOG
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.log
import ru.smartro.worknote.saveJSON
import ru.smartro.worknote.work.Resource
import ru.smartro.worknote.work.THR
import ru.smartro.worknote.work.WoRKoRDeR_know1
import ru.smartro.worknote.work.WorkOrderResponse_know1

class StartWorkOrderViewModel(app: Application) : AViewModel(app) {

    // WORKORDERS
    private val _workOrderList: MutableLiveData<Resource<WorkOrderResponse_know1>> = MutableLiveData(null)
    val mWorkOrderList: LiveData<Resource<WorkOrderResponse_know1>>
        get() = _workOrderList

    val mSelectedWorkOrders: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())

    

    fun getWorkOrderList(orgId: Int, wayBillId: Int) {
        viewModelScope.launch {
            LOG.info( "getWorkOder.before")
            try {
                val response = networkDat.getWorkOrder(orgId, wayBillId)
                mSelectedWorkOrders.postValue(mutableListOf())
                log("getWorkOder.after ${response.body().toString()}")
                when {
                    response.isSuccessful -> {
                        val gson = Gson()
                        val bodyInStringFormat = gson.toJson(response.body())
                        saveJSON(bodyInStringFormat, "getWorkOrder")
                        _workOrderList.postValue(Resource.success(response.body()))
                    }
                    else -> {
                        THR.BadRequestSynchro__o_id__w_id(response)
                        _workOrderList.postValue(Resource.error("Ошибка ${response.code()}", null))
                    }
                }
            } catch (e: Exception) {
                _workOrderList.postValue(Resource.network("Проблемы с подключением интернета", null))
            }
        }
    }

    fun insertWorkOrders(workOrders: List<WoRKoRDeR_know1>) {
        database.clearDataBase()
        database.insertWorkorder(workOrders)
    }
}