package ru.smartro.worknote.work.ac

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.act_start.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.R
import ru.smartro.worknote.base.AbstractAct
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.loadingShow
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.AuthBody
import ru.smartro.worknote.service.network.response.auth.AuthResponse
import ru.smartro.worknote.work.ac.choose.StartOwnerAct
import ru.smartro.worknote.util.MyUtil

class StartAct : AbstractAct() {
    private val viewModel_know0: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_start)
        actionBar?.title = "Вход в систему"
        auth_appversion.text = BuildConfig.VERSION_NAME
        baseview.setOnClickListener {
            MyUtil.hideKeyboard(this)
        }
        // TODO: 01.11.2021 !! !
        if (AppPreferences.isLogined) {
            val isHasTask = viewModel_know0.baseDat.hasWorkOrderInProgress_know0()
//            val isHasTask = true
            startActivity(Intent(this, MyUtil.getNextActClazz__todo(isHasTask)))
            finish()
        } else {
            initViews()
        }
    }

    private fun initViews() {
        auth_enter.setOnClickListener {
           clickAuthEnter()
        }

        if (BuildConfig.BUILD_TYPE == "debugProd") {
            actv_activity_auth__it_test_version.isVisible = false
        }

        if (BuildConfig.BUILD_TYPE != "debugProd") {
            auth_enter.setOnLongClickListener {
                auth_login.setText("admin@smartro.ru")
                auth_password.setText("xot1ieG5ro~hoa,ng4Sh")
                return@setOnLongClickListener true
            }
            if (BuildConfig.VERSION_NAME == "0.0.0.0-STAGE") {
                auth_login.setText("admin@smartro.ru")
                auth_password.setText("xot1ieG5ro~hoa,ng4Sh")
                clickAuthEnter()
            }
        }

/*        auth_enter.setOnLongClickListener {
            auth_login.setText("gkh2@smartro.ru")
            auth_password.setText("JT8NcST%sDqUpuc")
            return@setOnLongClickListener true
        }*/
    }

    private fun clickAuthEnter() {
        if (!auth_login.text.isNullOrBlank() && !auth_password.text.isNullOrBlank()) {
            loadingShow()
            viewModel_know0.auth(AuthBody(auth_login.text.toString(), auth_password.text.toString()))
                .observe(this, Observer { result ->
                    val data = result.data
                    when (result.status) {
                        Status.SUCCESS -> {
                            loadingHide()
                            toast("Вы авторизованы")
                            AppPreferences.isLogined = true
                            AppPreferences.userLogin = auth_login.text.toString()
                            AppPreferences.accessToken = data!!.data.token
                            startActivity(Intent(this, StartOwnerAct::class.java))
                            finish()
                        }
                        Status.ERROR -> {
                            loadingHide()
                            toast("Логин или пароль не совпадает")
                        }
                        Status.NETWORK -> {
                            loadingHide()
                            toast("Проблемы с интернетом")
                        }
                    }
                })
        } else {
            auth_password_out.error = "Проверьте пароль"
            login_login_out.error = "Проверьте логин"
        }
    }

    open class AuthViewModel(application: Application) : BaseViewModel(application) {

        fun auth(authModel: AuthBody): LiveData<Resource<AuthResponse>> {
            return networkDat.auth(authModel)
        }
    }
}