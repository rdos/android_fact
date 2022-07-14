package ru.smartro.worknote.presentation.checklist.vehicle

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.service.network.response.vehicle.Vehicle
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.checklist.ChecklistViewModel
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.Status
import ru.smartro.worknote.work.ac.PERMISSIONS

class StartVehicleF: AFragment(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: ChecklistViewModel by activityViewModels()

    private var etVehicleFilter: EditText? = null
    private var rvAdapter: StartVehicleAdapter? = null

    private var progressBar: ProgressBar? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onGetLayout(): Int = R.layout.f_start_vehicle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!MyUtil.hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 1)
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Автомобиль"
            setDisplayHomeAsUpEnabled(true)
        }

        progressBar = view.findViewById(R.id.progress_bar)

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout?.setOnRefreshListener(this)

        rvAdapter = StartVehicleAdapter { vehicle ->
            goToNextStep(vehicle)
        }

        etVehicleFilter = view.findViewById(R.id.et_act_start_vehicle__filter)
        etVehicleFilter?.addTextChangedListener { text: Editable? ->
            val filterText = text.toString()
            getAct().logSentry(filterText)
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

        val rv = view.findViewById<RecyclerView>(R.id.rv_act_start_vehicle)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = rvAdapter

        viewModel.mVehicleList.observe(viewLifecycleOwner) { result ->
            if((swipeRefreshLayout?.isRefreshing == true || rvAdapter?.isItemsEmpty() == true) && result != null) {
                val data = result.data
                swipeRefreshLayout?.isRefreshing = false
                hideProgressBar()
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
            }
        }

        Log.d("TEST ::: FUCK",
                "vm.mLastOwnerId=${viewModel.mLastOwnerId}, " +
                "getArgumentID(mLastOwnerId)=${getArgumentID()}")
        if(viewModel.mVehicleList.value == null || viewModel.mLastOwnerId != getArgumentID()) {
            showProgressBar()
            viewModel.getVehicleList(getArgumentID())
        } else {
            hideProgressBar()
        }
    }

    private fun goToNextStep(vehicle: Vehicle) {
        paramS().vehicleId = vehicle.id
        paramS().vehicleName = vehicle.name
        // TODO!! will be changed to navigateMain
        navigateMainChecklist(R.id.startWaybillF, vehicle.id)
    }

    override fun onResume() {
        super.onResume()
        if(etVehicleFilter != null && etVehicleFilter?.text.toString().isNotEmpty()) {
            val filterText = if(etVehicleFilter!!.text != null) etVehicleFilter!!.text.toString() else return
            getAct().logSentry(filterText)
            Handler(Looper.getMainLooper()).postDelayed({
                rvAdapter?.updateList(filterText)
            }, 800)
        }
    }

    // TODO:
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MyUtil.onMenuOptionClicked(requireContext(), item.itemId)
        when (item.itemId) {
            android.R.id.home -> {
                navigateBackChecklist()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        viewModel.getVehicleList(getArgumentID())
    }

    fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }
}