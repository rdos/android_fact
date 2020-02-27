package ru.smartro.worknote.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
import java.io.IOException

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _state = MutableLiveData<State>(State.Created(this))

    val state: LiveData<State>
        get() = _state

    private val job = Job()

    private val viewModelScope = CoroutineScope(job + Dispatchers.Main)

    private val workflowHolder = MutableLiveData<WorkflowModel?>()

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    val username = MutableLiveData<String>()
    val usernameStr: String
        get() = username.value ?: ""

    val passwordStr: String
        get() = password.value ?: ""

    val password = MutableLiveData<String>()

    private val _currentUserHolder: MutableLiveData<UserModel?> = MutableLiveData()

    val currentUserHolder : LiveData<UserModel?>
        get() = _currentUserHolder

    init {
        when (_state.value) {
            is State.Created -> onLoadData()
            else -> throw Exception("illegal initial state")
        }
    }

    //region  events
    fun onLoadData() {
        setState(State.ProcessInit(this))
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
        _currentUserHolder.value = userModel
        setState(State.InitWorkflow(this))
        viewModelScope.launch {
            var workflowModel = workflowRepository.getWorkFlowForUser(userModel.id)
            if (workflowModel == null) {
                workflowModel = WorkflowModel(userModel.id, false, null, null, null)
                workflowRepository.save(workflowModel)
            }
            workflowHolder.postValue(workflowModel)

        }
    }




    fun onAwaitCredentials() {
        setState(State.AwaitCredentials(this))
    }


    fun onWorkflowSet(workflowModel: WorkflowModel) {

    }

    fun onWorkDone() {
        setState(State.Done(this))
    }


    fun onLogin() {
        setState(State.SendCredentials(this))
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
            loginRepository.logout(userHolder = _currentUserHolder)
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

        open class SoftInProgress(subject: LoginViewModel) : State(subject)
        open class ModalInProgress(subject: LoginViewModel) : State(subject)

        class ProcessInit(subject: LoginViewModel) : ModalInProgress(subject)

        class InitWorkflow(subject: LoginViewModel) : ModalInProgress(subject)



        class AwaitCredentials(subject: LoginViewModel): State(subject)
        class SendCredentials(subject: LoginViewModel): SoftInProgress(subject)
        class CredentialsError(subject: LoginViewModel, val error: Int, val message: String? = null): State(subject)

        class Done(subject: LoginViewModel): State(subject)
    }

    private fun setState(toState: State) {
        if (isTransitionValid(toState)) {
            _state.postValue(toState)
            return
        }
        throw Exception("$toState - state not applicable")
    }

    private fun isTransitionValid(toState: State): Boolean {
        return when (state.value) {
            is State.Created -> when (toState) {
                is State.ProcessInit -> true

                else -> false
            }
            is State.ProcessInit -> when (toState) {
                is State.Done -> true
                is State.AwaitCredentials -> true

                else -> false
            }
            is State.AwaitCredentials -> when (toState) {
                is State.SendCredentials -> true

                else -> false
            }
            is State.SendCredentials -> when (toState) {
                is State.AwaitCredentials -> true
                is State.Done -> true
                else -> false
            }

            is State.Done -> false

            else -> false
        }
    }
}
