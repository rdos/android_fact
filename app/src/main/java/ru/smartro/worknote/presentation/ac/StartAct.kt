package ru.smartro.worknote.presentation.ac

import android.app.AlertDialog
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import ru.smartro.worknote.*
import ru.smartro.worknote.awORKOLDs.extensions.hideDialog
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.awORKOLDs.service.network.body.AuthBody
import ru.smartro.worknote.awORKOLDs.service.network.response.auth.AuthResponse
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.abs.AAct
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.presentation.work.PlatformEntity
import ru.smartro.worknote.presentation.work.Resource
import ru.smartro.worknote.presentation.work.Status


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

