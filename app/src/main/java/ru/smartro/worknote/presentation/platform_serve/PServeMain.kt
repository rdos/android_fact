package ru.smartro.worknote.presentation.platform_serve

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.Inull
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.extensions.hideDialog
import ru.smartro.worknote.awORKOLDs.util.PhotoTypeEnum
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.cam.CameraAct

class PServeMain :
    ActNOAbst() {

    private val vm: PlatformServeSharedViewModel by viewModels()
    private var mBackPressedCnt: Int = 3

    private var btnCompleteTask: AppCompatButton? = null
    private var tvContainersProgress: TextView? = null
    private var actvAddress: AppCompatTextView? = null
    private var switch: SwitchCompat? = null
    private var screenModeLabel: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_platformserve)
        supportActionBar?.hide()

        val plId = intent.getIntExtra("platform_id", Inull)
        vm.getPlatformEntity(plId)

        tvContainersProgress = findViewById(R.id.tv_platform_serve__cont_progress)
        btnCompleteTask = findViewById(R.id.acb_activity_platform_serve__complete)
        actvAddress = findViewById(R.id.tv_platform_serve__address)
        switch = findViewById(R.id.switch_mode)
        screenModeLabel = findViewById(R.id.screen_mode_label)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        switch?.setOnCheckedChangeListener { _, _ ->
            vm.changeScreenMode()
        }

        vm.mWasServedExtended.observe(this) {
            if(it) {
                switch?.visibility = View.GONE
                screenModeLabel?.visibility = View.GONE
            }
        }

        vm.mPlatformEntity.observe(this) { platform ->
            if(platform != null) {
                if(platform.containers.all { el -> el.status == StatusEnum.NEW }) {
                    vm.mScreenMode.observe(this) { screenMode ->
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

                if(vm.mBeforeMediaWasInited.value == false) {
                    initBeforeMedia(platform.platformId!!)
                    vm.mBeforeMediaWasInited.postValue(true)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 13 && resultCode == -1) {
            finish()
        }
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