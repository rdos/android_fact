package ru.smartro.worknote.work.ac.checklist

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_container_adapter.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActAbstract
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.awORKOLDs.extensions.toast
import ru.smartro.worknote.awORKOLDs.service.network.Resource
import ru.smartro.worknote.awORKOLDs.service.network.Status
import ru.smartro.worknote.awORKOLDs.service.network.response.vehicle.Vehicle
import ru.smartro.worknote.awORKOLDs.service.network.response.vehicle.VehicleResponse
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.work.ac.PERMISSIONS


//todo: DataHolder class
class StartVehicleAct : ActAbstract() {
    private var myAdapter: VehicleAdapter? = null
    private val vs: VehicleViewModel by viewModel()

    override fun onNewGPS() {
        // TODO: r_dos!!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MyUtil.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }
        setContentView(R.layout.act_start_vehicle)

        supportActionBar?.title = "Автомобиль"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        supportActionBar?.hide()
        /**====================================================================================== */
        val etVehicleFilter = findViewById<EditText>(R.id.et_act_start_vehicle__filter)
//        val textWatcher = TextWatcher()
        etVehicleFilter.addTextChangedListener { text: Editable? ->
            myAdapter?.let {
                myAdapter!!.updateList(text.toString())
            }
        }
        etVehicleFilter.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(etVehicleFilter.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })


        val rv = findViewById<RecyclerView>(R.id.rv_act_start_vehicle)
        rv.layoutManager = LinearLayoutManager(this)
            /**====================================================================================== */
            /**====================================================================================== */

            showingProgress(getPutExtraParam_NAME())
            vs.getVehicle(paramS().getOwnerId()).observe(this, Observer { result ->
                val data = result.data
                when (result.status) {
                            Status.SUCCESS -> {
                                myAdapter = VehicleAdapter(data?.data!!)
                                rv.adapter = myAdapter
                                if (isDevelMode()) {
                            etVehicleFilter.setText("Тигуан")
                            val vehicle = myAdapter!!.findVehicleByName("Тигуан")
                        vehicle?.let {
                            gotoNextAct(vehicle.id, vehicle.name)
                        }
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

    private fun gotoNextAct(vehicleId: Int, vehicleName: String) {
        paramS().vehicleId = vehicleId
        paramS().ownerName = vehicleName
        val intent = Intent(this@StartVehicleAct, StartWayBillAct::class.java)
        intent.putExtra(PUT_EXTRA_PARAM_NAME, paramS().ownerName)
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

    open class VehicleViewModel(application: Application) : BaseViewModel(application) {

        fun getVehicle(organisationId: Int): LiveData<Resource<VehicleResponse>> {
            return networkDat.getVehicle(organisationId)
        }
/*
    fun getCars(authModel: AuthBody): LiveData<Resource<CarsResponse>> {
        return network.aut h(authModel)*/

    }

    inner class VehicleAdapter(val vehicleList: List<Vehicle>) :
        RecyclerView.Adapter<VehicleAdapter.OwnerViewHolder>() {

        private var mItems: List<Vehicle> = vehicleList
        private var mItemsBefore: List<Vehicle> = vehicleList

        fun findVehicleByName(vehicleName: String): Vehicle? {
            val res = mItems.find { vehicle -> vehicle.name == vehicleName}
            return res
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.start_act__rv_item_know1, parent, false)
            return OwnerViewHolder(view)
        }

        fun filter(vehicleList: List<Vehicle>, filterText: String): List<Vehicle> {
            val query = filterText.toLowerCase()
            val filteredModeList = vehicleList.filter {
                try {
                    val text = it.name.toLowerCase()
                    (text.startsWith(query) || (text.contains(query)))
                } catch (ex: Exception) {
                    true
                }
            }
    //            val sYsTEM = mutableListOf<Vehicle>()
            return filteredModeList
        }

        fun updateList(filterText: String) {
            logSentry(filterText)
            mItems = filter(mItemsBefore, filterText)
            notifyDataSetChanged()
        }


        override fun getItemCount(): Int {
            return mItems.size
        }

        override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
            val vehicle = mItems[position]
            holder.itemView.choose_title.text = vehicle.name
            holder.itemView.setOnClickListener {
                setAntiErrorClick(holder.itemView)
                gotoNextAct(vehicle.id, vehicle.name)
            }
        }

        inner class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}