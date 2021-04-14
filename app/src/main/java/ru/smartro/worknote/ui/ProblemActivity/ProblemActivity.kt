package ru.smartro.worknote.ui.ProblemActivity

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
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_container_problem.*
import kotlinx.android.synthetic.main.alert_warning_camera.view.accept_btn
import kotlinx.android.synthetic.main.alert_warning_delete.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.extensions.hideDialog
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.extensions.warningContainerFailure
import ru.smartro.worknote.service.database.entity.problem.BreakDownEntity
import ru.smartro.worknote.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.service.database.entity.way_task.ContainerEntity
import ru.smartro.worknote.service.database.entity.way_task.PlatformEntity
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.ProblemEnum

class ProblemActivity : AppCompatActivity() {
    private lateinit var breakDown: List<BreakDownEntity>
    private lateinit var failureReason: List<FailReasonEntity>
    private var isContainerProblem = false
    private lateinit var platform: PlatformEntity
    private lateinit var container: ContainerEntity
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
            platform = Gson().fromJson(it.getStringExtra("wayPoint"), PlatformEntity::class.java)
            isContainerProblem = it.getBooleanExtra("isContainerProblem", false)
            if (isContainerProblem) {
                container = Gson().fromJson(it.getStringExtra("container_info"), ContainerEntity::class.java)
            }
        }
        initProblemPhoto()
        initSelectors()
        acceptProblem()
    }

    private fun initImageView() {
        val wayPoint = viewModel.findPlatformEntity(platform.platformId!!)
        if (isContainerProblem) {
            Glide.with(this).load(wayPoint!!.mediaContainerProblem!!.last()).into(problem_img)
        } else {
            Glide.with(this).load(wayPoint!!.mediaPlatformProblem!!.last()).into(problem_img)
        }
    }

    private fun initProblemPhoto() {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("wayPoint", Gson().toJson(platform))
        Log.d("POINT_PROBLEM", "initProblemPhoto: ${Gson().toJson(platform)}")
        if (isContainerProblem) {
            intent.putExtra("photoFor", PhotoTypeEnum.forContainerProblem)
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
                        if (isContainerProblem) {
                            viewModel.updateContainerProblem(
                                platformId = platform.platformId!!, containerId = container.containerId!!, problemComment = problemComment,
                                problem = problem, problemType = ProblemEnum.BREAKDOWN, failProblem = null
                            )
                        } else {
                            viewModel.updatePlatformProblem(
                                platformId = platform.platformId!!,
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
                                if (isContainerProblem) {
                                    viewModel.updateContainerProblem(
                                        platformId = platform.platformId!!, containerId = container.containerId!!, problemComment = problemComment,
                                        problem = problem, problemType = ProblemEnum.FAILURE, failProblem = null
                                    )
                                } else {
                                    viewModel.updatePlatformProblem(
                                        platformId = platform.platformId!!,
                                        problemComment = problemComment, problem = problem, problemType = ProblemEnum.FAILURE, failProblem = null
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
                                if (isContainerProblem) {
                                    viewModel.updateContainerProblem(
                                        platformId = platform.platformId!!,   containerId = container.containerId!!, problemComment = problemComment,
                                        problem = breakdown, problemType = ProblemEnum.BOTH, failProblem = failure)
                                } else {
                                    viewModel.updatePlatformProblem(
                                        platformId = platform.platformId!!,
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
        val failReasonString = failReason.map { it.problem }
        problem_choose_failure.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, failReasonString))
        val breakdowns = viewModel.findBreakDown()
        val breakdownsString = breakdowns.map { it.problem }
        problem_choose_breakdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, breakdownsString))

        problem_breakdown_tg.setOnCheckedChangeListener { compoundButton, check ->
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

        problem_failure_tg.setOnCheckedChangeListener { compoundButton, check ->
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