package ru.smartro.worknote.ui.problem

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_container_problem.*
import kotlinx.android.synthetic.main.alert_warning_camera.view.accept_btn
import kotlinx.android.synthetic.main.alert_warning_delete.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.extensions.hideDialog
import ru.smartro.worknote.extensions.loadingShow
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.extensions.warningContainerFailure
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.problem.ContainerBreakdownEntity
import ru.smartro.worknote.service.database.entity.problem.ContainerFailReasonEntity
import ru.smartro.worknote.service.database.entity.way_task.ContainerInfoEntity
import ru.smartro.worknote.service.database.entity.way_task.WayPointEntity
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.breakdown.BreakDownItem
import ru.smartro.worknote.service.network.body.breakdown.BreakdownBody
import ru.smartro.worknote.service.network.body.failure.FailureBody
import ru.smartro.worknote.service.network.body.failure.FailureItem
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.util.ActivityResult
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.StatusEnum

class ContainerProblemActivity : AppCompatActivity() {
    private lateinit var breakDown: List<ContainerBreakdownEntity>
    private lateinit var failureReason: List<ContainerFailReasonEntity>
    private var isContainerProblem = false
    private lateinit var wayPoint: WayPointEntity
    private lateinit var containerInfo: ContainerInfoEntity
    private val viewModel: ProblemViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_problem)
        baseview.setOnClickListener { MyUtil.hideKeyboard(this) }

        supportActionBar!!.title = "Проблема на площадке"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        breakDown = viewModel.findBreakDown()
        failureReason = viewModel.findFailReason()

        intent.let {
            wayPoint = Gson().fromJson(it.getStringExtra("wayPoint"), WayPointEntity::class.java)
            isContainerProblem = it.getBooleanExtra("isContainerProblem", false)
            if (isContainerProblem) {
                containerInfo = Gson().fromJson(it.getStringExtra("container_info"), ContainerInfoEntity::class.java)
            }
        }
        initProblemPhoto()
        initSelectors()
        acceptProblem()
    }

    private fun initImageView() {
        val wayPoint = viewModel.findServedPointEntity(wayPoint.id!!)
        if (isContainerProblem) {
            Glide.with(this).load(wayPoint!!.mediaProblemContainer!!.last()).into(container_problem_img)
        } else {
            Glide.with(this).load(wayPoint!!.mediaPointProblem!!.last()).into(container_problem_img)
        }
    }

    private fun initProblemPhoto() {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("wayPoint", Gson().toJson(wayPoint))
        Log.d("POINT_PROBLEM", "initProblemPhoto: ${Gson().toJson(wayPoint)}")
        if (isContainerProblem) {
            intent.putExtra("photoFor", PhotoTypeEnum.forProblemContainer)
        } else {
            intent.putExtra("photoFor", PhotoTypeEnum.forProblemPoint)
        }
        startActivityForResult(intent, 13)
        point_problem_btn.setOnClickListener {
            startActivityForResult(intent, 13)
        }
    }

    private fun findBreakDownId(): Int {
        var result = 0
        breakDown.forEach {
            if (it.problem == problem_container_choose_problem.text.toString()) {
                result = it.id
            }
        }
        return result
    }

    private fun findFailReasonId(): Int {
        var result = 0
        failureReason.forEach {
            if (it.reason == problem_container_choose_reason_cant.text.toString()) {
                result = it.id
            }
        }
        return result
    }

    private fun acceptProblem() {
        container_problem_accept.setOnClickListener {
            val pointEntity = viewModel.findServedPointEntity(wayPoint.id!!)
            Log.d("ContainerProblem", "acceptProblem: ${Gson().toJson(pointEntity)}")
            val media = if (isContainerProblem) {
                pointEntity?.mediaProblemContainer?.map { MyUtil.imageToBase64(it) }!!
            } else {
                pointEntity?.mediaPointProblem?.map { MyUtil.imageToBase64(it) }!!
            }
            val breakDownId = findBreakDownId()
            val failReasonId = findFailReasonId()
            if (container_problem_cant_tg.isChecked || container_problem_break_tg.isChecked) {
                if (container_problem_break_tg.isChecked) {
                    if (problem_container_choose_problem.text.isNullOrEmpty()) {
                        problem_container_choose_problem_out.error = "Выберите проблему"
                    } else {
                        sendBreakDown(media, breakDownId, container_problem_cant_tg.isChecked)
                    }
                }
                if (container_problem_cant_tg.isChecked) {
                    if (problem_container_choose_reason_cant.text.isNullOrEmpty()) {
                        problem_container_choose_problem_out.error = "Выберите причину невывоза"
                    } else {
                        warningContainerFailure("${getString(R.string.container_fail)} Причина: ${problem_container_choose_reason_cant.text}").run {
                            this.accept_btn.setOnClickListener {
                                sendFailure(media, failReasonId)
                            }
                            this.dismiss_btn.setOnClickListener {
                                hideDialog()
                            }
                        }
                    }
                }
            } else {
                toast("Выберите тип проблемы")
            }
        }
    }

    private fun sendBreakDown(media: List<String>, tId: Int, failure: Boolean) {
        loadingShow()
        val breakDownBody = BreakdownBody(createBreakDownBody(isContainerProblem, media, tId))
        viewModel.sendBreakdown(breakDownBody).observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    toast("Успешно отправлен ${result.msg}")
                    viewModel.updatePointStatus(wayPoint.id!!, StatusEnum.breakDown)
                    if (isContainerProblem) {
                        viewModel.updateContainerStatus(wayPoint.id!!, containerInfo.id!!, StatusEnum.breakDown)
                    } else {
                        setResult(ActivityResult.pointProblem)
                    }
                    if (!failure) {
                        finish()
                    }
                }
                Status.ERROR -> {
                    toast(result.msg)
                }
                Status.NETWORK -> {
                    toast("Проблемы с интернетом")
                }
            }
        })

    }

    private fun sendFailure(media: List<String>, tId: Int) {
        val list = ArrayList<FailureItem>()
        val body = FailureItem(
            co = wayPoint.co!!, woId = AppPreferences.wayTaskId, pId = wayPoint.id!!, oid = AppPreferences.organisationId, media = media,
            failureType = "failure", datetime = MyUtil.timeStamp(), comment = container_problem_comment.text.toString(), failureId = tId
        )
        list.add(body)
        val failureBody = FailureBody(list)
        viewModel.sendFailure(failureBody).observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    hideDialog()
                    viewModel.updatePointStatus(wayPoint.id!!, StatusEnum.failure)
                    toast("Успешно отправлен ${result.msg}")
                    setResult(99)
                    finish()
                }
                Status.ERROR -> {
                    toast(result.msg)
                }
                Status.NETWORK -> {
                    toast("Проблемы с интернетом")
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 && resultCode == Activity.RESULT_OK) {
            initImageView()
        }
    }

    private fun initSelectors() {
        val failReason = viewModel.findFailReason()
        val failReasonString = failReason.map { it.reason }
        problem_container_choose_reason_cant.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, failReasonString))
        val breakdowns = viewModel.findBreakDown()
        val breakdownsString = breakdowns.map { it.problem }
        problem_container_choose_problem.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, breakdownsString))

        container_problem_break_tg.setOnCheckedChangeListener { compoundButton, check ->
            problem_container_choose_problem_out.isVisible = check
            problem_container_choose_problem.setText("")
            if (check) {
                container_problem_break_tg.setTextColor(Color.WHITE)
            } else {
                container_problem_break_tg.setTextColor(Color.BLACK)
            }
            problem_container_choose_problem_out.setOnClickListener {
                problem_container_choose_problem.showDropDown()
            }
            problem_container_reason_cant_out.setOnClickListener {
                problem_container_choose_reason_cant.showDropDown()
            }
        }

        container_problem_cant_tg.setOnCheckedChangeListener { compoundButton, check ->
            problem_container_reason_cant_out.isVisible = check
            problem_container_choose_reason_cant.setText("")
            if (check) {
                container_problem_cant_tg.setTextColor(Color.WHITE)
            } else {
                container_problem_cant_tg.setTextColor(Color.BLACK)
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

    private fun createBreakDownBody(isContainerProblem: Boolean, media: List<String>, tId: Int): ArrayList<BreakDownItem> {
        return if (isContainerProblem) {
            val body = BreakDownItem(
                cId = containerInfo.id!!, allowed = listOf(1, 2), co = wayPoint.co!!, comment = container_problem_comment.text.toString(),
                datetime = MyUtil.timeStamp(), failureType = "unserve", media = media, oid = AppPreferences.organisationId,
                pId = wayPoint.id!!, redirect = "fact container", type = "container", woId = AppPreferences.wayTaskId, tId = tId
            )
            arrayListOf(body)
        } else {
            val body = BreakDownItem(
                cId = null, allowed = listOf(1, 2), co = wayPoint.co!!, comment = container_problem_comment.text.toString(),
                datetime = MyUtil.timeStamp(), failureType = null, media = media, oid = AppPreferences.organisationId,
                pId = wayPoint.id!!, redirect = "platform", type = "platform", woId = AppPreferences.wayTaskId, tId = tId
            )
            arrayListOf(body)
        }
    }

}