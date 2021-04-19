package ru.smartro.worknote.ui.problem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_container_problem.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.database.entity.problem.BreakDownEntity
import ru.smartro.worknote.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.service.database.entity.way_task.ContainerEntity
import ru.smartro.worknote.service.database.entity.way_task.PlatformEntity
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.ProblemEnum

class ExtremeProblemActivity : AppCompatActivity() {
    private lateinit var breakDown: List<BreakDownEntity>
    private lateinit var failureReason: List<FailReasonEntity>
    private var isContainerProblem = false
    private lateinit var platform: PlatformEntity
    private lateinit var container: ContainerEntity
    private val viewModel: ProblemViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_extreme)
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
        initViews()
        initExtremeProblemPhoto()
        acceptExtremeProblem()
    }

    private fun initViews() {
        val failReason = viewModel.findFailReason()
        val failReasonString = failReason.map { it.problem }
        problem_choose_failure.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, failReasonString))
        problem_choose_failure_out.setOnClickListener {
            problem_choose_failure.showDropDown()
        }
    }

    private fun initImageView() {
        val wayPoint = viewModel.findPlatformEntity(platform.platformId!!)
        if (isContainerProblem) {
            Glide.with(this).load(MyUtil.base64ToImage(wayPoint!!.mediaContainerProblem!!.last())).into(problem_img)
        } else {
            Glide.with(this).load(MyUtil.base64ToImage(wayPoint!!.mediaPlatformProblem!!.last())).into(problem_img)
        }
    }

    private fun initExtremeProblemPhoto() {
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

    private fun acceptExtremeProblem() {
        problem_accept_btn.setOnClickListener {
            if (!problem_choose_failure.text.isNullOrEmpty()) {
                val problemComment = problem_comment.text.toString()
                val failure =  problem_choose_failure.text.toString()
                viewModel.updatePlatformProblem(
                    platformId = platform.platformId!!,
                    problemComment = problemComment, problem = failure, problemType = ProblemEnum.FAILURE, failProblem = null
                )
                finish()
            }else{
                toast("Выберите причину поломки")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 && resultCode == Activity.RESULT_OK) {
            initImageView()
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