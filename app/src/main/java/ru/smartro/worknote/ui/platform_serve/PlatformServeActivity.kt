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
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.base.AbstractAct
import ru.smartro.worknote.extensions.hideDialog
import ru.smartro.worknote.extensions.showDialogFillKgoVolume
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.StatusEnum


class PlatformServeActivity : AbstractAct(), ContainerAdapter.ContainerPointClickListener, SeekBar.OnSeekBarChangeListener{
    private var mBackPressedCnt: Int = 3
    private val REQUEST_EXIT = 33
    private lateinit var mPlatformEntity: PlatformEntity
    private lateinit var mConrainerAdapter: ContainerAdapter
    private var mIsServeAgain: Boolean = false
    private val mViewModel: PlatformServeViewModel by viewModel()
    private lateinit var btnCompleteTask: AppCompatButton

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform_serve)
        intent.let {
            mPlatformEntity = mViewModel.findPlatformEntity(it.getIntExtra("platform_id", 0))
            mIsServeAgain = it.getBooleanExtra("mIsServeAgain", false)
        }
        supportActionBar?.title = "${mPlatformEntity.address}"
//        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        initContainer()
        if (!mPlatformEntity.isStartServeVolume()) {
            initBeforeMedia()
        }

        findViewById<AppCompatButton>(R.id.acb_activity_platform_serve__problem).setOnClickListener {
            val intent = Intent(this, ExtremeProblemActivity::class.java)
            intent.putExtra("platform_id", mPlatformEntity.platformId)
            intent.putExtra("isContainerProblem", false)
                startActivityForResult(intent, REQUEST_EXIT)
        }




        val acbKGOServed = findViewById<AppCompatButton>(R.id.acb_activity_platform_serve__kgo_served)
        if (mPlatformEntity.servedKGO.isNotEmpty()) {
            acbKGOServed.alpha = 1f
        }
        acbKGOServed.setOnClickListener {
            showDialogFillKgoVolume().let { view ->
                view.findViewById<TextInputEditText>(R.id.kgo_volume_in).setText(mPlatformEntity.servedKGO.volume.toString())
//                view.findViewById<Button>(R.id.btn_alert_kgo__takeaway).setOnClickListener(mOnClickListener)
//                view.findViewById<Button>(R.id.btn_alert_kgo__no_takeaway).setOnClickListener(mOnClickListener)
            }

            val intent = Intent(this@PlatformServeActivity, CameraActivity::class.java)
            intent.putExtra("platform_id", mPlatformEntity.platformId)
            intent.putExtra("photoFor", PhotoTypeEnum.forServedKGO)
            startActivityForResult(intent, 101)
            hideDialog()


        }



        btnCompleteTask = findViewById(R.id.acb_activity_platform_serve__complete)
        btnCompleteTask.setOnClickListener {
            val intent = Intent(this@PlatformServeActivity, CameraActivity::class.java)
            intent.putExtra("platform_id", mPlatformEntity.platformId!!)
            intent.putExtra("photoFor", PhotoTypeEnum.forAfterMedia)
            startActivityForResult(intent, 13)
        }




        val seekBar = findViewById<SeekBar>(R.id.acsb_activity_platform_serve__seekbar)
        seekBar.setOnSeekBarChangeListener(this)
        seekBar?.thumb = getThumb(1);

        mPlatformEntity.volumePickup?.let{
            seekBar.progress = mPlatformEntity.volumePickup!!.toInt()
                }

