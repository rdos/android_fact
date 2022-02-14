package ru.smartro.worknote.ui.problem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_container_extreme.*
import kotlinx.android.synthetic.main.activity_problem.*
import kotlinx.android.synthetic.main.activity_problem.baseview
import kotlinx.android.synthetic.main.activity_problem.problem_accept_btn
import kotlinx.android.synthetic.main.activity_problem.acb_activity_platform_serve__problem
import kotlinx.android.synthetic.main.activity_problem.problem_choose_failure_out
import kotlinx.android.synthetic.main.activity_problem.problem_comment
import kotlinx.android.synthetic.main.activity_problem.problem_img
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.base.AbstractAct
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.ProblemEnum

class ExtremeProblemActivity : AbstractAct() {
    private lateinit var platform: PlatformEntity
    private lateinit var container: ContainerEntity
    private var mIsContainer = false
    private val viewModel: ProblemViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_extreme)
        baseview.setOnClickListener { MyUtil.hideKeyboard(this) }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent.let {
            platform = viewModel.findPlatformEntity(it.getIntExtra("platform_id", 0))
            mIsContainer = it.getBooleanExtra("is_container", false)
            if (mIsContainer) {
                supportActionBar!!.title = "Невывоз контейнера"
                container = viewModel.findContainerEntity(it.getIntExtra("container_id", 0))
            } else {
                supportActionBar!!.title = "Невывоз на площадке"
            }
        }
        initViews()
        initExtremeProblemPhoto()
        acceptExtremeProblem()
    }

    private fun initViews() {
        val failReason = viewModel.findFailReason()
        problem_choose_failure_in.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, failReason))
        problem_choose_failure_out.setOnClickListener {
            problem_choose_failure.showDropDown()
        }
    }

    private fun initImageView() {
        val platform = viewModel.findPlatformEntity(platform.platformId!!)
        if (mIsContainer) {
            Glide.with(this).load(MyUtil.base64ToImage(container.failureMedia.last()?.image))
                .into(problem_img)
        } else {
            Glide.with(this).load(MyUtil.base64ToImage(platform.failureMedia.last()?.image))
                .into(problem_img)
        }
    }

    private fun initExtremeProblemPhoto() {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("platform_id", platform.platformId)
        if (mIsContainer) {
            intent.putExtra("photoFor", PhotoTypeEnum.forContainerProblem)
            intent.putExtra("container_id", container.containerId)
        } else {
            intent.putExtra("photoFor", PhotoTypeEnum.forPlatformProblem)
        }
        startActivityForResult(intent, 13)
        acb_activity_platform_serve__problem.setOnClickListener {
            startActivityForResult(intent, 13)
        }
    }

    private fun acceptExtremeProblem() {
        problem_accept_btn.setOnClickListener {
            if (!problem_choose_failure_in.text.isNullOrEmpty()) {
                val problemComment = problem_comment.text.toString()
                val failure = problem_choose_failure_in.text.toString()
                if (mIsContainer) {
                    viewModel.updateContainerProblem(
                        platformId = platform.platformId!!, containerId = container.containerId!!,
                        problemComment = problemComment, problemType = ProblemEnum.ERROR, problem = failure, failProblem = null
                    )
                } else {
                    viewModel.updatePlatformProblem(
                        platformId = platform.platformId!!,
                        problemComment = problemComment, problem = failure, problemType = ProblemEnum.ERROR, failProblem = null
                    )
                }
                setResult(99)
                finish()
            } else {
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