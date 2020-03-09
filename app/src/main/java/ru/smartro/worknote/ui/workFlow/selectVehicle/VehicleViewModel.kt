package ru.smartro.worknote.ui.workFlow.selectVehicle

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.Result
import ru.smartro.worknote.data.vehicle.VehicleRepository
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.VehicleModel
import ru.smartro.worknote.domain.models.WorkflowModel

class VehicleViewModel(
    private val vehicleRepository: VehicleRepository,
    private val loginRepository: LoginRepository,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {
    private val _currentUserHolder = MutableLiveData<UserModel?>()

    private val _authError = MutableLiveData<Boolean>(false)

    private val _vehicles = MutableLiveData<List<VehicleModel>>()

    private val _workflow = MutableLiveData<WorkflowModel?>()

    private val viewModelJob = Job()

    private val modelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _isUpdating = MutableLiveData<Boolean>(false)


    val lastSelected = MutableLiveData<Int?>(null)

    val authError: LiveData<Boolean>
        get() = _authError

    val vehicles: LiveData<List<VehicleModel>>
        get() = _vehicles

    val isUpdating: LiveData<Boolean>
        get() = _isUpdating

    val workflow: LiveData<WorkflowModel?>
        get() = _workflow

    val workDone = MutableLiveData<Boolean>(false)


    private fun logout() {

    }

    fun refresh(force: Boolean = false) {
        if (force) {
            loginRepository.dropAllCD()
            vehicleRepository.dropAllCD()
        }
        modelScope.launch {
            _isUpdating.postValue(true)
            _currentUserHolder.postValue(loginRepository.getLoggedInUser())
            val currentUser = _currentUserHolder.value ?: return@launch
            //            if (currentUser?.currentOrganisationId == null) {
//                _authError.postValue(true)
//                _isUpdating.postValue(false)
//                return@launch
//            }
            val middleResult = vehicleRepository.getAllVehiclesByUser(currentUser)
            if (middleResult is Result.Success) {
                if (middleResult.data.isNotEmpty() && _vehicles.value?.equals(middleResult.data) != true) {
                    _vehicles.postValue(middleResult.data)
                }
            } else {
                _authError.postValue(true)
                _isUpdating.postValue(false)
                return@launch
            }
            val workflowModel = workflowRepository.getWorkFlowForUser(currentUser.id)
                ?: WorkflowModel(currentUser.id, true, null, null, null)

            _workflow.postValue(workflowModel)
            _isUpdating.postValue(false)
            workDone.postValue(workflowModel.vehicleId !== null)
        }
    }

    fun getListener(): (it: View) -> Unit {
        return {
            it.isEnabled = false
            val vehicleId = this.lastSelected.value
            val workflowModel = this.workflow.value
            if (vehicleId !== null && workflowModel !== null) {
                modelScope.launch {
                    workflowModel.vehicleId = vehicleId
                    workflowRepository.save(workflowModel)
                    workDone.postValue(true)
                }
            }
        }
    }
}