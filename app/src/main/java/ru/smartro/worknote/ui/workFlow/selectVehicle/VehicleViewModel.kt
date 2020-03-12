package ru.smartro.worknote.ui.workFlow.selectVehicle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.Result
import ru.smartro.worknote.data.vehicle.VehicleRepository
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.VehicleModel
import ru.smartro.worknote.domain.models.WorkflowModel
import timber.log.Timber

class VehicleViewModel(
    private val vehicleRepository: VehicleRepository,
    private val loginRepository: LoginRepository,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private lateinit var currentUserHolder: MutableLiveData<UserModel>
    private lateinit var workflowHolder: MutableLiveData<WorkflowModel>

    private val _vehicles = MutableLiveData<List<VehicleModel>>()

    private val viewModelJob = Job()

    private val modelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _lastSelected = MutableLiveData<VehicleModel?>(null)

    val lastSelected: LiveData<VehicleModel?>
        get() = _lastSelected

    val vehicles: LiveData<List<VehicleModel>>
        get() = _vehicles


    private val _state = MutableLiveData<State>(
        State.Created(this)
    )

    val state: LiveData<State>
        get() = _state

    fun canRefresh(): Boolean {
        return isTransitionValid(State.SoftInProgress.Refresh(this))
    }

    fun canConfirmChoice(): Boolean {
        return state.value is State.ItChoseSomeVehicle
    }

    fun canSelect(): Boolean {
        return state.value is State.AwaitSelect
                || state.value is State.ItChoseSomeVehicle
    }

    //region events
    fun onInit() {
        if (state.value is State.Created) {
            onRefresh()
        }
    }


    fun onRefresh(force: Boolean = false) {
        setState(State.SoftInProgress.Refresh(this))
        modelScope.launch {
            loadUser()?.let {
                return@let loadWorkflow(it)
            }?.let {
                if (force) {
                    vehicleRepository.dropAllCD()
                }
                loadVehicles(it)
            }
        }
    }

    fun onChose(vehicle: VehicleModel) {
        if (!vehicles.value!!.contains(vehicle)) {
            setState(State.Error.AppError(this))
            return
        }
        _lastSelected.postValue(vehicle)
        setState(State.ItChoseSomeVehicle(this))
    }

    fun onCancelChose() {
        _lastSelected.postValue(null)
        setState(State.AwaitSelect(this))
    }

    fun onConfirmChoice() {
        val vehicle = _lastSelected.value ?: throw Exception("vehicle must be set")
        val workflow = workflowHolder.value ?: throw Exception("workflow must be set")
        modelScope.launch {
            workflow.vehicleId = vehicle.id
            workflowRepository.save(workflow)
            setState(State.Done(this@VehicleViewModel))
        }
    }

    private suspend fun onAuthError() {
        currentUserHolder.postValue(null)
        loginRepository.logout()
        setState(State.Error.AuthError(this))
    }

    //endregion

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
            setState(State.Error.AppError(this))
            return null
        }
        workflowHolder = MutableLiveData(workflowModel)

        return user
    }

    private suspend fun loadVehicles(currentUser: UserModel): List<VehicleModel>? {
        when (val vehiclesResult = getVehicles(currentUser)) {
            is Result.Success -> {
                _vehicles.postValue(vehiclesResult.data)
                setState(State.AwaitSelect(this))

                return vehiclesResult.data
            }
            is Result.Error -> {
                if (vehiclesResult.isAuthError) {
                    onAuthError()
                    return null
                } else if (vehiclesResult.isIOError) {
                    setState(State.Error.NetworkError(this))
                }
                setState(State.AwaitSelect(this))

                return _vehicles.value
            }
        }
    }


    private suspend fun getCurrentUser(): UserModel? {
        return loginRepository.getLoggedInUser()
    }

    private suspend fun getWorkflow(userModel: UserModel): WorkflowModel? {
        return workflowRepository.getWorkFlowForUser(userModel.id)
    }

    private suspend fun getVehicles(
        userModel: UserModel
    ): Result<List<VehicleModel>> {
        return withContext(Dispatchers.IO) {
            loginRepository.checkRefreshUser(userModel)

            return@withContext vehicleRepository.getAllVehiclesByUser(
                userModel,
                getOrganisationId()
            )
        }
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

    sealed class State(val subject: VehicleViewModel) {

        class Created(subject: VehicleViewModel) : State(subject)

        open class SoftInProgress(subject: VehicleViewModel) : State(subject) {
            class SetVehicle(subject: VehicleViewModel) : SoftInProgress(subject)
            class Refresh(subject: VehicleViewModel) : SoftInProgress(subject)
        }

        open class Error(subject: VehicleViewModel) : State(subject) {
            class AuthError(subject: VehicleViewModel) : Error(subject)
            class NetworkError(subject: VehicleViewModel) : Error(subject)
            class NotFindError(subject: VehicleViewModel) : Error(subject)
            class AppError(subject: VehicleViewModel) : Error(subject)
        }

        class AwaitSelect(subject: VehicleViewModel) : State(subject)

        class ItChoseSomeVehicle(subject: VehicleViewModel) : State(subject)

        class Done(subject: VehicleViewModel) : State(subject)
    }


    private fun isTransitionValid(toState: State): Boolean {
        return when (state.value) {
            is State.Created -> when (toState) {
                is State.SoftInProgress.Refresh -> true
                else -> false
            }
            is State.AwaitSelect -> when (toState) {
                is State.SoftInProgress.Refresh -> true
                is State.ItChoseSomeVehicle -> true
                else -> false
            }

            is State.ItChoseSomeVehicle -> when (toState) {
                is State.SoftInProgress.Refresh -> true
                is State.SoftInProgress.SetVehicle -> true
                is State.ItChoseSomeVehicle -> true
                is State.AwaitSelect -> true
                is State.Done -> true
                else -> false
            }

            is State.SoftInProgress.SetVehicle -> when (toState) {
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