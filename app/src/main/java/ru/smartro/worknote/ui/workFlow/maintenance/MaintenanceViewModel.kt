package ru.smartro.worknote.ui.workFlow.maintenance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.srpPlatform.SrpPlatformRepository
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.domain.models.SrpPlatformModel
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.WorkflowModel
import ru.smartro.worknote.domain.models.complex.SrpContainerWithRelations
import ru.smartro.worknote.domain.models.complex.SrpPlatformWithRelations
import timber.log.Timber

class MaintenanceViewModel(
    private val workflowRepository: WorkflowRepository,
    private val srpPlatformRepository: SrpPlatformRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val platformModel: MutableLiveData<SrpPlatformModel?> = MutableLiveData(null)
    val containers: MutableLiveData<List<SrpContainerWithRelations>> = MutableLiveData(listOf())
    private val platformIdHolder: MutableLiveData<Int?> = MutableLiveData(null)

    private lateinit var currentUserHolder: MutableLiveData<UserModel>
    private lateinit var workflowHolder: MutableLiveData<WorkflowModel>


    private val viewModelJob = Job()

    private val modelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _state = MutableLiveData<State?>(
        State.Created
    )

    val state: LiveData<State?>
        get() = _state

    //region events
    fun onInit(platformId: Int) {
        platformIdHolder.value = platformId
        if (state.value is State.Created) {
            onRefresh(true)
        }
    }

    fun onRefresh(force: Boolean = false) {
        setState(State.SoftInProgress.Refresh)
        modelScope.launch {
            if (force) {
                loginRepository.dropAllCD()
            }
            loadUser()?.let {
                return@let loadWorkflow()
            }?.let {
                return@let loadPlatform()
            }?.let {
                //todo late
            }
        }
    }

    private suspend fun onAuthError() {
        currentUserHolder.postValue(null)
        loginRepository.logout()
        setState(State.Error.AuthError)
    }

    //endregion

    private suspend fun loadUser(): Boolean? {
        val currentUser = getCurrentUser()
        if (currentUser == null) {
            onAuthError()
            return null
        }
        if (::currentUserHolder.isInitialized) {
            currentUserHolder.postValue(currentUser)
        } else {
            currentUserHolder = MutableLiveData(currentUser)
        }

        return true
    }

    private suspend fun loadWorkflow(): Boolean? {
        val userModel = currentUserHolder.value ?: throw Exception("current user must be set")
        val workflowModel = getWorkflow(userModel)
        if (workflowModel == null) {
            setState(State.Error.AppError)
            return null
        }
        if (::workflowHolder.isInitialized) {
            workflowHolder.postValue(workflowModel)
        } else {
            workflowHolder = MutableLiveData(workflowModel)
        }

        return true
    }

    private suspend fun loadPlatform(): Boolean {
        val platformId = platformIdHolder.value
        if (platformId == null) {
            setState(State.Error.AppError)
            return false
        }
        val platformWithRelations = srpPlatformRepository.getPlatformWithRelations(platformId)
        platformModel.postValue(platformWithRelations.toDomainModel(workflowHolder.value!!.workOrderId!!))
        containers.postValue(platformWithRelations.containers)

        return true
    }

    private suspend fun getCurrentUser(): UserModel? {
        return loginRepository.getLoggedInUser()
    }

    private suspend fun getWorkflow(userModel: UserModel): WorkflowModel? {
        return workflowRepository.getWorkFlowForUser(userModel.id)
    }

    private fun setState(toState: State) {
        Timber.d("state: ${state.value}  >>>  $toState")
        if (isTransitionValid(toState)) {
            _state.postValue(toState)
            return
        }
        val exception = Exception("from ${state.value} -TO- $toState - state is not applicable")
        Timber.e(exception)

        throw exception
    }

    sealed class State {

        object Created : State()

        sealed class SoftInProgress : State() {
            object Refresh : SoftInProgress()
        }

        sealed class Error : State() {
            object AuthError : Error()
            object NetworkError : Error()
            object NotFindError : Error()
            object AppError : Error()
        }

        object AwaitPhotoBefore: State()
        object AwaitPhotoAfter: State()
        object AwaitSelect : State()

        object Done : State()
    }


    private fun isTransitionValid(toState: State): Boolean {
        return when (state.value) {
            is State.Created -> when (toState) {
                is State.SoftInProgress.Refresh -> true
                else -> false
            }

            is State.SoftInProgress.Refresh -> when (toState) {
                is State.AwaitPhotoBefore -> true
                is State.Error -> true
                else -> false
            }

            is State.AwaitPhotoBefore -> when(toState) {
                is State.AwaitPhotoBefore -> true
                is State.AwaitSelect -> true
                else -> false
            }

            is State.AwaitSelect -> when (toState) {
                is State.AwaitSelect -> true
                is State.AwaitPhotoAfter -> true

                else -> false
            }

            is State.AwaitPhotoAfter -> when (toState) {
                is State.AwaitPhotoAfter -> true
                is State.Done -> true
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

            is State.Done -> false

            else -> false
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}
