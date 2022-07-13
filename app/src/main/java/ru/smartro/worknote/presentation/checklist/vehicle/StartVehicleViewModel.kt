package ru.smartro.worknote.presentation.checklist.vehicle

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel

class StartVehicleViewModel(application: Application) : BaseViewModel(application) {
    fun getVehicleList(organisationId: Int) = networkDat.getVehicle(organisationId)
}