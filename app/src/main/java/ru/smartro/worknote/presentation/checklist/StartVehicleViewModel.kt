package ru.smartro.worknote.presentation.checklist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel

class StartVehicleViewModel(application: Application) : BaseViewModel(application) {

    private val _vehiclesList: MutableLiveData<Int> = MutableLiveData()

//    fun getVehicleList(): LiveData<> {
//
//        return _vehiclesList
//    }


}