//        volumeAdditionalInM3 = null
//        val acivSelection = findViewById<AppCompatButton>(R.id.acb_fragment_container_service__selection)
//
//        acivSelection.setOnClickListener {
//            showDialogAdditionalVolumeContainer().let{ dialogView ->
//                val btnCancel = dialogView.findViewById<Button>(R.id.btn_alert_additional_volume_container__cancel)
//                btnCancel.setOnClickListener { hideDialog() }
//                val tietAdditionalVolumeInM3 = dialogView.findViewById<TextInputEditText>(R.id.tiet_alert_additional_volume_container)
//                volumePickup?.let{
//                    tietAdditionalVolumeInM3.setText(volumePickup.toString())
//                }
//
//                val btnOk = dialogView.findViewById<Button>(R.id.btn_alert_additional_volume_container__ok)
//                btnOk.setOnClickListener {
//                    val pickupVolumeText = tietAdditionalVolumeInM3.text.toString()
//                    if (pickupVolumeText.isBlank()) {
//                        tietAdditionalVolumeInM3.error = "Обязательное поле"
//                        return@setOnClickListener
//                    }
//                    volumePickup = pickupVolumeText.toDoubleOrNull()
//                    mViewModel.updateSelectionVolume(mPlatformEntity.platformId!!, volumePickup)
//                    hideDialog()
//
//                    val intent = Intent(this@PlatformServeActivity, CameraActivity::class.java)
//                    intent.putExtra("platform_id", mPlatformEntity.platformId!!)
//                    intent.putExtra("photoFor", PhotoTypeEnum.forPlatformPickupVolume)
//                    startActivityForResult(intent, 14)
//                }
//            }
//        }
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
        val intent = Intent(this@PlatformServeActivity, CameraActivity::class.java)
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
        val platform = mViewModel.findPlatformEntity(platformId = mPlatformEntity.platformId!!)
        val containers = mViewModel.findAllContainerInPlatform(mPlatformEntity.platformId!!)
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
        val containers = mViewModel.findAllContainerInPlatform(mPlatformEntity.platformId!!)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 && resultCode == Activity.RESULT_OK) {
            mViewModel.updateContainersVolumeIfnNull(mPlatformEntity.platformId!!, 1.0)
            mViewModel.updatePlatformStatus(mPlatformEntity.platformId!!, StatusEnum.SUCCESS)
            setResult(Activity.RESULT_OK)
            finish()
        } else if (requestCode == REQUEST_EXIT) {
            if (resultCode == 99) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onAlertButtonKgoClick(dialogView: View, isTakeaway : Boolean) {
        Log.i(TAG, "onButtonKgoClick.before id=${dialogView.id} isTakeaway=$isTakeaway")
        val kgoVolumeText = dialogView.findViewById<TextInputEditText>(R.id.kgo_volume_in).text.toString()
        if (kgoVolumeText.isNullOrBlank()) {
            return
        }
        val kgoVolume = kgoVolumeText.toDouble()
        mViewModel.updatePlatformKGO(mPlatformEntity.platformId!!, kgoVolume, isTakeaway)
        hideDialog()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        val tv = findViewById<TextView>(R.id.CustomBtn1)
        tv.text = "${progress} м³"
        if (progress >= 21) {
            seekBar?.progress = 21
        }
        if (progress <= 0) {
            tv.text = ""
        }
//        seekBar?.setThumb(getThumb(progress));
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (seekBar?.progress!! > 0 ) {
            mViewModel.updateSelectionVolume(mPlatformEntity.platformId!!, seekBar.progress.toDouble())
            val intent = Intent(this@PlatformServeActivity, CameraActivity::class.java)
            intent.putExtra("platform_id", mPlatformEntity.platformId!!)
            intent.putExtra("photoFor", PhotoTypeEnum.forPlatformPickupVolume)
            startActivityForResult(intent, 14)
        } else {
            mViewModel.updateSelectionVolume(mPlatformEntity.platformId!!, null)
        }
    }

    fun getThumb(progress: Int): Drawable? {
        val thumbView: View = LayoutInflater.from(this)
            .inflate(R.layout.activity_platform_serve__seekbar_thumb, null, false)
//        (thumbView.findViewById(R.id.tvProgress) as TextView).text = progress.toString() + ""
        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        thumbView.layout(0, 0, thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight())
        thumbView.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }
}
