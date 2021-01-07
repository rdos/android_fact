package ru.smartro.worknote.ui.auth

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.Resource
import ru.smartro.worknote.service.body.AuthBody
import ru.smartro.worknote.service.response.auth.AuthResponse

class AuthViewModel(application: Application) : BaseViewModel(application) {

    fun auth(authModel: AuthBody): LiveData<Resource<AuthResponse>> {
        return network.auth(authModel)
    }
}