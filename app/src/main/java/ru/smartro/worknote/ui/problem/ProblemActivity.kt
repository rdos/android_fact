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
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_problem.*
import kotlinx.android.synthetic.main.alert_warning_camera.view.accept_btn
import kotlinx.android.synthetic.main.alert_warning_delete.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.base.AbstractAct
import ru.smartro.worknote.extensions.hideDialog
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.extensions.warningContainerFailure
import ru.smartro.worknote.service.database.entity.work_order.ContainerEntity
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.ProblemEnum

class ProblemActivity : AbstractAct() {
    private val viewModel: ProblemViewModel by viewModel()
    private var isContainer = false
    private lateinit var container: ContainerEntity
    private var platformId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem)
        baseview.setOnClickListener { MyUtil.hideKeyboard(this) }

        supportActionBar!!.title = "Проблема на площадке"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent.let {
            platformId = it.getIntExtra("platform_id", 0)
            isContainer = it.getBooleanExtra("is_container", false)
            if (isContainer) {
                container = viewModel.findContainerEntity(it.getIntExtra("container_id", 0))
            }
        }
        initProblemPhoto()
        initSelectors()
        acceptProblem()
    }

    private fun initImageView() {
        val platform = viewModel.findPlatformEntity(platformId)
        if (isContainer) {
            Glide.with(this).load(MyUtil.base64ToImage(container.failureMedia.last()?.image)).into(problem_img)
        } else {
            Glide.with(this).load(MyUtil.base64ToImage(platform.failureMedia.last()?.image)).into(problem_img)
        }
    }

    private fun initProblemPhoto() {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("platform_id", platformId)
        if (isContainer) {
            intent.putExtra("photoFor", PhotoTypeEnum.forContainerProblem)
            intent.putExtra("container_id", container.containerId)
        } else {
            intent.putExtra("photoFor", PhotoTypeEnum.forPlatformProblem)
        }
        startActivityForResult(intent, 13)
        problem_btn.setOnClickListener {
            startActivityForResult(intent, 13)
        }
    }

    private fun acceptProblem() {
        problem_accept_btn.setOnClickListener {
            if (problem_failure_tg.isChecked || problem_breakdown_tg.isChecked) {
                val problemComment = problem_comment.text.toString()
                if (problem_breakdown_tg.isChecked && !problem_failure_tg.isChecked) {
                    if (problem_choose_breakdown.text.isNullOrEmpty()) {
                        problem_choose_breakdown_out.error = "Выберите проблему"
                    } else {
                        val problem = problem_choose_breakdown.text.toString()
                        if (isContainer) {
                            viewModel.updateContainerProblem(
                                platformId = platformId, containerId = container.containerId!!, problemComment = problemComment,
                                problem = problem, problemType = ProblemEnum.BREAKDOWN, failProblem = null
                            )
                        } else {
                            viewModel.updatePlatformProblem(
                                platformId = platformId,
                                problemComment = problemComment, problem = problem, problemType = ProblemEnum.BREAKDOWN, failProblem = null
                            )
                        }
                        finish()
                        setResult(99)
                        Log.d("PROBLEM_ACTIVITY", "acceptProblem: breakdown")
                    }
                }
                if (problem_failure_tg.isChecked && !problem_breakdown_tg.isChecked) {
                    if (problem_choose_failure.text.isNullOrEmpty()) {
                        problem_choose_breakdown_out.error = "Выберите причину невывоза"
                    } else {
                        warningContainerFailure("${getString(R.string.container_fail)} Причина: ${problem_choose_failure.text}").run {
                            this.accept_btn.setOnClickListener {
                                val problem = problem_choose_failure.text.toString()
                                if (isContainer) {
                                    viewModel.updateContainerProblem(
                                        platformId = platformId, containerId = container.containerId!!, problemComment = problemComment,
                                        problem = problem, problemType = ProblemEnum.ERROR, failProblem = null
                                    )
                                } else {
                                    viewModel.updatePlatformProblem(
                                        platformId = platformId,
                                        problemComment = problemComment, problem = problem, problemType = ProblemEnum.ERROR, failProblem = null
                                    )
                                }
                                Log.d("PROBLEM_ACTIVITY", "acceptProblem: failure")
                                hideDialog()
                                   setResult(99)
                                   finish()
                            }
                            this.dismiss_btn.setOnClickListener {
                                hideDialog()
                            }
                        }
                    }
                }
                if (problem_breakdown_tg.isChecked && problem_failure_tg.isChecked){
                    val breakdown = problem_choose_breakdown.text.toString()
                    val failure = problem_choose_failure.text.toString()
                    if (breakdown.isNotEmpty() && failure.isNotEmpty()){
                        warningContainerFailure("${getString(R.string.container_fail)} Причина: ${problem_choose_failure.text}").run {
                            this.accept_btn.setOnClickListener {
                                if (isContainer) {
                                    viewModel.updateContainerProblem(
                                        platformId = platformId,   containerId = container.containerId!!, problemComment = problemComment,
                                        problem = breakdown, problemType = ProblemEnum.BOTH, failProblem = failure)
                                } else {
                                    viewModel.updatePlatformProblem(
                                        platformId = platformId,
                                        problemComment = problemComment, problem = breakdown, problemType = ProblemEnum.BOTH, failProblem = failure
                                    )
                                }
                                Log.d("PROBLEM_ACTIVITY", "acceptProblem: both")
                                hideDialog()
                                setResult(99)
                                finish()
                            }
                            this.dismiss_btn.setOnClickListener {
                                hideDialog()
                            }
                        }
                    }else{
                        toast("Выберите тип проблемы")
                    }
                }
            } else {
                toast("Выберите тип проблемы")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 && resultCode == Activity.RESULT_OK) {
            initImageView()
        }
    }

    private fun initSelectors() {
        val failReason = viewModel.findFailReason()
        problem_choose_failure.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, failReason))
        val breakdowns = viewModel.findBreakDown()
        problem_choose_breakdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, breakdowns))

        problem_breakdown_tg.setOnCheckedChangeListener { _, check ->
            problem_choose_breakdown_out.isVisible = check
            problem_choose_breakdown.setText("")
            if (check) {
                problem_breakdown_tg.setTextColor(Color.WHITE)
            } else {
                problem_breakdown_tg.setTextColor(Color.BLACK)
            }
            problem_choose_breakdown_out.setOnClickListener {
                problem_choose_breakdown.showDropDown()
            }
            problem_choose_failure_out.setOnClickListener {
                problem_choose_failure.showDropDown()
            }
        }

        problem_failure_tg.setOnCheckedChangeListener { _, check ->
            problem_choose_failure_out.isVisible = check
            problem_choose_failure.setText("")
            if (check) {
                problem_failure_tg.setTextColor(Color.WHITE)
            } else {
                problem_failure_tg.setTextColor(Color.BLACK)
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