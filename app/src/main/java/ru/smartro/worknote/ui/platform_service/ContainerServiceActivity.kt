package ru.smartro.worknote.ui.platform_service

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_container_service.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.container_service.PercentAdapter
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.database.entity.work_order.ContainerEntity
import ru.smartro.worknote.ui.problem.ProblemActivity
import ru.smartro.worknote.util.MyUtil

class ContainerServiceActivity : AppCompatActivity() {
    private val REQUEST_EXIT = 41
    private lateinit var percentAdapter: PercentAdapter
    private val viewModel: PlatformServiceViewModel by viewModel()
    private var platformId = 0
    private var containerId = 0
    private lateinit var containerEntity: ContainerEntity

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
        containerEntity = viewModel.findContainerEntity(containerId)

        enter_info_problem_btn.setOnClickListener {
            val intent = Intent(this, ProblemActivity::class.java)
            intent.putExtra("is_container", true)
            intent.putExtra("container_id", containerId)
            intent.putExtra("platform_id", platformId)
            startActivityForResult(intent, REQUEST_EXIT)
        }

        supportActionBar?.title = containerEntity.number
        percentAdapter = PercentAdapter(this, arrayListOf(0, 25, 50, 75, 100, 125))
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

}