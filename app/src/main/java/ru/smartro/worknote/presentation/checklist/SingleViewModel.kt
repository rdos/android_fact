package ru.smartro.worknote.presentation.checklist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.service.network.response.organisation.OrganisationResponse
import ru.smartro.worknote.awORKOLDs.service.network.response.vehicle.VehicleResponse
import ru.smartro.worknote.work.Resource

class SingleViewModel(application: Application) : BaseViewModel(application) {

    val mOwnerList: LiveData<Resource<OrganisationResponse>> = networkDat.getOwners()

    private val _vehicleList: MutableLiveData<Resource<VehicleResponse>> = MutableLiveData()
    val mVehicleList: LiveData<Resource<VehicleResponse>>
        get() = _vehicleList

    val mWayBillList: LiveData<Int> = MutableLiveData()
    val mWorkOrderList: LiveData<Int> = MutableLiveData()

    fun getVehicles() {

    }

}