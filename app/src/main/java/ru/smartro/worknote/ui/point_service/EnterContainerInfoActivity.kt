package ru.smartro.worknote.ui.point_service

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_enter_container_info_acitivty.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.container_service.PercentAdapter
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.container_service.ServedContainerInfoEntity
import ru.smartro.worknote.service.database.entity.way_task.ContainerInfoEntity
import ru.smartro.worknote.service.database.entity.way_task.WayPointEntity
import ru.smartro.worknote.ui.problem.ContainerProblemActivity
import ru.smartro.worknote.util.StatusEnum

class EnterContainerInfoActivity : AppCompatActivity() {
    private val REQUEST_EXIT = 41
    private lateinit var containerInfo: ContainerInfoEntity
    private lateinit var percentAdapter: PercentAdapter
    private lateinit var wayPoint: WayPointEntity
    private val viewModel: PointServiceViewModel by viewModel()
    private var wayPointId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_container_info_acitivty)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        intent.let {
            containerInfo = Gson().fromJson(it.getStringExtra("container_info"), ContainerInfoEntity::class.java)
            wayPointId = it.getIntExtra("wayPointId", 0)
            wayPoint = Gson().fromJson(it.getStringExtra("wayPoint"), WayPointEntity::class.java)
        }
        enter_info_problem_btn.setOnClickListener {
            val intent = Intent(this, ContainerProblemActivity::class.java)
            intent.putExtra("wayPoint", Gson().toJson(wayPoint))
            intent.putExtra("isContainerProblem", true)
            intent.putExtra("container_info", Gson().toJson(containerInfo))
            startActivityForResult(intent, REQUEST_EXIT)
        }
        supportActionBar?.title = containerInfo.number
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
        val container = ServedContainerInfoEntity(
            cId = containerInfo.id, comment = comment_et.text.toString(), oid = AppPreferences.organisationId,
            volume = percentAdapter.getSelectedCount(), woId = AppPreferences.wayListId
        )
        viewModel.addServedContainerInfo(container, wayPointId)
        if (viewModel.currentContainerStatus(wayPoint.id!!, containerInfo.id!!) == StatusEnum.empty) {
            viewModel.updateContainerStatus(wayPointId, containerInfo.id!!, StatusEnum.completed)
        }
        val intent = Intent()
        intent.putExtra("filledContainer", 1)
        setResult(Activity.RESULT_OK, intent)
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