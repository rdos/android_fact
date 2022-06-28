package ru.smartro.worknote.presentation.platform_serve

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.extensions.hideDialog
import ru.smartro.worknote.awORKOLDs.util.PhotoTypeEnum
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.cam.CameraAct

class PlatformServeAct :
    ActNOAbst() {

    private val vm: PlatformServeSharedViewModel by viewModels()
    private var mBackPressedCnt: Int = 3

    private var btnCompleteTask: AppCompatButton? = null
    private var tvContainersProgress: TextView? = null
    private var actvAddress: AppCompatTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_platformserve)
        supportActionBar?.hide()

        tvContainersProgress = findViewById(R.id.tv_platform_serve__cont_progress)
        btnCompleteTask = findViewById(R.id.acb_activity_platform_serve__complete)
        actvAddress = findViewById(R.id.tv_platform_serve__address)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        vm.screenMode.observe(this) { screenMode ->
            if(screenMode != null) {
                navController.currentDestination?.apply {
                    when(screenMode) {
                        false -> {
                            if (id != R.id.extendedServeFragment) {
                                navController.popBackStack()
                            }
                            findViewById<TextView>(R.id.screen_mode_label).text = "Расширенный режим"
                        }
                        true -> {
                            if (id != R.id.simplifiedServeFragment) {
                                navController.navigate(R.id.simplifiedServeFragment)
                            }
                            findViewById<TextView>(R.id.screen_mode_label).text = "Упрощенный режим"
                        }
                    }
                }
            }
        }

        findViewById<SwitchCompat>(R.id.switch_mode).setOnCheckedChangeListener { buttonView, isChecked ->
            vm.changeScreenMode()
        }

        vm.platformEntity.observe(this) { platform ->
            if(platform != null) {

                if(vm.wasAskedForPhoto.value == false) {
                    initBeforeMedia(platform.platformId!!)
                    vm.wasAskedForPhoto.postValue(true)
                }

                tvContainersProgress?.text =
                    "№${platform.srpId} / ${platform.containers.size} конт."

                btnCompleteTask?.setOnClickListener {
                    vm.updatePlatformStatusSuccess(platform.platformId!!)
                    val intent = Intent(this, CameraAct::class.java)
                    intent.putExtra("platform_id", platform.platformId)
                    intent.putExtra("photoFor", PhotoTypeEnum.forAfterMedia)
                    startActivityForResult(intent, 13)
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

    private fun initBeforeMedia(platformId: Int) {
        val intent = Intent(this, CameraAct::class.java)
        intent.putExtra("platform_id", platformId)
        intent.putExtra("photoFor", PhotoTypeEnum.forBeforeMedia)
        hideDialog()
        startActivityForResult(intent, 1001)
    }

    override fun onBackPressed() {
        mBackPressedCnt--
        if (mBackPressedCnt <= 0) {
            super.onBackPressed()
            vm.updatePlatformStatusUnfinished()
            toast("Вы не завершили обслуживание КП.")
            return
        }
        toast("Вы не завершили обслуживание КП. Нажмите ещё раз, чтобы выйти")
    }
}
