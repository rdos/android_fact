package ru.smartro.worknote.work.ac.checklist

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.*
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
import ru.smartro.worknote.service.network.response.vehicle.Vehicle
import ru.smartro.worknote.service.network.response.vehicle.VehicleResponse
import ru.smartro.worknote.util.MyUtil

class StartVehicleAct : AbstractAct() {
    private val vs: VehicleViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_start_vehicle)
        supportActionBar?.title = "Выберите автомобиль"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val rv = findViewById<RecyclerView>(R.id.rv_act_start_vehicle)
        rv.layoutManager = LinearLayoutManager(this)
        showingProgress(getPutExtraParam_NAME())
        vs.getVehicle(AppPreferences.organisationId).observe(this, Observer { result ->
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    rv .adapter = VehicleAdapter(data?.data!!)
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

    open class VehicleViewModel(application: Application) : BaseViewModel(application) {

        fun getVehicle(organisationId: Int): LiveData<Resource<VehicleResponse>> {
            return networkDat.getVehicle(organisationId)
        }
/*
    fun getCars(authModel: AuthBody): LiveData<Resource<CarsResponse>> {
        return network.aut h(authModel)*/

    }

    inner class VehicleAdapter(private val items: List<Vehicle>) :
        RecyclerView.Adapter<VehicleAdapter.OwnerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.start_act__rv_item_know1, parent, false)
            return OwnerViewHolder(view)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
            val vehicle = items[position]
            holder.itemView.choose_title.text = vehicle.name
            holder.itemView.setOnClickListener {
                setAntiErrorClick(holder.itemView)
                AppPreferences.vehicleId = vehicle.id
                val intent = Intent(this@StartVehicleAct, StartWayBillAct::class.java)
                intent.putExtra(PUT_EXTRA_PARAM_NAME, vehicle.name)
                startActivity(intent)
            }
        }

        inner class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}