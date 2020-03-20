package ru.smartro.worknote.ui.workFlow.showSrpPlatform

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.smartro.worknote.data.DbLoginDataSource
import ru.smartro.worknote.data.LoginRepository
import ru.smartro.worknote.data.NetworkLoginDataSource
import ru.smartro.worknote.data.NetworkState
import ru.smartro.worknote.data.srpPlatform.SrpPlatformDBDataSource
import ru.smartro.worknote.data.srpPlatform.SrpPlatformRepository
import ru.smartro.worknote.database.getDatabase

class SrpPlatformShowViewModelFactory(private val activity: Activity) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SrpPlatformShowViewModel::class.java)) {
            val app = activity.application
            val db = getDatabase(app)
            val networkState = NetworkState(activity)

            return SrpPlatformShowViewModel(
                loginRepository = LoginRepository(
                    dataSourceNetwork = NetworkLoginDataSource(),
                    dbLoginDataSource = DbLoginDataSource(
                        db
                    ),
                    networkState = networkState
                ), srpPlatformRepository = SrpPlatformRepository(
                    srpPlatformDBDataSource = SrpPlatformDBDataSource(db)
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}