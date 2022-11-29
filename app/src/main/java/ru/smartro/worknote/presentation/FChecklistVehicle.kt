package ru.smartro.worknote.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AF
import ru.smartro.worknote.log.work.VehicleEntity

class FChecklistVehicle: AF(), SwipeRefreshLayout.OnRefreshListener {

    private var mVehicleAdapter: VehicleAdapter? = null
    private val viewModel: XChecklistAct.ChecklistViewModel by activityViewModels()

    private var etVehicleFilter: EditText? = null

    private var srlRefresh: SwipeRefreshLayout? = null

    override fun onGetLayout(): Int = R.layout.f_start_vehicle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!App.getAppliCation().hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 1)
        }

        (requireActivity() as XChecklistAct).apply {
            // TODO::Vlad -- Можно поставить один раз тут и не трогать в последующих фрагах
            acibGoToBack?.visibility = View.VISIBLE
            acibGoToBack?.setOnClickListener {
                navigateBack()
            }
            setBarTitle("Автомобиль")
        }

        srlRefresh = view.findViewById(R.id.srl__f_start_vehicle__refresh)
        srlRefresh?.setOnRefreshListener(this)

        mVehicleAdapter = VehicleAdapter { vehicle ->
            goToNextStep(vehicle)
        }

        etVehicleFilter = view.findViewById(R.id.et__f_start_vehicle__filter)
        etVehicleFilter?.addTextChangedListener { text: Editable? ->
            val filterText = text.toString()
            logSentry(filterText)
            mVehicleAdapter?.updateList(filterText)
        }
        etVehicleFilter?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(etVehicleFilter?.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })

        val rv = view.findViewById<RecyclerView>(R.id.rv__f_start_vehicle__vehicles)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = mVehicleAdapter

//        viewModel.mVehicleList.observe(viewLifecycleOwner) { result ->
//            if(result != null) {
//                val data = result.data
//                srlRefresh?.isRefreshing = false
//                (requireActivity() as XChecklistAct).hideProgressBar()
//                when (result.status) {
//                    Status.SUCCESS -> {
//                        val vehicles = data?.data
//                        vehicles?.let {
//                            rvAdapter?.setItems(it)
//                            if (getAct().isDevelMode()) {
//                                val vehicle = it.find { el -> el.name == "Тигуан" }
//                                if(vehicle != null) {
//                                    goToNextStep(vehicle)
//                                } else {
//                                    toast("Не удаётся найти машину с именем \"Тигуан\"")
//                                }
//                            }
//                        }
//                    }
//                    Status.ERROR -> {
//                        toast(result.msg)
//                    }
//                    Status.NETWORK -> {
//                        toast("Проблемы с интернетом")
//                    }
//                }
//            } else {
//                rvAdapter?.clearItems()
//                if(getArgumentName() == null)
//                    (requireActivity() as XChecklistAct).showProgressBar()
//                else
//                    (requireActivity() as XChecklistAct).showProgressBar(getArgumentName()!!)
//            }
//        }

        LOG.debug("viewModel.mLastOwnerId=${viewModel.mLastOwnerId}")
        LOG.debug("getArgumentID(mLastOwnerId)=${getArgumentID()}")

        val vehicleS= viewModel.database.getVehicleS()
        mVehicleAdapter?.setItems(vehicleS)
        onRefresh()
    }

    private fun goToNextStep(vehicle: VehicleEntity) {
        paramS().vehicleId = vehicle.id
        paramS().vehicleName = vehicle.name
        // TODO!! will be changed to navigateMain
        navigateMainChecklist(R.id.startWaybillF, vehicle.id, vehicle.name)
    }

    override fun onResume() {
        super.onResume()
//        if(etVehicleFilter != null && etVehicleFilter?.text.toString().isNotEmpty()) {
//            val filterText = if(etVehicleFilter!!.text != null) etVehicleFilter!!.text.toString() else return
////            logSentry(filterText)
////            Handler(Looper.getMainLooper()).postDelayed({
////                rvAdapter?.updateList(filterText)
////            }, 500)
//        }
    }


    override fun onRefresh() {
        getVehicleList()
        srlRefresh?.isRefreshing = false
        (requireActivity() as XChecklistAct).showProgressBar()
    }

    private fun getVehicleList() {
        val vehicleRequestGET = RGETVehicle()
        vehicleRequestGET.getLiveDate().observe(viewLifecycleOwner) { result ->
            LOG.debug("${result}")
            (requireActivity() as XChecklistAct).hideProgressBar()
            if (result.isSent) {
                val vehicleS= viewModel.database.getVehicleS()
                mVehicleAdapter?.setItems(vehicleS)
                if (getAct().isDevelMode()) {
                    val vehicle = vehicleS.find { el -> el.name == "Тигуан" }
                    if(vehicle != null) {
                        goToNextStep(vehicle)
                    } else {
                        toast("Не удаётся найти машину с именем \"Тигуан\"")
                    }
                }
            }
        }
        App.oKRESTman().put(vehicleRequestGET)
    }


    class VehicleAdapter(private val listener: (VehicleEntity) -> Unit): RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

        private val mItems: MutableList<VehicleEntity> = mutableListOf()
        private var mFilteredItems: MutableList<VehicleEntity> = mutableListOf()
        fun setItems(vehicleList: List<VehicleEntity>) {
            mItems.clear()
            mItems.addAll(vehicleList)
            mFilteredItems.clear()
            mFilteredItems.addAll(vehicleList)
            notifyDataSetChanged()
        }

        fun clearItems() {
            mItems.clear()
            notifyDataSetChanged()
        }

        fun updateList(_filterText: String) {
            val filterText = _filterText.lowercase()
            mFilteredItems.clear()
            mFilteredItems.addAll(mItems.filter { el ->
                if(el.name != null) {
                    val name = el.name.lowercase()
                    name.startsWith(filterText) || name.contains(filterText)
                } else {
                    false
                }
            })
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.f_start_vehicle__rv_item, parent, false)
            return VehicleViewHolder(view, listener)
        }

        override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
            // TODO TEST I THINK THAT IF GETITEMCOUNT RETURNS FILTEREDITEMS SIZE THEN WE CAN GET RID OF IF HERE
//        if(mFilteredItems.isNotEmpty() && position < mFilteredItems.size) {
            holder.bind(mFilteredItems[position])
//        }
        }

        override fun getItemCount(): Int = mFilteredItems.size

        class VehicleViewHolder(val itemView: View, val listener: (VehicleEntity) -> Unit): RecyclerView.ViewHolder(itemView) {
            fun bind(vehicle: VehicleEntity) {
                itemView.findViewById<TextView>(R.id.vehicle_name).text = vehicle.name
                itemView.setOnClickListener {
                    listener(vehicle)
                }
            }
        }
    }
}