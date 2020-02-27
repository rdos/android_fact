package ru.smartro.worknote.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.Result
import ru.smartro.worknote.data.organisations.OrganisationsRepository
import ru.smartro.worknote.domain.models.OrganisationModel
import ru.smartro.worknote.domain.models.UserModel

class OrganisationSelectViewModel(private val organisationsRepository: OrganisationsRepository, private val loginRepository: LoginRepository) : ViewModel() {


    private val currentUserHolder: MutableLiveData<UserModel?> = MutableLiveData()
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val _hasAuthError = MutableLiveData<Boolean>(false)


    val hasAuthError: LiveData<Boolean>
        get() = _hasAuthError

    val selectDone = MutableLiveData<Boolean>(false)

    val currentOrganisationId = MutableLiveData<Int?>()

    private val _organisations = MutableLiveData<List<OrganisationModel>>()
    val organisations: LiveData<List<OrganisationModel>>
        get() = _organisations


    init {
        uiScope.launch {
            loginRepository.getLoggedInUser(currentUserHolder)
            _organisations.value = getOrganisations()
        }
    }

    fun setCommitCurrenOrganisation()
    {
        val userId = currentUserHolder.value?.id
        val orgId = currentOrganisationId.value
        if (userId !== null && orgId !== null) {
            uiScope.launch {
                withContext(Dispatchers.IO) {
                   // loginRepository.setCurrentOrganisation(userId, organisationId = orgId)
                    selectDone.postValue(true)
                }
            }
        }
    }

    private suspend fun getOrganisations(): List<OrganisationModel> {
        return withContext(Dispatchers.IO) {
            when (val userResult = loginRepository.checkRefreshUser(currentUserHolder.value!!)) {
                is Result.Error -> {
                    currentUserHolder.postValue(null)
                    _hasAuthError.postValue(true)
                    return@withContext emptyList<OrganisationModel>()
                }
                is Result.Success -> currentUserHolder.postValue(userResult.data)
            }
            when (val result = organisationsRepository.getOrganisations(currentUserHolder.value!!)) {
                is Result.Error -> {
                    _hasAuthError.postValue(true)
                    emptyList<OrganisationModel>()
                }
                is Result.Success -> {
                    result.data
                }
            }
        }
    }


}