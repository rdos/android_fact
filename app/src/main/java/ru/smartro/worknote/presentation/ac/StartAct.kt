package ru.smartro.worknote.presentation.ac

import android.app.Application
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AAct
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.awORKOLDs.service.network.body.AuthBody
import ru.smartro.worknote.awORKOLDs.service.network.response.auth.AuthResponse
import ru.smartro.worknote.presentation.work.Resource


class StartAct : AAct() {

    private val vm: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        vm = ViewModelProvider(this)[AuthViewModel::class.java]
        AppliCation().stopWorkERS()
        if (paramS().isRestartApp) {
            paramS().AppRestarted()
        }

        setContentView(R.layout.act_start)
        supportActionBar?.hide()
    }

    open class AuthViewModel(app: Application) : AViewModel(app) {

        fun auth(authModel: AuthBody): LiveData<Resource<AuthResponse>> {
            return networkDat.auth(authModel)
        }
    }

}

