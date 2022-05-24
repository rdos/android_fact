package ru.smartro.worknote.work.ui

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.act_platform_failure.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.awORKOLDs.extensions.toast
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.awORKOLDs.util.PhotoTypeEnum

class PlatformFailureAct : ActNOAbst() {
    private lateinit var mAcactvFailureIn: AppCompatAutoCompleteTextView
    private lateinit var platform: PlatformEntity
    private val viewModel: NonPickupPlatformViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_platform_failure)
        val baseview = findViewById<ConstraintLayout>(R.id.baseview)
        baseview.setOnClickListener { MyUtil.hideKeyboard(this) }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        intent.let {
            platform = viewModel.findPlatformEntity(it.getIntExtra("platform_id", 0))
            supportActionBar!!.title = "Невывоз на площадке"
        }

        mAcactvFailureIn = findViewById(R.id.acactv_act_non_pickup__failure_in)
        val failReason = viewModel.findFailReason()
        mAcactvFailureIn.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, failReason))
        val tilFailureOut = findViewById<TextInputLayout>(R.id.til_act_non_pickup__failure_out)
        tilFailureOut.setOnClickListener {
            mAcactvFailureIn.showDropDown()
        }

        initExtremeProblemPhoto()

        val btnAccept = findViewById<Button>(R.id.btn_non_pickup__accept)
        btnAccept.setOnClickListener {
            if (!mAcactvFailureIn.text.isNullOrEmpty()) {
                val problemComment = problem_comment.text.toString()
                val failure = mAcactvFailureIn.text.toString()
                viewModel.updatePlatformProblem(
                    platformId = platform.platformId!!,
                    problemComment = problemComment, problem = failure)
                setResult(99)
                finish()
                showingProgress()
            } else {
                toast("Выберите причину невывоза")
            }
        }

    }
    
    private fun initImageView() {
        val platform = viewModel.findPlatformEntity(platform.platformId!!)
        Glide.with(this).load(MyUtil.base64ToImage(platform.failureMedia.last()?.image))
            .into(problem_img)
    }

    private fun initExtremeProblemPhoto() {
        val intent = Intent(this, CameraAct::class.java)
        intent.putExtra("platform_id", platform.platformId)
        intent.putExtra("photoFor", PhotoTypeEnum.forPlatformProblem)
        startActivityForResult(intent, 13)
        acb_activity_platform_serve__problem.setOnClickListener {
            startActivityForResult(intent, 13)
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

    class NonPickupPlatformViewModel(application: Application) : BaseViewModel(application) {

        fun findPlatformEntity(platformId: Int): PlatformEntity {
            return baseDat.findPlatformEntity(platformId)
        }

        fun updatePlatformProblem(platformId: Int, problemComment: String,
                                  problem: String) {
            baseDat.updateNonPickupPlatform(platformId, problemComment, problem)
        }

        fun findFailReason(): List<String> {
            return baseDat.findAllFailReason()
        }

    }
}