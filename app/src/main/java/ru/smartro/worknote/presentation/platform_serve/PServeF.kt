package ru.smartro.worknote.presentation.platform_serve

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import ru.smartro.worknote.LoG
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.awORKOLDs.extensions.hideDialog
import ru.smartro.worknote.awORKOLDs.extensions.showDialogFillKgoVolume
import ru.smartro.worknote.awORKOLDs.extensions.showDlgPickup
import ru.smartro.worknote.log
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.PlatformEntity


class PServeF :
    AFragment(),
    PServeAdapter.ContainerPointClickListener {

    private var mBackPressedCnt: Int = 2

    private val THUMB_INACTIVE = "Inactive"
    private val THUMB_ACTIVE = "Active"
    private lateinit var mContainerAdapter: PServeAdapter
    private var mVolumePickup: Double? = null
    private var tvVolumePickup: TextView? = null
    private var acbKGORemaining: AppCompatButton? = null
    private var mAcbKGOServed: AppCompatButton? = null
    private var acbProblem: AppCompatButton? = null
    private var recyclerView: RecyclerView? = null
    private var acsbVolumePickup: SeekBar? = null

    private var btnCompleteTask: AppCompatButton? = null
    private var tvPlatformSrpId: TextView? = null
    private var actvAddress: AppCompatTextView? = null
    private var scScreenMode: SwitchCompat? = null

    private val vm: ServePlatformVM by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.f_pserve
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val plId = getArgumentID()
        if (savedInstanceState == null) {
            log("savedInstanceState == null")
        } else {
            log("savedInstanceState HE null")
        }

        tvPlatformSrpId = view.findViewById(R.id.tv_f_pserve__sprid)

        btnCompleteTask = view.findViewById(R.id.acb_activity_platform_serve__complete)
        actvAddress = view.findViewById(R.id.tv_platform_serve__address)
        scScreenMode = view.findViewById(R.id.sc_screen_mode)

        tvVolumePickup = view.findViewById(R.id.et_act_platformserve__volumepickup)
        recyclerView = view.findViewById<RecyclerView?>(R.id.rv_activity_platform_serve).apply {
            recycledViewPool.setMaxRecycledViews(0, 0)
        }
        acbProblem = view.findViewById(R.id.acb_activity_platform_serve__problem)
        mAcbKGOServed = view.findViewById(R.id.acb_activity_platform_serve__kgo_served)
        acbKGORemaining = view.findViewById(R.id.apb_activity_platform_serve__kgo_remaining)
        acsbVolumePickup = view.findViewById(R.id.acsb_activity_platform_serve__seekbar)





//        TODO::: 15.08.2022 18:14 CHECK ABOVE THEN DELETE THIS IF NECESSARY
//        scPServeSimplifyMode?.isFocusable = false
        scScreenMode?.setOnClickListener {
            val newScreenMode = !paramS().lastScreenMode
            paramS().lastScreenMode = newScreenMode
            navigateMain(R.id.PServeByTypesF, plId)
        }
        

        val platformEntity = vm.getPlatformEntity()
        tvPlatformSrpId?.text =
            "№${platformEntity.srpId} / ${platformEntity.containers.size} конт."

        btnCompleteTask?.setOnClickListener {
            navigateMain(R.id.PhotoAfterMediaF, platformEntity.platformId!!)
        }

        actvAddress?.text = "${platformEntity.address}"
        if (platformEntity.containers.size >= 7 ) {
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

        mContainerAdapter = PServeAdapter(
            getAct(),
            this,
            platformEntity.containers.sortedBy { !it.isActiveToday }
        )
        recyclerView?.adapter = mContainerAdapter

        if (platformEntity.failureMedia.size > 0) {
            acbProblem?.let { setUseButtonStyleBackgroundRed(it) }
        }
        acbProblem?.setOnClickListener {
            navigateMain(R.id.PhotoFailureMediaF, platformEntity.platformId)
        }
        if (platformEntity.isServedKGONotEmpty()) {
            mAcbKGOServed?.let { setUseButtonStyleBackgroundGreen(it) }
        }
        mAcbKGOServed?.setOnClickListener {
            showDialogFillKgoVolume().let { view ->
                val tietKGOVolumeIn = view.findViewById<TextInputEditText>(R.id.kgo_volume_in)
                tietKGOVolumeIn.setText(platformEntity.getServedKGOVolume())
                val btnSave = view.findViewById<Button>(R.id.btn_alert_kgo__save)
                btnSave.setOnClickListener {
                    val servedKGOVolumeText = tietKGOVolumeIn.text.toString()
                    if (servedKGOVolumeText.isNullOrBlank()) {
                        return@setOnClickListener
                    }
                    navigateMain(R.id.PhotoKgoServedF, platformEntity.platformId, servedKGOVolumeText)
                    hideDialog()
                }
            }
        }
        if (platformEntity.isRemainingKGONotEmpty()) {
            acbKGORemaining?.let { setUseButtonStyleBackgroundGreen(it) }
        }

        acbKGORemaining?.setOnClickListener {
            showDialogFillKgoVolume().let { view ->
                val tietKGOVolumeIn = view.findViewById<TextInputEditText>(R.id.kgo_volume_in)
                tietKGOVolumeIn.setText(platformEntity.getRemainingKGOVolume())
                val btnSave = view.findViewById<Button>(R.id.btn_alert_kgo__save)
                btnSave.setOnClickListener{
                    val remainingKGOVolumeText = tietKGOVolumeIn.text.toString()
                    if (remainingKGOVolumeText.isNullOrBlank()) {
                        return@setOnClickListener
                    }
                    navigateMain(R.id.PhotoKgoRemainingF, platformEntity.platformId, remainingKGOVolumeText)
                    hideDialog()
                }
            }
        }
        if (platformEntity.isPickupNotEmpty()) {
            acsbVolumePickup?.progress = platformEntity.volumePickup!!.toInt()
            tvVolumePickuptext(platformEntity.volumePickup)
            acsbVolumePickup?.thumb = getThumb(R.drawable.bg_button_green__usebutton)
        } else {
            acsbVolumePickup?.thumb = getThumb(null)
        }
        acsbVolumePickup?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            private var mProgressAtStartTracking = 0
            private val SENSITIVITY = 1
            override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) {
                tvVolumePickuptext(progress)
                if(progress > 0 && tag != THUMB_ACTIVE){
                    acsbVolumePickup?.thumb = getThumb(R.drawable.bg_button_green__usebutton)
                    acsbVolumePickup?.tag = THUMB_ACTIVE
                } else if(progress <= 0 && tag != THUMB_INACTIVE) {
                    acsbVolumePickup?.thumb = getThumb(null)
                    acsbVolumePickup?.tag = THUMB_INACTIVE
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mProgressAtStartTracking = seekBar!!.progress
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if(Math.abs(mProgressAtStartTracking - seekBar!!.progress) <= SENSITIVITY){
                    acsbVolumePickup?.apply {
                        onClickPickup(this)
                    }
                    return
                }

                acsbVolumePickup?.apply {
                    if (progress > 0 ) {
                        gotoMakePhotoForPickup(progress.toDouble())
                    } else {
                        vm.updateVolumePickup(platformEntity.platformId!!, null)
                        vm.updateVolumePickup(platformEntity.platformId!!, null)
                    }
                }

            }
        })
    }


    private fun onClickPickup(acsbVolumePickup: SeekBar) {
        acsbVolumePickup.isEnabled = false
        try {
            showDlgPickup().let{ dialogView ->
                val tietAdditionalVolumeInM3 = dialogView.findViewById<TextInputEditText>(R.id.tiet_alert_additional_volume_container)
                vm.getPlatformEntity().volumePickup?.let{
                    tietAdditionalVolumeInM3.setText(vm.getPlatformEntity().volumePickup.toString())
                }

                val btnOk = dialogView.findViewById<Button>(R.id.btn_alert_additional_volume_container__ok)
                btnOk.setOnClickListener {
                    hideDialog()
                    val volume = tietAdditionalVolumeInM3.text.toString().toDoubleOrNull()
                    if (volume == null) {
                        acsbVolumePickup.progress = 0
                    } else {
                        gotoMakePhotoForPickup(volume)
                    }
                }
            }
        } finally {
            acsbVolumePickup.isEnabled = true
        }
    }

    private fun gotoMakePhotoForPickup(newVolume: Double) {
        navigateMain(R.id.PhotoPickupMediaF, vm.getPlatformId(), newVolume.toString())
    }

    private fun tvVolumePickuptext(progressDouble: Double?) {
        val progressText = if (progressDouble != null)
            String.format("%.1f", progressDouble)
        else
            String.format("%.1f", 0.0)

        tvVolumePickup?.text = "$progressText м³"
        mVolumePickup = progressDouble
    }

    private fun tvVolumePickuptext(progress: Int) {
        if (progress < 0) {
            tvVolumePickuptext(0.0)
            return
        }
        tvVolumePickuptext(progress.toDouble())
    }

    private fun getThumb(background: Int? = null): Drawable {
        val thumbView: View = LayoutInflater.from(getAct())
            .inflate(R.layout.act_platformserve__pickup_seekbarthumb, null, false)
        if(background != null)
            thumbView
                .findViewById<AppCompatButton>(R.id.acb_act_platformserve__pickup_seekbarthumb)
                .setBackgroundDrawable(ContextCompat.getDrawable(getAct(), R.drawable.bg_button_green__usebutton))
        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(thumbView.measuredWidth, thumbView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        thumbView.layout(0, 0, thumbView.measuredWidth, thumbView.measuredHeight)
        thumbView.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }

    private fun setUseButtonStyleBackgroundGreen(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(getAct(), R.drawable.bg_button_green__usebutton))
    }

    private fun setUseButtonStyleBackgroundRed(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(getAct(), R.drawable.bg_button_red__usebutton))
    }

    override fun startContainerService(item: ContainerEntity) {
        navigateMain(R.id.ContainerServeBottomDialog, item.containerId!!, vm.getPlatformId().toString())
    }

    override fun onBackPressed() {
        mBackPressedCnt--
        if (mBackPressedCnt <= 0) {
            vm.updatePlatformStatusUnfinished()
            navigateBack(R.id.MapF)
            toast("Вы не завершили обслуживание КП.")
        } else {
            toast("Вы не завершили обслуживание КП. Нажмите ещё раз, чтобы выйти")
        }
    }


    /********************************************************************************************************************
     ********************************************************************************************************************
     ********************************************************************************************************************
     * **************************************VIEW MODEL
     */
//
//    class PServeFVM(app: Application) : AViewModel(app) {
//
//        private val _platformEntity: MutableLiveData<PlatformEntity> = MutableLiveData(null)
//        val mPlatformEntity: LiveData<PlatformEntity>
//            get() = _platformEntity
//
//
//
//
//    }
}