package ru.smartro.worknote.work.platform_serve

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.Inull
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.extensions.hideDialog
import ru.smartro.worknote.awORKOLDs.extensions.showDialogFillKgoVolume
import ru.smartro.worknote.awORKOLDs.extensions.showDlgPickup
import ru.smartro.worknote.awORKOLDs.extensions.toast
import ru.smartro.worknote.work.ui.CameraAct
import ru.smartro.worknote.work.ui.PlatformFailureAct
import ru.smartro.worknote.awORKOLDs.util.PhotoTypeEnum
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.PlatformEntity


class PlatformServeAct : ActNOAbst(), ContainerAdapter.ContainerPointClickListener
//    , SeekBar.OnSeekBarChangeListener
{
    private val THUMB_INACTIVE = "Inactive"
    private val THUMB_ACTIVE = "Active"
    private var prevVolumeValue: Double? = null
    private var newVolumeValue: Double? = null
    private var mVolumePickup: Double? = null
    private var acbPickup: AppCompatButton? = null
    private var tvVolumePickup: TextView? = null
    private var mBackPressedCnt: Int = 3
    private val REQUEST_EXIT = 33
    private lateinit var mPlatformEntity: PlatformEntity
    private lateinit var mConrainerAdapter: ContainerAdapter
    private var mIsServeAgain: Boolean = false
    private val vm: PlatformServeViewModel by viewModel()
    private lateinit var btnCompleteTask: AppCompatButton

    // TODO: 14.01.2022 r_dos))
    private lateinit var mRemainingKGOVolumeText: String
    private lateinit var mServedKGOVolumeText: String
    private lateinit var mAcbKGORemaining: AppCompatButton
    private lateinit var mAcbKGOServed: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_platformserve)
        supportActionBar?.hide()

        mPlatformEntity = vm.baseDat.findPlatformEntity(intent.getIntExtra("platform_id", Inull))
        mIsServeAgain = intent.getBooleanExtra("mIsServeAgain", false)

//        supportActionBar?.title =
        val actvAddress = findViewById<AppCompatTextView>(R.id.actv_act_platform_serve__address)
        actvAddress.text = "${mPlatformEntity.address}"
        initContainer()
//        todo/vlad: initBeforeMedia logic
        initBeforeMedia()
