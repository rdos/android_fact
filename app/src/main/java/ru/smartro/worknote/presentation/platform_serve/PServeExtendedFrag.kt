package ru.smartro.worknote.presentation.platform_serve

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.extensions.hideDialog
import ru.smartro.worknote.awORKOLDs.extensions.showDialogFillKgoVolume
import ru.smartro.worknote.awORKOLDs.extensions.showDlgPickup
import ru.smartro.worknote.presentation.platform_serve.adapters.ExtendedContainerAdapter
import ru.smartro.worknote.work.ContainerEntity


class PServeExtendedFrag :
    AFragment(),
    ExtendedContainerAdapter.ContainerPointClickListener {

    private val THUMB_INACTIVE = "Inactive"
    private val THUMB_ACTIVE = "Active"
    private lateinit var mConrainerAdapter: ExtendedContainerAdapter
    private val vm: PlatformServeSharedViewModel by activityViewModels()
    private var mVolumePickup: Double? = null
    private var tvVolumePickup: TextView? = null
    private var acbKGORemaining: AppCompatButton? = null
    private var mAcbKGOServed: AppCompatButton? = null
    private var acbProblem: AppCompatButton? = null
    private var recyclerView: RecyclerView? = null
    private var acsbVolumePickup: SeekBar? = null

    override fun onGetLayout(): Int {
        return R.layout.fragment_platform_serve_extended
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvVolumePickup = view.findViewById(R.id.et_act_platformserve__volumepickup)
        recyclerView = view.findViewById<RecyclerView?>(R.id.rv_activity_platform_serve).apply {
            recycledViewPool.setMaxRecycledViews(0, 0)
        }
        acbProblem = view.findViewById(R.id.acb_activity_platform_serve__problem)
        mAcbKGOServed = view.findViewById(R.id.acb_activity_platform_serve__kgo_served)
        acbKGORemaining = view.findViewById(R.id.apb_activity_platform_serve__kgo_remaining)
        acsbVolumePickup = view.findViewById<SeekBar?>(R.id.acsb_activity_platform_serve__seekbar)
        vm.mPlatformEntity.observe(viewLifecycleOwner) { platform ->
            if(platform != null) {
                mConrainerAdapter = ExtendedContainerAdapter(
                    getAct(),
                    this,
                    platform.containers.sortedBy { !it.isActiveToday }
                )
                recyclerView?.adapter = mConrainerAdapter

                if (platform.failureMedia.size > 0) {
                    acbProblem?.let { setUseButtonStyleBackgroundRed(it) }
                }
                acbProblem?.setOnClickListener {
//                    val intent = Intent(getAct(), PlatformFailureAct::class.java)
//                    intent.putExtra("platform_id", platform.platformId)
//                    intent.putExtra("isContainerProblem", false)
//                    startActivityForResult(intent, REQUEST_EXIT)
                    navigateMain(R.id.PhotoFailureMediaF, platform.platformId)
                }
                if (platform.isServedKGONotEmpty()) {
                    mAcbKGOServed?.let { setUseButtonStyleBackgroundGreen(it) }
                }
                mAcbKGOServed?.setOnClickListener {
                    showDialogFillKgoVolume().let { view ->
                        val tietKGOVolumeIn = view.findViewById<TextInputEditText>(R.id.kgo_volume_in)
                        tietKGOVolumeIn.setText(platform.getServedKGOVolume())
                        val btnSave = view.findViewById<Button>(R.id.btn_alert_kgo__save)
                        btnSave.setOnClickListener {
                            val servedKGOVolumeText = tietKGOVolumeIn.text.toString()
                            if (servedKGOVolumeText.isNullOrBlank()) {
                                return@setOnClickListener
                            }
//                            intent.putExtra("photoFor", PhotoTypeEnum.forServedKGO)
//                                val intent = Intent(getAct(), CameraAct::class.java)
//                                intent.putExtra("platform_id", platform.platformId)

                            vm.updatePlatformKGO(platform.platformId!!, servedKGOVolumeText, isServedKGO = true)
                            navigateMain(R.id.PhotoKgoServedF, platform.platformId)
//                                startActivityForResult(intent, 101)
                            hideDialog()
                        }
                    }
                }
                if (platform.isRemainingKGONotEmpty()) {
                    acbKGORemaining?.let { setUseButtonStyleBackgroundGreen(it) }
                }

                acbKGORemaining?.setOnClickListener {
                    showDialogFillKgoVolume().let { view ->
                        val tietKGOVolumeIn = view.findViewById<TextInputEditText>(R.id.kgo_volume_in)
                        tietKGOVolumeIn.setText(platform.getRemainingKGOVolume())
                        val btnSave = view.findViewById<Button>(R.id.btn_alert_kgo__save)
                        btnSave.setOnClickListener{
                            val remainingKGOVolumeText = tietKGOVolumeIn.text.toString()
                            if (remainingKGOVolumeText.isNullOrBlank()) {
                                return@setOnClickListener
                            }
//                            val intent = Intent(getAct(), CameraAct::class.java)
//                            intent.putExtra("platform_id", platform.platformId)
//                            intent.putExtra("photoFor", PhotoTypeEnum.forRemainingKGO)
//                            startActivityForResult(intent, 102)
                            vm.updatePlatformKGO(platform.platformId, remainingKGOVolumeText, isServedKGO = false)
                            navigateMain(R.id.PhotoKgoRemainingF, platform.platformId)
                            hideDialog()
                        }
                    }
                }
                if (platform.isPickupNotEmpty()) {
                    acsbVolumePickup?.progress = platform.volumePickup!!.toInt()
                    tvVolumePickuptext(platform.volumePickup)
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
                                vm.updateVolumePickup(platform.platformId!!, null)
                            }
                        }

                    }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult.requestCode=${requestCode}")
        Log.d(TAG, "onActivityResult.resultCode=${resultCode}")
        if (requestCode == 13 && resultCode == Activity.RESULT_OK) {
            getAct().apply {
                setResult(Activity.RESULT_OK)
                finish()
            }
//        } else if (requestCode == REQUEST_EXIT) {
//            if (resultCode == 99) {
//                getAct().apply {
//                    setResult(Activity.RESULT_OK)
//                    finish()
//                }
//            }
        } else {
            val platform = vm.mPlatformEntity.value
            if(platform != null) {
                if (resultCode == 101 && requestCode == 101) {


                } else if (resultCode == 102 && requestCode == 102) {


                } else if (requestCode == 14 && resultCode == 404) {


                } else if (requestCode == 14 && resultCode == 14) {

                }
            }
        }
    }

    private fun onClickPickup(acsbVolumePickup: SeekBar) {
        acsbVolumePickup.isEnabled = false
        try {
            if(vm.mPlatformEntity.value != null) {
                showDlgPickup().let{ dialogView ->
                    val tietAdditionalVolumeInM3 = dialogView.findViewById<TextInputEditText>(R.id.tiet_alert_additional_volume_container)
                    vm.mPlatformEntity.value!!.volumePickup?.let{
                        tietAdditionalVolumeInM3.setText(vm.mPlatformEntity.value!!.volumePickup.toString())
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
            }
        } finally {
            acsbVolumePickup.isEnabled = true
        }
    }

    private fun gotoMakePhotoForPickup(newVolume: Double) {
        if(vm.mPlatformEntity.value == null) {
            return
        }
        navigateMain(R.id.PhotoPickupMediaF, vm.mPlatformEntity.value!!.platformId, newVolume.toString())
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
        findNavController().navigate(PServeExtendedFragDirections
            .actionExtendedServeFragmentToContainerServeBottomDialog(item.containerId!!))
    }
}