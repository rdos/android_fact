package ru.smartro.worknote.work.ac.checklist

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.awORKOLDs.extensions.toast
import ru.smartro.worknote.awORKOLDs.service.network.Resource
import ru.smartro.worknote.awORKOLDs.service.network.Status
import ru.smartro.worknote.awORKOLDs.service.network.body.WayListBody
import ru.smartro.worknote.awORKOLDs.service.network.response.way_list.Data
import ru.smartro.worknote.awORKOLDs.service.network.response.way_list.WayListResponse
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.work.ac.PERMISSIONS
import java.text.SimpleDateFormat
import java.util.*

class StartWayBillAct : ActNOAbst() {
    private val viewModel: WayListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MyUtil.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }
        setContentView(R.layout.act_start_waybill)
                                                            //!r_dos//!r_dos//!r_dos//!r_dos
        val nameNotFounT = getPutExtraParam_NAME()
        supportActionBar?.title = "Путевой Лист"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val rv = findViewById<RecyclerView>(R.id.rv_act_start_waybill)
        rv.layoutManager = LinearLayoutManager(this)
        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val body = WayListBody(
            date = currentDate,
            organisationId = paramS().getOwnerId(),
            vehicleId = paramS().getVehicleId()
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
                    rv.adapter = WaybillAdapter(wayBills)
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
        paramS().wayBillId = wayBillId
        paramS().wayBillNumber = wayBillNumber
        val intent = Intent(this, StartWorkOrderAct::class.java)
        intent.putExtra(PUT_EXTRA_PARAM_NAME, paramS().wayBillNumber)
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

    inner class WaybillAdapter(private val items: List<Data>) :
        RecyclerView.Adapter<WaybillAdapter.WaybillViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaybillViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.act_start_waybill__rv_item, parent, false)
            return WaybillViewHolder(view)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: WaybillViewHolder, position: Int) {
            val wayBill = items[position]
            holder.tvNumber.text = wayBill.number
            holder.itemView.setOnClickListener {
                setAntiErrorClick(holder.itemView)
                gotoNextAct(wayBill.id, wayBill.number)
            }
        }
        // WayBill и уТВерждение
        inner class WaybillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvNumber: TextView by lazy {
                itemView.findViewById(R.id.act_start_waybill__rv_item__number)
            }
            val tvDriverName: TextView by lazy {
                itemView.findViewById(R.id.act_start_waybill__rv_item__driver)
            }
            val tvRouteName: TextView by lazy {
                itemView.findViewById(R.id.act_start_waybill__rv_item__route_name)
            }
        }
    }
}