package ru.smartro.worknote.work.ac

import android.Manifest
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.act_start.cl_act_start
import kotlinx.android.synthetic.main.act_start.auth_appversion
import kotlinx.android.synthetic.main.act_start.actv_activity_auth__it_test_version
import kotlinx.android.synthetic.main.act_start.auth_enter
import kotlinx.android.synthetic.main.act_start.auth_login
import kotlinx.android.synthetic.main.act_start.auth_password_out
import kotlinx.android.synthetic.main.act_start.login_login_out
import kotlinx.android.synthetic.main.act_start.auth_password
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.R
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.extensions.hideProgress
import ru.smartro.worknote.extensions.showingProgress
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.isShowForUser
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.AuthBody
import ru.smartro.worknote.service.network.response.auth.AuthResponse
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.work.abs.ActNOAbst
import ru.smartro.worknote.work.ac.checklist.StartOwnerAct
import ru.smartro.worknote.work.ac.map.MapAct

public val PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.READ_PHONE_STATE,
    Manifest.permission.LOCATION_HARDWARE,
    Manifest.permission.ACCESS_NETWORK_STATE
)
class StartAct : ActNOAbst() {
    private val vm: AuthViewModel by viewModel()

    private fun gotoNextAct() {
//            val isHasTask = true
        val isHasTask = vm.baseDat.hasWorkOrderInProgress_know0()
        startActivity(Intent(this, if (isHasTask) MapAct::class.java else StartOwnerAct::class.java))
        finish()
        AppliCation().runLocationService()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_start)
        actionBar?.title = "Вход в Систему"
        auth_appversion.text = BuildConfig.VERSION_NAME
        cl_act_start.setOnClickListener {
            MyUtil.hideKeyboard(this)
        }
        // TODO: 01.11.2021 !! !
        if (paramS().token.isShowForUser()) {
            gotoNextAct()
        } else {
            initViews()
        }

    }


    private fun initViews() {
        auth_enter.setOnClickListener {
           clickAuthEnter()
        }

        actv_activity_auth__it_test_version.isVisible = false
        if (BuildConfig.BUILD_TYPE != "debugProd") {
            val versionName = BuildConfig.VERSION_NAME
            //oopsTestqA
            val textIsTestEnv = getString(R.string.act_st_art_it_test_version).format(versionName)

            actv_activity_auth__it_test_version.isVisible = true
            actv_activity_auth__it_test_version.text = textIsTestEnv // + actv_activity_auth__it_test_version.text

        }

        if (BuildConfig.BUILD_TYPE != "debugProd") {
            auth_enter.setOnLongClickListener {
                auth_login.setText("admin@smartro.ru")
                auth_password.setText("xot1ieG5ro~hoa,ng4Sh")
                return@setOnLongClickListener true
            }
        }
//
//        if (isDevelMode) {
//            auth_login.setText("admin@smartro.ru")
//            auth_password.setText("xot1ieG5ro~hoa,ng4Sh")
//            clickAuthEnter()
//        }


/*        auth_enter.setOnLongClickListener {
            auth_login.setText("gkh2@smartro.ru")
            auth_password.setText("JT8NcST%sDqUpuc")
            return@setOnLongClickListener true
        }*/
    }

    private fun clickAuthEnter() {
        if (!auth_login.text.isNullOrBlank() && !auth_password.text.isNullOrBlank()) {
            showingProgress()
            vm.auth(AuthBody(auth_login.text.toString(), auth_password.text.toString()))
                .observe(this, Observer { result ->
                    val data = result.data
                    when (result.status) {
                        Status.SUCCESS -> {
                            hideProgress()
                            toast("Вы авторизованы")
//                            AppPreferences.BoTlogin = auth_login.text.toString()
                            if (paramS().token.isNullOrEmpty()) {
                                oopsTestqA()
                            }
                            paramS().token = data!!.data.token
                            gotoNextAct()
                            finish()
                        }
                        Status.ERROR -> {
                            hideProgress()
                            toast("Логин или пароль не совпадает")
                        }
                        Status.NETWORK -> {
                            hideProgress()
                            toast("Проблемы с интернетом")
                        }
                    }
                })
        } else {
            auth_password_out.error = "Проверьте пароль"
            login_login_out.error = "Проверьте логин"
        }
    }

    override fun onStop() {
        super.onStop()
        AppliCation().stopWorkERS()
    }


    open class AuthViewModel(application: Application) : BaseViewModel(application) {

        fun auth(authModel: AuthBody): LiveData<Resource<AuthResponse>> {
            return networkDat.auth(authModel)
        }
    }
}