package ru.smartro.worknote.ui.choose.way_list_3

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_choose.choose_rv
import kotlinx.android.synthetic.main.activity_choose.next_btn
import kotlinx.android.synthetic.main.activity_way_bill.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.WayBillAdapter
import ru.smartro.worknote.base.AbstractAct
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.loadingShow
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.WayListBody
import ru.smartro.worknote.service.network.response.way_list.Data
import ru.smartro.worknote.ui.choose.vehicle_2.VehicleActivity
import ru.smartro.worknote.ui.choose.way_task_4.WayTaskActivity
import ru.smartro.worknote.util.MyUtil
import java.text.SimpleDateFormat
import java.util.*

class WayBillActivity : AbstractAct() {
    private val viewModel: WayListViewModel by viewModel()
    private lateinit var adapter: WayBillAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_way_bill)
        supportActionBar?.title = "Выберите путевой лист"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val body = WayListBody(
            date = currentDate,
            organisationId = AppPreferences.organisationId,
            vehicleId = AppPreferences.vehicleId
        )

        loadingShow()
        viewModel.getWayList(body).observe(this, Observer { result ->
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    if (data?.data.isNullOrEmpty()) {
                        empty_title.isVisible = true
                        // TODO: 27.10.2021 SR-3259!!!
                        empty_title.text = getString(R.string.empty_way_task)
                        logout_btn.isVisible = true
                        logout_btn.setOnClickListener {
                            MyUtil.logout(this)
                        }
                        choose_vehicle.isVisible = true
                        choose_vehicle.setOnClickListener {
                            startActivity(Intent(this, VehicleActivity::class.java))
                            finish()
                        }
                    } else {
                        adapter = WayBillAdapter(data?.data as ArrayList<Data>)
                        choose_rv.adapter = adapter
                    }
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
                AppPreferences.wayBillId = adapter.getSelectedId()
                AppPreferences.wayBillNumber = adapter.getSelectedNumber()
                startActivity(Intent(this, WayTaskActivity::class.java))
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout_organisation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MyUtil.onMenuOptionClicked(this, item.itemId)
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}