package ru.smartro.worknote.presentation.platform_serve

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.util.PhotoTypeEnum
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.work.cam.CameraAct
import ru.smartro.worknote.work.cam.PhotoBeforeMediaF

class PServeF : AFragment() {
    override fun onGetLayout(): Int {
        return R.layout.f_platformserve
    }

    companion object {
        fun newInstance(workOrderId: Any? = null): PServeF {
            workOrderId as Int
            val fragment = PServeF()
            fragment.addArgument(workOrderId)
            return fragment
        }
    }


    private var btnCompleteTask: AppCompatButton? = null
    private var tvContainersProgress: TextView? = null
    private var actvAddress: AppCompatTextView? = null
    private var switch: SwitchCompat? = null
    private var screenModeLabel: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val plId = getArgumentID()
        val vm = (getAct() as PServeMain).vm
        vm.getPlatformEntity(plId)

        tvContainersProgress = view.findViewById(R.id.tv_platform_serve__cont_progress)
        btnCompleteTask = view.findViewById(R.id.acb_activity_platform_serve__complete)
        actvAddress = view.findViewById(R.id.tv_platform_serve__address)
        switch = view.findViewById(R.id.switch_mode)
        screenModeLabel = view.findViewById(R.id.screen_mode_label)

        val navHostFragment =
            getAct().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        switch?.setOnCheckedChangeListener { _, _ ->
            vm.changeScreenMode()
        }

        vm.mWasServedExtended.observe(getAct()) {
            if(it) {
                switch?.visibility = View.GONE
                screenModeLabel?.visibility = View.GONE
            }
        }

        vm.mPlatformEntity.observe(getAct()) { platform ->
            if(platform != null) {
                if(platform.containers.all { el -> el.status == StatusEnum.NEW }) {
                    vm.mScreenMode.observe(getAct()) { screenMode ->
                        if(screenMode != null) {
                            navController.currentDestination?.apply {
                                when(screenMode) {
                                    false -> {
                                        if (id != R.id.simplifiedServeFragment) {
                                            navController.popBackStack()
                                        }
                                        screenModeLabel?.text = "Упрощенный режим"
                                    }
                                    true -> {
                                        if (id != R.id.extendedServeFragment) {
                                            navController.navigate(R.id.extendedServeFragment)
                                        }
                                        screenModeLabel?.text = "Расширенный режим"
                                    }
                                }
                            }
                        }
                    }
                } else {
                    switch?.visibility = View.GONE
                    screenModeLabel?.visibility = View.GONE
                    navController.navigate(R.id.extendedServeFragment)
                }



                tvContainersProgress?.text =
                    "№${platform.srpId} / ${platform.containers.size} конт."

                btnCompleteTask?.setOnClickListener {
                    vm.updatePlatformStatusSuccess(platform.platformId!!)
//                    val intent = Intent(this, CameraAct::class.java)
//                    intent.putExtra("platform_id", platform.platformId)
//                    intent.putExtra("photoFor", PhotoTypeEnum.forAfterMedia)
//                    startActivityForResult(intent, 13)
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
    }
}