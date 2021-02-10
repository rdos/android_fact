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
import ru.smartro.worknote.service.db.entity.container_service.ServedPointEntity
import ru.smartro.worknote.service.db.entity.way_task.ContainerInfoEntity
import ru.smartro.worknote.service.db.entity.way_task.WayPointEntity
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.served.ContainerInfoServed
import ru.smartro.worknote.service.network.body.served.ContainerPointServed
import ru.smartro.worknote.service.network.body.served.ServiceResultBody
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum


class PointServiceActivity : AppCompatActivity(), ContainerPointAdapter.ContainerPointClickListener {
    private val TAG = "PointServiceActivity"
    private lateinit var wayPoint: WayPointEntity
    private val viewModel: PointServiceViewModel by viewModel()
    private var pointEntityId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_service)
        intent.let {
            wayPoint = it.getSerializableExtra("container") as WayPointEntity
        }
        supportActionBar?.title = "${wayPoint.address}"
        createPointEntity()
        initContainer()
        initBeforeMedia()
        initAfterMedia()
    }

    private fun createPointEntity() {
        pointEntityId = viewModel.findLastId(ServedPointEntity::class.java, "id")!!
        val emptyPointEntity = ServedPointEntity(
            beginnedAt = System.currentTimeMillis() / 1000L, finishedAt = null,
            mediaBefore = null, mediaAfter = null, oid = AppPreferences.organisationId, woId = AppPreferences.wayTaskId,
            cs = null, co = wayPoint.coordinate, pId = wayPoint.id
        )
        viewModel.insertOrUpdateServedPoint(emptyPointEntity)
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

    private fun initContainer() {
        point_service_rv.adapter = ContainerPointAdapter(this, wayPoint.containerInfo!!)
    }

    override fun startContainerPointService(item: ContainerInfoEntity) {
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
            val servedPointEntity = viewModel.findServedPointEntity(wayPoint.id!!)
            val cs = ArrayList<ContainerInfoServed>()
            val beforeMedia = arrayListOf<String>()
            val afterMedia = arrayListOf<String>()

            for (photoBeforeEntity in servedPointEntity.mediaAfter!!) {
                beforeMedia.add(MyUtil.getFileToByte(photoBeforeEntity))
            }

            for (photoAfterEntity in servedPointEntity.mediaAfter!!) {
                afterMedia.add(MyUtil.getFileToByte(photoAfterEntity))
            }

            val servedPoint = ContainerPointServed(
                beginnedAt = AppPreferences.serviceStartedAt, co = wayPoint.coordinate!!, cs = cs, woId = AppPreferences.wayTaskId,
                oid = AppPreferences.organisationId, finishedAt = System.currentTimeMillis() / 1000L, mediaAfter = afterMedia, mediaBefore = beforeMedia, pId = wayPoint.id!!
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
                                toast("Успешно отправлен!")
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
