package ru.smartro.worknote.ui.platform_serve

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
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import ru.smartro.worknote.*
import ru.smartro.worknote.awORKOLDs.extensions.hideDialog
import ru.smartro.worknote.awORKOLDs.extensions.showDialogFillKgoVolume
import ru.smartro.worknote.awORKOLDs.extensions.showDlgPickup
import ru.smartro.worknote.awORKOLDs.util.PhotoTypeEnum
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.cam.CameraAct
import ru.smartro.worknote.work.ui.PlatformFailureAct


class ExtendedServeFragment :
    AFragment(),
    ContainerAdapter.ContainerPointClickListener {

    private val THUMB_INACTIVE = "Inactive"
    private val THUMB_ACTIVE = "Active"
    private val REQUEST_EXIT = 33
    private lateinit var mPlatformEntity: PlatformEntity
    private lateinit var mConrainerAdapter: ContainerAdapter
    private val vm: PlatformServeSharedViewModel by activityViewModels()

    // TODO: 14.01.2022 r_dos))
    private lateinit var mRemainingKGOVolumeText: String
    private lateinit var mServedKGOVolumeText: String

    private var prevVolumeValue: Double? = null
    private var newVolumeValue: Double? = null
    private var mVolumePickup: Double? = null
    private var acbPickup: AppCompatButton? = null
    private var tvVolumePickup: TextView? = null
    private var mAcbKGORemaining: AppCompatButton? = null
    private var mAcbKGOServed: AppCompatButton? = null
    private var acbProblem: AppCompatButton? = null
    private var recyclerView: RecyclerView? = null
    private var acsbVolumePickup: SeekBar? = null

    //todo: ответсвенность)
//    private fun acbPickup(): AppCompatButton {
//            //        etVolumePickup.let {
//            //                // mEtVolumePickup = findViewById(R.id.et_act_platformserve__volumepickup)
//            //            //  lateinit  Vs componentName(acbTest????)
//            //        }
//        if (acbPickup == null) {
//            acbPickup = thumbView?.findViewById(R.id.acb_act_platformserve__pickup_seekbarthumb)
//            if (acbPickup == null) {
//                acbPickup = AppCompatButton(this)
//            }
//        }
//        return acbPickup!!
    private fun initBeforeMedia() {
        val intent = Intent(getAct(), CameraAct::class.java)
        intent.putExtra("platform_id", mPlatformEntity.platformId)
        intent.putExtra("photoFor", PhotoTypeEnum.forBeforeMedia)
        hideDialog()
        startActivityForResult(intent, 1001)
    }

    override fun onGetLayout(): Int {
        return R.layout.fragment_platform_serve_extended
    }

    override fun onPause() {
        super.onPause()
        Log.d("TEST :::", "LC:::ExtendedServeFrag/onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d("TEST :::", "LC:::ExtendedServeFrag/onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TEST :::", "LC:::ExtendedServeFrag/onDestroy")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("TEST :::", "LC:::ExtendedServeFrag/onDestroyView")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TEST :::", "LC:::ExtendedServeFrag/onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("TEST :::", "LC:::ExtendedServeFrag/onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TEST :::", "LC:::ExtendedServeFrag/onViewCreated")
        val plId = getAct().intent.getIntExtra("platform_id", Inull)
        Log.d("TEST :::", "ExtendedServeFrag/onViewCreated: platformId= ${plId}")
        mPlatformEntity = vm.getPlatformEntity(plId)
        tvVolumePickup = view.findViewById(R.id.et_act_platformserve__volumepickup)
        recyclerView = view.findViewById(R.id.rv_activity_platform_serve)

        initContainer()
//        todo/vlad: initBeforeMedia logic
        initBeforeMedia()

        acbProblem = view.findViewById<AppCompatButton?>(R.id.acb_activity_platform_serve__problem).apply {
            if (mPlatformEntity.failureMedia.size > 0) {
                setUseButtonStyleBackgroundRed(this)
            }
            setOnClickListener {
                val intent = Intent(getAct(), PlatformFailureAct::class.java)
                intent.putExtra("platform_id", mPlatformEntity.platformId)
                intent.putExtra("isContainerProblem", false)
                startActivityForResult(intent, REQUEST_EXIT)
            }
        }

        /** COPY PAST START*/     /** COPY PAST START*/   /** COPY PAST START*/ /** COPY PAST START*/
        /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/
        /** COPY PAST r_dos!!!*/

        mAcbKGOServed = view.findViewById<AppCompatButton?>(R.id.acb_activity_platform_serve__kgo_served).apply {
            if (mPlatformEntity.isServedKGONotEmpty()) {
                setUseButtonStyleBackgroundGreen(this)
            }
            setOnClickListener {
                showDialogFillKgoVolume().let { view ->
                    val tietKGOVolumeIn = view.findViewById<TextInputEditText>(R.id.kgo_volume_in)
                    tietKGOVolumeIn.setText(mPlatformEntity.getServedKGOVolume())
                    val btnSave = view.findViewById<Button>(R.id.btn_alert_kgo__save)
                    btnSave.setOnClickListener {
                        mServedKGOVolumeText = tietKGOVolumeIn.text.toString()
                        if (mServedKGOVolumeText.isNullOrBlank()) {
                            return@setOnClickListener
                        }

                        val intent = Intent(getAct(), CameraAct::class.java)
                        intent.putExtra("platform_id", mPlatformEntity.platformId)
                        intent.putExtra("photoFor", PhotoTypeEnum.forServedKGO)
                        startActivityForResult(intent, 101)
                        hideDialog()
                    }
                }
            }
        }


        /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/
        /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/

        mAcbKGORemaining = view.findViewById<AppCompatButton?>(R.id.apb_activity_platform_serve__kgo_remaining).apply {
            if (mPlatformEntity.isRemainingKGONotEmpty()) {
                setUseButtonStyleBackgroundGreen(this)
            }
            setOnClickListener {
                showDialogFillKgoVolume().let { view ->
                    val tietKGOVolumeIn = view.findViewById<TextInputEditText>(R.id.kgo_volume_in)
                    tietKGOVolumeIn.setText(mPlatformEntity.getRemainingKGOVolume())
                    val btnSave = view.findViewById<Button>(R.id.btn_alert_kgo__save)
                    btnSave.setOnClickListener{
                        mRemainingKGOVolumeText = tietKGOVolumeIn.text.toString()
                        if (mRemainingKGOVolumeText.isNullOrBlank()) {
                            return@setOnClickListener
                        }
                        val intent = Intent(getAct(), CameraAct::class.java)
                        intent.putExtra("platform_id", mPlatformEntity.platformId)
                        intent.putExtra("photoFor", PhotoTypeEnum.forRemainingKGO)
                        startActivityForResult(intent, 102)
                        hideDialog()
                    }
                }
            }
        }
        /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/
        /** COPY PAST r_dos!!!*/
        /** COPY PAST END*/     /** COPY PAST END*/   /** COPY PAST END*/ /** COPY PAST END*/




        /** VOLUME PICKUP
         *
         * */

        acsbVolumePickup = view.findViewById<SeekBar?>(R.id.acsb_activity_platform_serve__seekbar).apply {
            thumb = getThumb(null)
            if (mPlatformEntity.isPickupNotEmpty()) {
                progress = max
                tvVolumePickuptext(mPlatformEntity.volumePickup)
            }
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                private var mProgressAtStartTracking = 0
                private val SENSITIVITY = 1
                override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) {
                    tvVolumePickuptext(progress)
                    if(progress > 0 && tag != THUMB_ACTIVE){
                        thumb = getThumb(R.drawable.bg_button_green__usebutton)
                        tag = THUMB_ACTIVE
                    } else if(progress <= 0 && tag != THUMB_INACTIVE) {
                        thumb = getThumb(null)
                        tag = THUMB_INACTIVE
                    }
//                неРазРаб acsbVolumePickup.max - 13
//                if (progress >= acsbVolumePickup.max - 13) {
//                    acsbVolumePickup?.progress = acsbVolumePickup.max - 13
//                }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    mProgressAtStartTracking = seekBar!!.progress
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if(Math.abs(mProgressAtStartTracking - seekBar!!.progress) <= SENSITIVITY){
                        // react to thumb click
                        acsbVolumePickup?.apply {
                            onClickPickup(this)
                        }
                        return
                    }

                    acsbVolumePickup?.apply {
                        if (progress > 0 ) {
                            newVolumeValue = progress.toDouble()
                            gotoMakePhotoForPickup()
                        } else {
                            vm.updateSelectionVolume(mPlatformEntity.platformId!!, null)
                            prevVolumeValue = null
                        }
                    }

                }
            })
        }
        //todo: r_dos seekBar ws acsbVolumePickup
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
        } else if (requestCode == REQUEST_EXIT) {
            if (resultCode == 99) {
                getAct().apply {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        } else if (resultCode == 101 && requestCode == 101) {
            vm.updatePlatformKGO(mPlatformEntity.platformId!!, mServedKGOVolumeText, isServedKGO = true)
            mAcbKGOServed?.let { setUseButtonStyleBackgroundGreen(it) }
        } else if (resultCode == 102 && requestCode == 102) {
            vm.updatePlatformKGO(mPlatformEntity.platformId!!, mRemainingKGOVolumeText, isServedKGO = false)
            mAcbKGORemaining?.let { setUseButtonStyleBackgroundGreen(it) }
        } else if (requestCode == 14 && resultCode == 404) {
            acsbVolumePickup?.progress = prevVolumeValue?.toInt() ?: 0
            vm.updateSelectionVolume(mPlatformEntity.platformId!!, prevVolumeValue)
            tvVolumePickuptext(prevVolumeValue)
        } else if (requestCode == 14 && resultCode == 14) {
            vm.updateSelectionVolume(mPlatformEntity.platformId!!, newVolumeValue)
            acsbVolumePickup?.progress = newVolumeValue?.toInt() ?: (prevVolumeValue?.toInt() ?: 0)
            prevVolumeValue = newVolumeValue
            tvVolumePickuptext(prevVolumeValue)
        }
    }

    private fun onClickPickup(acsbVolumePickup: SeekBar) {
        acsbVolumePickup.isEnabled = false
        try {
            showDlgPickup().let{ dialogView ->
                val tietAdditionalVolumeInM3 = dialogView.findViewById<TextInputEditText>(R.id.tiet_alert_additional_volume_container)
                mPlatformEntity.volumePickup?.let{
                    tietAdditionalVolumeInM3.setText(mPlatformEntity.volumePickup.toString())
                } //mVolumePickup.isShowForUser()

                val btnOk = dialogView.findViewById<Button>(R.id.btn_alert_additional_volume_container__ok)
                btnOk.setOnClickListener {
                    val volume = tietAdditionalVolumeInM3.text.toString().toDoubleOrNull()
                    vm.updateSelectionVolume(mPlatformEntity.platformId!!, volume)
                    if (volume == null) {
                        acsbVolumePickup.progress = 0
                    } else {
                        newVolumeValue = volume
                        gotoMakePhotoForPickup()
                    }
                }
            }
        } finally {
            acsbVolumePickup.isEnabled = true
        }
    }

    private fun gotoMakePhotoForPickup() {
        val intent = Intent(getAct(), CameraAct::class.java)
        intent.putExtra("platform_id", mPlatformEntity.platformId!!)
        intent.putExtra("photoFor", PhotoTypeEnum.forPlatformPickupVolume)
        startActivityForResult(intent, 14)
    }
    private fun tvVolumePickuptext(progressDouble: Double?) {
//        var progressText = progress.toString()
        val progressText = if (progressDouble != null)
            String.format("%.1f", progressDouble)
        else
            String.format("%.1f", 0.0)

        tvVolumePickup?.text = "$progressText м³"
        mVolumePickup = progressDouble
//
    }

    private fun tvVolumePickuptext(progress: Int) {
        if (progress < 0) {
            tvVolumePickuptext(0.0)
            return
        }
        tvVolumePickuptext(progress.toDouble())
    }

    private fun getThumb(background: Int? = null): Drawable? {
        // todo vlad// probably should replace getAct() with reqContext here idk
        val thumbView: View = LayoutInflater.from(getAct())
            .inflate(R.layout.act_platformserve__pickup_seekbarthumb, null, false)
        if(background != null)
            thumbView
                .findViewById<AppCompatButton>(R.id.acb_act_platformserve__pickup_seekbarthumb)
                .setBackgroundDrawable(ContextCompat.getDrawable(getAct(), R.drawable.bg_button_green__usebutton))
//        (thumbView.findViewById(R.id.tvProgress) as TextView).text = progress.toString() + ""
        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(thumbView.measuredWidth, thumbView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        thumbView.layout(0, 0, thumbView.measuredWidth, thumbView.measuredHeight)
        thumbView.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }

//    }

    private fun setUseButtonStyleBackgroundGreen(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(getAct(), R.drawable.bg_button_green__usebutton))
    }


    private fun setStyleBackgroundGreen(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(getAct(), R.drawable.bg_button_green__default))
    }

    private fun setUseButtonStyleBackgroundRed(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(getAct(), R.drawable.bg_button_red__usebutton))
    }

