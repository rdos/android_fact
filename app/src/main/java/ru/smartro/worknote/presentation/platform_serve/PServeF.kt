package ru.smartro.worknote.presentation.platform_serve

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.App
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.log
import ru.smartro.worknote.toast

class PServeF : AFragment() {
    private var mBackPressedCnt: Int = 2

    override fun onGetLayout(): Int {
        return R.layout.f_pserve
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

    private var navController: NavController? = null
    private var btnCompleteTask: AppCompatButton? = null
    private var tvContainersProgress: TextView? = null
    private var actvAddress: AppCompatTextView? = null
    private var scPServeSimplifyMode: SwitchCompat? = null
    private var screenModeLabel: TextView? = null
    private val viewModel: PlatformServeSharedViewModel by activityViewModels()

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

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // TODO?::Vlad setonclicklistener > setonchangedlistener
        scPServeSimplifyMode?.setOnClickListener {
            toggleScreenMode()
        }

        viewModel.mWasServedExtended.observe(getAct()) {
            if(it) {
                scPServeSimplifyMode?.visibility = View.GONE
                screenModeLabel?.visibility = View.GONE
                if(navController?.currentDestination?.id != R.id.PServeExtendedF) {
                    navController?.navigate(R.id.PServeExtendedF)
                }
            }
        }

        viewModel.mPlatformEntity.observe(getAct()) { platform ->
            if(platform != null) {
                if(platform.containers.all { el -> el.status == StatusEnum.NEW }) {
                    setScreenMode(paramS().lastScreenMode)
                } else {
                    scPServeSimplifyMode?.visibility = View.GONE
                    screenModeLabel?.visibility = View.GONE
                    if(platform.containers.any { el -> el.volume != null && el.volume!! > 1.25f })
                        navController?.navigate(R.id.PServeSimplifyF)
                    else
                        navController?.navigate(R.id.PServeExtendedF)
                }

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
    }

    private fun toggleScreenMode() {
        val newScreenMode = !paramS().lastScreenMode
        paramS().lastScreenMode = newScreenMode
        setScreenMode(newScreenMode)
    }

    private fun setScreenMode(mode: Boolean) {
        navController?.currentDestination?.apply {
            scPServeSimplifyMode?.isChecked = mode
            when(mode) {
                App.ScreenMode.EXTENDED -> {
                    screenModeLabel?.text = "список"
                    if (id == R.id.PServeSimplifyF) {
                        navController?.popBackStack()
                    }
                }
                App.ScreenMode.SIMPLIFY -> {
                    screenModeLabel?.text = "по типам"
                    if (id == R.id.PServeExtendedF) {
                        navController?.navigate(R.id.PServeSimplifyF)
                    }
                }
                else -> {}
            }
        }
    }
}