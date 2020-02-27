package ru.smartro.worknote.ui.login

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.smartro.worknote.data.DbLoginDataSource
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.NetworkLoginDataSource
import ru.smartro.worknote.data.NetworkState
import ru.smartro.worknote.data.workflow.WorkflowDBDataSource
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.database.getDatabase

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory(val activity: Activity) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val db = getDatabase(activity.application)

            return LoginViewModel(
                loginRepository = LoginRepository(
                    dbLoginDataSource = DbLoginDataSource(db),
                    dataSourceNetwork = NetworkLoginDataSource(),
                    networkState = NetworkState(activity = activity)
                ),
                workflowRepository = WorkflowRepository(WorkflowDBDataSource(db))
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
