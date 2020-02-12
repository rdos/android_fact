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
import ru.smartro.worknote.database.getDatabase

class OrganisationSelectViewModelFactory(val activity: Activity) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrganisationSelectViewModel::class.java)) {
            return OrganisationSelectViewModel(
                organisationsRepository = OrganisationsRepository(
                    organisationsNetworkDataSource = OrganisationsNetworkDataSource(),
                    organisationsDBDataSource = OrganisationsDBDataSource(getDatabase(activity.application))
                ),
                loginRepository = LoginRepository(
                    dataSourceNetwork = NetworkLoginDataSource(),
                    dbLoginDataSource = DbLoginDataSource(
                        getDatabase(activity.application)
                    ),
                    networkState = NetworkState(activity = activity)
                )

            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}