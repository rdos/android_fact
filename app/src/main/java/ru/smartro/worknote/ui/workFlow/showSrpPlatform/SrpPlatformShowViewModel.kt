package ru.smartro.worknote.ui.workFlow.showSrpPlatform

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.srpPlatform.SrpPlatformRepository
import ru.smartro.worknote.domain.models.UserModel

class SrpPlatformShowViewModel(
    private val srpPlatformRepository: SrpPlatformRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private lateinit var currentUserHolder: MutableLiveData<UserModel>

    private val _platforms = MutableLiveData<List<PlatformToShow>>()

    private val viewModelJob = Job()

    private val modelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _isAuthError = MutableLiveData(false)

    val isAuthError: LiveData<Boolean>
        get() = _isAuthError

    val platforms: LiveData<List<PlatformToShow>>
        get() = _platforms


    fun onRefresh(workOrderId: Int, force: Boolean = false) {
        modelScope.launch {
            loadUser()?.let {
                loadPlatforms(workOrderId)
            }
        }
    }

    private suspend fun onAuthError() {
        currentUserHolder.postValue(null)
        loginRepository.logout()
        _isAuthError.postValue(true)
    }

    //endregion

    private suspend fun loadPlatforms(workOrderId: Int): Boolean? {
        _platforms.postValue(getPlatforms(workOrderId))

        return true
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

    private suspend fun getCurrentUser(): UserModel? {
        return loginRepository.getLoggedInUser()
    }

    private suspend fun getPlatforms(workOrderId: Int): List<PlatformToShow> {
        return srpPlatformRepository.getPlatformsWithContainerCount(workOrderId = workOrderId)
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}