package ru.smartro.worknote.presentation.checklist.workorder

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import io.sentry.Sentry
import kotlinx.coroutines.launch
import ru.smartro.worknote.LOG
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.awORKOLDs.SynchroOidWidOutBodyDataWorkorder
import ru.smartro.worknote.awORKOLDs.util.THR
import ru.smartro.worknote.saveJSON
import ru.smartro.worknote.presentation.work.Resource

class StartWorkOrderViewModel(app: Application) : AViewModel(app) {

//    // WORKORDERS
//    private val _workOrderList: MutableLiveData<Resource<SynchroOidWidOutBodyDataWorkorder>> = MutableLiveData(null)
//    val mWorkOrderList: LiveData<Resource<SynchroOidWidOutBodyDataWorkorder>>
//        get() = _workOrderList

    val mSelectedWorkOrdersIndecies: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())

    

//    fun getWorkOrderList(orgId: Int, wayBillId: Int) {
//        viewModelScope.launch {
//            LOG.info( "getWorkOder.before")
//            try {
//                val response = networkDat.getWorkOrder(orgId, wayBillId)
//                mSelectedWorkOrdersIndecies.postValue(mutableListOf())
//                LOG.debug("getWorkOder.after ${response.body().toString()}")
//                when {
//                    response.isSuccessful -> {
//                        val gson = Gson()
//                        val bodyInStringFormat = gson.toJson(response.body())
//                        saveJSON(bodyInStringFormat, "getWorkOrder")
//                        _workOrderList.postValue(Resource.success(response.body()))
//                    }
//                    else -> {
//                        THR.BadRequestSynchro__o_id__w_id(response)
//                        _workOrderList.postValue(Resource.error("Ошибка ${response.code()}", null))
//                    }
//                }
//            } catch (e: Exception) {
//                Sentry.captureException(e)
//                _workOrderList.postValue(Resource.network("Проблемы с подключением: ${e.stackTraceToString()}", null))
//            }
//        }
//    }

    fun insertWorkOrders(workOrders: List<SynchroOidWidOutBodyDataWorkorder>) {
        database.clearDataBase()
        database.insUpdWorkOrderS(workOrders)
    }
}