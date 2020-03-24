package ru.smartro.worknote.ui.workFlow.workorder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.Result
import ru.smartro.worknote.data.waybillBody.WaybillBodyRepository
import ru.smartro.worknote.data.waybillHead.WaybillRepository
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.WaybillHeadModel
import ru.smartro.worknote.domain.models.WorkOrderModel
import ru.smartro.worknote.domain.models.WorkflowModel
import timber.log.Timber

class WorkOrderViewModel(
    private val workflowRepository: WorkflowRepository,
    private val waybillBodyRepository: WaybillBodyRepository,
    private val waybillHeadRepository: WaybillRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private lateinit var currentUserHolder: MutableLiveData<UserModel>
    private lateinit var workflowHolder: MutableLiveData<WorkflowModel>

    val wayBillHead = MutableLiveData<WaybillHeadModel?>(null)

    private val _workOrders = MutableLiveData<List<WorkOrderModel>>()

    private val viewModelJob = Job()

    private val modelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _lastSelected = MutableLiveData<Int?>(null)

    private val _state = MutableLiveData<State?>(
        State.Created
    )

    val state: LiveData<State?>
        get() = _state

    val lastSelected: LiveData<Int?>
        get() = _lastSelected


    val workOrders: LiveData<List<WorkOrderModel>>
        get() = _workOrders


    fun canRefresh(): Boolean {
        return isTransitionValid(State.SoftInProgress.Refresh)
    }

    fun canConfirmChoice(): Boolean {
        return state.value is State.ItChoseSomeWorkOrder
    }

    fun canSelect(): Boolean {
        return state.value is State.AwaitSelect
                || state.value is State.ItChoseSomeWorkOrder
    }

    //region events

    fun onReset() {
        _lastSelected.postValue(null)
        _workOrders.postValue(null)
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
            }?.let {
                return@let loadWayBillName()
            }?.let {
                if (lastSelected.value == null) {
                    loadSelected()
                }
                return@let it
            }?.let {
                if (force) {
                    waybillBodyRepository.dropAllCD()
                }
                return@let loadWorkOrders()
            }?.let {
                setAwaitOrChose()
            }
        }
    }

    fun onChose(workOrderModel: WorkOrderModel) {
        if (!workOrders.value!!.contains(workOrderModel)) {
            setState(State.Error.AppError)
            return
        }
        _lastSelected.postValue(workOrderModel.srpId)
        setState(State.ItChoseSomeWorkOrder)
    }

    fun onCancelChose() {
        _lastSelected.postValue(null)
        setState(State.AwaitSelect)
    }

    fun onConfirmChoice() {
        val workOrderId = _lastSelected.value ?: throw Exception("work order must be set")
        val workflow = workflowHolder.value ?: throw Exception("workflow must be set")
        modelScope.launch {
            workflow.workOrderId = workOrderId
            workflow.isInProgress = true
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
        _lastSelected.value = workflow.workOrderId
        if (_lastSelected.value != null) {
            setState(State.ItChoseSomeWorkOrder)
        }
    }

    private suspend fun loadWorkOrders(): Boolean? {
        when (val workOrdersResult = getWorkOrders()) {
            is Result.Success -> {
                _workOrders.postValue(workOrdersResult.data)
                setState(State.AwaitSelect)

                return true
            }
            is Result.Error -> {
                if (workOrdersResult.isAuthError) {
                    onAuthError()
                    return null
                } else if (workOrdersResult.isIOError) {
                    setState(State.Error.NetworkError)
                }
                setState(State.AwaitSelect)

                return true
            }
        }
    }

    private fun setAwaitOrChose() {
        if (lastSelected.value != null) {
            setState(State.ItChoseSomeWorkOrder)
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

    private suspend fun loadWayBillName(): Boolean? {
        val wayBillHeadModel = waybillHeadRepository.get(
            workflowHolder.value?.wayBillId!!
        )
        wayBillHead.postValue(wayBillHeadModel)

        return true
    }

    private suspend fun getCurrentUser(): UserModel? {
        return loginRepository.getLoggedInUser()
    }

    private suspend fun getWorkflow(userModel: UserModel): WorkflowModel? {
        return workflowRepository.getWorkFlowForUser(userModel.id)
    }

    private suspend fun getWorkOrders(): Result<List<WorkOrderModel>> {
        val organisationId = workflowHolder.value?.organisationId
            ?: throw Exception("workflow and their organisation must be set")
        var userModel = currentUserHolder.value ?: throw Exception("current user must be set")
        val waybillId =
            workflowHolder.value?.wayBillId ?: throw Exception("current way bill must be set")

        return withContext(Dispatchers.IO) {
            when (val result = loginRepository.checkRefreshUser(userModel)) {
                is Result.Error -> {
                    if (result.isAuthError) {
                        return@withContext Result.Error(result.exception)
                    }
                }
                is Result.Success -> {
                    userModel = result.data
                    currentUserHolder.postValue(userModel)
                }
            }

            return@withContext waybillBodyRepository.getWorkOrders(
                organisationId = organisationId,
                user = userModel,
                waybillId = waybillId
            )
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
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
            object SetWaybill : SoftInProgress()
            object Refresh : SoftInProgress()
        }

        sealed class Error : State() {
            object AuthError : Error()
            object NetworkError : Error()
            object NotFindError : Error()
            object AppError : Error()
        }

        object AwaitSelect : State()

        object ItChoseSomeWorkOrder : State()

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
                is State.ItChoseSomeWorkOrder -> true
                else -> false
            }

            is State.ItChoseSomeWorkOrder -> when (toState) {
                is State.SoftInProgress.Refresh -> true
                is State.SoftInProgress.SetWaybill -> true
                is State.ItChoseSomeWorkOrder -> true
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
                is State.ItChoseSomeWorkOrder -> true
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

}