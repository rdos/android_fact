package ru.smartro.worknote.presentation.platform_serve

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.log
import ru.smartro.worknote.presentation.ac.MainAct
import ru.smartro.worknote.presentation.platform_serve.adapters.PServeGroupedByClientsAdapter
import ru.smartro.worknote.toast


class PServeGroupByContainersF : AFragment() {
    private var mBackPressedCnt: Int = 2

    private var btnCompleteTask: AppCompatButton? = null
    private var tvContainersProgress: TextView? = null
    private var actvAddress: AppCompatTextView? = null
    private var scPServeSimplifyMode: SwitchCompat? = null
    private var screenModeLabel: TextView? = null

    private val viewModel: PServeByTypesViewModel by viewModels()

    override fun onGetLayout(): Int {
        return R.layout.f_pserve_by_types
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val plId = getArgumentID()
        if (savedInstanceState == null) {
            log("savedInstanceState == null")
        } else {
            log("savedInstanceState HE null")
        }

        tvContainersProgress = view.findViewById(R.id.tv_platform_serve__cont_progress)
        btnCompleteTask = view.findViewById(R.id.acb_activity_platform_serve__complete)
        actvAddress = view.findViewById(R.id.tv_platform_serve__address)
        scPServeSimplifyMode = view.findViewById(R.id.sc_pserve_symplify_mode)
        screenModeLabel = view.findViewById(R.id.screen_mode_label)

        val adapterCurrentTask = PServeGroupedByClientsAdapter(requireContext(), object : PServeGroupedByClientsAdapter.SimplifyContainerServeListener {
            override fun onDecrease(clientName: String, typeName: String) {
                viewModel.onDecrease(clientName, typeName)
            }

            override fun onIncrease(clientName: String, typeName: String) {
                viewModel.onIncrease(clientName, typeName)
            }

            override fun onAddPhoto(clientName: String, typeName: String) {
                navigateMain(R.id.PhotoBeforeMediaContainerSimplifyF, viewModel.mPlatformEntity.value!!.platformId!!)
            }
        })

        val rvCurrentTask = view.findViewById<RecyclerView>(R.id.rv_main)
        rvCurrentTask.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterCurrentTask
        }

        viewModel.mSortedContainers.observe(viewLifecycleOwner) { list ->
            if(list != null) {
                adapterCurrentTask.containers = list
            }
        }

        viewModel.mServedContainers.observe(viewLifecycleOwner) { list ->
            if(list != null) {
                adapterCurrentTask.served = list
            }
        }

        viewModel.mPlatformEntity.observe(getAct()) { platform ->
            if(platform != null) {
                tvContainersProgress?.text =
                    "№${platform.srpId} / ${platform.containers.size} конт."

                btnCompleteTask?.setOnClickListener {
                    navigateMain(R.id.PhotoAfterMediaF, platform.platformId!!)
                }

                actvAddress?.text = "${platform.address}"
                if (platform.containers.size >= 7 ) {
                    actvAddress?.apply {
                        setOnClickListener { view ->
                            maxLines = if (maxLines < 3) {
                                3
                            } else {
                                1
                            }
                        }
                    }
                } else {
                    actvAddress?.maxLines = 3
                }
            }
        }

        viewModel.getPlatformEntity(plId)

        if (getAct() is MainAct) {
            (getAct() as MainAct).setSpecialProcessingForRecycler(rvCurrentTask)
        }




//        rvCurrentTask.viewTreeObserver
//            .addOnGlobalLayoutListener(
//                object : OnGlobalLayoutListener {
//                    override fun onGlobalLayout() {
//                        // At this point the layout is complete and the
//                        // dimensions of recyclerView and any child views
//                        if (getAct() is PServeAct) {
//                            (getAct() as PServeAct).onNewfromAFragment()
//                        }
//                        // are known.
//                        rvCurrentTask.viewTreeObserver
//                            .removeOnGlobalLayoutListener(this)
//                    }
//                })
    }

    override fun onBackPressed() {
        mBackPressedCnt--
        if (mBackPressedCnt <= 0) {
            viewModel.updatePlatformStatusUnfinished()
            navigateBack(R.id.MapF)
            toast("Вы не завершили обслуживание КП.")
        } else {
            toast("Вы не завершили обслуживание КП. Нажмите ещё раз, чтобы выйти")
        }
    }
}