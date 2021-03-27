package ru.smartro.worknote.ui.point_service

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_container_service.*
import kotlinx.android.synthetic.main.alert_accept_task.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.container_service.ContainerPointAdapter
import ru.smartro.worknote.extensions.*
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.way_task.ContainerInfoEntity
import ru.smartro.worknote.service.database.entity.way_task.WayPointEntity
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.served.ContainerInfoServed
import ru.smartro.worknote.service.network.body.served.ContainerPointServed
import ru.smartro.worknote.service.network.body.served.ServiceResultBody
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.ui.problem.ContainerProblemActivity
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.StatusEnum


class PointServiceActivity : AppCompatActivity(), ContainerPointAdapter.ContainerPointClickListener {
    private val REQUEST_EXIT = 33
    private lateinit var wayPoint: WayPointEntity
    private val viewModel: PointServiceViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_service)
        intent.let {
            val itemJson = it.getStringExtra("container")
            wayPoint = Gson().fromJson(itemJson, WayPointEntity::class.java)
        }
        supportActionBar?.title = "${wayPoint.address}"
        viewModel.createServedPointEntityIfNull(wayPoint)
        initContainer()
        initBeforeMedia()
        initAfterMedia()

        point_problem_btn.setOnClickListener {
            val intent = Intent(this, ContainerProblemActivity::class.java)
            intent.putExtra("wayPoint", Gson().toJson(wayPoint))
            intent.putExtra("isContainerProblem", false)
            startActivityForResult(intent, REQUEST_EXIT)
            Log.d("POINT_RPOBLEM", "onCreate: ${Gson().toJson(wayPoint)}")
        }
    }

    private fun initBeforeMedia() {
        warningCameraShow("Сделайте фото КП до обслуживания").run {
            AppPreferences.serviceStartedAt = System.currentTimeMillis() / 1000L
            this.accept_btn.setOnClickListener {
                val intent = Intent(this@PointServiceActivity, CameraActivity::class.java)
                intent.putExtra("wayPoint", Gson().toJson(wayPoint))
                intent.putExtra("photoFor", PhotoTypeEnum.forBeforeMedia)
                startActivity(intent)
                hideDialog()
            }
        }
    }

    private fun initAfterMedia() {
        if (viewModel.findWayTask().p!!.find { it.id == wayPoint.id }?.cs!!.any { it.status != StatusEnum.empty }) {
            complete_task_btn.isVisible = true
            complete_task_btn.setOnClickListener {
                warningCameraShow("Сделайте фото КП после обслуживания").run {
                    this.accept_btn.setOnClickListener {
                        val intent = Intent(this@PointServiceActivity, CameraActivity::class.java)
                        intent.putExtra("wayPoint", Gson().toJson(wayPoint))
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
        val wayPointEntity = wayTask.p!!.find { it.id == wayPoint.id }
        point_service_rv.adapter = ContainerPointAdapter(this, wayPointEntity?.cs!!)
        point_info_tv.text = "№${wayPointEntity.srp_id} / ${wayPointEntity.cs!!.size} конт."
        Log.d("PointServiceActivity", "wayPointEntity: ${Gson().toJson(wayPointEntity)}")
    }

    override fun startContainerPointService(item: ContainerInfoEntity) {
        val intent = Intent(this, EnterContainerInfoActivity::class.java)
        intent.putExtra("container_info", Gson().toJson(item))
        intent.putExtra("wayPointId", wayPoint.id)
        intent.putExtra("wayPoint", Gson().toJson(wayPoint))
        startActivityForResult(intent, 14)
    }

    override fun onBackPressed() {
        toast("Заполните данные")
    }

    private fun sendServedPoint() {
        loadingShow()
        val servedPointEntity = viewModel.findServedPointEntity(wayPoint.id!!)
        val isBreakDownPoint = viewModel.findWayTask().p!!.find { it.id == wayPoint.id }?.cs?.any { it.status == StatusEnum.breakDown }
        Log.d("PointServiceAct", "sendServedPoint: ${Gson().toJson(servedPointEntity)}")
        CoroutineScope(Dispatchers.IO).launch {
            val cs = servedPointEntity?.cs?.map {
                ContainerInfoServed(cId = it.cId!!, comment = it.comment!!, oid = AppPreferences.organisationId.toString(), woId = AppPreferences.wayTaskId, volume = it.volume!!)
            }
            val beforeMedia = servedPointEntity?.mediaBefore?.map { MyUtil.imageToBase64(it) }
            val afterMedia = servedPointEntity?.mediaAfter?.map { MyUtil.imageToBase64(it) }

            val servedPoint = ContainerPointServed(
                beginnedAt = AppPreferences.serviceStartedAt, co = wayPoint.co!!, cs = cs!!, woId = AppPreferences.wayTaskId,
                oid = AppPreferences.organisationId, finishedAt = System.currentTimeMillis() / 1000L, mediaAfter = afterMedia!!, mediaBefore = beforeMedia!!, pId = wayPoint.id!!
            )

            val ps = ArrayList<ContainerPointServed>()
            ps.add(servedPoint)

            val serviceResultBody = ServiceResultBody(ps)
            Log.d("served", "sendServedPoint: ${Gson().toJson(serviceResultBody)}")

            withContext(Dispatchers.Main) {
                Log.d("served", "sendServedPoint: ${Gson().toJson(serviceResultBody)}")
                viewModel.served(serviceResultBody).observe(this@PointServiceActivity, Observer { result ->
                    when (result.status) {
                        Status.SUCCESS -> {
                            toast("Успешно отправлен!")
                            loadingHide()
                            if (isBreakDownPoint!!) {
                                viewModel.updatePointStatus(wayPoint.id!!, StatusEnum.breakDown)
                            } else {
                                viewModel.updatePointStatus(wayPoint.id!!, StatusEnum.completed)
                            }
                            setResult(Activity.RESULT_OK)
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
        } else if (requestCode == 14) {
            if (resultCode == Activity.RESULT_OK) {
                initContainer()
                initAfterMedia()
            } else if (resultCode == 99) {
                setResult(99)
                finish()
            }
        } else if (requestCode == REQUEST_EXIT) {
            if (resultCode == 99) {
                setResult(Activity.RESULT_OK )
                finish()
            }
        }
    }
}
