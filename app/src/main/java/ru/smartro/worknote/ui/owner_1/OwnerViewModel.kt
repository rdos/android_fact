package ru.smartro.worknote.ui.owner_1

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.Resource
import ru.smartro.worknote.service.response.owner.OwnerResponse

class OwnerViewModel(application: Application) : BaseViewModel(application) {
    fun getOwners(): LiveData<Resource<OwnerResponse>> {
        return network.getOwners()
    }
}