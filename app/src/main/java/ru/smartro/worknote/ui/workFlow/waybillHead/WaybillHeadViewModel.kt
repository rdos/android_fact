package ru.smartro.worknote.ui.workFlow.waybillHead

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.waybill.WaybillRepository
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.WaybillHeadModel
import ru.smartro.worknote.domain.models.WorkflowModel
import timber.log.Timber
import java.time.LocalDate

class WaybillHeadViewModel(
    private val workflowRepository: WorkflowRepository,
    private val waybillRepository: WaybillRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private lateinit var currentUserHolder: MutableLiveData<UserModel?>
    private lateinit var workflowHolder: MutableLiveData<WorkflowModel?>

    private val _waybills = MutableLiveData<List<WaybillHeadModel>>()

    private val viewModelJob = Job()

    private val modelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _lastSelected = MutableLiveData<WaybillHeadViewModel?>(null)

    private val _state = MutableLiveData<State>(
        State.Created
    )

    val state: LiveData<State>
        get() = _state

    val lastSelected: LiveData<WaybillHeadViewModel?>
        get() = _lastSelected


    val waybills: LiveData<List<WaybillHeadModel>>
        get() = _waybills

    private val _date: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())


    //region events
    private suspend fun onAuthError() {
        currentUserHolder.postValue(null)
        loginRepository.logout()
        setState(State.Error.AuthError)
    }
    //endregion


    fun refresh(force: Boolean = false) {
        if (force) {
            loginRepository.dropAllCD()
            waybillRepository.dropAllCD()
        }
        modelScope.launch {
//            _isUpdating.postValue(true)
//            _currentUserHolder.postValue(loginRepository.getLoggedInUser())
//            val currentUser = _currentUserHolder.value
//            if (currentUser?.currentOrganisationId == null) {
//                _authError.postValue(true)
//                return@launch
//            }

//
//            val middleResult = waybillRepository.getAllWaybillsByCriteria(currentUser)
//            if (middleResult is Result.Success) {
//                if (middleResult.data.isNotEmpty() && _vehicles.value?.equals(middleResult.data) != true) {
//                    _vehicles.postValue(middleResult.data)
//                }
//            } else {
//                _authError.postValue(true)
//                return@launch
//            }
//            val workflowModel = workflowRepository.getWorkFlowForUser(currentUser.id)
//                ?: WorkflowModel(currentUser.id, true, null, null)
//
//            _workflow.postValue(workflowModel)
//            _isUpdating.postValue(false)
//            workDone.postValue(workflowModel.vehicleId !== null)
        }
    }


//    object Converter {
//        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
//        @InverseMethod("stringToDate")
//        fun dateToString(
//            view: EditText, oldValue: LocalDate,
//            value: LocalDate
//        ): String {
//            return value.format(formatter)
//        }
//
//        fun stringToDate(
//            view: EditText, oldValue: String,
//            value: String
//        ): LocalDate {
//            return LocalDate.parse(value, formatter)
//        }
//    }
private suspend fun loadUser(): UserModel? {
    val currentUser = getCurrentUser()
    if (currentUser == null) {
        onAuthError()
        return null
    }
    currentUserHolder = MutableLiveData(currentUser)

    return currentUser
}

    private suspend fun loadWorkflow(user: UserModel): UserModel? {
        val workflowModel = getWorkflow(user)
        if (workflowModel == null) {
            setState(State.Error.AppError)
            return null
        }
        workflowHolder = MutableLiveData(workflowModel)

        return user
    }

    private suspend fun getCurrentUser(): UserModel? {
        return loginRepository.getLoggedInUser()
    }

    private suspend fun getWorkflow(userModel: UserModel): WorkflowModel? {
        return workflowRepository.getWorkFlowForUser(userModel.id)
    }

    private fun getOrganisationId(): Int {
        return workflowHolder.value?.organisationId
            ?: throw Exception("workflow and their organisation must be set")
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

    sealed class State() {

        object Created : State()

        sealed class SoftInProgress : State() {
            object SetWaybill : SoftInProgress()
            object Refresh : SoftInProgress()
        }

        sealed class Error() : State() {
            object AuthError : Error()
            object NetworkError : Error()
            object NotFindError : Error()
            object AppError : Error()
        }

        object AwaitSelect : State()

        object ItChoseSomeWayBill : State()

        object Done : State()
    }


    private fun isTransitionValid(toState: State): Boolean {
        return when (state.value) {
            is State.Created -> when (toState) {
                is State.SoftInProgress.Refresh -> true
                else -> false
            }
            is State.AwaitSelect -> when (toState) {
                is State.SoftInProgress.Refresh -> true
                is State.ItChoseSomeWayBill -> true
                else -> false
            }

            is State.ItChoseSomeWayBill -> when (toState) {
                is State.SoftInProgress.Refresh -> true
                is State.SoftInProgress.SetWaybill -> true
                is State.ItChoseSomeWayBill -> true
                is State.AwaitSelect -> true
                is State.Done -> true
                else -> false
            }

            is State.SoftInProgress.SetWaybill -> when (toState) {
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

            is State.Done -> false

            else -> false
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}