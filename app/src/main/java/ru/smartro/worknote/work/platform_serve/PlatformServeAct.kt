package ru.smartro.worknote.work.platform_serve

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.base.AbstractAct
import ru.smartro.worknote.extensions.hideDialog
import ru.smartro.worknote.extensions.showDlgPickup
import ru.smartro.worknote.extensions.showDialogFillKgoVolume
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.simulateClick
import ru.smartro.worknote.work.AppPreferences
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.ui.problem.PlatformFailureAct
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.StatusEnum


class PlatformServeAct : AbstractAct(), ContainerAdapter.ContainerPointClickListener
//    , SeekBar.OnSeekBarChangeListener
{

    private var mVolumePickup: Double? = null
    //    private var mVolumePickup by Delegates.notNull<Int>()
    private var etVolumePickup: AppCompatButton? = null
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 && resultCode == Activity.RESULT_OK) {
            vm.updateContainersVolumeIfnNull(mPlatformEntity.platformId!!, 1.0)
            vm.updatePlatformStatus(mPlatformEntity.platformId!!, StatusEnum.SUCCESS)
            setResult(Activity.RESULT_OK)
            finish()
        } else if (requestCode == REQUEST_EXIT) {
            if (resultCode == 99) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        } else if (resultCode == 101 && requestCode == 101) {
            vm.updatePlatformKGO(mPlatformEntity.platformId!!, mServedKGOVolumeText, isServedKGO = true)
            setUseButtonStyleAlpha(mAcbKGOServed)
        } else if (resultCode == 102 && requestCode == 102) {
            vm.updatePlatformKGO(mPlatformEntity.platformId!!, mRemainingKGOVolumeText, isServedKGO = false)
            setUseButtonStyleAlpha(mAcbKGORemaining)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_platformserve)
        intent.let {
            mPlatformEntity = vm.findPlatformEntity(it.getIntExtra("platform_id", 0))
            mIsServeAgain = it.getBooleanExtra("mIsServeAgain", false)
        }
        supportActionBar?.hide()
//        supportActionBar?.title =
        val actvAddress = findViewById<AppCompatTextView>(R.id.actv_act_platform_serve__address)
        actvAddress.text = "${mPlatformEntity.address}"
        initContainer()
        if (!mPlatformEntity.isStartServeVolume()) {
            initBeforeMedia()
        }
