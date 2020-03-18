package ru.smartro.worknote.ui.workFlow.workorder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.waybillBody.WaybillBodyRepository
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.WaybillHeadModel
import ru.smartro.worknote.domain.models.WorkflowModel

class WorkOrderViewModel(
    private val workflowRepository: WorkflowRepository,
    private val waybillBodyRepository: WaybillBodyRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private lateinit var currentUserHolder: MutableLiveData<UserModel>
    private lateinit var workflowHolder: MutableLiveData<WorkflowModel>
    private lateinit var currentVehicleId: MutableLiveData<Int>

    private val _waybills = MutableLiveData<List<WaybillHeadModel>>()

    private val viewModelJob = Job()

    private val modelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _lastSelected = MutableLiveData<Int?>(null)

    private val _state = MutableLiveData<State?>(

    )

    val state: LiveData<State?>
        get() = _state

    val lastSelected: LiveData<Int?>
        get() = _lastSelected


    val waybills: LiveData<List<WaybillHeadModel>>
        get() = _waybills


    fun onInit() {
        modelScope.launch {
            loadUser()?.let {
                return@let loadWorkflow()
            }?.let {
                waybillBodyRepository.refresh(
                    currentUserHolder.value!!,
                    workflowHolder.value!!.organisationId!!,
                    workflowHolder.value!!.wayBillId!!
                )
            }

        }
    }


    private suspend fun loadUser(): Boolean? {
        val currentUser = getCurrentUser()
        if (currentUser == null) {
            // onAuthError()
            return null
        }
        currentUserHolder = MutableLiveData(currentUser)

        return true
    }

    private suspend fun getCurrentUser(): UserModel? {
        return loginRepository.getLoggedInUser()
    }

    private suspend fun getWorkflow(userModel: UserModel): WorkflowModel? {
        return workflowRepository.getWorkFlowForUser(userModel.id)
    }


    private suspend fun loadWorkflow(): Boolean? {
        val userModel = currentUserHolder.value ?: throw Exception("current user must be set")
        val workflowModel = getWorkflow(userModel)
        if (workflowModel == null) {
            //      setState(State.Error.AppError)
            return null
        }
        workflowHolder = MutableLiveData(workflowModel)

        return true
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    sealed class State

}