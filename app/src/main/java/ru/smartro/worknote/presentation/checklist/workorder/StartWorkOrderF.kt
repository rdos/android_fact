package ru.smartro.worknote.presentation.checklist.workorder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
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
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.Status
import ru.smartro.worknote.work.ac.PERMISSIONS

class StartWorkOrderF: AFragment(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: ChecklistViewModel by activityViewModels()
    private var rvAdapter: StartWorkOrderAdapter? = null
    private var rv: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var progressBar: ProgressBar? = null

    override fun onGetLayout(): Int = R.layout.f_start_workorder

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
            title = "Сменное Задание"
            setDisplayHomeAsUpEnabled(true)
        }
        progressBar = view.findViewById(R.id.progress_bar)

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout?.setOnRefreshListener(this)

        val takeAll = view.findViewById<AppCompatButton>(R.id.take_all)
        takeAll.setOnClickListener { gotoNextAct() }
        takeAll.isEnabled = false

        val takeSelected = view.findViewById<AppCompatButton>(R.id.take_selected)
        takeSelected.visibility = View.GONE
        takeSelected.setOnClickListener { gotoNextAct() }

        rvAdapter = StartWorkOrderAdapter()
        rvAdapter?.setListener { woInd ->
            val isSelected = viewModel.mSelectedWorkOrders.value?.contains(woInd)
            if(isSelected != null) {
                if(isSelected) {
                    viewModel.mSelectedWorkOrders.value.let {
                        viewModel.mSelectedWorkOrders.postValue(it?.apply { remove(woInd) } ?: it)
                    }
                    rvAdapter?.notifyItemChanged(woInd, false)
                } else {
                    viewModel.mSelectedWorkOrders.value.let {
                        viewModel.mSelectedWorkOrders.postValue(it?.apply { add(woInd) } ?: it)
                    }
                    rvAdapter?.notifyItemChanged(woInd, true)
                }
            }
        }

        rv = view.findViewById<RecyclerView>(R.id.rv_workorders).apply {
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
                            gotoNextAct()
                        } else {
                            rvAdapter?.setItems(workOrders)
                        }
                        hideProgressBar()
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
                    takeSelected.visibility = View.GONE
                } else {
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
            showProgressBar()
            viewModel.getWorkOrderList(paramS().getOwnerId(), getArgumentID())
        } else {
            hideProgressBar()
        }
    }

    fun showProgressBar() {
        Log.d("TEST ::::", "SHOW PROGRESS BAR! is progress null = ${progressBar == null}")
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }

    // TODO::vlad очень плохо(
    fun gotoNextAct() {
        val intent = Intent(requireActivity(), MapAct::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        val workOrders = viewModel.mWorkOrderList.value?.data?.dataKnow100?.woRKoRDeRknow1s
        // Если выбран только один воркордер
        if(viewModel.mSelectedWorkOrders.value?.size == 1) {
            val selectedWorkOrderIndex = viewModel.mSelectedWorkOrders.value?.get(0)
            if (selectedWorkOrderIndex != null) {
                val workOrder = workOrders?.get(selectedWorkOrderIndex)
                workOrder?.let {
                    intent.putExtra(getAct().PUT_EXTRA_PARAM_ID, it.id)
                    viewModel.insertWorkOrders(it)
                }
            }
        // Если выбрано больше воркордеров
        } else if((viewModel.mSelectedWorkOrders.value?.size ?: 0) > 0) {
            workOrders?.filterIndexed { index, _ ->
                viewModel.mSelectedWorkOrders.value?.contains(index) ?: false
            }?.let {
                viewModel.insertWorkOrders(it)
            }
        // Если получен только один воркордер
        } else if(workOrders?.size == 1){
            viewModel.insertWorkOrders(workOrders)
            intent.putExtra(getAct().PUT_EXTRA_PARAM_ID, workOrders[0].id)
            viewModel.insertWorkOrders(workOrders)
        // Если сюда пришёл - значит нажали "Взять все"
        } else {
            workOrders?.let {
                viewModel.insertWorkOrders(it)
            }
        }
        startActivity(intent)
        getAct().finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navigateBackChecklist()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        viewModel.getWorkOrderList(paramS().getOwnerId(), paramS().wayBillId)
    }
}