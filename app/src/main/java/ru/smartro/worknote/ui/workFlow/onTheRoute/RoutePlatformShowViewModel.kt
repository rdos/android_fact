package ru.smartro.worknote.ui.workFlow.onTheRoute

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.srpPlatform.SrpPlatformRepository
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.WorkflowModel

class RoutePlatformShowViewModel(
    private val srpPlatformRepository: SrpPlatformRepository,
    private val loginRepository: LoginRepository,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private lateinit var currentUserHolder: MutableLiveData<UserModel>
    private lateinit var workflowHolder: MutableLiveData<WorkflowModel>

    private val _platforms = MutableLiveData<List<PlatformToShow>>()

    private val viewModelJob = Job()

    private val modelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _isAuthError = MutableLiveData(false)


    val lastExpendedPosition: MutableLiveData<Int?> = MutableLiveData(null)

    val isAuthError: LiveData<Boolean>
        get() = _isAuthError

    val platforms: LiveData<List<PlatformToShow>>
        get() = _platforms


    fun onInit() {
        onRefresh()
    }


    fun onRefresh(force: Boolean = false) {
        modelScope.launch {
            loadUser()?.let {
                return@let loadWorkflow()
            }?.let {
                loadPlatforms()
            }
        }
    }

    private suspend fun onAuthError() {
        currentUserHolder.postValue(null)
        loginRepository.logout()
        _isAuthError.postValue(true)
    }

    //endregion

    private suspend fun loadPlatforms(): Boolean? {
        val workOrderId = workflowHolder.value!!.workOrderId!!
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

    private suspend fun loadWorkflow(): Boolean? {
        val userModel = currentUserHolder.value ?: throw Exception("current user must be set")
        val workflowModel = getWorkflow(userModel)
        if (workflowModel == null) {
            _isAuthError.postValue(true)
            return null
        }
        if (::workflowHolder.isInitialized) {
            workflowHolder.postValue(workflowModel)
        } else {
            workflowHolder = MutableLiveData(workflowModel)
        }

        return true
    }

    private suspend fun getCurrentUser(): UserModel? {
        return loginRepository.getLoggedInUser()
    }

    private suspend fun getPlatforms(workOrderId: Int): List<PlatformToShow> {
        return srpPlatformRepository.getPlatformsWithContainerCount(workOrderId = workOrderId).map {
            PlatformToShow(
                name = it.name,
                address = it.address,
                containersCount = it.containersCount
            )
        }
    }

    private suspend fun getWorkflow(userModel: UserModel): WorkflowModel? {
        return workflowRepository.getWorkFlowForUser(userModel.id)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}