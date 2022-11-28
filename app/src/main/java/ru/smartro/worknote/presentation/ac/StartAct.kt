package ru.smartro.worknote.presentation.ac

import android.app.Application
import android.os.Bundle
import androidx.activity.viewModels
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AAct
import ru.smartro.worknote.andPOintD.AViewModel


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
// TODO: R_dos!!!
//        fun auth(): LiveData<Resource<AuthResponse>> {
//                                                    //            viewModelScope.coroutineContext
//            val authRequest = AuthRequest()
//            App.oKRESTman().add(authRequest)
//            
////            Test().main()
//            val res = networkDat.auth(authModel)
//            return res
//        }
    }

}

