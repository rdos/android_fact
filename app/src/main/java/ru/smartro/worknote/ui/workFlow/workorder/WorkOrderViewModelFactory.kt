package ru.smartro.worknote.ui.workFlow.workorder

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.smartro.worknote.data.DbLoginDataSource
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.NetworkLoginDataSource
import ru.smartro.worknote.data.NetworkState
import ru.smartro.worknote.data.waybillBody.DbWaybillWIthRelationsConverter
import ru.smartro.worknote.data.waybillBody.WaybillBodyDBDataSource
import ru.smartro.worknote.data.waybillBody.WaybillBodyNetworkDataSource
import ru.smartro.worknote.data.waybillBody.WaybillBodyRepository
import ru.smartro.worknote.data.workflow.WorkflowDBDataSource
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.database.getDatabase

class WorkOrderViewModelFactory(private val activity: Activity) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkOrderViewModel::class.java)) {
            val app = activity.application
            val db = getDatabase(app)
            val networkState = NetworkState(activity)

            return WorkOrderViewModel(
                workflowRepository = WorkflowRepository(
                    workflowDBDataSource = WorkflowDBDataSource(
                        db
                    )
                ),
                loginRepository = LoginRepository(
                    dataSourceNetwork = NetworkLoginDataSource(),
                    dbLoginDataSource = DbLoginDataSource(
                        db
                    ),
                    networkState = networkState
                ), waybillBodyRepository = WaybillBodyRepository(
                    networkState = networkState,
                    waybillDBDataSource = WaybillBodyDBDataSource(
                        db,
                        DbWaybillWIthRelationsConverter()
                    ),
                    waybillNetworkDataSource = WaybillBodyNetworkDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}