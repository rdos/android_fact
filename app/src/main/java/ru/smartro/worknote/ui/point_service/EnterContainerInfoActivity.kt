package ru.smartro.worknote.ui.point_service

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_enter_container_info_acitivty.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.container_service.PercentAdapter
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.db.entity.container_info.ContainerInfoEntity
import ru.smartro.worknote.service.response.way_task.ContainerInfo

class EnterContainerInfoActivity : AppCompatActivity() {
    private lateinit var containerInfo: ContainerInfo
    private lateinit var percentAdapter: PercentAdapter
    private val viewModel: PointServiceViewModel by viewModel()
    private var wayPointId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_container_info_acitivty)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        intent.let {
            containerInfo = it.getSerializableExtra("container_info") as ContainerInfo
            wayPointId = it.getIntExtra("wayPointId", 0)
        }
        supportActionBar?.title = containerInfo.number
        percentAdapter = PercentAdapter(this, arrayListOf(0, 25, 50, 75, 100, 125))
        enter_info_percent_rv.adapter = percentAdapter
        back_button.setOnClickListener {
            finish()
        }
        save_btn.setOnClickListener {
            saveContainerInfo()
            val intent = Intent()
            intent.putExtra("filledContainer", 1)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun saveContainerInfo() {
        val container = ContainerInfoEntity(id = 0, containerId = containerInfo.id, comment = comment_et.text.toString(), o_id = AppPreferences.organisationId, volume = percentAdapter.getSelectedCount(), wo_id = AppPreferences.wayTaskId, wayPointId = wayPointId)
        viewModel.insertContainer(container)
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