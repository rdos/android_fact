package ru.smartro.worknote.ui.home

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

class HomeViewModelFactory(val activity: Activity) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                loginRepository = LoginRepository(
                    dataSourceNetwork = NetworkLoginDataSource(),
                    dbLoginDataSource = DbLoginDataSource(
                        getDatabase(activity.application)
                    ),
                    networkState = NetworkState(activity)
                ),
                organisationsRepository = OrganisationsRepository(
                    organisationsNetworkDataSource = OrganisationsNetworkDataSource(),
                    organisationsDBDataSource = OrganisationsDBDataSource(getDatabase(activity.application))
                )

            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}