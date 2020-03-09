package ru.smartro.worknote.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.Result
import ru.smartro.worknote.data.organisations.OrganisationsRepository
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.domain.models.OrganisationModel
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.WorkflowModel
import timber.log.Timber
import java.time.temporal.ChronoUnit

class OrganisationSelectViewModel(
    private val organisationsRepository: OrganisationsRepository,
    private val loginRepository: LoginRepository,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private lateinit var currentUserHolder: MutableLiveData<UserModel>
    private lateinit var workflowHolder: MutableLiveData<WorkflowModel>

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _state = MutableLiveData<State>(State.Created(this))

    val state: LiveData<State>
        get() = _state

    val selectDone = MutableLiveData<Boolean>(false)

    val currentOrganisationId = MutableLiveData<Int?>()

    private val _organisations = MutableLiveData<List<OrganisationModel>>()
    val organisations: LiveData<List<OrganisationModel>>
        get() = _organisations


    init {
        when (_state.value) {
            is State.Created -> onRefresh()
            else -> throw Exception("illegal initial state")
        }
    }

    //region events
    fun onRefresh() {
        setState(State.SoftInProgress.Refresh(this))
        uiScope.launch {
            loadUser()?.let {
                return@let loadWorkflow(it)
            }?.let {
                return@let loadOrganisations(it)
            }?.let {
                setState(State.AwaitSelect(this@OrganisationSelectViewModel))
            }
        }
    }

    private suspend fun loadUser(): UserModel? {
        val currentUser = getCurrentUser()
        if (currentUser == null) {
            onAuthError()
            return null
        }
        currentUserHolder = MutableLiveData(currentUser)

        return  currentUser
    }

    private suspend fun loadWorkflow(user: UserModel): UserModel? {
        val workflowModel = getWorkflow(user)
        if(workflowModel == null) {
            setState(State.Error.AppError(this))
            return null
        }
        workflowHolder = MutableLiveData(workflowModel)

        return user
    }

    private suspend fun loadOrganisations(currentUser: UserModel): List<OrganisationModel>? {
        when (val organisationsResult = getOrganisations(currentUser)) {
            is Result.Success -> {
                _organisations.postValue(organisationsResult.data)
                return _organisations.value
            }
            is Result.Error -> {
                if (organisationsResult.isAuthError) {
                    onAuthError()
                    return null
                } else if (organisationsResult.isIOError) {
                    setState(State.Error.NetworkError(this))
                }

                return _organisations.value
            }
        }
    }

    //endregion



    private suspend fun getCurrentUser(): UserModel? {
        return loginRepository.getLoggedInUser()
    }


    private suspend fun getWorkflow(userModel: UserModel): WorkflowModel? {
        return workflowRepository.getWorkFlowForUser(userModel.id)
    }

    private suspend fun onAuthError() {
        currentUserHolder.postValue(null)
        loginRepository.logout()
        setState(State.Error.AuthError(this))
    }

    fun setCommitCurrentOrganisation() {
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

    private suspend fun getOrganisations(userModel: UserModel): Result<List<OrganisationModel>> {
        return withContext(Dispatchers.IO) {
            return@withContext organisationsRepository.getOrganisations(userModel)
        }
    }

    fun canRefresh(): Boolean {
        return isTransitionValid(State.SoftInProgress.Refresh(this))
    }

    private fun setState(toState: State) {
        if (isTransitionValid(toState)) {
            _state.postValue(toState)
            return
        }
        val exception = Exception("from ${state.value} -TO- $toState - state is not applicable")
        Timber.e(exception)

        throw exception
    }

    sealed class State(val subject: OrganisationSelectViewModel) {
        class Created(subject: OrganisationSelectViewModel) : State(subject)

        open class SoftInProgress(subject: OrganisationSelectViewModel) : State(subject) {
            class SendOrganisation(subject: OrganisationSelectViewModel) : SoftInProgress(subject)
            class Refresh(subject: OrganisationSelectViewModel) : SoftInProgress(subject)
        }

        open class Error(subject: OrganisationSelectViewModel) : State(subject) {
            class AuthError(subject: OrganisationSelectViewModel) : Error(subject)
            class NetworkError(subject: OrganisationSelectViewModel) : Error(subject)
            class NotFindError(subject: OrganisationSelectViewModel) : Error(subject)
            class ServerError(subject: OrganisationSelectViewModel) : Error(subject)
            class AppError(subject: OrganisationSelectViewModel) : Error(subject)
        }

        class AwaitSelect(subject: OrganisationSelectViewModel) : State(subject)

        class Done(subject: OrganisationSelectViewModel) : State(subject)
    }


    private fun isTransitionValid(toState: State): Boolean {
        return when (state.value) {
            is State.Created -> when (toState) {
                is State.SoftInProgress.Refresh -> true
                else -> false
            }
            is State.AwaitSelect -> when (toState) {
                is State.SoftInProgress.Refresh -> true
                is State.SoftInProgress.SendOrganisation -> true
                is State.Done -> true
                else -> false
            }

            is State.SoftInProgress.SendOrganisation -> when (toState) {
                is State.Done -> true
                else -> false
            }

            is State.SoftInProgress.Refresh -> when (toState) {
                is State.AwaitSelect -> true
                is State.Error -> true
                else -> false
            }

            is State.Error.AuthError -> false

            is State.Error.AppError -> false

            is State.Error.NetworkError -> when (toState) {
                is State.SoftInProgress.Refresh -> true
                is State.AwaitSelect -> true
                else -> false
            }

            is State.Error.NotFindError -> when (toState) {
                is State.AwaitSelect -> true
                else -> false
            }

            is State.Error.ServerError -> when (toState) {
                is State.AwaitSelect -> true
                else -> false
            }



            is State.Done -> false

            else -> false
        }
    }

}