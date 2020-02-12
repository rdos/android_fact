package ru.smartro.worknote.ui.workFlow.selectVehicle

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
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.VehicleModel

class VehicleViewModel(
    private val vehicleRepository: VehicleRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _currentUserHolder = MutableLiveData<UserModel?>()

    private val _authError = MutableLiveData<Boolean>(false)

    private val _vehicles = MutableLiveData<List<VehicleModel>>()

    private val viewModelJob = Job()

    private val modelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _isUpdating = MutableLiveData<Boolean>(false)

    val authError: LiveData<Boolean>
        get() = _authError

    val vehicles: LiveData<List<VehicleModel>>
        get() = _vehicles

    val isUpdating: LiveData<Boolean>
        get() = _isUpdating

    init {
        refresh()
    }

    fun refresh() {
        modelScope.launch {
            _isUpdating.postValue(true)
            loginRepository.getLoggedInUser(_currentUserHolder)
            val currentUser = _currentUserHolder.value
            if (currentUser?.currentOrganisationId == null) {
                _authError.postValue(true)
                return@launch
            }
            val middleResult = vehicleRepository.getAllVehiclesByUser(currentUser)
            if (middleResult is Result.Success) {
                _vehicles.postValue(middleResult.data)
            } else {
                _authError.postValue(true)
                return@launch
            }


            _isUpdating.postValue(false)
        }
    }

}