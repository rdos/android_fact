package ru.smartro.worknote.presentation.checklist.workorder

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.FragmentA
import ru.smartro.worknote.awORKOLDs.extensions.showDialogAction
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.ac.XChecklistAct
import ru.smartro.worknote.presentation.ac.MainAct
import ru.smartro.worknote.presentation.work.Status
import ru.smartro.worknote.presentation.work.WoRKoRDeR_know1

class StartWorkOrderF: FragmentA(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: StartWorkOrderViewModel by viewModels()
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
                    // TODO::: Vlad
                    showDialogAction(
                        resources.getString(R.string.different_unload_points),
                        onAccept = {
                            goToNextStep(workOrders)
                        }
                    )
                }
            }
        }

        val takeSelected = view.findViewById<AppCompatButton>(R.id.acb__f_start_workorder__take_selected).apply {
            visibility = View.GONE
            setOnClickListener {
                val selectedIndexes = viewModel.mSelectedWorkOrdersIndecies.value
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
        rvAdapter?.setListener { woIndex ->
            val isSelected = viewModel.mSelectedWorkOrdersIndecies.value?.contains(woIndex)
            if(isSelected != null) {
                if(isSelected) {
                    viewModel.mSelectedWorkOrdersIndecies.value.let {
                        viewModel.mSelectedWorkOrdersIndecies.postValue(it?.apply { remove(woIndex) } ?: it)
                    }
                    rvAdapter?.updateItemSelection(listOf(woIndex), false)
                } else {
                    var hasDifferentUnloadPoint = false

                    val workOrderS = viewModel.mWorkOrderList.value!!.data!!.dataKnow100.woRKoRDeRknow1s

                    val choosedWo = workOrderS[woIndex]
                    val choosedWoCoordLat = choosedWo.uNLoaDknow1.coords[0]
                    val choosedWoCoordLong = choosedWo.uNLoaDknow1.coords[1]

                    viewModel.mSelectedWorkOrdersIndecies.value!!.forEach { selectedWoIndex ->
                        val selectedWo = workOrderS[selectedWoIndex]
                        val selectedWoCoordLat = selectedWo.uNLoaDknow1.coords[0]
                        val selectedWoCoordLong = selectedWo.uNLoaDknow1.coords[1]
                        if(choosedWoCoordLat != selectedWoCoordLat ||
                                choosedWoCoordLong != selectedWoCoordLong) {
                            hasDifferentUnloadPoint = true
                            return@forEach
                        }
                    }

                    if(hasDifferentUnloadPoint) {
                        navigateNext(R.id.InfoPointsUploadFD)
                    }

                    viewModel.mSelectedWorkOrdersIndecies.value.let {
                        viewModel.mSelectedWorkOrdersIndecies.postValue(it?.apply { add(woIndex) } ?: it)
                    }
                    rvAdapter?.updateItemSelection(listOf(woIndex), true)
                }
            }

//            if(isSelected != null) {
//                viewModel.mSelectedWorkOrders.value.let {
//                    val updatedList = it?.apply { if(isSelected) remove(woInd) else add(woInd) } ?: it
//                    viewModel.mSelectedWorkOrders.postValue(updatedList)
//                    rvAdapter?.updateItemSelection(listOf(woInd), !isSelected)
//                }
//            }
        }

        rv = view.findViewById<RecyclerView>(R.id.rv__f_start_workorder__workorders).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
        }

        viewModel.mWorkOrderList.observe(viewLifecycleOwner) { result ->
            if(result != null) {
                swipeRefreshLayout?.isRefreshing = false
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        val workOrders = data!!.dataKnow100.woRKoRDeRknow1s
                        if (workOrders.size == 1) {
                            goToNextStep(workOrders)
                        } else if(workOrders.size > 1) {
                            takeAll.isEnabled = true
                            takeAll?.visibility = View.VISIBLE
                            rvAdapter?.setItems(workOrders)
                        } else {
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
            } else {
                if(getArgumentName() == null)
                    (requireActivity() as XChecklistAct).showProgressBar()
                else
                    (requireActivity() as XChecklistAct).showProgressBar(getArgumentName()!!)
                rvAdapter?.clearItems()
            }
        }

        viewModel.mSelectedWorkOrdersIndecies.observe(viewLifecycleOwner) {
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

        viewModel.getWorkOrderList(paramS().getOwnerId(), getArgumentID())
    }

    fun goToNextStep(workOrders: List<WoRKoRDeR_know1>) {
        val intent = Intent(requireActivity(), MainAct::class.java)
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.mWorkOrderList.removeObservers(viewLifecycleOwner)
        viewModel.mSelectedWorkOrdersIndecies.removeObservers(viewLifecycleOwner)
    }

    override fun onRefresh() {
        viewModel.getWorkOrderList(paramS().getOwnerId(), paramS().wayBillId)
    }
}