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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.smartro.worknote.MainActivity
import ru.smartro.worknote.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        val modelFactory = LoginViewModelFactory(this)
        loginViewModel = ViewModelProvider(this, modelFactory).get(LoginViewModel::class.java)
        setViewListeners()
        setModelListeners()
        setContentView(binding.root)
    }

    private fun showLoginFailed(errorString: Int, arg: String? = null) {
        Toast.makeText(applicationContext, getString(errorString, arg), Toast.LENGTH_LONG).show()
    }

    private fun setViewListeners() {
        binding.login.setOnClickListener {
            loginViewModel.onLogin()
        }
        binding.username.afterTextChanged {
            loginViewModel.username.value = it
        }

        binding.password.apply {
            afterTextChanged {
                loginViewModel.password.value = it
            }
            setOnEditorActionListener { v, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        loginViewModel.password.value = v.text.toString()
                        loginViewModel.onLogin()
                    }
                }
                false
            }
        }
    }

    private fun setModelListeners() {
        //validation
        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            if (loginState.usernameError != null) {
                binding.username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                binding.password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.state.observe(this, Observer {
            //in progress
            when (it) {
                is LoginViewModel.State.SoftInProgress -> {
                    binding.loading.visibility = View.VISIBLE
                    binding.progressBarInclude.progressBarLayout.visibility = View.GONE
                }
                is LoginViewModel.State.ModalInProgress -> {
                    binding.progressBarInclude.progressBarLayout.visibility = View.VISIBLE
                    binding.loading.visibility = View.GONE
                }
                else -> {
                    binding.loading.visibility = View.GONE
                    binding.progressBarInclude.progressBarLayout.visibility = View.GONE
                }

            }

            // login button
            when (it) {
                is LoginViewModel.State.AwaitCredentials -> binding.login.visibility = View.VISIBLE
                is LoginViewModel.State.CredentialsError -> binding.login.visibility = View.VISIBLE
                else -> binding.login.visibility = View.GONE
            }

            //errors
            when (it) {
                is LoginViewModel.State.CredentialsError -> {
                    showLoginFailed(it.error, it.message)
                }
            }

            //redirect
            when (it) {
                is LoginViewModel.State.Done.NeedsOrganisation -> redirectToSelectOrganisation()
                is LoginViewModel.State.Done.NeedsVehicle -> redirectToWorkflow()
                is LoginViewModel.State.Done.NeedsWaybill -> redirectToWorkflow()
                is LoginViewModel.State.Done -> redirectToWorkflow()
            }
        })
    }

    private fun redirectToSelectOrganisation() {
        setResult(Activity.RESULT_OK)
        val intent = Intent(this, OrganisationSelectActivity::class.java)
        startActivity(intent)
        finish()
        return
    }

    private fun redirectToWorkflow() {
        setResult(Activity.RESULT_OK)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        return
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
