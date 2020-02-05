package ru.smartro.worknote.ui.login

import android.opengl.Visibility
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
import ru.smartro.worknote.domain.models.UserModel
import java.io.IOException
import java.util.*

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val job = Job()

    private val viewModelScope = CoroutineScope(job + Dispatchers.Main)

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
    private val _userIsUpdating: MutableLiveData<Boolean> = MutableLiveData(true)
    val userIsUpdating : LiveData<Boolean>
        get() = _userIsUpdating

    init {
        viewModelScope.launch {
            _userIsUpdating.postValue(true)
            loginRepository.getLoggedInUser(_currentUserHolder)
            _userIsUpdating.postValue(false)
        }
    }

    fun login(userModel: UserModel) {
        //     val result = loginRepository.login(userModel)
    }

    fun login() {
        loginDataChanged()
        if (_loginForm.value?.isDataValid != true) {
            _loginResult.value = LoginResult(error = R.string.login_validation_err)
            return
        }

        viewModelScope.launch {
            val result = loginRepository.login(usernameStr, passwordStr)
            if (result is Result.Success) {
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = result.data.name))
            } else if (result is Result.Error) {
                if (result.exception is IOException) {
                    _loginResult.value = LoginResult(
                        error = R.string.api_error_no_connection
                    )
                } else {
                    val param = result.message ?: result.exception.message ?: ""
                    _loginResult.value = LoginResult(
                        error = R.string.api_error,
                        errorParam = param
                    )
                }
            }
        }
        // can be launched in a separate asynchronous job


    }

    fun logut() {
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
}
