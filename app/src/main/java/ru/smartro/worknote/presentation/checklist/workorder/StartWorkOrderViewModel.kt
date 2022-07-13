package ru.smartro.worknote.presentation.checklist.workorder

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.work.Resource
import ru.smartro.worknote.work.WorkOrderResponse_know1

class StartWorkOrderViewModel(application: Application) : BaseViewModel(application) {

    fun getWorkOrderList(orgId: Int, wayBillId: Int): LiveData<Resource<WorkOrderResponse_know1>> =
        networkDat.getWorkOrder(orgId, wayBillId)

}