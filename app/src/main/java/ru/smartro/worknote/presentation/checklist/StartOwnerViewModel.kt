package ru.smartro.worknote.presentation.checklist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.service.network.response.organisation.OrganisationResponse
import ru.smartro.worknote.work.Resource

class StartOwnerViewModel(application: Application) : BaseViewModel(application) {

    private val _ownersList: MutableLiveData<Resource<OrganisationResponse>> = MutableLiveData()


    fun getOwnerList(): LiveData<Resource<OrganisationResponse>> {

        return _ownersList
    }


}