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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.act_container_breakdown.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.workold.base.BaseViewModel
import ru.smartro.worknote.workold.extensions.toast
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.workold.util.MyUtil
import ru.smartro.worknote.workold.util.PhotoTypeEnum
import ru.smartro.worknote.workold.util.NonPickupEnum

class ContainerBreakdownAct : ActNOAbst() {
    private lateinit var mAcactvFailureIn: AppCompatAutoCompleteTextView
    private lateinit var mAcactvBreakDownIn: AppCompatAutoCompleteTextView
    private lateinit var platform: PlatformEntity
    private lateinit var mContainer: ContainerEntity
    private val vs: ContainerBreakdownViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_container_breakdown)
        val baseview = findViewById<ConstraintLayout>(R.id.baseview)
        baseview.setOnClickListener { MyUtil.hideKeyboard(this) }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent.let {
            platform = vs.findPlatformEntity(it.getIntExtra("platform_id", 0))
            supportActionBar!!.title = "Поломка контейнера"
            mContainer = vs.findContainerEntity(it.getIntExtra("container_id", 0))
        }

          mAcactvBreakDownIn = findViewById(R.id.acactv_act_non_pickup__breakdown_in)
        val breakDown = vs.findBreakDown()
        mAcactvBreakDownIn.setAdapter(ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line,
            android.R.id.text1, breakDown))
        val tilBreakdownOut = findViewById<TextInputLayout>(R.id.til_act_non_pickup__breakdown_out)
        tilBreakdownOut.setOnClickListener {
            mAcactvBreakDownIn.showDropDown()
        }

        initExtremeProblemPhoto()

        val tietComment = findViewById<TextInputEditText>(R.id.tiet_act_container_breakdown__comment)
        tietComment.setText(mContainer.comment)

        val btnAccept = findViewById<Button>(R.id.btn_non_pickup__accept)
        btnAccept.setOnClickListener {

            if (mAcactvBreakDownIn.text.isNullOrEmpty()) {
                toast("Выберите причину поломки")
                return@setOnClickListener
            }
            if (!mAcactvBreakDownIn.text.isNullOrEmpty()) {
                val problemComment = tietComment.text.toString()
                val breakDown1 = mAcactvBreakDownIn.text.toString()
                vs.baseDat.updateContainerFailure(
                    platformId = platform.platformId!!, containerId = mContainer.containerId!!,
                    problemComment = problemComment, nonPickupType = NonPickupEnum.BREAKDOWN,
                    problem = breakDown1)

            }
            setResult(99)
            finish()
        }

    }


    private fun initImageView() {
        Glide.with(this).load(MyUtil.base64ToImage(mContainer.breakdownMedia.last()?.image))
            .into(problem_img)

    }

    private fun initExtremeProblemPhoto() {
        val intent = Intent(this, CameraAct::class.java)
        intent.putExtra("platform_id", platform.platformId)
        intent.putExtra("photoFor", PhotoTypeEnum.forContainerBreakdown)
        intent.putExtra("container_id", mContainer.containerId)
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

    class ContainerBreakdownViewModel(application: Application) : BaseViewModel(application) {


        fun findPlatformEntity(platformId: Int): PlatformEntity {
            return baseDat.findPlatformEntity(platformId)
        }

        fun findContainerEntity(containerId: Int): ContainerEntity {
            return baseDat.findContainerEntity(containerId)
        }


        fun findBreakDown(): List<String> {
            return baseDat.findAllBreakDown()
        }

        fun findFailReason(): List<String> {
            return baseDat.findAllFailReason()
        }

    }
}