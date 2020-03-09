package ru.smartro.worknote.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.whenCreated
import androidx.recyclerview.widget.LinearLayoutManager
import ru.smartro.worknote.MainActivity
import ru.smartro.worknote.R
import ru.smartro.worknote.databinding.ActivitySelectOrganisationBinding

class OrganisationSelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectOrganisationBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySelectOrganisationBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)

        val viewModel = ViewModelProvider(
            this,
            OrganisationSelectViewModelFactory(this)
        ).get(OrganisationSelectViewModel::class.java)
        val adapter = getAdapter(viewModel)

        binding.content.organisationsList.adapter = adapter
        setObservers(viewModel, adapter)

        val manager = LinearLayoutManager(this)
        binding.content.organisationsList.layoutManager = manager
        binding.lifecycleOwner = this
        setContentView(binding.root)
    }

    private fun setObservers(viewModel: OrganisationSelectViewModel, adapter: OrganisationAdapter) {

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onRefresh(true)
        }

        viewModel.organisations.observe(this, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        viewModel.currentOrganisationId.observe(this, Observer {
            if (it !== null) {
                viewModel.onCommitCurrentOrganisation()
            }
        })
        viewModel.state.observe(this, Observer {
            binding.swipeRefreshLayout.isEnabled = viewModel.canRefresh()
            when (it) {
                is OrganisationSelectViewModel.State.SoftInProgress -> {
                    binding.swipeRefreshLayout.isRefreshing = true
                }
                else -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }

            when (it) {
                is OrganisationSelectViewModel.State.Done -> {
                    setResult(Activity.RESULT_OK)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is OrganisationSelectViewModel.State.Error.AuthError -> {
                    showFailed(R.string.app_auth_error)
                    setResult(Activity.RESULT_OK)
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is OrganisationSelectViewModel.State.Error.AppError -> {
                    showFailed(R.string.app_error)
                    setResult(Activity.RESULT_OK)
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is OrganisationSelectViewModel.State.Error.NetworkError -> {
                    showFailed(R.string.api_error_no_connection)
                }
                is OrganisationSelectViewModel.State.Error.NotFindError -> {
                    showFailed(R.string.api_error_not_find)
                }
            }

        })
    }

    private fun getAdapter(viewModel: OrganisationSelectViewModel): OrganisationAdapter {
        return OrganisationAdapter(viewModel)
    }

    private fun showFailed(errorString: Int, arg: String? = null) {
        Toast.makeText(applicationContext, getString(errorString, arg), Toast.LENGTH_LONG).show()
    }

}
