package ru.smartro.worknote.presentation.checklist.waybill

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.service.network.body.WayListBody
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.Status
import ru.smartro.worknote.work.ac.PERMISSIONS
import java.text.SimpleDateFormat
import java.util.*

class StartWaybillF: AFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var mRvWaybill: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var mTvNotFoundData: TextView? = null

    private val viewModel: StartWaybillViewModel by viewModels()

    override fun onGetLayout(): Int = R.layout.f_start_waybill

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TEST :::", "ARGARAARGATRAGAGGAARRAAAGTRATGAARAGTAGT ${getArgumentID()}")
        if (!MyUtil.hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 1)
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Путевой Лист"
            setDisplayHomeAsUpEnabled(true)
        }

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout?.setOnRefreshListener(this)

        mTvNotFoundData = view.findViewById(R.id.tv_act_start_waybill__not_found_data)
        mTvNotFoundData?.text = getString(R.string.tv_no_fount_data)

        val rvAdapter = StartWayBillAdapter() {
            goToNextStep(it.id, it.number)
        }

        mRvWaybill = view.findViewById<RecyclerView?>(R.id.rv_act_start_waybill).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
        }
        hideNotFoundData()
        showingProgress(paramS().vehicleName)

        viewModel.mWayBillListResponse.observe(viewLifecycleOwner) { result ->
            if(result != null) {
                swipeRefreshLayout?.isRefreshing = false
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        val wayBills = data?.data!!
                        if (wayBills.isNotEmpty()) {
                            hideNotFoundData()
                            Log.d("TEST::::", "SIZE::: ${wayBills}")
                            if (wayBills.size == 1) {
                                goToNextStep(wayBills[0].id, wayBills[0].number)
                            } else {
                                rvAdapter.setItems(wayBills)
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
            }
        }

        getWayBillList()
    }

    private fun getWayBillList() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val body = WayListBody(
            date = currentDate,
            organisationId = paramS().getOwnerId(),
            vehicleId = paramS().getVehicleId()
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
}