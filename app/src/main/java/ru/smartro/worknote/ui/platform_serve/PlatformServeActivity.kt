package ru.smartro.worknote.ui.platform_serve

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.base.AbstractAct
import ru.smartro.worknote.extensions.hideDialog
import ru.smartro.worknote.extensions.showDialogAdditionalVolumeContainer
import ru.smartro.worknote.extensions.showDialogFillKgoVolume
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.work_order.ContainerEntity
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity
import ru.smartro.worknote.util.MyUtil.toStr
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.StatusEnum


class PlatformServeActivity : AbstractAct(), ContainerAdapter.ContainerPointClickListener, View.OnClickListener {
    private val REQUEST_EXIT = 33
    private lateinit var mPlatformEntity: PlatformEntity
    private lateinit var mConrainerAdapter: ContainerAdapter
    private val mOnClickListener = this as View.OnClickListener
    private var mIsServeAgain: Boolean = false
    private val mViewModel: PlatformServeViewModel by viewModel()
    private lateinit var btnKGO: AppCompatButton
    private lateinit var btnCompleteTask: AppCompatButton

    override fun onClick(buttonView: View) {
        Log.d(TAG, "onClick.before id=${buttonView.id}")

        when(buttonView.id) {
            R.id.btn_alert_kgo__takeaway -> {
                onAlertButtonKgoClick(buttonView.rootView, true)
                setButtonKGODrawableEnd(true)
            }
            R.id.btn_alert_kgo__no_takeaway -> {
                onAlertButtonKgoClick(buttonView.rootView,false)
                setButtonKGODrawableEnd(false)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform_serve)
        intent.let {
            mPlatformEntity = mViewModel.findPlatformEntity(it.getIntExtra("platform_id", 0))
            mIsServeAgain = it.getBooleanExtra("mIsServeAgain", false)
        }
        supportActionBar?.title = "${mPlatformEntity.address}"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initContainer()
        initBeforeMedia()

        findViewById<AppCompatButton>(R.id.problem_btn).setOnClickListener {
            val intent = Intent(this, ExtremeProblemActivity::class.java)
            intent.putExtra("platform_id", mPlatformEntity.platformId)
            intent.putExtra("isContainerProblem", false)
                startActivityForResult(intent, REQUEST_EXIT)
        }
        btnKGO = findViewById(R.id.btn_activity_platform_serve__kgo)
        if (mPlatformEntity.volumeKGO != null) {
            setButtonKGODrawableEnd(mPlatformEntity.isTakeawayKGO)
        }
        btnKGO.setOnClickListener {
            val intent = Intent(this@PlatformServeActivity, CameraActivity::class.java)
            intent.putExtra("platform_id", mPlatformEntity.platformId)
            intent.putExtra("photoFor", PhotoTypeEnum.forKGO)
            startActivityForResult(intent, 101)
            hideDialog()
        }

        btnCompleteTask = findViewById(R.id.btn_activity_platform_serve__complete_task)
        btnCompleteTask.setOnClickListener {
            val intent = Intent(this@PlatformServeActivity, CameraActivity::class.java)
            intent.putExtra("platform_id", mPlatformEntity.platformId!!)
            intent.putExtra("photoFor", PhotoTypeEnum.forAfterMedia)
            startActivityForResult(intent, 13)
        }

        var volumeSelectionInM3 = mPlatformEntity.volumePickup
//        volumeAdditionalInM3 = null
        val acivSelection = findViewById<AppCompatButton>(R.id.acb_fragment_container_service__selection)

        acivSelection.setOnClickListener {
            showDialogAdditionalVolumeContainer().let{ dialogView ->
                val btnCancel = dialogView.findViewById<Button>(R.id.btn_alert_additional_volume_container__cancel)
                btnCancel.setOnClickListener { hideDialog() }
                val tietAdditionalVolumeInM3 = dialogView.findViewById<TextInputEditText>(R.id.tiet_alert_additional_volume_container)
                volumeSelectionInM3?.let{
                    tietAdditionalVolumeInM3.setText(volumeSelectionInM3.toString())
                }

                val btnOk = dialogView.findViewById<Button>(R.id.btn_alert_additional_volume_container__ok)
                btnOk.setOnClickListener {
                    volumeSelectionInM3 = tietAdditionalVolumeInM3.text.toString().toDoubleOrNull()
                    mViewModel.updateSelectionVolume(mPlatformEntity.platformId!!, volumeSelectionInM3)
                    hideDialog()

                    val intent = Intent(this@PlatformServeActivity, CameraActivity::class.java)
                    intent.putExtra("platform_id", mPlatformEntity.platformId!!)
                    intent.putExtra("photoFor", PhotoTypeEnum.forPlatformPickupVolume)
                    startActivityForResult(intent, 14)
                }
            }
        }
    }

    private fun setButtonKGODrawableEnd(isTakeawayKGO: Boolean) {
        if (isTakeawayKGO) {
            btnKGO.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_check) , null)
        } else {
            btnKGO.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_check_gray) , null)
        }
    }

    private fun initBeforeMedia() {
        AppPreferences.serviceStartedAt = System.currentTimeMillis() / 1000L
        val intent = Intent(this@PlatformServeActivity, CameraActivity::class.java)
        intent.putExtra("platform_id", mPlatformEntity.platformId)
        intent.putExtra("photoFor", PhotoTypeEnum.forBeforeMedia)
        startActivity(intent)
        hideDialog()
    }

    // TODO: 28.10.2021 isActiveToday что это за поле?
    private fun initAfterMedia() {
        val platformEntity = mViewModel.findPlatformEntity(mPlatformEntity.platformId!!)
        btnCompleteTask.isEnabled = platformEntity.containers.filter {
            it.isActiveToday == true
        }.all { it.status != StatusEnum.NEW }
    }

    private fun initContainer() {
        val platform = mViewModel.findPlatformEntity(platformId = mPlatformEntity.platformId!!)
        val containers = mViewModel.findAllContainerInPlatform(mPlatformEntity.platformId!!)
        mConrainerAdapter = ContainerAdapter(this, containers as ArrayList<ContainerEntity>)
        findViewById<RecyclerView>(R.id.platform_service_rv).recycledViewPool.setMaxRecycledViews(0, 0);
        findViewById<RecyclerView>(R.id.platform_service_rv).adapter = mConrainerAdapter
        findViewById<TextView>(R.id.point_info_tv).text = "№${platform.srpId} / ${platform.containers!!.size} конт."
    }

    fun updateRecyclerview() {
        val containers = mViewModel.findAllContainerInPlatform(mPlatformEntity.platformId!!)
        mConrainerAdapter.updateData(containers as ArrayList<ContainerEntity>)
        initAfterMedia()
    }

    override fun startContainerService(item: ContainerEntity) {
        ContainerServiceFragment(item.containerId!!, mPlatformEntity.platformId!!)
            .show(supportFragmentManager, "ContainerServiceFragment")
    }

    override fun onBackPressed() {
        toast("Заполните данные")
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
        } else if (resultCode == 101 && requestCode == 101) {
            showDialogFillKgoVolume().let { view ->
                view.findViewById<TextInputEditText>(R.id.kgo_volume_in).setText(mPlatformEntity.volumeKGO.toStr())
                view.findViewById<Button>(R.id.btn_alert_kgo__takeaway).setOnClickListener(mOnClickListener)
                view.findViewById<Button>(R.id.btn_alert_kgo__no_takeaway).setOnClickListener(mOnClickListener)
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

}
