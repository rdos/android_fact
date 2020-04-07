package ru.smartro.worknote.ui.workFlow.maintenance

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.smartro.worknote.data.DbLoginDataSource
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.NetworkLoginDataSource
import ru.smartro.worknote.data.NetworkState
import ru.smartro.worknote.data.srpPlatform.SrpPlatformDBDataSource
import ru.smartro.worknote.data.srpPlatform.SrpPlatformRepository
import ru.smartro.worknote.data.workflow.WorkflowDBDataSource
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.database.getDatabase

class MaintenanceViewModelFactory(private val activity: Activity) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MaintenanceViewModel::class.java)) {
            val app = activity.application
            val db = getDatabase(app)
            val networkState = NetworkState(activity)

            return MaintenanceViewModel(
                WorkflowRepository(
                    WorkflowDBDataSource(db)
                ),
                SrpPlatformRepository(SrpPlatformDBDataSource(db)),
                LoginRepository(NetworkLoginDataSource(), DbLoginDataSource(db), networkState)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}