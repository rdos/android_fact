package ru.smartro.worknote.presentation.checklist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.Status
import ru.smartro.worknote.work.ac.PERMISSIONS

class StartOwnerF: AFragment() {

    override fun onGetLayout(): Int = R.layout.f_start_owner

    private val viewModel: SingleViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Организация"

        if (!MyUtil.hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 1)
        }

        showingProgress()

        val adapter = StartOwnerAdapter { owner ->
            goToNextStep(owner.id, owner.name)
        }
        val rv = view.findViewById<RecyclerView>(R.id.rv_act_start_owner).apply {
            layoutManager = LinearLayoutManager(requireContext())
            setAdapter(adapter)
        }

        showingProgress()

        viewModel.mOwnerList.observe(viewLifecycleOwner) { result ->
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    val owners = data!!.data.organisations
                    if (owners.size == 1)
                        goToNextStep(owners[0].id, owners[0].name)
                    else
                        adapter.setItems(owners)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu_logout, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // TODO:
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MyUtil.onMenuOptionClicked(requireContext(), item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun goToNextStep(ownerId: Int, ownerName: String) {
        paramS().ownerId = ownerId
        paramS().ownerName = ownerName
        // TODO!! will be changed to navigateMain
        val navHost = (getAct().supportFragmentManager.findFragmentById(R.id.checklist_nav_host) as NavHostFragment)
        val navController = navHost.navController
        val argSBundle = getArgSBundle(ownerId, null)
        navController.navigate(R.id.startVehicleF, argSBundle)
    }
}