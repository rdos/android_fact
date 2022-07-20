package ru.smartro.worknote.presentation.checklist.waybill

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.PERMISSIONS
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.service.network.body.WayListBody
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.checklist.ChecklistViewModel
import ru.smartro.worknote.presentation.checklist.ViewState
import ru.smartro.worknote.presentation.checklist.XChecklistAct
import ru.smartro.worknote.toast
import java.text.SimpleDateFormat
import java.util.*

class StartWaybillF: AFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var mRvWaybill: RecyclerView? = null
    private var srlRefresh: SwipeRefreshLayout? = null
    private var actvNoData: AppCompatTextView? = null

    private val viewModel: ChecklistViewModel by activityViewModels()

    override fun onGetLayout(): Int = R.layout.f_start_waybill

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!MyUtil.hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 1)
        }

        (requireActivity() as XChecklistAct).setBarTitle("Путевой Лист")

        actvNoData = view.findViewById(R.id.actv__f_start_waybill__no_data)

        srlRefresh = view.findViewById(R.id.srl__f_start_waybill__refresh)
        srlRefresh?.setOnRefreshListener(this)

        val rvAdapter = StartWayBillAdapter {
            goToNextStep(it.id, it.number)
        }
        mRvWaybill = view.findViewById<RecyclerView>(R.id.rv__f_start_waybill__waybills).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
        }

        viewModel.mWayBillList.observe(viewLifecycleOwner) { wayBills ->
            if(wayBills != null) {
                if (wayBills.isNotEmpty()) {
                    if (wayBills.size == 1 && viewModel.mLastWayBillId == -1) {
                        viewModel.mLastWayBillId = wayBills[0].id
                        goToNextStep(wayBills[0].id, wayBills[0].number)
                    } else {
                        hideNoData()
                        rvAdapter.setItems(wayBills)
                    }
                    return@observe
                } else {
                    showNoData()
                }
            } else {
                rvAdapter.clearItems()
            }
        }

        viewModel.mWayBillsViewState.observe(viewLifecycleOwner) { state ->
            Log.d("TEST :::", "WAYBILL STATE::: ${state}")
            if(state !is ViewState.LOADING) {
                (requireActivity() as XChecklistAct).hideProgressBar()
            }

            when(state) {
                is ViewState.IDLE -> {
                    getWayBillList()
                }
                is ViewState.LOADING -> {
                    if(getArgumentName() == null)
                        (requireActivity() as XChecklistAct).showProgressBar()
                    else
                        (requireActivity() as XChecklistAct).showProgressBar(getArgumentName()!!)
                    hideNoData()
                }
                is ViewState.DATA -> {
                    srlRefresh?.isRefreshing = false
                }
                is ViewState.ERROR -> {
                    toast(state.msg)
                }
                is ViewState.MESSAGE -> {
                    toast(state.msg)
                }
                is ViewState.REFRESH -> {
                    hideNoData()
                }
                else -> {
                    throw Exception("Illegal View State in ${this::class.java.name}")
                }
            }
        }
    }

    fun showNoData() {
        actvNoData?.visibility = View.VISIBLE
        mRvWaybill?.visibility = View.GONE
    }

    fun hideNoData() {
        actvNoData?.visibility = View.GONE
        mRvWaybill?.visibility = View.VISIBLE
    }

    private fun getWayBillList(isRefresh: Boolean = false) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val body = WayListBody(
            date = currentDate,
            organisationId = paramS().getOwnerId(),
            vehicleId = getArgumentID()
        )
        viewModel.getWayBillsList(body, isRefresh)
    }

    override fun onRefresh() {
        getWayBillList(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("TEST :::", "${this::class.java.simpleName} :: ON DESTROY VIEW")
        viewModel.mWayBillList.removeObservers(viewLifecycleOwner)
        viewModel.mWayBillsViewState.postValue(ViewState.IDLE())
    }

    private fun goToNextStep(wayBillId: Int, wayBillNumber: String) {
        viewModel.mWayBillsViewState.postValue(ViewState.IDLE())
        paramS().wayBillId = wayBillId
        paramS().wayBillNumber = wayBillNumber
        navigateMainChecklist(R.id.startWorkOrderF, wayBillId, wayBillNumber)
    }
}