//    // TODO: 28.10.2021 isActiveToday что это за поле?
//    private fun initAfterMedia() {
//        val platformEntity = mViewModel.findPlatformEntity(mPlatformEntity.platformId!!)
//        btnCompleteTask.isEnabled = platformEntity.containers.filter {
//            it.isActiveToday == true
//        }.all { it.status != StatusEnum.NEW }
//    }

    private fun initContainer() {
        val containers = vm.baseDat.findContainersSortedByIsActiveToday(mPlatformEntity.platformId!!)
        mConrainerAdapter = ContainerAdapter(getAct(), this, (containers) as ArrayList<ContainerEntity>)
        recyclerView?.apply {
            recycledViewPool.setMaxRecycledViews(0, 0)
            adapter = mConrainerAdapter
        }
    }

    fun updateRecyclerview() {
        val containers = vm.baseDat.findContainersSortedByIsActiveToday(mPlatformEntity.platformId!!)
        Log.d("TEST :::: ", "Containers: ${containers.joinToString { el -> "VOLUME: ${el.volume} ::: IS ACTIVTE TODAY: ${el.isActiveToday}" }}")
        mConrainerAdapter.updateData(containers as ArrayList<ContainerEntity>)
//        initAfterMedia()
    }

    override fun startContainerService(item: ContainerEntity) {
        findNavController().navigate(
            ExtendedServeFragmentDirections
                .actionExtendedServeFragmentToContainerServeBottomDialog(
                    item.containerId!!
                )
        )
    }

}