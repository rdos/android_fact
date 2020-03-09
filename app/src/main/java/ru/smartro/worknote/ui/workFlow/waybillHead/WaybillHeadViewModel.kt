package ru.smartro.worknote.ui.workFlow.waybillHead

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.waybill.WaybillRepository
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.domain.models.UserModel
import ru.smartro.worknote.domain.models.WaybillHeadModel
import ru.smartro.worknote.domain.models.WorkflowModel
import java.time.LocalDate

class WaybillHeadViewModel(
    private val workflowRepository: WorkflowRepository,
    private val waybillRepository: WaybillRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _currentUserHolder = MutableLiveData<UserModel?>()

    private val _authError = MutableLiveData<Boolean>(false)

    private val _waybills = MutableLiveData<List<WaybillHeadModel>>()

    private val _workflow = MutableLiveData<WorkflowModel?>()

    private val viewModelJob = Job()

    private val modelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _isUpdating = MutableLiveData<Boolean>(false)

    val lastSelected = MutableLiveData<Int?>(null)

    val authError: LiveData<Boolean>
        get() = _authError

    val waybills: LiveData<List<WaybillHeadModel>>
        get() = _waybills

    val isUpdating: LiveData<Boolean>
        get() = _isUpdating

    val workflow: LiveData<WorkflowModel?>
        get() = _workflow

    val workDone = MutableLiveData<Boolean>(false)

    private val _date: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())


    init {
        refresh()
    }

    fun refresh(force: Boolean = false) {
        if (force) {
            loginRepository.dropAllCD()
            waybillRepository.dropAllCD()
        }
        modelScope.launch {
            _isUpdating.postValue(true)
            _currentUserHolder.postValue(loginRepository.getLoggedInUser())
            val currentUser = _currentUserHolder.value
//            if (currentUser?.currentOrganisationId == null) {
//                _authError.postValue(true)
//                return@launch
//            }

//
//            val middleResult = waybillRepository.getAllWaybillsByCriteria(currentUser)
//            if (middleResult is Result.Success) {
//                if (middleResult.data.isNotEmpty() && _vehicles.value?.equals(middleResult.data) != true) {
//                    _vehicles.postValue(middleResult.data)
//                }
//            } else {
//                _authError.postValue(true)
//                return@launch
//            }
//            val workflowModel = workflowRepository.getWorkFlowForUser(currentUser.id)
//                ?: WorkflowModel(currentUser.id, true, null, null)
//
//            _workflow.postValue(workflowModel)
//            _isUpdating.postValue(false)
//            workDone.postValue(workflowModel.vehicleId !== null)
        }
    }


//    object Converter {
//        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
//        @InverseMethod("stringToDate")
//        fun dateToString(
//            view: EditText, oldValue: LocalDate,
//            value: LocalDate
//        ): String {
//            return value.format(formatter)
//        }
//
//        fun stringToDate(
//            view: EditText, oldValue: String,
//            value: String
//        ): LocalDate {
//            return LocalDate.parse(value, formatter)
//        }
//    }


}