//        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        val acbProblem = findViewById<AppCompatButton>(R.id.acb_activity_platform_serve__problem)
        setNotUseButtonStyleAlpha(acbProblem)
        if (mPlatformEntity.failureMedia.size > 0) {
           setUseButtonStyleAlpha(acbProblem)
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


        mAcbKGOServed = findViewById<AppCompatButton>(R.id.acb_activity_platform_serve__kgo_served)
        setNotUseButtonStyleAlpha(mAcbKGOServed)
        if (mPlatformEntity.isServedKGONotEmpty()) {
            setUseButtonStyleAlpha(mAcbKGOServed)
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
                    val intent = Intent(this@PlatformServeAct, CameraActivity::class.java)
                    intent.putExtra("platform_id", mPlatformEntity.platformId)
                    intent.putExtra("photoFor", PhotoTypeEnum.forServedKGO)
                    startActivityForResult(intent, 101)
                    hideDialog()
                    }
                }
            }

        /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/
            /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/         /** COPY PAST r_dos!!!*/

            mAcbKGORemaining = findViewById<AppCompatButton>(R.id.apb_activity_platform_serve__kgo_remaining)
            setNotUseButtonStyleAlpha(mAcbKGORemaining)
            if (mPlatformEntity.isRemainingKGONotEmpty()) {
                setUseButtonStyleAlpha(mAcbKGORemaining)
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
                        val intent = Intent(this@PlatformServeAct, CameraActivity::class.java)
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
            val intent = Intent(this@PlatformServeAct, CameraActivity::class.java)
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
         *
         * */

        mVolumePickup = mPlatformEntity.volumePickup
        etVolumePickuptext(mVolumePickup)

        val acsbVolubmePickup = findViewById<SeekBar>(R.id.acsb_activity_platform_serve__seekbar)
        acsbVolubmePickup.thumb = getThumb(1);
        mVolumePickup?.let{
            acsbVolubmePickup.progress = it.toInt()
        }
        //todo: r_dos seekBar ws acsbVolubmePickup
        acsbVolubmePickup.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) {
                etVolumePickuptext(progress)
                    //неРазРаб acsbVolubmePickup.max - 13
                if (progress >= acsbVolubmePickup.max - 13) {
                    acsbVolubmePickup?.progress = acsbVolubmePickup.max - 13
                }
                if (progress <= 0) {
                    etVolumePickuptext(progress)
                }
            }

            override fun onStartTrackingTouch(s: SeekBar?) {

            }

            override fun onStopTrackingTouch(s: SeekBar?) {
                if (acsbVolubmePickup.progress > 0 ) {
                    vm.updateSelectionVolume(mPlatformEntity.platformId!!, acsbVolubmePickup.progress.toDouble())
                    gotoMakePhotoForPickup()
                } else {
                    vm.updateSelectionVolume(mPlatformEntity.platformId!!, null)
                }
            }
        })

        etVolumePickup().setOnClickListener {
            etVolumePickup().isEnabled = false
            try {
                showDlgPickup().let{ dialogView ->
                    val tietAdditionalVolumeInM3 = dialogView.findViewById<TextInputEditText>(R.id.tiet_alert_additional_volume_container)
                    mVolumePickup?.let{
                        tietAdditionalVolumeInM3.setText(mVolumePickup.toString())
                    } //mVolumePickup.isShowForUser()

                    val btnOk = dialogView.findViewById<Button>(R.id.btn_alert_additional_volume_container__ok)
                    btnOk.setOnClickListener {
                        val volume = tietAdditionalVolumeInM3.text.toString().toDoubleOrNull()
                        if (volume == null) {
                            acsbVolubmePickup.progress = 0
                        }else /** ?.let*/ {
                            acsbVolubmePickup.progress = acsbVolubmePickup.max - 13
                        }

                        vm.updateSelectionVolume(mPlatformEntity.platformId!!, volume)
                        etVolumePickuptext(volume)
                        if (volume != null) {
                            gotoMakePhotoForPickup()
                        }
                        hideDialog()
                    }
                }
            } finally {
                etVolumePickup().isEnabled = true
            }
        }

        // TODO: r_dos !)!
        etVolumePickup().simulateClick()
    }

    private fun gotoMakePhotoForPickup() {
        val intent = Intent(this@PlatformServeAct, CameraActivity::class.java)
        intent.putExtra("platform_id", mPlatformEntity.platformId!!)
        intent.putExtra("photoFor", PhotoTypeEnum.forPlatformPickupVolume)
        startActivityForResult(intent, 14)
    }



    //todo: ответсвенность)
    private fun etVolumePickup(): AppCompatButton {
            //        etVolumePickup.let {
            //                // mEtVolumePickup = findViewById(R.id.et_act_platformserve__volumepickup)
            //            //  lateinit  Vs componentName(acbTest????)
            //        }
        if (etVolumePickup == null) {
            etVolumePickup = findViewById(R.id.et_act_platformserve__volumepickup)
            if (etVolumePickup == null) {
                etVolumePickup = AppCompatButton(this)
            }
        }
        return etVolumePickup!!
    }


    private fun etVolumePickuptext(progressDouble: Double?) {
//        var progressText = progress.toString()
        var progressText = "м³"
        if (progressDouble != null) {
             progressText = String.format("%.1f", progressDouble)
        }
        etVolumePickup().text = "${progressText}"
        mVolumePickup = progressDouble
//
    }
    private fun etVolumePickuptext(progress: Int) {
//        mEtVolumePickupText(progress.toDouble())
//        ³ во во примерно так
        var progressText = progress.toString()
        if (progress <= 0) {
            progressText = ""
        }

        etVolumePickup().text = "${progressText} м³"
        mVolumePickup = progress.toDouble()
    }

    private fun getThumb(progress: Int): Drawable? {
        val thumbView: View = LayoutInflater.from(this)
            .inflate(R.layout.act_platformserve__pickup_seekbarthumb, null, false)
//        (thumbView.findViewById(R.id.tvProgress) as TextView).text = progress.toString() + ""
        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        thumbView.layout(0, 0, thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight())
        thumbView.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }





    private fun setUseButtonStyleAlpha(appCompatButton: AppCompatButton) {
        appCompatButton.alpha = 1f
    }

    private fun setNotUseButtonStyleAlpha(appCompatButton: AppCompatButton) {
        appCompatButton.alpha = 0.2f
    }

//    private fun setButtonKGODrawableEnd(isTakeawayKGO: Boolean) {
//        if (isTakeawayKGO) {
//            btnKGO.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_check) , null)
//        } else {
//            btnKGO.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_check_gray) , null)
//        }
//    }

    private fun initBeforeMedia() {
        AppPreferences.serviceStartedAt = System.currentTimeMillis() / 1000L
        val intent = Intent(this@PlatformServeAct, CameraActivity::class.java)
        intent.putExtra("platform_id", mPlatformEntity.platformId)
        intent.putExtra("photoFor", PhotoTypeEnum.forBeforeMedia)
        startActivity(intent)
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
        val platform = vm.findPlatformEntity(platformId = mPlatformEntity.platformId!!)
        val containers = vm.findAllContainerInPlatform(mPlatformEntity.platformId!!)
//        val arrays = containers as ArrayList<ContainerEntity>
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
//        arrays.add(arrays.get(0))
        mConrainerAdapter = ContainerAdapter(this, containers as ArrayList<ContainerEntity>)
        findViewById<RecyclerView>(R.id.rv_activity_platform_serve).recycledViewPool.setMaxRecycledViews(0, 0);
        findViewById<RecyclerView>(R.id.rv_activity_platform_serve).adapter = mConrainerAdapter
        findViewById<TextView>(R.id.tv_activity_platform_serve__point_info).text = "№${platform.srpId} / ${platform.containers!!.size} конт."
    }

    fun updateRecyclerview() {
        val containers = vm.findAllContainerInPlatform(mPlatformEntity.platformId!!)
        mConrainerAdapter.updateData(containers as ArrayList<ContainerEntity>)
//        initAfterMedia()
    }

    override fun startContainerService(item: ContainerEntity) {
        ContainerServiceFragment(item.containerId!!, mPlatformEntity.platformId!!)
            .show(supportFragmentManager, "ContainerServiceFragment")
    }

    override fun onBackPressed() {
        mBackPressedCnt--
        if (mBackPressedCnt <= 0) {
            super.onBackPressed()
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

}
