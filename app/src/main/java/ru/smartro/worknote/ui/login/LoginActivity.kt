package ru.smartro.worknote.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.EventLog
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
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
            binding.loading.visibility = View.VISIBLE
            loginViewModel.login()
        }
        binding.username.afterTextChanged {
            loginViewModel.username.value = it
        }

        binding.password.apply {
            setOnEditorActionListener { v, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        loginViewModel.password.value = v.text.toString()
                        loginViewModel.login()
                    }
                }
                false
            }
        }
    }

    private fun setModelListeners() {
        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            if (loginState.usernameError != null) {
                binding.username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                binding.password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            binding.loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error, loginResult.errorParam)
                return@Observer
            }
        })

        //UI side effects
        loginViewModel.state.observe(this, Observer {
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
            when (it) {
                is LoginViewModel.State.CanSetOrganisation -> {
                    setResult(Activity.RESULT_OK)
                    val intent = Intent(this, OrganisationSelectActivity::class.java)
                    startActivity(intent)
                    finish()
                    return@Observer
                }
                is LoginViewModel.State.CanGoToWorkflow -> {
                    setResult(Activity.RESULT_OK)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    return@Observer
                }
            }
        })
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
