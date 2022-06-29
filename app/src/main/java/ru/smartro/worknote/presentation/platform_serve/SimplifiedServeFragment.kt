package ru.smartro.worknote.presentation.platform_serve

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.platform_serve.adapters.SimplifiedContainerAdapter
import ru.smartro.worknote.presentation.platform_serve.adapters.TypedContainerAdapter
import kotlin.reflect.typeOf


class SimplifiedServeFragment : AFragment(), SimplifiedContainerAdapter.ClientContainerListener {

    private val vm: PlatformServeSharedViewModel by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.fragment_platform_serve_simplified
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterCurrentTask = SimplifiedContainerAdapter(requireContext(), this)
        val adapterOffTask = SimplifiedContainerAdapter(requireContext(), this)

        val rvCurrentTask = view.findViewById<RecyclerView>(R.id.rv_main).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterCurrentTask
        }
        val rvOffTask = view.findViewById<RecyclerView>(R.id.rv_off_task).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterOffTask
        }

        vm.sortedContainers.observe(viewLifecycleOwner) { list ->
            if(list != null) {
                val contsCurrentTask = list.find { it.isActiveToday }
                if(contsCurrentTask != null) {
                    adapterCurrentTask.containers = contsCurrentTask.clientGroupedContainers
                }

                val contsOffTask = list.find { !it.isActiveToday }
                if(contsOffTask != null) {
                    view.findViewById<LinearLayoutCompat>(R.id.containers_off_task).visibility = View.VISIBLE
                    adapterOffTask.containers = contsOffTask.clientGroupedContainers
                } else {
                    view.findViewById<LinearLayoutCompat>(R.id.containers_off_task).visibility = View.GONE
                }
            }
        }
    }

    override fun onDecrease(clientGroupId: Int, typeGroupId: Int) {
        vm.onDecrease(clientGroupId, typeGroupId)
    }

    override fun onIncrease(clientGroupId: Int, typeGroupId: Int) {
        vm.onIncrease(clientGroupId, typeGroupId)
    }

    override fun onAddPhoto(clientGroupId: Int, typeGroupId: Int) {
        vm.onAddPhoto(clientGroupId, typeGroupId)
    }
}