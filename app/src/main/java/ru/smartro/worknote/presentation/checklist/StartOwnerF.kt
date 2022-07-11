package ru.smartro.worknote.presentation.checklist

import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.checklist.adapters.OwnerAdapter
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.Status
import ru.smartro.worknote.work.ac.PERMISSIONS

class StartOwnerF: AFragment() {

    override fun onGetLayout(): Int = R.layout.f_start_owner

    private val viewModel: ChecklistViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().actionBar?.title = "Организация"

        if (!MyUtil.hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 1)
        }

        showingProgress()

        val adapter = OwnerAdapter { ownerId ->
            findNavController().navigate(StartOwnerFDirections.actionStartOwnerFToStartVehicleF())
        }
        val rv = view.findViewById<RecyclerView>(R.id.rv_act_start_owner).apply {
            layoutManager = LinearLayoutManager(requireContext())
            setAdapter(adapter)
        }

//        viewModel.getOwners().observe(viewLifecycleOwner) { owners ->
//            adapter.setItems(owners)
//        }
    }
}