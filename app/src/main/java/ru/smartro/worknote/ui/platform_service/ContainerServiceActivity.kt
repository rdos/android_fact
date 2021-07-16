package ru.smartro.worknote.ui.platform_service

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_container_service.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.container_service.PercentAdapter
import ru.smartro.worknote.adapter.container_service.PercentModel
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity
import ru.smartro.worknote.util.MyUtil

class ContainerServiceActivity : AppCompatActivity() {
    private val REQUEST_EXIT = 41
    private lateinit var percentAdapter: PercentAdapter
    private val viewModel: PlatformServiceViewModel by viewModel()
    private var platformId = 0
    private var containerId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_service)
        baseview.setOnClickListener {
            MyUtil.hideKeyboard(this)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        intent.let {
            containerId = it.getIntExtra("container_id", 0)
            platformId = it.getIntExtra("platform_id", 0)
        }

        enter_info_problem_btn.setOnClickListener {
            val intent = Intent(this, ExtremeProblemActivity::class.java)
            intent.putExtra("is_container", true)
            intent.putExtra("container_id", containerId)
            intent.putExtra("platform_id", platformId)
            startActivityForResult(intent, REQUEST_EXIT)
        }

        comment_clear.setOnClickListener {
            comment_et.setText("")
        }

        viewModel.findContainerEntity(containerId).let {
            supportActionBar?.title = it.number
            comment_et.setText(it.comment)
            percentAdapter = if (it.volume != null) {
                PercentAdapter(this, selectedPercents(it.volume!!, true))
            } else {
                PercentAdapter(this, selectedPercents(it.volume!!, false))

            }
        }
        enter_info_percent_rv.adapter = percentAdapter
        back_button.setOnClickListener {
            finish()
        }
        save_btn.setOnClickListener {
            if (percentAdapter.getSelectedCount() != -1.00) {
                completeContainer()
            } else {
                toast("Выберите один из вариантов заполненности")
            }
        }
    }

    private fun completeContainer() {
        val volume = percentAdapter.getSelectedCount()
        val comment = comment_et.text.toString()
        viewModel.updateContainerVolume(platformId, containerId, volume, comment)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EXIT && resultCode == 99) {
            setResult(99)
            finish()
        }
    }

    fun selectedPercents(selectedVolume: Double, isNotNull: Boolean): ArrayList<PercentModel> {
        val resultPercents = arrayListOf<PercentModel>()
        val allPercents = arrayListOf(0.00, 0.25, 0.50, 0.75, 1.00, 1.25)
        if (isNotNull) {
            allPercents.forEach {
                resultPercents.add(PercentModel(it, it == selectedVolume))
            }
        } else {
            allPercents.forEach {
                resultPercents.add(PercentModel(it, false))
            }
        }
        return resultPercents
    }
}