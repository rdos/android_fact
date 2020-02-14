package ru.smartro.worknote.ui.workFlow.selectVehicle

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.smartro.worknote.data.DbLoginDataSource
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.NetworkLoginDataSource
import ru.smartro.worknote.data.NetworkState
import ru.smartro.worknote.data.vehicle.VehicleDBDataSource
import ru.smartro.worknote.data.vehicle.VehicleNetworkDataSource
import ru.smartro.worknote.data.vehicle.VehicleRepository
import ru.smartro.worknote.data.workflow.WorkflowDBDataSource
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.database.getDatabase

class VehicleViewModelFactory(val activity: Activity) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VehicleViewModel::class.java)) {
            return VehicleViewModel(
                vehicleRepository = VehicleRepository(
                    vehicleDBDataSource = VehicleDBDataSource(getDatabase(activity.application)),
                    vehicleNetworkDataSource = VehicleNetworkDataSource(),
                    networkState = NetworkState(activity)
                ),
                loginRepository = LoginRepository(
                    dataSourceNetwork = NetworkLoginDataSource(),
                    dbLoginDataSource = DbLoginDataSource(
                        getDatabase(activity.application)
                    ),
                    networkState = NetworkState(activity)
                ),
                workflowRepository = WorkflowRepository(
                    workflowDBDataSource = WorkflowDBDataSource(getDatabase(activity.application))
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}