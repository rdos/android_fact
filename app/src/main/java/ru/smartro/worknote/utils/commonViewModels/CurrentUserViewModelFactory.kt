package ru.smartro.worknote.utils.commonViewModels

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

class CurrentUserViewModelFactory(val activity: Activity) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrentUserViewModel::class.java)) {
            return CurrentUserViewModel(
                organisationsRepository = OrganisationsRepository(
                    organisationsNetworkDataSource = OrganisationsNetworkDataSource(),
                    organisationsDBDataSource = OrganisationsDBDataSource(getDatabase(activity.application))
                ),
                loginRepository = LoginRepository(
                    dataSourceNetwork = NetworkLoginDataSource(),
                    dbLoginDataSource = DbLoginDataSource(
                        getDatabase(activity.application)
                    ),
                    networkState = NetworkState(activity)
                ),
                application = activity.application

            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}