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
import ru.smartro.worknote.awORKOLDs.WaybillRequestPOST
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.ac.XChecklistAct
import ru.smartro.worknote.presentation.work.WaybillEntity

class StartWaybillF: FragmentA(), SwipeRefreshLayout.OnRefreshListener {

    private var mWayBillAdapter: WayBillAdapter? = null
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

        mWayBillAdapter = WayBillAdapter {
            goToNextStep(it.id, it.number)
        }
        mRvWaybill = view.findViewById<RecyclerView>(R.id.rv__f_start_waybill__waybills).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mWayBillAdapter
        }

//        viewModel.mWayBillList.observe(viewLifecycleOwner) { wayBills ->
//            if(wayBills != null) {
//                if (wayBills.isNotEmpty()) {
//                    if (wayBills.size == 1 && viewModel.mLastWayBillId == -1) {
//                        viewModel.mLastWayBillId = wayBills[0].id
//                        goToNextStep(wayBills[0].id, wayBills[0].number)
//                    } else {
//                        hideNoData()
//                        rvAdapter.setItems(wayBills)
//                    }
//                    return@observe
//                } else {
//                    showNoData()
//                }
//            } else {
//                rvAdapter.clearItems()
//            }
//        }

//        viewModel.mWayBillsViewState.observe(viewLifecycleOwner) { state ->
//            LOG.debug("WAYBILL STATE::: ${state}")
//            if(state !is XChecklistAct.ViewState.LOADING) {
//                (requireActivity() as XChecklistAct).hideProgressBar()
//            }
//
//            when(state) {
//                is XChecklistAct.ViewState.IDLE -> {
//                    getWayBillList()
//                }
//                is XChecklistAct.ViewState.LOADING -> {
//                    if(getArgumentName() == null)
//                        (requireActivity() as XChecklistAct).showProgressBar()
//                    else
//                        (requireActivity() as XChecklistAct).showProgressBar(getArgumentName()!!)
//                    hideNoData()
//                }
//                is XChecklistAct.ViewState.DATA -> {
//                    srlRefresh?.isRefreshing = false
//                }
//                is XChecklistAct.ViewState.ERROR -> {
//                    toast(state.msg)
//                }
//                is XChecklistAct.ViewState.MESSAGE -> {
//                    toast(state.msg)
//                }
//                is XChecklistAct.ViewState.REFRESH -> {
//                    hideNoData()
//                }
//                else -> {
//                    throw Exception("Illegal View State in ${this::class.java.name}")
//                }
//            }
//        }

        val waybillS= viewModel.database.getWaybillS()
        mWayBillAdapter?.setItems(waybillS)
        actvNoData?.visibility = View.GONE
        onRefresh()
    }


    private fun getWayBillList() {

//        viewModel.getWayBillsList(body)


        val waybillRequest = WaybillRequestPOST()
        waybillRequest.getLiveDate().observe(viewLifecycleOwner) { result ->
            LOG.debug("${result}")
            (requireActivity() as XChecklistAct).hideProgressBar()
            if (result.isSent) {
                val waybillS = viewModel.database.getWaybillS()
                if (waybillS.size == 1) {
                    goToNextStep(waybillS[0].id, waybillS[0].number)
                } else if(waybillS.size > 0) {
                    mWayBillAdapter?.setItems(waybillS)
                } else {
                    actvNoData?.visibility = View.VISIBLE
                }
            }
        }
        App.oKRESTman().add(waybillRequest)
        App.oKRESTman().send()
    }

    override fun onRefresh() {
        actvNoData?.visibility = View.GONE
        getWayBillList()
        srlRefresh?.isRefreshing = false
        (requireActivity() as XChecklistAct).showProgressBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LOG.debug("${this::class.java.simpleName} :: ON DESTROY VIEW")
//        viewModel.mWayBillList.removeObservers(viewLifecycleOwner)
        viewModel.mWayBillsViewState.postValue(XChecklistAct.ViewState.IDLE())
    }

    private fun goToNextStep(wayBillId: Int, wayBillNumber: String) {
        viewModel.mWayBillsViewState.postValue(XChecklistAct.ViewState.IDLE())
        paramS().wayBillId = wayBillId
        paramS().wayBillNumber = wayBillNumber
        navigateMainChecklist(R.id.startWorkOrderF, wayBillId, wayBillNumber)
    }


    class WayBillAdapter(private val listener: (WaybillEntity) -> Unit): RecyclerView.Adapter<WayBillAdapter.WayBillViewHolder>() {

        private val mItems: MutableList<WaybillEntity> = mutableListOf()
        fun setItems(wayBillsList: List<WaybillEntity>) {
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

        class WayBillViewHolder(val itemView: View, val listener: (WaybillEntity) -> Unit): RecyclerView.ViewHolder(itemView) {
            fun bind(wayBill: WaybillEntity) {
                itemView.findViewById<TextView>(R.id.waybill_number).text = wayBill.number
                itemView.setOnClickListener {
                    listener(wayBill)
                }
            }
        }
    }
}