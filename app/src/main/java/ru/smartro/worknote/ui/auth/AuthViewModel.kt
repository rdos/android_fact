package ru.smartro.worknote.ui.auth

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.network.body.AuthBody
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.response.auth.AuthResponse

class AuthViewModel(application: Application) : BaseViewModel(application) {

    fun auth(authModel: AuthBody): LiveData<Resource<AuthResponse>> {
        return network.auth(authModel)
    }
}