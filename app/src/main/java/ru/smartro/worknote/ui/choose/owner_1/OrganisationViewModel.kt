package ru.smartro.worknote.ui.choose.owner_1

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.response.organisation.OrganisationResponse

class OrganisationViewModel(application: Application) : BaseViewModel(application) {
    fun getOwners(): LiveData<Resource<OrganisationResponse>> {
        return network.getOwners()
    }
}