package ru.smartro.worknote.ui.login

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.smartro.worknote.data.DbLoginDataSource
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.NetworkLoginDataSource
import ru.smartro.worknote.data.NetworkState
import ru.smartro.worknote.data.organisations.OrganisationsDBDataSource
import ru.smartro.worknote.data.organisations.OrganisationsNetworkDataSource
import ru.smartro.worknote.data.organisations.OrganisationsRepository
import ru.smartro.worknote.data.workflow.WorkflowDBDataSource
import ru.smartro.worknote.data.workflow.WorkflowRepository
import ru.smartro.worknote.database.getDatabase

class OrganisationSelectViewModelFactory(val activity: Activity) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrganisationSelectViewModel::class.java)) {
            val db = getDatabase(activity.application)
            return OrganisationSelectViewModel(
                organisationsRepository = OrganisationsRepository(
                    organisationsNetworkDataSource = OrganisationsNetworkDataSource(),
                    organisationsDBDataSource = OrganisationsDBDataSource(db),
                    networkState = NetworkState(activity)
                ),
                loginRepository = LoginRepository(
                    dataSourceNetwork = NetworkLoginDataSource(),
                    dbLoginDataSource = DbLoginDataSource(db),
                    networkState = NetworkState(activity = activity)
                ),
                workflowRepository = WorkflowRepository(WorkflowDBDataSource(db))
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}