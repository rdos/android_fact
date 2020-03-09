package ru.smartro.worknote.utils.commonViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.Result
import ru.smartro.worknote.data.organisations.OrganisationsRepository
import ru.smartro.worknote.domain.models.OrganisationModel
import ru.smartro.worknote.domain.models.UserModel

class CurrentUserViewModel(
    private val loginRepository: LoginRepository,
    private val organisationsRepository: OrganisationsRepository,
    application: Application
) : AndroidViewModel(application) {

    private val viewModelJob = Job()

    private val modelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _currentOrganisation = MutableLiveData<OrganisationModel?>()
    private val _currentUserHolder: MutableLiveData<UserModel?> = MutableLiveData()

    private val _isUpdating = MutableLiveData<Boolean>(false)

    val isUpdating: LiveData<Boolean>
        get() = _isUpdating

    val currentUser: LiveData<UserModel?>
        get() = _currentUserHolder

    val currentOrganisation: LiveData<OrganisationModel?>
        get() = _currentOrganisation

    init {
        update()
    }

    fun refresh() {
        update()
    }

    private fun update() {
        modelScope.launch {
            _isUpdating.value = true
            _currentUserHolder.postValue(loginRepository.getLoggedInUser())
            val currentUserModel = _currentUserHolder.value
            if (currentUserModel == null) {
                _isUpdating.value = false
                return@launch
            }
//            val currentOrgId = currentUserModel.currentOrganisationId
//            if (currentOrgId === null) {
//                _isUpdating.value = false
//                return@launch
//            }
//            when (val orgResult =
//                organisationsRepository.getOrganisation(currentOrgId, currentUserModel)) {
//                is Result.Success -> _currentOrganisation.value = orgResult.data
//            }
//

            _isUpdating.setValue(false)
        }
    }

    fun logut() {
        modelScope.launch {
            loginRepository.logout()
            _currentUserHolder.postValue(null)
        }
    }


}