//        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        val acbProblem = findViewById<AppCompatButton>(R.id.acb_activity_platform_serve__problem)
        if (mPlatformEntity.failureMedia.size > 0) {
            setUseButtonStyleBackgroundRed(acbProblem)
        }
        acbProblem.setOnClickListener {
            val intent = Intent(this, PlatformFailureAct::class.java)
            intent.putExtra("platform_id", mPlatformEntity.platformId)
            intent.putExtra("isContainerProblem", false)
            startActivityForResult(intent, REQUEST_EXIT)
        }

        /** COPY PAST START*/     /** COPY PAST START*/   /** COPY PAST START*/ /** COPY PAST START*/
        /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/
        /** COPY PAST r_dos!!!*/


        mAcbKGOServed = findViewById(R.id.acb_activity_platform_serve__kgo_served)
        if (mPlatformEntity.isServedKGONotEmpty()) {
            setUseButtonStyleBackgroundGreen(mAcbKGOServed)
        }
        mAcbKGOServed.setOnClickListener {
            showDialogFillKgoVolume().let { view ->
                val tietKGOVolumeIn = view.findViewById<TextInputEditText>(R.id.kgo_volume_in)
                tietKGOVolumeIn.setText(mPlatformEntity.getServedKGOVolume())
                val btnSave = view.findViewById<Button>(R.id.btn_alert_kgo__save)
                btnSave.setOnClickListener {
                    mServedKGOVolumeText = tietKGOVolumeIn.text.toString()
                    if (mServedKGOVolumeText.isNullOrBlank()) {
                        return@setOnClickListener
                    }

                    val intent = Intent(this@PlatformServeAct, CameraAct::class.java)
                    intent.putExtra("platform_id", mPlatformEntity.platformId)
                    intent.putExtra("photoFor", PhotoTypeEnum.forServedKGO)
                    startActivityForResult(intent, 101)
                    hideDialog()
                }
            }
        }

        /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/
        /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/

        mAcbKGORemaining = findViewById(R.id.apb_activity_platform_serve__kgo_remaining)

        if (mPlatformEntity.isRemainingKGONotEmpty()) {
            setUseButtonStyleBackgroundGreen(mAcbKGORemaining)
        }
        mAcbKGORemaining.setOnClickListener {
            showDialogFillKgoVolume().let { view ->
                val tietKGOVolumeIn = view.findViewById<TextInputEditText>(R.id.kgo_volume_in)
                tietKGOVolumeIn.setText(mPlatformEntity.getRemainingKGOVolume())
                val btnSave = view.findViewById<Button>(R.id.btn_alert_kgo__save)
                btnSave.setOnClickListener{
                    mRemainingKGOVolumeText = tietKGOVolumeIn.text.toString()
                    if (mRemainingKGOVolumeText.isNullOrBlank()) {
                        return@setOnClickListener
                    }
                    val intent = Intent(this@PlatformServeAct, CameraAct::class.java)
                    intent.putExtra("platform_id", mPlatformEntity.platformId)
                    intent.putExtra("photoFor", PhotoTypeEnum.forRemainingKGO)
                    startActivityForResult(intent, 102)
                    hideDialog()
                }
            }
        }
        /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/
        /** COPY PAST r_dos!!!*/
        /** COPY PAST END*/     /** COPY PAST END*/   /** COPY PAST END*/ /** COPY PAST END*/


        btnCompleteTask = findViewById(R.id.acb_activity_platform_serve__complete)
        btnCompleteTask.setOnClickListener {
            vm.updatePlatformStatusSuccess(mPlatformEntity.platformId!!)
            val intent = Intent(this@PlatformServeAct, CameraAct::class.java)
            intent.putExtra("platform_id", mPlatformEntity.platformId!!)
            intent.putExtra("photoFor", PhotoTypeEnum.forAfterMedia)
            startActivityForResult(intent, 13)
        }

        if (mPlatformEntity.containers.size >= 7 ) {
            actvAddress.setOnClickListener { view ->
//            v.get
//            view = cast()
//            actvAddress.maxLines = if (actvAddress.maxLines > 0)  1 else 3 /** \ */
                if (actvAddress.maxLines < 3) {
                    actvAddress.maxLines = 3
                } else {
                    actvAddress.maxLines = 1
                }
            }
        } else {
            actvAddress.maxLines = 3
        }

        /** VOLUME PICKUP
         *
         * */

        val acsbVolumePickup = findViewById<SeekBar>(R.id.acsb_activity_platform_serve__seekbar)
        acsbVolumePickup.thumb = getThumb()
        if (mPlatformEntity.isPickupNotEmpty()) {
            acsbVolumePickup.progress = acsbVolumePickup.max
            tvVolumePickuptext(mPlatformEntity.volumePickup)
        }
        //todo: r_dos seekBar ws acsbVolumePickup
        acsbVolumePickup.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            private var mProgressAtStartTracking = 0
            private val SENSITIVITY = 1
            override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) {
                tvVolumePickuptext(progress)
                if(progress > 0 && acsbVolumePickup.tag != THUMB_ACTIVE){
                    acsbVolumePickup.thumb = getThumb(R.drawable.bg_button_green__usebutton)
                    acsbVolumePickup.tag = THUMB_ACTIVE
                } else if(progress <= 0 && acsbVolumePickup.tag != THUMB_INACTIVE) {
                    acsbVolumePickup.thumb = getThumb()
                    acsbVolumePickup.tag = THUMB_INACTIVE
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
                    onClickPickup(acsbVolumePickup)
                    return
                }

                if (acsbVolumePickup.progress > 0 ) {
                    newVolumeValue = acsbVolumePickup.progress.toDouble()
                    gotoMakePhotoForPickup()
                } else {
                    vm.updateSelectionVolume(mPlatformEntity.platformId!!, null)
                    prevVolumeValue = null
                }
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult.requestCode=${requestCode}")
        Log.d(TAG, "onActivityResult.resultCode=${resultCode}")
        if (requestCode == 13 && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        } else if (requestCode == REQUEST_EXIT) {
            if (resultCode == 99) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        } else if (resultCode == 101 && requestCode == 101) {
            vm.updatePlatformKGO(mPlatformEntity.platformId!!, mServedKGOVolumeText, isServedKGO = true)
            setUseButtonStyleBackgroundGreen(mAcbKGOServed)
        } else if (resultCode == 102 && requestCode == 102) {
            vm.updatePlatformKGO(mPlatformEntity.platformId!!, mRemainingKGOVolumeText, isServedKGO = false)
            setUseButtonStyleBackgroundGreen(mAcbKGORemaining)
        } else if (requestCode == 14 && resultCode == 404) {
            val acsbVolumePickup = findViewById<SeekBar>(R.id.acsb_activity_platform_serve__seekbar)
            acsbVolumePickup.progress = prevVolumeValue?.toInt() ?: 0
            vm.updateSelectionVolume(mPlatformEntity.platformId!!, prevVolumeValue)
            tvVolumePickuptext(prevVolumeValue)
        } else if (requestCode == 14 && resultCode == 14) {
            val acsbVolumePickup = findViewById<SeekBar>(R.id.acsb_activity_platform_serve__seekbar)
            vm.updateSelectionVolume(mPlatformEntity.platformId!!, newVolumeValue)
            acsbVolumePickup.progress = newVolumeValue?.toInt() ?: (prevVolumeValue?.toInt() ?: 0)
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
                    hideDialog()
                }
            }
        } finally {
            acsbVolumePickup.isEnabled = true
        }
    }

    private fun gotoMakePhotoForPickup() {
        val intent = Intent(this@PlatformServeAct, CameraAct::class.java)
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

        tvVolumePickup().text = "$progressText м³"
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
        val thumbView: View = LayoutInflater.from(this)
            .inflate(R.layout.act_platformserve__pickup_seekbarthumb, null, false)
        if(background != null)
            thumbView
                .findViewById<AppCompatButton>(R.id.acb_act_platformserve__pickup_seekbarthumb)
                .setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_button_green__usebutton))
