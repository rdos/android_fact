package ru.smartro.worknote.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.smartro.worknote.MainActivity
import ru.smartro.worknote.R
import ru.smartro.worknote.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val modelFactory = LoginViewModelFactory(application)
        loginViewModel = ViewModelProvider(this, modelFactory).get(LoginViewModel::class.java)
        binding.viewModel = loginViewModel
        binding.lifecycleOwner = this

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading


        loginViewModel.currentUserHolder.observe(this@LoginActivity, Observer {
            if (it !== null) {
                if (it.currentOrganisationId === null) {
                    setResult(Activity.RESULT_OK)
                    val intent = Intent(this, OrganisationSelectActivity::class.java)
                    startActivity(intent)
                    finish()
                    return@Observer
                }
                setResult(Activity.RESULT_OK)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })


        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error, loginResult.errorParam)
                return@Observer
            }
            if (loginResult.success != null) {
                val intent = Intent(this, OrganisationSelectActivity::class.java)
                startActivity(intent)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        password.apply {
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login()
                }
                false
            }
        }

        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            loginViewModel.login()
        }

    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(errorString: Int, arg: String? = null) {
        Toast.makeText(applicationContext, getString(errorString, arg), Toast.LENGTH_LONG).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
