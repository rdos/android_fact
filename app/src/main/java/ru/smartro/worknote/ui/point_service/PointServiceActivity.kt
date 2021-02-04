package ru.smartro.worknote.ui.point_service

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_container_service.*
import kotlinx.android.synthetic.main.alert_accept_task.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.container_service.ContainerPointAdapter
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.loadingShow
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.extensions.warningCameraShow
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.Status
import ru.smartro.worknote.service.body.served.ContainerInfoServed
import ru.smartro.worknote.service.body.served.ContainerPointServed
import ru.smartro.worknote.service.body.served.ServiceResultBody
import ru.smartro.worknote.service.response.way_task.ContainerInfo
import ru.smartro.worknote.service.response.way_task.WayPoint
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum


class PointServiceActivity : AppCompatActivity(), ContainerPointAdapter.ContainerPointClickListener {
    private lateinit var wayPoint: WayPoint
    private val viewModel: PointServiceViewModel by viewModel()
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
            AppPreferences.serviceStartedAt = System.currentTimeMillis() / 1000L
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
                complete_task_btn.setOnClickListener {
                    warningCameraShow("Сделайте фото КП после обслуживания").run {
                        this.accept_btn.setOnClickListener {
                            val intent = Intent(this@PointServiceActivity, CameraActivity::class.java)
                            intent.putExtra("container", wayPoint)
                            intent.putExtra("photoFor", PhotoTypeEnum.forAfterMedia)
                            startActivityForResult(intent, 13)
                            loadingHide()
                        }
                    }
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

    private fun sendServedPoint() {
        loadingShow()
        lifecycleScope.launch(Dispatchers.IO) {
            val cs = ArrayList<ContainerInfoServed>()
            val beforeMedia = arrayListOf<String>()
            val afterMedia = arrayListOf<String>()

            for (containerInfoEntity in viewModel.findContainerInfoNOLV()) {
                cs.add(ContainerInfoServed(cId = containerInfoEntity.id, volume = containerInfoEntity.volume, comment = containerInfoEntity.comment, oid = containerInfoEntity.o_id, woId = containerInfoEntity.wo_id))
            }
            for (photoBeforeEntity in viewModel.findBeforePhotosByIdNoLv(wayPoint.id)) {
                beforeMedia.add(MyUtil.getFileToByte(photoBeforeEntity.photoPath))
            }
            for (photoAfterEntity in viewModel.findAfterPhotosByIdNoLv(wayPoint.id)) {
                afterMedia.add(MyUtil.getFileToByte(photoAfterEntity.photoPath))
            }
            val servedPoint = ContainerPointServed(
                beginnedAt = AppPreferences.serviceStartedAt, co = wayPoint.coordinate, cs = cs, woId = AppPreferences.wayListId,
                oid = AppPreferences.organisationId, finishedAt = System.currentTimeMillis() / 1000L, mediaAfter = afterMedia, mediaBefore = beforeMedia, pId = wayPoint.id
            )
            val ps = ArrayList<ContainerPointServed>()
            ps.add(servedPoint)
            val serviceResultBody = ServiceResultBody(ps)

            withContext(Dispatchers.Main) {
                Log.d("served", "sendServedPoint: ${Gson().toJson(serviceResultBody)}")
                viewModel.served(serviceResultBody)
                    .observe(this@PointServiceActivity, Observer { result ->
                        when (result.status) {
                            Status.SUCCESS -> {
                                toast("Заверешено!")
                                loadingHide()
                                finish()
                            }
                            Status.ERROR -> {
                                toast(result.msg)
                                loadingHide()
                            }
                            Status.NETWORK -> {
                                toast(result.msg)
                                loadingHide()
                            }
                        }
                    })
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 && resultCode == Activity.RESULT_OK) {
            sendServedPoint()
        }
    }
}
