package ru.smartro.worknote.work.ac

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_auth.*
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
import ru.smartro.worknote.work.ac.choose.OwnerAct
import ru.smartro.worknote.util.MyUtil

class StartAct : AbstractAct() {
    private val vm: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        actionBar?.title = "Вход в систему"
        auth_appversion.text = BuildConfig.VERSION_NAME
        baseview.setOnClickListener {
            MyUtil.hideKeyboard(this)
        }
        // TODO: 01.11.2021 !! !
        if (AppPreferences.isLogined) {
            val isHasTask = vm.db.hasWorkOrderInProgress()
            if (isHasTask) {
                startActivity(Intent(this, MyUtil.getNextActClazz__todo(isHasTask)))
            } else {
                startActivity(Intent(this, MyUtil.getNextActClazz__todo(isHasTask)))
            }
            finish()
        } else {
            initViews()
        }
    }

    private fun initViews() {
        auth_enter.setOnClickListener {
            if (!auth_login.text.isNullOrBlank() && !auth_password.text.isNullOrBlank()) {
                loadingShow()
                vm.auth(AuthBody(auth_login.text.toString(), auth_password.text.toString()))
                    .observe(this, Observer { result ->
                        val data = result.data
                        when (result.status) {
                            Status.SUCCESS -> {
                                loadingHide()
                                toast("Вы авторизованы")
                                AppPreferences.isLogined = true
                                AppPreferences.userLogin = auth_login.text.toString()
                                AppPreferences.accessToken = data!!.data.token
                                startActivity(Intent(this, OwnerAct::class.java))
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

        if (BuildConfig.BUILD_TYPE == "debugProd") {
            actv_activity_auth__it_test_version.isVisible = false
        }

        if (BuildConfig.BUILD_TYPE != "debugProd") {
            auth_enter.setOnLongClickListener {
                auth_login.setText("admin@smartro.ru")
                auth_password.setText("xot1ieG5ro~hoa,ng4Sh")
                return@setOnLongClickListener true
            }
        }

/*        auth_enter.setOnLongClickListener {
            auth_login.setText("gkh2@smartro.ru")
            auth_password.setText("JT8NcST%sDqUpuc")
            return@setOnLongClickListener true
        }*/
    }

   open class AuthViewModel(application: Application) : BaseViewModel(application) {

        fun auth(authModel: AuthBody): LiveData<Resource<AuthResponse>> {
            return network.auth(authModel)
        }
    }
}