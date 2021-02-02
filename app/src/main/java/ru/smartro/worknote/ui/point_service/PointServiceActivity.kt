package ru.smartro.worknote.ui.point_service

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_container_service.*
import kotlinx.android.synthetic.main.alert_accept_task.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.container_service.ContainerPointAdapter
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.extensions.warningCameraShow
import ru.smartro.worknote.service.response.way_task.ContainerInfo
import ru.smartro.worknote.service.response.way_task.WayPoint
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.util.PhotoTypeEnum
import java.io.File


class PointServiceActivity : AppCompatActivity(), ContainerPointAdapter.ContainerPointClickListener {
    private val INTENT_REQUEST = 13;
    private lateinit var wayPoint: WayPoint
    private val viewModel: PointServiceViewModel by viewModel()
    private lateinit var imageFile: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_service)
        intent.let {
            wayPoint = it.getSerializableExtra("container") as WayPoint
        }
        supportActionBar?.title = "${wayPoint.address}"
        initContainer()
        initBeforeMedia()
        initAfterMedia()
    }

    private fun initBeforeMedia() {
        warningCameraShow("Сделайте фото КП до обслуживания").run {
            this.accept_btn.setOnClickListener {
                val intent = Intent(this@PointServiceActivity, CameraActivity::class.java)
                intent.putExtra("container", wayPoint)
                intent.putExtra("photoFor", PhotoTypeEnum.forBeforeMedia)
                startActivity(intent)
                loadingHide()
            }
        }
    }

    private fun initAfterMedia() {
        viewModel.findContainerInfo().observe(this, Observer {
            Log.d("8r8f", "initContainer: ${Gson().toJson(it)}")
            if (it.size == wayPoint.containerInfo.size) {
                complete_task_btn.isVisible = true
                end_task_btn.isVisible = false
                complete_task_btn.setOnClickListener {
                    warningCameraShow("Сделайте фото КП после обслуживания").run {
                        this.accept_btn.setOnClickListener {
                            val intent = Intent(this@PointServiceActivity, CameraActivity::class.java)
                            intent.putExtra("container", wayPoint)
                            intent.putExtra("photoFor", PhotoTypeEnum.forAfterMedia)
                            startActivity(intent)
                            loadingHide()
                        }
                    }
                }
            } else {
                complete_task_btn.isVisible = false
                end_task_btn.isVisible = true
                end_task_btn.setOnClickListener {

                }
            }
        })
    }

    private fun initContainer() {
        viewModel.findContainerInfo().observe(this, Observer {
            Log.d("8r8f", "initContainer: ${Gson().toJson(it)}")
            point_service_rv.adapter = ContainerPointAdapter(this, wayPoint.containerInfo as ArrayList<ContainerInfo>, it)
        })
    }

    override fun startContainerPointService(item: ContainerInfo) {
        val intent = Intent(this, EnterContainerInfoActivity::class.java)
        intent.putExtra("container_info", item)
        intent.putExtra("wayPointId", wayPoint.id)
        startActivity(intent)
    }

    override fun onBackPressed() {
        //nothing
        toast("Заполните данные")
    }
}
