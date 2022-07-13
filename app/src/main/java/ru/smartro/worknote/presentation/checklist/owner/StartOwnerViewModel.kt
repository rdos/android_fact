package ru.smartro.worknote.presentation.checklist.owner

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.service.network.response.organisation.OrganisationResponse
import ru.smartro.worknote.work.Resource

class StartOwnerViewModel(application: Application) : BaseViewModel(application) {
    fun getOwnersList(): LiveData<Resource<OrganisationResponse>> = networkDat.getOwners()
}