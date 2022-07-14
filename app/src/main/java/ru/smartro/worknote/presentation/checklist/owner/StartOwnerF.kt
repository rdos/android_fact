package ru.smartro.worknote.presentation.checklist.owner

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.checklist.ChecklistViewModel
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.Status
import ru.smartro.worknote.work.ac.PERMISSIONS

class StartOwnerF: AFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var progressBar: ProgressBar? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onGetLayout(): Int = R.layout.f_start_owner

    private val viewModel: ChecklistViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Организация"

        if (!MyUtil.hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 1)
        }

        progressBar = view.findViewById(R.id.progress_bar)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout?.setOnRefreshListener(this)

        val rvAdapter = StartOwnerAdapter { owner ->
            goToNextStep(owner.id, owner.name)
        }
        val rv = view.findViewById<RecyclerView>(R.id.rv_act_start_owner).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
        }

        viewModel.mOwnersList.observe(viewLifecycleOwner) { result ->
            if(result != null) {
                val data = result.data
                swipeRefreshLayout?.isRefreshing = false
                hideProgressBar()
                when (result.status) {
                    Status.SUCCESS -> {
                        val owners = data!!.data.organisations
                        Log.d("TEST :::", "owners size: ${owners.size}")
                        if (owners.size == 1)
                            goToNextStep(owners[0].id, owners[0].name)
                        else
                            rvAdapter.setItems(owners)
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
        if(viewModel.mOwnersList.value == null) {
            showProgressBar()
            viewModel.getOwnersList()
        } else {
            hideProgressBar()
        }
    }

    private fun goToNextStep(ownerId: Int, ownerName: String) {
        paramS().ownerId = ownerId
        paramS().ownerName = ownerName
        // TODO!! will be changed to navigateMain
        navigateMainChecklist(R.id.startVehicleF, ownerId)
    }

    override fun onRefresh() {
        viewModel.getOwnersList()
    }

    fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }
}