package ru.smartro.worknote.presentation.checklist.workorder

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.work.Resource
import ru.smartro.worknote.work.THR
import ru.smartro.worknote.work.WoRKoRDeR_know1
import ru.smartro.worknote.work.WorkOrderResponse_know1

class StartWorkOrderViewModel(application: Application) : BaseViewModel(application) {

    private val _workOrderList: MutableLiveData<Resource<WorkOrderResponse_know1>> = MutableLiveData(null)
    val mWorkOrderList: LiveData<Resource<WorkOrderResponse_know1>>
        get() = _workOrderList

    val mSelectedWorkOrders: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())

    fun getWorkOrderList(orgId: Int, wayBillId: Int) {
        viewModelScope.launch {
            Log.i(TAG, "getWorkOder.before")
            try {
                val response = networkDat.getWorkOrder(orgId, wayBillId)
                Log.d(TAG, "getWorkOder.after ${response.body().toString()}")
                when {
                    response.isSuccessful -> {
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
        baseDat.clearDataBase()
        baseDat.insertWorkorder(workOrders)
    }

    fun insertWorkOrders(workOrder: WoRKoRDeR_know1) {
        insertWorkOrders(listOf(workOrder))
    }
}