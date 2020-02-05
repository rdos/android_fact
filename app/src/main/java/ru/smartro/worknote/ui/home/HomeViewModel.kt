package ru.smartro.worknote.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.Result
import ru.smartro.worknote.data.organisations.OrganisationsRepository
import ru.smartro.worknote.domain.models.UserModel

class HomeViewModel(
    loginRepository: LoginRepository,
    organisationsRepository: OrganisationsRepository
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val currentUserHolder: MutableLiveData<UserModel?> = MutableLiveData()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        uiScope.launch {
            loginRepository.getLoggedInUser(currentUserHolder)
            val currentOrgId = currentUserHolder.value?.currentOrganisationId
            val currentUser = currentUserHolder.value
            if (currentOrgId !== null && currentUser !== null) {
                val organistionResult =
                    organisationsRepository.getOrganisation(currentOrgId, currentUser)
                if (organistionResult is Result.Success) {
                    _text.value = organistionResult.data.name
                }
            }
        }
    }
}