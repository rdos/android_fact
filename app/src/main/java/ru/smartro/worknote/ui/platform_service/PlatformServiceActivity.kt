package ru.smartro.worknote.ui.platform_service

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_container_problem.problem_btn
import kotlinx.android.synthetic.main.activity_platform_service.*
import kotlinx.android.synthetic.main.alert_accept_task.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.container_service.ConteinerAdapter
import ru.smartro.worknote.extensions.hideDialog
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.extensions.warningCameraShow
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.way_task.ContainerEntity
import ru.smartro.worknote.service.database.entity.way_task.PlatformEntity
import ru.smartro.worknote.ui.ProblemActivity.ProblemActivity
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.StatusEnum


class PlatformServiceActivity : AppCompatActivity(), ConteinerAdapter.ContainerPointClickListener {
    private val REQUEST_EXIT = 33
    private lateinit var platform: PlatformEntity
    private val viewModel: PlatformServiceViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform_service)
        intent.let {
            val itemJson = it.getStringExtra("container")
            platform = Gson().fromJson(itemJson, PlatformEntity::class.java)
        }
        supportActionBar?.title = "${platform.address}"
        initContainer()
        initBeforeMedia()
        initAfterMedia()

        problem_btn.setOnClickListener {
            val intent = Intent(this, ProblemActivity::class.java)
            intent.putExtra("wayPoint", Gson().toJson(platform))
            intent.putExtra("isContainerProblem", false)
            startActivityForResult(intent, REQUEST_EXIT)
            Log.d("POINT_RPOBLEM", "onCreate: ${Gson().toJson(platform)}")
        }
    }

    private fun initBeforeMedia() {
        warningCameraShow("Сделайте фото КП до обслуживания").run {
            AppPreferences.serviceStartedAt = System.currentTimeMillis() / 1000L
            this.accept_btn.setOnClickListener {
                val intent = Intent(this@PlatformServiceActivity, CameraActivity::class.java)
                intent.putExtra("wayPoint", Gson().toJson(platform))
                intent.putExtra("photoFor", PhotoTypeEnum.forBeforeMedia)
                startActivity(intent)
                hideDialog()
            }
        }
    }

    private fun initAfterMedia() {
        if (viewModel.findWayTask().platfroms!!.find { it.platformId == platform.platformId }?.containers!!.any { it.status != StatusEnum.EMPTY }) {
            complete_task_btn.isVisible = true
            complete_task_btn.setOnClickListener {
                warningCameraShow("Сделайте фото КП после обслуживания").run {
                    this.accept_btn.setOnClickListener {
                        val intent = Intent(this@PlatformServiceActivity, CameraActivity::class.java)
                        intent.putExtra("wayPoint", Gson().toJson(platform))
                        intent.putExtra("photoFor", PhotoTypeEnum.forAfterMedia)
                        startActivityForResult(intent, 13)
                        hideDialog()
                    }
                }
            }
        } else {
            complete_task_btn.isVisible = false
        }
    }

    private fun initContainer() {
        val wayTask = viewModel.findWayTask()
        val wayPointEntity = wayTask.platfroms!!.find { it.platformId == platform.platformId }
        platform_service_rv.adapter = ConteinerAdapter(this, wayPointEntity?.containers!!)
        point_info_tv.text = "№${wayPointEntity.srpId} / ${wayPointEntity.containers!!.size} конт."
        Log.d("PointServiceActivity", "wayPointEntity: ${Gson().toJson(wayPointEntity)}")
    }

    override fun startContainerPointService(item: ContainerEntity) {
        val intent = Intent(this, ContainerServiceActivity::class.java)
        intent.putExtra("container_info", Gson().toJson(item))
        intent.putExtra("wayPointId", platform.platformId)
        intent.putExtra("wayPoint", Gson().toJson(platform))
        startActivityForResult(intent, 14)
    }

    override fun onBackPressed() {
        toast("Заполните данные")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 && resultCode == Activity.RESULT_OK) {
            viewModel.updatePlatformStatus(platform.platformId!!, StatusEnum.COMPLETED)
            setResult(Activity.RESULT_OK)
            finish()
        }
        else if (requestCode == 14) {
                if (resultCode == Activity.RESULT_OK) {
                    initContainer()
                    initAfterMedia()
                } else if (resultCode == 99) {
                    setResult(RESULT_OK)
                    finish()
                }
            } else if (requestCode == REQUEST_EXIT) {
                if (resultCode == 99) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        initContainer()
        initAfterMedia()
    }
}
