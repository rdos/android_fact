package ru.smartro.worknote.presentation.checklist.workorder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.MapAct
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.Status
import ru.smartro.worknote.work.ac.PERMISSIONS

class StartWorkOrderF: AFragment(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: StartWorkOrderViewModel by viewModels()
    private var rvAdapter: StartWorkOrderAdapter? = null
    private var rv: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

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

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout?.setOnRefreshListener(this)

        rvAdapter = StartWorkOrderAdapter()
        rvAdapter?.setListener { woInd ->
            val isSelected = viewModel.mSelectedWorkOrders.value?.contains(woInd)
            if(isSelected != null) {
                if(isSelected) {
                    viewModel.mSelectedWorkOrders.value.let {
                        viewModel.mSelectedWorkOrders.postValue(it?.apply { remove(woInd) } ?: it)
                    }
                    rvAdapter?.updateItem(woInd, false)
                } else {
                    viewModel.mSelectedWorkOrders.value.let {
                        viewModel.mSelectedWorkOrders.postValue(it?.apply { add(woInd) } ?: it)
                    }
                    rvAdapter?.updateItem(woInd, true)
                }
            }
        }

        rv = view.findViewById<RecyclerView>(R.id.rv_workorders).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
        }

        viewModel.mWorkOrderList.observe(viewLifecycleOwner) { result ->
            if(result == null) {
                showingProgress()
            } else {
                swipeRefreshLayout?.isRefreshing = false
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        val workOrders = data!!.dataKnow100.woRKoRDeRknow1s
                        if (workOrders.size == 1) {
                            gotoNextAct()
                        }
                        rvAdapter?.setItems(workOrders)
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
                hideProgress()
            }
        }

        val takeAll = view.findViewById<AppCompatButton>(R.id.take_all)
        takeAll.setOnClickListener { gotoNextAct() }
        val takeSelected = view.findViewById<AppCompatButton>(R.id.take_selected)
        takeSelected.setOnClickListener { gotoNextAct() }

        viewModel.mSelectedWorkOrders.observe(viewLifecycleOwner) {
            Log.d("TEST:::", "SELECTED::: ${it}")
            if(it != null) {
                if(it.isEmpty()) {
                    takeSelected.visibility = View.GONE
                } else {
                    takeSelected.visibility = View.VISIBLE
                }
            }
        }

        viewModel.getWorkOrderList(paramS().getOwnerId(), paramS().wayBillId)
    }

    // TODO::vlad очень плохо(
    fun gotoNextAct() {
        val intent = Intent(requireActivity(), MapAct::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        val workOrders = viewModel.mWorkOrderList.value?.data?.dataKnow100?.woRKoRDeRknow1s
        if(viewModel.mSelectedWorkOrders.value?.size == 1) {
            val selectedWorkOrderIndex = viewModel.mSelectedWorkOrders.value?.get(0)
            if (selectedWorkOrderIndex != null) {
                val workOrder = workOrders?.get(selectedWorkOrderIndex)
                workOrder?.let {
                    intent.putExtra(getAct().PUT_EXTRA_PARAM_ID, it.id)
                    viewModel.insertWorkOrders(it)
                }
            }
        } else if((viewModel.mSelectedWorkOrders.value?.size ?: 0) > 0) {
            workOrders?.filterIndexed { index, _ ->
                viewModel.mSelectedWorkOrders.value?.contains(index) ?: false
            }?.let {
                viewModel.insertWorkOrders(it)
            }
        } else if(workOrders?.size == 1){
            viewModel.insertWorkOrders(workOrders)
            intent.putExtra(getAct().PUT_EXTRA_PARAM_ID, workOrders[0].id)
            viewModel.insertWorkOrders(workOrders)
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