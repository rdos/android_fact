package ru.smartro.worknote.work.ac.checklist

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_container_adapter.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.base.AbstractAct
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.extensions.hideProgress
import ru.smartro.worknote.extensions.showingProgress
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.work.AppPreferences
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.WayListBody
import ru.smartro.worknote.service.network.response.way_list.Data
import ru.smartro.worknote.service.network.response.way_list.WayListResponse
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.work.ac.PERMISSIONS
import java.text.SimpleDateFormat
import java.util.*

class StartWayBillAct : AbstractAct() {
    private val viewModel: WayListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MyUtil.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }
        setContentView(R.layout.act_start_waybill)
        val nameNotFounT = getPutExtraParam_NAME()
        supportActionBar?.title = "Путевой Лист"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val rv = findViewById<RecyclerView>(R.id.rv_act_start_waybill)
        rv.layoutManager = LinearLayoutManager(this)
        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val body = WayListBody(
            date = currentDate,
            organisationId = AppPreferences.organisationId,
            vehicleId = AppPreferences.vehicleId
        )

        showingProgress(nameNotFounT)
        viewModel.getWayList(body).observe(this, Observer { result ->
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    hideProgress()
                    val wayBills = data?.data!!
                    if (wayBills.isNullOrEmpty()) {
                        logSentry("todo")
//                        empty_title.isVisible = true
//                        // TODO: 27.10.2021 SR-3259!!!
//                        empty_title.text = getString(R.string.empty_way_task)
//                        logout_btn.isVisible = true
//                        logout_btn.setOnClickListener {
//                            MyUtil.logout(this)
//                        }
//                        choose_vehicle.isVisible = true
//                        choose_vehicle.setOnClickListener {
//                            startActivity(Intent(this, StartVehicleAct::class.java))
//                            finish()
                        }
                    rv.adapter = WayBillAdapter(wayBills)
                    if (wayBills.size == 1) {
                        gotoNextAct(wayBills[0].id, wayBills[0].number)
                    }

                }
                Status.ERROR -> {
                    toast(result.msg)
                    hideProgress()
                }
                Status.NETWORK -> {
                    toast("Проблемы с интернетом")
                    hideProgress()
                }
            }
        })

    }

    override fun onBackPressed() {
//        sendMessage(StartVehicleAct::class.java)
        super.onBackPressed()
    }

    private fun gotoNextAct(wayBillId: Int, wayBillNumber: String) {
        AppPreferences.wayBillId = wayBillId
        val intent = Intent(this, StartWorkOrderAct::class.java)
        intent.putExtra(PUT_EXTRA_PARAM_NAME, wayBillNumber)
        startActivity(intent)
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

    open class WayListViewModel(application: Application) : BaseViewModel(application) {

        fun getWayList(body : WayListBody): LiveData<Resource<WayListResponse>> {
            return networkDat.getWayList(body)
        }

    }

    inner class WayBillAdapter(private val items: List<Data>) :
        RecyclerView.Adapter<WayBillAdapter.OwnerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.start_act__rv_item_know1, parent, false)
            return OwnerViewHolder(view)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
            val wayBill = items[position]
            holder.itemView.choose_title.text = wayBill.number
            holder.itemView.setOnClickListener {
                setAntiErrorClick(holder.itemView)
                gotoNextAct(wayBill.id, wayBill.number)
            }
        }

        inner class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}