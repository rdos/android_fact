package ru.smartro.worknote.presentation.checklist.waybill

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.service.network.body.WayListBody
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.checklist.ChecklistViewModel
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.Status
import ru.smartro.worknote.work.ac.PERMISSIONS
import java.text.SimpleDateFormat
import java.util.*

class StartWaybillF: AFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var mRvWaybill: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var progressBar: ProgressBar? = null

    private val viewModel: ChecklistViewModel by activityViewModels()

    override fun onGetLayout(): Int = R.layout.f_start_waybill

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!MyUtil.hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 1)
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Путевой Лист"
            setDisplayHomeAsUpEnabled(true)
        }

        progressBar = view.findViewById(R.id.progress_bar)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout?.setOnRefreshListener(this)

        val rvAdapter = StartWayBillAdapter() {
            goToNextStep(it.id, it.number)
        }
        mRvWaybill = view.findViewById<RecyclerView>(R.id.rv_act_start_waybill).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
        }

        viewModel.mWayBillListResponse.observe(viewLifecycleOwner) { result ->
            if(result != null) {
                swipeRefreshLayout?.isRefreshing = false
                val data = result.data
                hideProgressBar()
                when (result.status) {
                    Status.SUCCESS -> {
                        val wayBills = data?.data!!
                        if (wayBills.isNotEmpty()) {
                            if (wayBills.size == 1) {
                                goToNextStep(wayBills[0].id, wayBills[0].number)
                            } else {
                                rvAdapter.setItems(wayBills)
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
            "vm.lastOwnerId=${viewModel.mLastOwnerId}, " +
                    "params.ownerId=${paramS().getOwnerId()}, " +
                    "vm.mLastVehicleId=${viewModel.mLastVehicleId}, " +
                    "getArgumentID(VehicleId)=${getArgumentID()}")
        if(viewModel.mWayBillListResponse.value == null ||
            viewModel.mLastVehicleId != getArgumentID() ||
            viewModel.mLastOwnerId != paramS().getOwnerId()
        ) {
            showProgressBar()
            getWayBillList()
        } else {
            hideProgressBar()
        }
    }

    private fun getWayBillList() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val body = WayListBody(
            date = currentDate,
            organisationId = paramS().getOwnerId(),
            vehicleId = getArgumentID()
        )
        viewModel.getWayBillsList(body)
    }

    override fun onRefresh() {
        getWayBillList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu_logout_organisation, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // TODO:
        MyUtil.onMenuOptionClicked(requireContext(), item.itemId)
        when (item.itemId) {
            android.R.id.home -> {
                navigateBackChecklist()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToNextStep(wayBillId: Int, wayBillNumber: String) {
        paramS().wayBillId = wayBillId
        paramS().wayBillNumber = wayBillNumber
        navigateMainChecklist(R.id.startWorkOrderF, wayBillId)
    }

    fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }
}