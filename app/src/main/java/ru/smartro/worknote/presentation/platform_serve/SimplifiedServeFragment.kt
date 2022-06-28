package ru.smartro.worknote.presentation.platform_serve

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.search.Line
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R


class SimplifiedServeFragment : AFragment() {

    private val vm: PlatformServeSharedViewModel by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.fragment_platform_serve_simplified
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter1 = ContainerSimplifiedAdapter(requireContext())
        val adapter2 = ContainerSimplifiedAdapter(requireContext())

        val rv1 = view.findViewById<RecyclerView>(R.id.rv_main).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapter1
        }
        val rv2 = view.findViewById<RecyclerView>(R.id.rv_off_task).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapter2
        }

        vm.sortedContainers.observe(viewLifecycleOwner) { list ->
            if(list != null) {
                Log.d("TEST :::", "CONTS::: ${list}")
                adapter1.containers = list.find { it.isActiveToday }!!.clientGroupedContainers


//                if(list.find { !it.isActiveToday } != null) {
//
//                }
            }
        }

    }

}