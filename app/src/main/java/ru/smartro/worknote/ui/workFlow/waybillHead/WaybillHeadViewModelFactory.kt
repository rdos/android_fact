package ru.smartro.worknote.ui.workFlow.waybillHead

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.smartro.worknote.data.DbLoginDataSource
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.NetworkLoginDataSource
import ru.smartro.worknote.data.NetworkState
import ru.smartro.worknote.data.waybill.WaybillDBDataSource
import ru.smartro.worknote.data.waybill.WaybillNetworkDataSource
import ru.smartro.worknote.data.waybill.WaybillRepository
import ru.smartro.worknote.data.workflow.WorkflowDBDataSource
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.database.getDatabase

class WaybillHeadViewModelFactory(private val activity: Activity) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaybillHeadViewModel::class.java)) {
            val app = activity.application
            val db = getDatabase(app)
            val networkState = NetworkState(activity)

            return WaybillHeadViewModel(
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
                ), waybillRepository = WaybillRepository(
                    waybillNetworkDataSource = WaybillNetworkDataSource(),
                    waybillDBDataSource = WaybillDBDataSource(
                        db
                    ),
                    networkState = networkState
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}