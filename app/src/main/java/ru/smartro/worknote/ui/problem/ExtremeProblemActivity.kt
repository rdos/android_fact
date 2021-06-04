package ru.smartro.worknote.ui.problem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_problem.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.ProblemEnum

class ExtremeProblemActivity : AppCompatActivity() {
    private lateinit var platform: PlatformEntity
    private val viewModel: ProblemViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_extreme)
        baseview.setOnClickListener { MyUtil.hideKeyboard(this) }

        supportActionBar!!.title = "Проблема на площадке"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent.let {
            platform = viewModel.findPlatformEntity(it.getIntExtra("platform_id", 0))
        } 
        initViews()
        initExtremeProblemPhoto()
        acceptExtremeProblem()
    }

    private fun initViews() {
        val failReason = viewModel.findFailReason()
        problem_choose_failure.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, failReason))
        problem_choose_failure_out.setOnClickListener {
            problem_choose_failure.showDropDown()
        }
    }

    private fun initImageView() {
        val platform = viewModel.findPlatformEntity(platform.platformId!!)
        Glide.with(this).load(MyUtil.base64ToImage(platform.failureMedia.last()?.image))
            .into(problem_img)
    }

    private fun initExtremeProblemPhoto() {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("photoFor", PhotoTypeEnum.forPlatformProblem)
        intent.putExtra("platform_id", platform.platformId)
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
                    problemComment = problemComment, problem = failure, problemType = ProblemEnum.ERROR, failProblem = null
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