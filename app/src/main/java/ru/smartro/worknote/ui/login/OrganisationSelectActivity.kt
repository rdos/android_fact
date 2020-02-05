package ru.smartro.worknote.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ru.smartro.worknote.MainActivity
import ru.smartro.worknote.R
import ru.smartro.worknote.databinding.ActivitySelectOrganisationBinding

class OrganisationSelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectOrganisationBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_organisation)

        setSupportActionBar(binding.toolbar)
        val application = requireNotNull(this).application

        val viewModel = ViewModelProvider(
            this,
            OrganisationSelectViewModelFactory(application)
        ).get(OrganisationSelectViewModel::class.java)
        val adapter = OrganisationAdapter(viewModel)
        binding.content.organisationsList.adapter = adapter

        viewModel.organisations.observe(this, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        val manager =  LinearLayoutManager(this)
        binding.content.organisationsList.layoutManager = manager
        binding.lifecycleOwner = this

        viewModel.currentOrganisationId.observe(this, Observer {
            if (it !== null) {
                viewModel.setCommitCurrenOrganisation()
            }
        })
        viewModel.selectDone.observe(this, Observer {
            if (it) {
                setResult(Activity.RESULT_OK)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

    }

}
