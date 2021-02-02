package ru.smartro.worknote.ui.choose.vehicle_2

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.Resource
import ru.smartro.worknote.service.response.vehicle.VehicleResponse

class VehicleViewModel(application: Application) : BaseViewModel(application) {

    fun getVehicle(organisationId: Int): LiveData<Resource<VehicleResponse>> {
        return network.getVehicle(organisationId)
    }
/*
    fun getCars(authModel: AuthBody): LiveData<Resource<CarsResponse>> {
        return network.aut h(authModel)*/

}

