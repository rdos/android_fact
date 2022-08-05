package ru.smartro.worknote.presentation.platform_serve

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.platform_serve.adapters.SimplifiedContainerAdapter
import ru.smartro.worknote.presentation.platform_serve.adapters.TypedContainerAdapter


class PServeSimplifyF : AFragment() {

    private val vm: PlatformServeSharedViewModel by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.f_pserve_simplify
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterCurrentTask = SimplifiedContainerAdapter(requireContext(), object : SimplifiedContainerAdapter.SimplifyContainerServeListener {
            override fun onDecrease(clientName: String, typeName: String) {
                vm.onDecrease(clientName, typeName)
            }

            override fun onIncrease(clientName: String, typeName: String) {
                vm.onIncrease(clientName, typeName)
            }

            override fun onAddPhoto(clientName: String, typeName: String) {
                navigateMain(R.id.PhotoBeforeMediaContainerSimplifyF, vm.mPlatformEntity.value!!.platformId!!)
            }
        })

        val rvCurrentTask = view.findViewById<RecyclerView>(R.id.rv_main).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterCurrentTask
        }

        vm.mSortedContainers.observe(viewLifecycleOwner) { list ->
            if(list != null) {
                adapterCurrentTask.containers = list
            }
        }

        vm.mServedContainers.observe(viewLifecycleOwner) { list ->
            if(list != null) {
                Log.d("TEST ::::", "m SERVED CONTAINERS : ${list}")
                adapterCurrentTask.served = list
            }
        }

//        if(paramS().isWalkthroughWasShown == false) {
//            navigateMain(R.id.WalkthroughStepAF, 1)
//        }
    }
}