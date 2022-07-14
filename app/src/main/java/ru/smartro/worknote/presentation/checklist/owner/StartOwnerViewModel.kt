package ru.smartro.worknote.presentation.checklist.owner

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.service.network.RetrofitClient
import ru.smartro.worknote.awORKOLDs.service.network.response.organisation.Organisation
import ru.smartro.worknote.awORKOLDs.service.network.response.organisation.OrganisationResponse
import ru.smartro.worknote.work.Resource
import ru.smartro.worknote.work.THR

class StartOwnerViewModel(application: Application) : BaseViewModel(application) {
    private val _ownersList: MutableLiveData<Resource<OrganisationResponse>> = MutableLiveData(null)
    val mOwnersList: LiveData<Resource<OrganisationResponse>>
        get() = _ownersList

    fun getOwnersList() {
        viewModelScope.launch {
            Log.i(TAG, "getOwners")
            val response = networkDat.getOwners()
            try {
                when {
                    response.isSuccessful -> {
                        _ownersList.postValue(Resource.success(response.body()))
                    }
                    else -> {
                        THR.BadRequestOwner(response)
                        _ownersList.postValue(Resource.error("Ошибка ${response.code()}", null))

                    }
                }
            } catch (e: Exception) {
                _ownersList.postValue(Resource.network("Проблемы с подключением интернета", null))
            }
        }
    }
}