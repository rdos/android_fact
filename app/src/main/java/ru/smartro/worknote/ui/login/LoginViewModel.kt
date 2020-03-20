package ru.smartro.worknote.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.smartro.worknote.R
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.Result
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.WorkflowModel
import timber.log.Timber
import java.io.IOException

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _state = MutableLiveData<State>(State.Created(this))

    val state: LiveData<State>
        get() = _state

    private val viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val workflowHolder = MutableLiveData<WorkflowModel?>()

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    val username = MutableLiveData<String>()
    val usernameStr: String
        get() = username.value ?: ""

    val passwordStr: String
        get() = password.value ?: ""

    val password = MutableLiveData<String>()

    private val _currentUserHolder: MutableLiveData<UserModel?> = MutableLiveData()

    init {
        when (_state.value) {
            is State.Created -> onLoadData()
            else -> throw Exception("illegal initial state")
        }
    }

    //region  events
    fun onLoadData() {
        setState(State.ModalInProgress.ProcessInit(this))
        viewModelScope.launch {
            val userModel = loginRepository.getLoggedInUser()
            if (userModel != null) {
                onUserSet(userModel)
            } else {
                onAwaitCredentials()
            }

        }
    }

    fun onUserSet(userModel: UserModel) {
        _currentUserHolder.postValue(userModel)
        viewModelScope.launch {
            var workflowModel = workflowRepository.getWorkFlowForUser(userModel.id)
            if (workflowModel == null) {
                workflowModel = WorkflowModel(userModel.id, false, null, null, null, null)
                workflowRepository.save(workflowModel)
            }
            workflowHolder.value = workflowModel
            onDone()
        }

    }

    fun onDone() {
        val workflow = workflowHolder.value
        when {
            workflow == null -> throw  Exception("workflow must be set")
            workflow.organisationId == null -> setState(State.Done.NeedsOrganisation(this))
            workflow.vehicleId == null -> setState(State.Done.NeedsVehicle(this))
            workflow.wayBillId == null -> setState(State.Done.NeedsWaybill(this))
            else -> setState(State.Done(this))
        }
    }

    fun onAwaitCredentials() {
        setState(State.AwaitCredentials(this))
    }


    fun onLogin() {
        setState(State.SoftInProgress.SendCredentials(this))
        loginDataChanged()
        if (_loginForm.value?.isDataValid != true) {
            setState(State.CredentialsError(this, R.string.login_validation_err, null))
            return
        }

        viewModelScope.launch {
            val result = loginRepository.login(usernameStr, passwordStr)
            if (result is Result.Success) {
                onUserSet(result.data)
            } else if (result is Result.Error) {
                if (result.exception is IOException) {
                    setState(State.CredentialsError(this@LoginViewModel, R.string.api_error_no_connection, null))
                } else {
                    val param = result.message ?: result.exception.message ?: ""
                    setState(State.CredentialsError(this@LoginViewModel, R.string.api_error, param))
                }
            }
        }

    }

//endregion


    fun logout() {
        viewModelScope.launch {
            loginRepository.logout()
            _currentUserHolder.postValue(null)
        }
    }

    fun loginDataChanged() {
        if (!isUserNameValid()) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid()) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(): Boolean {
        return if (usernameStr.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(usernameStr).matches()
        } else {
            usernameStr.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(): Boolean {
        return passwordStr.length > 5
    }

    sealed class State(protected val subject: LoginViewModel) {

        class Created(subject: LoginViewModel) : State(subject)

        open class SoftInProgress(subject: LoginViewModel) : State(subject) {
            class SendCredentials(subject: LoginViewModel): SoftInProgress(subject)
        }

        open class ModalInProgress(subject: LoginViewModel) : State(subject) {
            class ProcessInit(subject: LoginViewModel) : ModalInProgress(subject)
        }


        class AwaitCredentials(subject: LoginViewModel): State(subject)

        class CredentialsError(subject: LoginViewModel, val error: Int, val message: String? = null): State(subject)

        open class Done(subject: LoginViewModel): State(subject) {
            class NeedsOrganisation(subject: LoginViewModel): Done(subject)
            class NeedsVehicle(subject: LoginViewModel): Done(subject)
            class NeedsWaybill(subject: LoginViewModel): Done(subject)
        }
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

    private fun isTransitionValid(toState: State): Boolean {
        return when (state.value) {
            is State.Created -> when (toState) {
                is State.ModalInProgress.ProcessInit -> true

                else -> false
            }
            is State.ModalInProgress.ProcessInit -> when (toState) {
                is State.Done -> true
                is State.AwaitCredentials -> true

                else -> false
            }
            is State.AwaitCredentials -> when (toState) {
                is State.SoftInProgress.SendCredentials -> true
                is State.CredentialsError -> true

                else -> false
            }
            is State.CredentialsError -> when (toState) {
                is State.SoftInProgress.SendCredentials -> true
                is State.CredentialsError -> true
                else -> false
            }

            is State.SoftInProgress.SendCredentials -> when (toState) {
                is State.CredentialsError -> true
                is State.Done -> true
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