//        (thumbView.findViewById(R.id.tvProgress) as TextView).text = progress.toString() + ""
        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(thumbView.measuredWidth, thumbView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        thumbView.layout(0, 0, thumbView.measuredWidth, thumbView.measuredHeight)
        thumbView.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }

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
//    }

    private fun tvVolumePickup(): TextView {
        if (tvVolumePickup == null) {
            tvVolumePickup = findViewById(R.id.et_act_platformserve__volumepickup)
            if (tvVolumePickup == null) {
                tvVolumePickup = TextView(this)
            }
        }
        return tvVolumePickup!!
    }


    private fun setUseButtonStyleBackgroundGreen(appCompatButton: AppCompatButton) {
//        appCompatButton.alpha = 1f
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_button_green__usebutton))
    }

    private fun setStyleBackgroundGreen(appCompatButton: AppCompatButton) {
//        appCompatButton.alpha = 1f
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_button_green__default))
    }


    private fun setUseButtonStyleBackgroundRed(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_button_red__usebutton))
    }

//    private fun setButtonKGODrawableEnd(isTakeawayKGO: Boolean) {
//        if (isTakeawayKGO) {
//            btnKGO.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_check) , null)
//        } else {
//            btnKGO.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_check_gray) , null)
//        }
//    }

    private fun initBeforeMedia() {
        paramS().serviceStartedAt = System.currentTimeMillis() / 1000L
        val intent = Intent(this@PlatformServeAct, CameraAct::class.java)
        intent.putExtra("platform_id", mPlatformEntity.platformId)
        intent.putExtra("photoFor", PhotoTypeEnum.forBeforeMedia)
        startActivityForResult(intent, 1001)
        hideDialog()
    }

//    // TODO: 28.10.2021 isActiveToday что это за поле?
//    private fun initAfterMedia() {
//        val platformEntity = mViewModel.findPlatformEntity(mPlatformEntity.platformId!!)
//        btnCompleteTask.isEnabled = platformEntity.containers.filter {
//            it.isActiveToday == true
//        }.all { it.status != StatusEnum.NEW }
//    }

    private fun initContainer() {
        val containers = vm.findAllContainerInPlatform(mPlatformEntity.platformId!!)
        Log.d("TEST :::", "INIT containers: ${containers.joinToString(separator = "\n") { el -> "{ ${el.containerId} : ${el.typeName} : ${el.status} }" }}")
        mConrainerAdapter = ContainerAdapter(this, containers as ArrayList<ContainerEntity>)
        findViewById<RecyclerView>(R.id.rv_activity_platform_serve).recycledViewPool.setMaxRecycledViews(0, 0);
        findViewById<RecyclerView>(R.id.rv_activity_platform_serve).adapter = mConrainerAdapter
        findViewById<TextView>(R.id.tv_activity_platform_serve__point_info).text = "№${mPlatformEntity.srpId} / ${mPlatformEntity.containers!!.size} конт."
    }

    fun updateRecyclerview() {
        val containers = vm.findAllContainerInPlatform(mPlatformEntity.platformId!!)
        Log.d("TEST :::", "UPDATE containers: ${containers.joinToString(separator = "\n") { el -> "{ ${el.containerId} : ${el.typeName} : ${el.status} }" }}")
        mConrainerAdapter.updateData(containers as ArrayList<ContainerEntity>)
//        initAfterMedia()
    }

    override fun startContainerService(item: ContainerEntity) {
        val fragment = ContainerServiceFragment()
        fragment.addArgument(mPlatformEntity.platformId!!, item.containerId!!)
        fragment.show(supportFragmentManager, "ContainerServiceFragment")
    }

    override fun onBackPressed() {
        mBackPressedCnt--
        if (mBackPressedCnt <= 0) {
            super.onBackPressed()
            vm.updatePlatformStatusUnfinished(mPlatformEntity.platformId!!)
            toast("Вы не завершили обслуживание КП.")
            return
        }
        toast("Вы не завершили обслуживание КП. Нажмите ещё раз, чтобы выйти")
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    override fun onClick(buttonView: View) {
//        Log.d(TAG, "onClick.before id=${buttonView.id}")
//
//        when(buttonView.id) {
//            R.id.btn_alert_kgo__takeaway -> {
//                onAlertButtonKgoClick(buttonView.rootView, true)
//                setButtonKGODrawableEnd(true)
//            }
//            R.id.btn_alert_kgo__no_takeaway -> {
//                onAlertButtonKgoClick(buttonView.rootView,false)
//                setButtonKGODrawableEnd(false)
//            }
//        }
//
//    }

}
