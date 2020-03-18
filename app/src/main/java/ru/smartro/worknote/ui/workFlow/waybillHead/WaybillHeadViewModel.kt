package ru.smartro.worknote.ui.workFlow.waybillHead

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.Result
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

    private lateinit var currentUserHolder: MutableLiveData<UserModel>
    private lateinit var workflowHolder: MutableLiveData<WorkflowModel>
    private lateinit var currentVehicleId: MutableLiveData<Int>

    private val _waybills = MutableLiveData<List<WaybillHeadModel>>()

    private val viewModelJob = Job()

    private val modelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _lastSelected = MutableLiveData<Int?>(null)

    private val _state = MutableLiveData<State>(
        State.Created
    )

    val state: LiveData<State>
        get() = _state

    val lastSelected: LiveData<Int?>
        get() = _lastSelected


    val waybills: LiveData<List<WaybillHeadModel>>
        get() = _waybills

    private val _date: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())


    fun canRefresh(): Boolean {
        return isTransitionValid(State.SoftInProgress.Refresh)
    }

    fun canConfirmChoice(): Boolean {
        return state.value is State.ItChoseSomeWayBill
    }

    fun canSelect(): Boolean {
        return state.value is State.AwaitSelect
                || state.value is State.ItChoseSomeWayBill
    }

    //region events

    fun onReset() {
        _lastSelected.postValue(null)
        _waybills.postValue(null)
        _state.postValue(State.Created)
    }

    fun onInit() {
        if (state.value is State.Created) {
            onRefresh()
        }
    }


    fun onRefresh(force: Boolean = false) {
        setState(State.SoftInProgress.Refresh)
        modelScope.launch {
            loadUser()?.let {
                return@let loadWorkflow()
            }?.let{
                loadVehicle()
            }?.let {
                if (lastSelected.value == null) {
                    loadSelected()
                }
                return@let it
            }?.let {
                if (force) {
                    waybillRepository.dropAllCD()
                }
                return@let loadWaybills()
            }?.let {
                setAwaitOrChose()
            }
        }
    }

    fun onChose(waybill: WaybillHeadModel) {
        if (!waybills.value!!.contains(waybill)) {
            setState(State.Error.AppError)
            return
        }
        _lastSelected.postValue(waybill.id)
        setState(State.ItChoseSomeWayBill)
    }

    fun onCancelChose() {
        _lastSelected.postValue(null)
        setState(State.AwaitSelect)
    }

    fun onConfirmChoice() {
        val waybillId = _lastSelected.value ?: throw Exception("waybill must be set")
        val workflow = workflowHolder.value ?: throw Exception("workflow must be set")
        modelScope.launch {
            workflow.wayBillId = waybillId
            workflowRepository.save(workflow)
            setState(State.Done)
        }
    }

    private suspend fun onAuthError() {
        currentUserHolder.postValue(null)
        loginRepository.logout()
        setState(State.Error.AuthError)
    }
    //endregion


    private fun loadSelected() {
        val workflow = workflowHolder.value ?: return
        _lastSelected.value =  workflow.wayBillId
        if (_lastSelected.value != null) {
            setState(State.ItChoseSomeWayBill)
        }
    }

    private suspend fun loadWaybills(): Boolean? {
        when (val waybillsResult = getWaybills()) {
            is Result.Success -> {
                _waybills.postValue(waybillsResult.data)
                setState(State.AwaitSelect)

                return true
            }
            is Result.Error -> {
                if (waybillsResult.isAuthError) {
                    onAuthError()
                    return null
                } else if (waybillsResult.isIOError) {
                    setState(State.Error.NetworkError)
                }
                setState(State.AwaitSelect)

                return true
            }
        }
    }

    private fun setAwaitOrChose()
    {
        if (lastSelected.value != null) {
            setState(State.ItChoseSomeWayBill)
        } else {
            setState(State.AwaitSelect)
        }
    }

    private suspend fun loadUser(): Boolean? {
        val currentUser = getCurrentUser()
        if (currentUser == null) {
            onAuthError()
            return null
        }
        currentUserHolder = MutableLiveData(currentUser)

    return true
    }

    private suspend fun loadWorkflow(): Boolean? {
        val userModel = currentUserHolder.value?: throw Exception("current user must be set")
        val workflowModel = getWorkflow(userModel)
        if (workflowModel == null) {
            setState(State.Error.AppError)
            return null
        }
        workflowHolder = MutableLiveData(workflowModel)

        return true
    }

    private fun loadVehicle(): Boolean? {
        val vehicleId = workflowHolder.value?.vehicleId
        if (vehicleId == null) {
            setState(State.Error.AppError)
            return null
        }
        currentVehicleId = MutableLiveData(vehicleId)

        return true
    }

    private suspend fun getCurrentUser(): UserModel? {
        return loginRepository.getLoggedInUser()
    }

    private suspend fun getWorkflow(userModel: UserModel): WorkflowModel? {
        return workflowRepository.getWorkFlowForUser(userModel.id)
    }

    private suspend fun getWaybills(): Result<List<WaybillHeadModel>> {
        val date = _date.value ?: throw Exception("current date must be not null")
        val vehicleId = currentVehicleId.value ?: throw Exception("current vehicle id must be not null")
        val organisationId = workflowHolder.value?.organisationId
            ?: throw Exception("workflow and their organisation must be set")
        var userModel = currentUserHolder.value?: throw Exception("current user must be set")

        return withContext(Dispatchers.IO) {
            when (val result = loginRepository.checkRefreshUser(userModel)) {
                is Result.Error -> {
                    return@withContext Result.Error(result.exception)
                }
                is Result.Success -> {
                    userModel = result.data
                    currentUserHolder.postValue(userModel)
                }
            }

            return@withContext waybillRepository.getAllWaybillsByCriteria(
                criteria = WaybillRepository.WaybillCriteria(
                    date = date,
                    vehicleId = vehicleId,
                    organisationId = organisationId,
                    user = userModel
                )
            )
        }
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