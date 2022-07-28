package ru.smartro.worknote.presentation.platform_serve

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.platform_serve.adapters.SimplifiedContainerAdapter
import ru.smartro.worknote.presentation.platform_serve.adapters.TypedContainerAdapter


class PServeSimplifyFrag : AFragment() {

    private val vm: PlatformServeSharedViewModel by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.f_pserve_simplify
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterCurrentTask = SimplifiedContainerAdapter(requireContext(), object : TypedContainerAdapter.TypedContainerListener {
            override fun onDecrease(clientGroupInd: Int, typeGroupInd: Int) {
                vm.onDecrease(clientGroupInd, typeGroupInd)
            }

            override fun onIncrease(clientGroupInd: Int, typeGroupInd: Int) {
                vm.onIncrease(clientGroupInd, typeGroupInd)
            }

            override fun onAddPhoto(clientGroupInd: Int, typeGroupInd: Int) {
                navigateMain(R.id.PhotoBeforeMediaContainerSimplifyF, vm.mPlatformEntity.value!!.platformId!!)
            }

            override fun onInit() {

            }
        })

        val rvCurrentTask = view.findViewById<RecyclerView>(R.id.rv_main).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterCurrentTask
        }
        rvCurrentTask.viewTreeObserver
            .addOnGlobalLayoutListener(
                object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        // At this point the layout is complete and the
                        // dimensions of recyclerView and any child views
                        getAct().onNewfromAFragment()
                        // are known.
                        rvCurrentTask.viewTreeObserver
                            .removeOnGlobalLayoutListener(this)
                    }
                })
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

        paramS().isWalkthroughWasShown = false
    }
}