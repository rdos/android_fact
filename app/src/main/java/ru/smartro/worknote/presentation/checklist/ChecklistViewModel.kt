package ru.smartro.worknote.presentation.checklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChecklistViewModel: ViewModel() {

    private val _ownersList: MutableLiveData<Int> = MutableLiveData(-1)
    val mOwnerList: LiveData<Int>
        get() = _ownersList

    private val _vehiclesList: MutableLiveData<Int> = MutableLiveData(-1)
    val mVehiclesList: LiveData<Int>
        get() = _vehiclesList

    private val _wayBillList: MutableLiveData<Int> = MutableLiveData(-1)
    val mWayBillList: LiveData<Int>
        get() = _wayBillList

    private val _workOrderList: MutableLiveData<Int> = MutableLiveData(-1)
    val mWorkOrderList: LiveData<Int>
        get() = _workOrderList



}