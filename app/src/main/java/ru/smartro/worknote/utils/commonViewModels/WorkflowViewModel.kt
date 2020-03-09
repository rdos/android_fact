package ru.smartro.worknote.utils.commonViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.domain.models.UserModel

import ru.smartro.worknote.domain.models.WorkflowModel

class WorkflowViewModel(
    private val workflowRepository: WorkflowRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val job = Job()

    private val viewModelScope = CoroutineScope(job + Dispatchers.Main)

    private lateinit var workflowModel: WorkflowModel

    private lateinit var currentUser: UserModel

    private val currentUserHolder = MutableLiveData<UserModel?>()

    private val _vehicleId = MutableLiveData<Int?>(null)

    private val _waybillId = MutableLiveData<Int?>(null)

    val _isUpdating = MutableLiveData<Boolean>(false)
    val authError = MutableLiveData<Boolean>(false)

    val isUpdating: LiveData<Boolean>
        get() = _isUpdating

    init {
        viewModelScope.launch {
            _isUpdating.postValue(true)
            currentUserHolder.postValue(loginRepository.getLoggedInUser())
            if (currentUserHolder.value == null) {
                authError.postValue(true)
                _isUpdating.postValue(false)
                return@launch
            }

            workflowModel = workflowRepository.getWorkFlowForUser(currentUser.id)
                ?: WorkflowModel(currentUser.id, false, null, null, null)
            _isUpdating.postValue(false)
        }
    }


    fun setVehicleId(vehicleId: Int) {
        viewModelScope.launch {
            workflowRepository.save(workflowModel.apply { this.vehicleId = vehicleId })
            _vehicleId.postValue(vehicleId)
        }
    }

    fun getVehicleId(): Int? {
        return _vehicleId.value
    }

    fun removeVehicle() {
        viewModelScope.launch {
            workflowRepository.save(workflowModel.apply { this.vehicleId = null })
            _vehicleId.postValue(null)
        }
    }


    fun setWaybillId(wayBillId: Int) {
        viewModelScope.launch {
            workflowRepository.save(workflowModel.apply { this.wayBillId = wayBillId })
            _waybillId.postValue(wayBillId)
        }
    }

    fun getWaybillId(): Int? {
        return _waybillId.value
    }

    fun removeWayBillId() {
        viewModelScope.launch {
            workflowRepository.save(workflowModel.apply { this.wayBillId = null })
            _waybillId.postValue(null)
        }
    }
}