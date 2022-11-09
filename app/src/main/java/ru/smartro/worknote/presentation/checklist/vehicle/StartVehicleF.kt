package ru.smartro.worknote.presentation.checklist.vehicle

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.View
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
import ru.smartro.worknote.andPOintD.ANOFragment
import ru.smartro.worknote.awORKOLDs.service.network.response.vehicle.Vehicle
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.ac.XChecklistAct
import ru.smartro.worknote.work.Status

class StartVehicleF: ANOFragment(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: XChecklistAct.ChecklistViewModel by activityViewModels()

    private var etVehicleFilter: EditText? = null
    private var rvAdapter: StartVehicleAdapter? = null

    private var srlRefresh: SwipeRefreshLayout? = null

    override fun onGetLayout(): Int = R.layout.f_start_vehicle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!MyUtil.hasPermissions(requireContext(), PERMISSIONS)) {
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

        rvAdapter = StartVehicleAdapter { vehicle ->
            goToNextStep(vehicle)
        }

        etVehicleFilter = view.findViewById(R.id.et__f_start_vehicle__filter)
        etVehicleFilter?.addTextChangedListener { text: Editable? ->
            val filterText = text.toString()
            logSentry(filterText)
            rvAdapter?.updateList(filterText)
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
        rv.adapter = rvAdapter

        viewModel.mVehicleList.observe(viewLifecycleOwner) { result ->
            if(result != null) {
                val data = result.data
                srlRefresh?.isRefreshing = false
                (requireActivity() as XChecklistAct).hideProgressBar()
                when (result.status) {
                    Status.SUCCESS -> {
                        val vehicles = data?.data
                        vehicles?.let {
                            rvAdapter?.setItems(it)
                            if (getAct().isDevelMode()) {
                                val vehicle = it.find { el -> el.name == "Тигуан" }
                                if(vehicle != null) {
                                    goToNextStep(vehicle)
                                } else {
                                    toast("Не удаётся найти машину с именем \"Тигуан\"")
                                }
                            }
                        }
                    }
                    Status.ERROR -> {
                        toast(result.msg)
                    }
                    Status.NETWORK -> {
                        toast("Проблемы с интернетом")
                    }
                }
            } else {
                rvAdapter?.clearItems()
                if(getArgumentName() == null)
                    (requireActivity() as XChecklistAct).showProgressBar()
                else
                    (requireActivity() as XChecklistAct).showProgressBar(getArgumentName()!!)
            }
        }

        LOG.debug("viewModel.mLastOwnerId=${viewModel.mLastOwnerId}")
        LOG.debug("getArgumentID(mLastOwnerId)=${getArgumentID()}")
        if(viewModel.mVehicleList.value == null) {
            if(getArgumentName() == null)
                (requireActivity() as XChecklistAct).showProgressBar()
            else
                (requireActivity() as XChecklistAct).showProgressBar(getArgumentName()!!)
            viewModel.getVehicleList(getArgumentID())
        } else if(viewModel.mLastOwnerId != getArgumentID()) {
            viewModel.clearVehicleList()
            viewModel.getVehicleList(getArgumentID())
        }
    }

    private fun goToNextStep(vehicle: Vehicle) {
        paramS().vehicleId = vehicle.id
        paramS().vehicleName = vehicle.name
        // TODO!! will be changed to navigateMain
        navigateMainChecklist(R.id.startWaybillF, vehicle.id, vehicle.name)
    }

    override fun onResume() {
        super.onResume()
        if(etVehicleFilter != null && etVehicleFilter?.text.toString().isNotEmpty()) {
            val filterText = if(etVehicleFilter!!.text != null) etVehicleFilter!!.text.toString() else return
            logSentry(filterText)
            Handler(Looper.getMainLooper()).postDelayed({
                rvAdapter?.updateList(filterText)
            }, 500)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LOG.debug("${this::class.java.simpleName} :: ON DESTROY VIEW")
        viewModel.mVehicleList.removeObservers(viewLifecycleOwner)
    }

    override fun onRefresh() {
        viewModel.getVehicleList(getArgumentID())
    }
}