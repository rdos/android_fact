package ru.smartro.worknote.ui.choose.owner_1

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_choose.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.OwnerAdapter
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.loadingShow
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.response.organisation.Organisation
import ru.smartro.worknote.ui.choose.vehicle_2.VehicleActivity
import ru.smartro.worknote.util.MyUtil

class OrganisationActivity : AppCompatActivity() {
    private val viewModel: OrganisationViewModel by viewModel()
    private lateinit var adapter: OwnerAdapter
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MyUtil.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }
        setContentView(R.layout.activity_choose)
        supportActionBar?.title = "Выберите организацию"
        loadingShow()
        viewModel.getOwners().observe(this, Observer { result ->
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    adapter = OwnerAdapter(data!!.data.organisations as ArrayList<Organisation>)
                    choose_rv.adapter = adapter
                    loadingHide()
                }
                Status.ERROR -> {
                    toast(result.msg)
                    loadingHide()
                }
                Status.NETWORK -> {
                    toast("Проблемы с интернетом")
                    loadingHide()
                }
            }
        })

        next_btn.setOnClickListener {
            if (adapter.getSelectedId() != -1) {
                AppPreferences.organisationId = adapter.getSelectedId()
                Log.d("OwnerActivity", "getSelectedId: ${adapter.getSelectedId()}")
                startActivity(Intent(this, VehicleActivity::class.java))
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MyUtil.onMenuOptionClicked(this, item.itemId)
        return super.onOptionsItemSelected(item)
    }
}
