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
            val networkState = NetworkState(activity)
            val db = getDatabase(activity.application)
            return CurrentUserViewModel(
                organisationsRepository = OrganisationsRepository(
                    organisationsNetworkDataSource = OrganisationsNetworkDataSource(),
                    organisationsDBDataSource = OrganisationsDBDataSource(db),
                    networkState = networkState
                ),
                loginRepository = LoginRepository(
                    dataSourceNetwork = NetworkLoginDataSource(),
                    dbLoginDataSource = DbLoginDataSource(db),
                    networkState = networkState
                ),
                application = activity.application

            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}