package ru.smartro.worknote.presentation.checklist.workorder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.MapAct
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.checklist.ChecklistViewModel
import ru.smartro.worknote.presentation.checklist.XChecklistAct
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.Status
import ru.smartro.worknote.work.WoRKoRDeR_know1
import ru.smartro.worknote.work.WorkOrderResponse_know1
import ru.smartro.worknote.work.ac.PERMISSIONS

class StartWorkOrderF: AFragment(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: ChecklistViewModel by activityViewModels()
    private var rvAdapter: StartWorkOrderAdapter? = null
    private var rv: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onGetLayout(): Int = R.layout.f_start_workorder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!MyUtil.hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 1)
        }

        (requireActivity() as XChecklistAct).setBarTitle("Сменное Задание")

        swipeRefreshLayout = view.findViewById(R.id.srl__f_start_workorder__refresh)
        swipeRefreshLayout?.setOnRefreshListener(this)

        val takeAll = view.findViewById<AppCompatButton>(R.id.acb__f_start_workorder__take_all).apply {
            visibility = View.GONE
            isEnabled = false
            setOnClickListener {
                val workOrders = viewModel.mWorkOrderList.value?.data?.dataKnow100?.woRKoRDeRknow1s
                if(workOrders != null) {
                    goToNextStep(workOrders)
                }
            }
        }

        val takeSelected = view.findViewById<AppCompatButton>(R.id.acb__f_start_workorder__take_selected).apply {
            visibility = View.GONE
            isEnabled = false
            setOnClickListener {
                val selectedIndexes = viewModel.mSelectedWorkOrders.value
                if(selectedIndexes != null) {
                    val workOrders = viewModel.mWorkOrderList.value?.data?.dataKnow100?.woRKoRDeRknow1s
                    if(workOrders != null) {
                        val selectedWorkOrders =
                            workOrders.filterIndexed { i, _  -> selectedIndexes.contains(i) }
                        goToNextStep(selectedWorkOrders)
                    }
                }
            }
        }

        rvAdapter = StartWorkOrderAdapter()
        rvAdapter?.setListener { woInd ->
            val isSelected = viewModel.mSelectedWorkOrders.value?.contains(woInd)
            if(isSelected != null) {
                if(isSelected) {
                    viewModel.mSelectedWorkOrders.value.let {
                        viewModel.mSelectedWorkOrders.postValue(it?.apply { remove(woInd) } ?: it)
                    }
                    rvAdapter?.updateItemSelection(listOf(woInd), false)
                } else {
                    viewModel.mSelectedWorkOrders.value.let {
                        viewModel.mSelectedWorkOrders.postValue(it?.apply { add(woInd) } ?: it)
                    }
                    rvAdapter?.updateItemSelection(listOf(woInd), true)
                }
            }
        }

        rv = view.findViewById<RecyclerView>(R.id.rv__f_start_workorder__workorders).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
        }

        viewModel.mWorkOrderList.observe(viewLifecycleOwner) { result ->
            if(result != null) {
                takeAll.isEnabled = true
                swipeRefreshLayout?.isRefreshing = false
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        val workOrders = data!!.dataKnow100.woRKoRDeRknow1s
                        if (workOrders.size == 1) {
                            goToNextStep(workOrders)
                        } else if(workOrders.size > 1) {
                            takeAll?.visibility = View.VISIBLE
                            rvAdapter?.setItems(workOrders)
                        } else {
                            // TODO::Vlad
                            toast("Нет данных. Перезагрузите страницу")
                        }
                        (requireActivity() as XChecklistAct).hideProgressBar()
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

        viewModel.mSelectedWorkOrders.observe(viewLifecycleOwner) {
            if(it != null) {
                if(it.isEmpty()) {
                    rvAdapter?.clearSelections()
                    takeSelected.visibility = View.GONE
                } else {
                    rvAdapter?.updateItemSelection(it, true)
                    takeSelected.visibility = View.VISIBLE
                }
            }
        }

        Log.d("TEST ::: FUCK",
            "vm.lastOwnerId=${viewModel.mLastOwnerId}, " +
                    "params.ownerId=${paramS().getOwnerId()}, " +
                    "vm.lastWayBillId=${viewModel.mLastWayBillId}, " +
                    "getArgumentID(waybillId)=${getArgumentID()}")
        if(viewModel.mWorkOrderList.value == null ||
            viewModel.mLastOwnerId != paramS().getOwnerId() ||
            viewModel.mLastWayBillId != getArgumentID()
        ) {
            viewModel.getWorkOrderList(paramS().getOwnerId(), getArgumentID())
        }
    }

    fun goToNextStep(workOrders: List<WoRKoRDeR_know1>) {
        val intent = Intent(requireActivity(), MapAct::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

        if(workOrders.size == 1) {
            val workOrder = workOrders[0]
            intent.putExtra(getAct().PUT_EXTRA_PARAM_ID, workOrder.id)
            viewModel.insertWorkOrders(listOf(workOrder))
        } else {
            viewModel.insertWorkOrders(workOrders)
        }
        startActivity(intent)
        getAct().finish()
    }

    override fun onRefresh() {
        viewModel.getWorkOrderList(paramS().getOwnerId(), paramS().wayBillId)
    }
}