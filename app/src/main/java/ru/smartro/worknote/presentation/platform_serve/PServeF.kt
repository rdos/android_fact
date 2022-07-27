package ru.smartro.worknote.presentation.platform_serve

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.toast

class PServeF : AFragment() {
    override fun onGetLayout(): Int {
        return R.layout.f_pserve
    }

    override fun onBackPressed() {
        super.onBackPressed()
        vm.updatePlatformStatusUnfinished()
        navigateClose()
        toast("Вы не завершили обслуживание КП.")
    }

    private var btnCompleteTask: AppCompatButton? = null
    private var tvContainersProgress: TextView? = null
    private var actvAddress: AppCompatTextView? = null
    private var scPServeSimplifyMode: SwitchCompat? = null
    private var screenModeLabel: TextView? = null
    private val vm: PlatformServeSharedViewModel by activityViewModels()

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
        val navController = navHostFragment.navController

        // TODO?::Vlad setonclicklistener > setonchangedlistener
        scPServeSimplifyMode?.setOnClickListener {
            vm.changeScreenMode()
        }

        vm.mWasServedExtended.observe(getAct()) {
            if(it) {
                scPServeSimplifyMode?.visibility = View.GONE
                screenModeLabel?.visibility = View.GONE
                if(navController.currentDestination?.id != R.id.PServeExtendedFrag) {
                    navController.navigate(R.id.PServeExtendedFrag)
                }
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
                                        if (id != R.id.PServeExtendedFrag) {
                                            navController.popBackStack()
                                        }
                                        screenModeLabel?.text = "Стандартный режим"
                                        if(scPServeSimplifyMode?.isChecked != false)
                                            scPServeSimplifyMode?.isChecked = false
                                    }
                                    true -> {
                                        if (id != R.id.PServeSimplifyFrag) {
                                            navController.navigate(R.id.PServeSimplifyFrag)
                                        }
                                        screenModeLabel?.text = "Сгруппированный режим"
                                        if(scPServeSimplifyMode?.isChecked != true)
                                            scPServeSimplifyMode?.isChecked = true
                                    }
                                }
                            }
                        }
                    }
                } else {
                    vm.mScreenMode.removeObservers(getAct())
                    scPServeSimplifyMode?.visibility = View.GONE
                    screenModeLabel?.visibility = View.GONE
                    navController.navigate(R.id.PServeExtendedFrag)
                }

                tvContainersProgress?.text =
                    "№${platform.srpId} / ${platform.containers.size} конт."

                btnCompleteTask?.setOnClickListener {
                    vm.updatePlatformStatusSuccess(platform.platformId!!)
//                    val intent = Intent(this, CameraAct::class.java)
//                    intent.putExtra("platform_id", platform.platformId)
//                    intent.putExtra("photoFor", PhotoTypeEnum.forAfterMedia)
//                    startActivityForResult(intent, 13)
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

        vm.getPlatformEntity(plId)
    }
}