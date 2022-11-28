package ru.smartro.worknote.presentation.checklist.workorder

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.FragmentA
import ru.smartro.worknote.awORKOLDs.SynchroOidWidOutBodyDataWorkorder
import ru.smartro.worknote.awORKOLDs.SynchroOidWidRESTconnection
import ru.smartro.worknote.awORKOLDs.SynchroOidWidRequestPOST
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.ac.XChecklistAct
import ru.smartro.worknote.presentation.ac.MainAct

class StartWorkOrderF: FragmentA(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: StartWorkOrderViewModel by viewModels()
    private var mSynchroOidWidOutBodyDataWorkorderS: List<SynchroOidWidOutBodyDataWorkorder>? = null
    private var mWorkOrderAdapter: WorkOrderAdapter? = null
    private var rv: RecyclerView? = null
    private var srlRefresh: SwipeRefreshLayout? = null

    private var diffUnloadPointsDialogWasShownAll = false
    private var diffUnloadPointsDialogWasShownSelected = false

    private var takeAll: AppCompatButton? = null
    private var takeSelected: AppCompatButton? = null

    override fun onGetLayout(): Int = R.layout.f_start_workorder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!MyUtil.hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 1)
        }

        (requireActivity() as XChecklistAct).setBarTitle("Сменное Задание")

        srlRefresh = view.findViewById(R.id.srl__f_start_workorder__refresh)
        srlRefresh?.setOnRefreshListener(this)

        takeAll = view.findViewById<AppCompatButton>(R.id.acb__f_start_workorder__take_all).apply {
            visibility = View.GONE
            isEnabled = false
            setOnClickListener {
                val workOrders = mSynchroOidWidOutBodyDataWorkorderS
                if(workOrders != null) {

                    if(diffUnloadPointsDialogWasShownAll) {
                        goToNextStep(workOrders)
                        return@setOnClickListener
                    }

                    var hasDifferentUnloadPoint = false

                    val choosedWo = workOrders[0]
                    val choosedWoCoordLat = choosedWo.uNLoaDknow1.coords[0]
                    val choosedWoCoordLong = choosedWo.uNLoaDknow1.coords[1]

                    workOrders.forEach {
                        val selectedWoCoordLat = it.uNLoaDknow1.coords[0]
                        val selectedWoCoordLong = it.uNLoaDknow1.coords[1]
                        if(choosedWoCoordLat != selectedWoCoordLat ||
                            choosedWoCoordLong != selectedWoCoordLong) {
                            hasDifferentUnloadPoint = true
                            return@forEach
                        }
                    }

                    if(hasDifferentUnloadPoint) {
                        diffUnloadPointsDialogWasShownAll = true
                        navigateNext(R.id.DInfoPointsUploadF)
                    } else {
                        goToNextStep(workOrders)
                    }
                }
            }
        }

        takeSelected = view.findViewById<AppCompatButton>(R.id.acb__f_start_workorder__take_selected).apply {
            visibility = View.GONE
            setOnClickListener {
                val selectedIndexes = viewModel.mSelectedWorkOrdersIndecies.value
                if(selectedIndexes != null) {
                    val workOrders = mSynchroOidWidOutBodyDataWorkorderS
                    if(workOrders != null) {
                        val selectedWorkOrders =
                            workOrders.filterIndexed { i, _  -> selectedIndexes.contains(i) }

                        if(diffUnloadPointsDialogWasShownSelected) {
                            goToNextStep(selectedWorkOrders)
                            return@setOnClickListener
                        }

                        var hasDifferentUnloadPoint = false

                        val choosedWo = selectedWorkOrders[0]
                        val choosedWoCoordLat = choosedWo.uNLoaDknow1.coords[0]
                        val choosedWoCoordLong = choosedWo.uNLoaDknow1.coords[1]

                        selectedWorkOrders.forEach {
                            val selectedWoCoordLat = it.uNLoaDknow1.coords[0]
                            val selectedWoCoordLong = it.uNLoaDknow1.coords[1]
                            if(choosedWoCoordLat != selectedWoCoordLat ||
                                choosedWoCoordLong != selectedWoCoordLong) {
                                hasDifferentUnloadPoint = true
                                return@forEach
                            }
                        }

                        if(hasDifferentUnloadPoint) {
                            diffUnloadPointsDialogWasShownSelected = true
                            navigateNext(R.id.DInfoPointsUploadF)
                        } else {
                            goToNextStep(selectedWorkOrders)
                        }
                    }
                }
            }
        }

        mWorkOrderAdapter = WorkOrderAdapter()
        mWorkOrderAdapter?.setListener { woIndex ->
            val isSelected = viewModel.mSelectedWorkOrdersIndecies.value?.contains(woIndex)
            if(isSelected != null) {
                if(isSelected) {
                    viewModel.mSelectedWorkOrdersIndecies.value.let {
                        viewModel.mSelectedWorkOrdersIndecies.postValue(it?.apply { remove(woIndex) } ?: it)
                    }
                    mWorkOrderAdapter?.updateItemSelection(listOf(woIndex), false)
                } else {
                    viewModel.mSelectedWorkOrdersIndecies.value.let {
                        viewModel.mSelectedWorkOrdersIndecies.postValue(it?.apply { add(woIndex) } ?: it)
                    }
                    mWorkOrderAdapter?.updateItemSelection(listOf(woIndex), true)
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
            adapter = mWorkOrderAdapter
        }

//        viewModel.mWorkOrderList.observe(viewLifecycleOwner) { result ->
//            if(result != null) {
//                srlRefresh?.isRefreshing = false
//                val data = result.data
//                when (result.status) {
//                    Status.SUCCESS -> {
//                        val workOrders = data!!.dataKnow100.woRKoRDeRknow1s
//                        if (workOrders.size == 1) {
//                            goToNextStep(workOrders)
//                        } else if(workOrders.size > 1) {
//                            takeAll.isEnabled = true
//                            takeAll?.visibility = View.VISIBLE
//                            mWorkOrderAdapter?.setItems(workOrders)
//                        } else {
//                            toast("Нет данных. Перезагрузите страницу")
//                        }
//                        (requireActivity() as XChecklistAct).hideProgressBar()
//                    }
//                    Status.ERROR -> {
//                        toast(result.msg)
//                    }
//                    Status.NETWORK -> {
//                        toast("Проблемы с интернетом")
//                    }
//                }
//            } else {
//                if(getArgumentName() == null)
//                    (requireActivity() as XChecklistAct).showProgressBar()
//                else
//                    (requireActivity() as XChecklistAct).showProgressBar(getArgumentName()!!)
//                mWorkOrderAdapter?.clearItems()
//            }
//        }

        viewModel.mSelectedWorkOrdersIndecies.observe(viewLifecycleOwner) {
            if(it != null) {
                if(it.isEmpty()) {
                    mWorkOrderAdapter?.clearSelections()
                    takeSelected?.visibility = View.GONE
                } else {
                    mWorkOrderAdapter?.updateItemSelection(it, true)
                    takeSelected?.visibility = View.VISIBLE
                }
            }
        }

//        viewModel.getWorkOrderList(paramS().getOwnerId(), getArgumentID())
        onRefresh()
    }


    fun goToNextStep(workOrders: List<SynchroOidWidOutBodyDataWorkorder>) {
//        todo: Ох, рано встаёт охрана!
        // TODO: добавить логирование
        if (workOrders.size <= 0) {
            return
        }
        var checkName: String? = workOrders[0].waste_type?.name
        for(workOrder in workOrders) {
            if (checkName == workOrder.waste_type?.name) {
                checkName = workOrder.waste_type?.name
            } else {
                toast("Нельзя одновременно взять два задания с разными типами отходов")
                return
            }
        }



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
//        viewModel.mWorkOrderList.removeObservers(viewLifecycleOwner)
        viewModel.mSelectedWorkOrdersIndecies.removeObservers(viewLifecycleOwner)
    }

    override fun onRefresh() {
        getWorkOrderList()
        srlRefresh?.isRefreshing = false
        (requireActivity() as XChecklistAct).showProgressBar()
    }


    private fun getWorkOrderList() {
        val synchroOidWidRequest = SynchroOidWidRequestPOST()
        synchroOidWidRequest.getLiveDate().observe(viewLifecycleOwner) { result ->
            LOG.debug("safka${result}")
            (requireActivity() as XChecklistAct).hideProgressBar()
            if (result.isSent) {
                (requireActivity() as XChecklistAct).hideProgressBar()
                val workOrderS = (result as SynchroOidWidRESTconnection).workOrderS ?: listOf()
                if (workOrderS.size == 1) {
                    goToNextStep(workOrderS)
                } else if(workOrderS.size > 1) {
                    takeAll?.isEnabled = true
                    takeAll?.visibility = View.VISIBLE
                    mSynchroOidWidOutBodyDataWorkorderS = workOrderS
                    mWorkOrderAdapter?.setItems(workOrderS)
                } else {
                    toast("Нет данных. Перезагрузите страницу")
                }
            }
        }
        App.oKRESTman().put(synchroOidWidRequest)
    }

    class WorkOrderAdapter(): RecyclerView.Adapter<WorkOrderAdapter.WorkOrderViewHolder>() {

        private val mItems: MutableList<SynchroOidWidOutBodyDataWorkorder> = mutableListOf()
        private var listener: ((Int) -> Unit)? = null
        fun setItems(workOrders: List<SynchroOidWidOutBodyDataWorkorder>) {
            mItems.clear()
            mItems.addAll(workOrders)
            notifyDataSetChanged()
        }
        fun isItemsEmpty() = mItems.isEmpty()

        fun setListener(_listener: (Int) -> Unit) {
            listener = _listener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkOrderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.f_start_workorder__rv_item, parent, false)
            return WorkOrderViewHolder(view, listener)
        }

        override fun onBindViewHolder(holder: WorkOrderViewHolder, position: Int) {
            holder.bind(mItems[position], position)
        }

        override fun onBindViewHolder(holder: WorkOrderViewHolder, position: Int, payloads: List<Any>) {
            if(payloads.isNotEmpty()) {
                when {
                    // Обнови один элемент
                    payloads[0] is Boolean -> holder.updateItem(payloads[0] as Boolean)
                }
            } else {
                super.onBindViewHolder(holder, position, payloads);
            }
        }

        override fun getItemCount(): Int = mItems.size

        fun updateItemSelection(indexes: List<Int>, isSelected: Boolean) {
            for(ind in indexes) {
                notifyItemChanged(ind, isSelected)
            }
        }

        fun clearSelections() {
            if(mItems.isNotEmpty()) {
                notifyItemRangeChanged(0, mItems.size, false)
            }
        }

        fun clearItems() {
            mItems.clear()
            notifyDataSetChanged()
        }

        class WorkOrderViewHolder(val itemView: View, val listener: ((Int) -> Unit)?): RecyclerView.ViewHolder(itemView) {
            fun bind(workOrder: SynchroOidWidOutBodyDataWorkorder, position: Int){
                itemView.findViewById<TextView>(R.id.wo_name).text = workOrder.name

                if (workOrder.waste_type != null) {
                    itemView.findViewById<TextView>(R.id.choose_st).apply {
                        text = workOrder.waste_type.name
                        setTextColor(Color.parseColor("#${workOrder.waste_type.color.hex}"))
                    }
                }

                itemView.findViewById<TextView>(R.id.wo_status).apply {
                    if(workOrder.beginnedAt != null && workOrder.finishedAt == null) {
                        text = "В работе"
                        setTextColor(itemView.context.getColor(R.color.yellow))
                    } else if (workOrder.finishedAt != null) {
                        text = "Завершено"
                        setTextColor(itemView.context.getColor(R.color.green))
                    } else {
                        text = "Новое"
                    }
                }

                itemView.setOnClickListener { if(listener != null) listener!!(position) }
            }

            fun updateItem(isSelected: Boolean = false) {
                itemView.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                    itemView.context,
                    if (isSelected)
                        R.drawable.bg_button_green__usebutton
                    else
                        R.drawable.bg_button_green__default
                ))
            }
        }
    }
}