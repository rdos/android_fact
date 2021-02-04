package ru.smartro.worknote.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_auth.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.Status
import ru.smartro.worknote.service.body.AuthBody
import ru.smartro.worknote.ui.choose.owner_1.OrganisationActivity
import ru.smartro.worknote.ui.map.MapActivity

class AuthActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        actionBar?.title = "Вход в систему"
        if (AppPreferences.isLogined) {
            if (AppPreferences.thisUserHasTask) {
                startActivity(Intent(this, MapActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, OrganisationActivity::class.java))
                finish()
            }
        } else {
            initViews()
        }
    }

    private fun initViews() {
        auth_enter.setOnClickListener {
            if (!auth_login.text.isNullOrBlank() && !auth_password.text.isNullOrBlank()) {
                viewModel.auth(AuthBody(auth_login.text.toString(), auth_password.text.toString()))
                    .observe(this, Observer { result ->
                        val data = result.data
                        when (result.status) {
                            Status.SUCCESS -> {
                                toast("Вы авторизованы")
                                AppPreferences.isLogined = true
                                AppPreferences.userLogin = auth_login.text.toString()
                                AppPreferences.accessToken = data!!.data.token
                                startActivity(Intent(this, OrganisationActivity::class.java))
                                finish()
                            }
                            Status.ERROR -> {
                                toast("Логин или пароль не совпадает")
                            }
                            Status.NETWORK -> {
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
    }
}