package ru.smartro.worknote.presentation.checklist.waybill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.FragmentA
import ru.smartro.worknote.awORKOLDs.service.network.body.WayListBody
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.LOG
import ru.smartro.worknote.awORKOLDs.service.network.response.way_list.WayBillDto
import ru.smartro.worknote.presentation.ac.XChecklistAct
import java.text.SimpleDateFormat
import java.util.*

class StartWaybillF: FragmentA(), SwipeRefreshLayout.OnRefreshListener {

    private var mRvWaybill: RecyclerView? = null
    private var srlRefresh: SwipeRefreshLayout? = null
    private var actvNoData: AppCompatTextView? = null

    private val viewModel: XChecklistAct.ChecklistViewModel by activityViewModels()

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
            LOG.debug("WAYBILL STATE::: ${state}")
            if(state !is XChecklistAct.ViewState.LOADING) {
                (requireActivity() as XChecklistAct).hideProgressBar()
            }

            when(state) {
                is XChecklistAct.ViewState.IDLE -> {
                    getWayBillList()
                }
                is XChecklistAct.ViewState.LOADING -> {
                    if(getArgumentName() == null)
                        (requireActivity() as XChecklistAct).showProgressBar()
                    else
                        (requireActivity() as XChecklistAct).showProgressBar(getArgumentName()!!)
                    hideNoData()
                }
                is XChecklistAct.ViewState.DATA -> {
                    srlRefresh?.isRefreshing = false
                }
                is XChecklistAct.ViewState.ERROR -> {
                    toast(state.msg)
                }
                is XChecklistAct.ViewState.MESSAGE -> {
                    toast(state.msg)
                }
                is XChecklistAct.ViewState.REFRESH -> {
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
        srlRefresh?.isRefreshing = false
        (requireActivity() as XChecklistAct).showProgressBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LOG.debug("${this::class.java.simpleName} :: ON DESTROY VIEW")
        viewModel.mWayBillList.removeObservers(viewLifecycleOwner)
        viewModel.mWayBillsViewState.postValue(XChecklistAct.ViewState.IDLE())
    }

    private fun goToNextStep(wayBillId: Int, wayBillNumber: String) {
        viewModel.mWayBillsViewState.postValue(XChecklistAct.ViewState.IDLE())
        paramS().wayBillId = wayBillId
        paramS().wayBillNumber = wayBillNumber
        navigateMainChecklist(R.id.startWorkOrderF, wayBillId, wayBillNumber)
    }


    class StartWayBillAdapter(private val listener: (WayBillDto) -> Unit): RecyclerView.Adapter<StartWayBillAdapter.WayBillViewHolder>() {

        private val mItems: MutableList<WayBillDto> = mutableListOf()
        fun setItems(wayBillsList: List<WayBillDto>) {
            mItems.clear()
            mItems.addAll(wayBillsList)
            notifyDataSetChanged()
        }
        override fun getItemCount(): Int = mItems.size

        fun clearItems() {
            mItems.clear()
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WayBillViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.f_start_waybill__rv_item, parent, false)
            return WayBillViewHolder(view, listener)
        }

        override fun onBindViewHolder(holder: WayBillViewHolder, position: Int) {
            holder.bind(mItems[position])
        }

        class WayBillViewHolder(val itemView: View, val listener: (WayBillDto) -> Unit): RecyclerView.ViewHolder(itemView) {
            fun bind(wayBill: WayBillDto) {
                itemView.findViewById<TextView>(R.id.waybill_number).text = wayBill.number
                itemView.setOnClickListener {
                    listener(wayBill)
                }
            }
        }
    }
}