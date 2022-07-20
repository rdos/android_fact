package ru.smartro.worknote.presentation.checklist.waybill

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.service.network.body.WayListBody
import ru.smartro.worknote.awORKOLDs.service.network.response.way_list.WayListResponse
import ru.smartro.worknote.work.Resource

class StartWaybillViewModel(application: Application) : BaseViewModel(application) {

    private val _wayBillListResponse: MutableLiveData<Resource<WayListResponse>> = MutableLiveData(null)
    val mWayBillListResponse: LiveData<Resource<WayListResponse>>
        get() = _wayBillListResponse

    fun getWayBillsList(body : WayListBody) {
        viewModelScope.launch {
//            _wayBillListResponse.postValue(networkDat.getWayList(body))
        }
    }

}