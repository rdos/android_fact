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
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.extensions.toast
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
import ru.smartro.worknote.util.ContainerStatusEnum
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum

class ContainerProblemActivity : AppCompatActivity() {
    private lateinit var breakDown: List<ContainerBreakdownEntity>
    private lateinit var failureReason: List<ContainerFailReasonEntity>

    private lateinit var wayPoint: WayPointEntity
    private lateinit var containerInfo: ContainerInfoEntity
    private val viewModel: ProblemViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_problem)
        supportActionBar!!.title = "Проблема на площадке"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        breakDown = viewModel.findBreakDown()
        failureReason = viewModel.findFailReason()
        intent.let {
            wayPoint = Gson().fromJson(it.getStringExtra("wayPoint"), WayPointEntity::class.java)
            containerInfo = Gson().fromJson(it.getStringExtra("container_info"), ContainerInfoEntity::class.java)
        }
        initProblemPhoto()
        initSelectors()
        acceptProblem()
    }

    private fun initImageView() {
        val wayPoint = viewModel.findServedPointEntity(wayPoint.id!!)
        Glide.with(this).load(wayPoint!!.mediaProblemContainer!!.last()).into(container_problem_img)
    }

    private fun initProblemPhoto() {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("wayPoint", Gson().toJson(wayPoint))
        intent.putExtra("photoFor", PhotoTypeEnum.forProblemContainer)
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
            val media = pointEntity?.mediaProblemContainer?.map { MyUtil.getFileToByte(it) }
            val breakDownId = findBreakDownId()
            val failReasonId = findFailReasonId()
            if (container_problem_cant_tg.isChecked || container_problem_break_tg.isChecked) {
                if (container_problem_break_tg.isChecked) {
                    if (problem_container_choose_problem.text.isNullOrEmpty()) {
                        problem_container_choose_problem_out.error = "Выберите проблему"
                    } else {
                        sendBreakDown(media!!, breakDownId)
                    }
                }
                if (container_problem_cant_tg.isChecked) {
                    if (problem_container_choose_reason_cant.text.isNullOrEmpty()) {
                        problem_container_choose_problem_out.error = "Выберите причину невывоза"
                    } else {
                        sendFailure(media!!, failReasonId)
                        viewModel.updateContainerStatus(wayPoint.id!!, containerInfo.id!!, ContainerStatusEnum.failure)
                    }
                } else {
                    viewModel.updateContainerStatus(wayPoint.id!!, containerInfo.id!!, ContainerStatusEnum.breakDown)
                }
            } else {
                toast("Выберите тип проблемы")
            }
        }
    }

    private fun sendBreakDown(media: List<String>, tId: Int) {
        val list = ArrayList<BreakDownItem>()
        val body = BreakDownItem(
            cId = containerInfo.id!!, allowed = listOf(1, 2), co = wayPoint.co!!, comment = container_problem_comment.text.toString(),
            datetime = System.currentTimeMillis() / 1000L, failureType = "unserve", media = media, oid = AppPreferences.organisationId,
            pId = wayPoint.id!!, redirect = "fact container", type = "container", woId = AppPreferences.wayTaskId, tId = tId
        )
        list.add(body)
        val breakDownBody = BreakdownBody(list)
        viewModel.sendBreakdown(breakDownBody).observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    toast("Успешно отправлен ${result.msg}")
                    viewModel.updateContainerStatus(wayPoint.id!!, containerInfo.id!!, ContainerStatusEnum.breakDown)
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
            failureType = "failure", datetime = System.currentTimeMillis() / 1000L, comment = container_problem_comment.text.toString(), failureId = tId
        )
        list.add(body)
        val failureBody = FailureBody(list)
        viewModel.sendFailure(failureBody).observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    toast("Успешно отправлен ${result.msg}")
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
        container_problem_break_tg.setOnCheckedChangeListener { compoundButton, check ->
            problem_container_choose_problem_out.isVisible = check
            problem_container_choose_problem.setText("")
            if (check) {
                container_problem_break_tg.setTextColor(Color.WHITE)
            } else {
                container_problem_break_tg.setTextColor(Color.BLACK)
            }

            val breakdowns = viewModel.findBreakDown()
            val failReason = viewModel.findFailReason()
            val breakdownsString = breakdowns.map { it.problem }
            val failReasonString = failReason.map { it.reason }

            problem_container_choose_problem.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, breakdownsString))
            problem_container_choose_reason_cant.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, failReasonString))

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
}