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
import ru.smartro.worknote.abs.ActAbstract
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.toast
import ru.smartro.worknote.awORKOLDs.service.network.Resource
import ru.smartro.worknote.awORKOLDs.service.network.Status
import ru.smartro.worknote.awORKOLDs.service.network.body.WayListBody
import ru.smartro.worknote.awORKOLDs.service.network.response.way_list.Data
import ru.smartro.worknote.awORKOLDs.service.network.response.way_list.WayListResponse
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.work.ac.PERMISSIONS
import java.text.SimpleDateFormat
import java.util.*

class StartWayBillAct : ActAbstract() {
    private var mRvWaybill: RecyclerView? = null
    private var mTvNotFoundData: TextView? = null
    private val viewModel: WayListViewModel by viewModel()
    override fun onNewGPS() {
        // TODO: r_dos!!!
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MyUtil.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }
        setContentView(R.layout.act_start_waybill)

        val putExtraParamName = getPutExtraParam_NAME()
        supportActionBar?.title = "Путевой Лист"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mTvNotFoundData = findViewById(R.id.tv_act_start_waybill__not_found_data)
        mTvNotFoundData?.text = getString(R.string.tv_no_fount_data)
        mRvWaybill = findViewById(R.id.rv_act_start_waybill)
        mRvWaybill?.layoutManager = LinearLayoutManager(this)
        showingProgress(putExtraParamName)
        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val body = WayListBody(
            date = currentDate,
            organisationId = paramS().getOwnerId(),
            vehicleId = paramS().getVehicleId()
        )
        viewModel.getWayList(body).observe(this, Observer { result ->
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    val wayBills = data?.data!!
                    if (wayBills.isNotEmpty()) {
                        hideNotFoundData()
                        if (wayBills.size == 1) {
                            gotoNextAct(wayBills[0].id, wayBills[0].number)
                        } else {
                            mRvWaybill?.adapter = WaybillAdapter(wayBills)
                        }
                    } else {
                        showNotFoundData()
                    }
                    hideProgress()
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

    private fun hideNotFoundData() {
        mTvNotFoundData?.visibility = View.GONE
        mRvWaybill?.visibility = View.VISIBLE
    }

    private fun showNotFoundData(text: String? = null) {
        mTvNotFoundData?.visibility = View.VISIBLE
        mRvWaybill?.visibility = View.GONE
        if (text != null) {
            mTvNotFoundData?.text = text
        }
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
        // TODO:  
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
        inner class WaybillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvNumber: TextView by lazy {
                itemView.findViewById(R.id.act_start_waybill__rv_item__number)
            }
//            val tvDriverName: TextView by lazy {
//                itemView.findViewById(R.id.act_start_waybill__rv_item__driver)
//            }
//            val tvRouteName: TextView by lazy {
//                itemView.findViewById(R.id.act_start_waybill__rv_item__route_name)
//            }
        }
    }
}