package ru.smartro.worknote.ui.choose.way_task_4

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_choose.*
import kotlinx.android.synthetic.main.alert_accept_task.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.WayTaskAdapter
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.loadingShow
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.problem.ContainerBreakdownEntity
import ru.smartro.worknote.service.database.entity.problem.ContainerFailReasonEntity
import ru.smartro.worknote.service.database.entity.way_task.WayTaskEntity
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.ProgressBody
import ru.smartro.worknote.service.network.body.WayTaskBody
import ru.smartro.worknote.service.network.response.way_task.WayInfo
import ru.smartro.worknote.service.network.response.way_task.WayTaskResponse
import ru.smartro.worknote.ui.map.MapActivity

class WayTaskActivity : AppCompatActivity(), WayTaskAdapter.SelectListener {
    private val TAG = "WayTaskActivity_LOG"
    private val viewModel: WayTaskViewModel by viewModel()
    private lateinit var adapter: WayTaskAdapter
    private lateinit var selectedWayInfo: WayInfo
    private lateinit var wayTask: WayTaskResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Выберите сменное задание"
        setContentView(R.layout.activity_choose)

        loadingShow()
        viewModel.getWayTask(AppPreferences.wayListId, WayTaskBody(AppPreferences.organisationId))
            .observe(this, Observer { result ->
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        wayTask = data!!
                        adapter = WayTaskAdapter(data.data.wos as ArrayList<WayInfo>, this)
                        choose_rv.adapter = adapter
                        loadingHide()
                    }
                    Status.ERROR -> {
                        toast(result.msg)
                        loadingHide()
                    }
                    Status.NETWORK -> {
                        toast("Проблемы с интернетом")
                        loadingHide()
                    }
                }
            })

        next_btn.setOnClickListener {
            if (adapter.getSelectedId() == -1) {
                toast("Выберите задание")
            } else {
                val inflater = this.layoutInflater
                val view = inflater.inflate(R.layout.alert_accept_task, null)
                val builder = AlertDialog.Builder(this).setView(view)
                builder.setCancelable(false)
                val dialog = builder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()

                view.accept_btn.setOnClickListener {
                    loadingShow()
                    AppPreferences.wayTaskId = adapter.getSelectedId()
                    saveFailReason()
                    saveBreakDownTypes(dialog)
                }

                view.dismiss_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun saveBreakDownTypes(dialog: AlertDialog) {
        viewModel.getBreakDownTypes().observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val entities = result.data?.data?.filter {
                            it.attributes.organisationId == AppPreferences.organisationId
                        }?.map {
                            ContainerBreakdownEntity(it.attributes.id, it.attributes.name)
                        }
                        withContext(Dispatchers.Main) {
                            viewModel.insertBreakDown(entities!!)
                            acceptProgress()
                        }
                    }
                }
                Status.ERROR -> {
                    toast(result.msg)
                }
            }

        })
    }

    private fun saveFailReason() {
        viewModel.getFailReason().observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    val entities = result.data?.data?.filter {
                        it.oid == AppPreferences.organisationId }!!.map {
                        ContainerFailReasonEntity(it.id, it.name) }
                    viewModel.insertFailReason(entities)
                }
                Status.ERROR -> {
                    toast(result.msg)
                }
            }
        })
    }

    private fun acceptProgress() {
        viewModel.progress(AppPreferences.wayTaskId, ProgressBody(System.currentTimeMillis() / 1000L))
            .observe(this, Observer { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        loadingHide()
                        viewModel.beginTransaction()
                        AppPreferences.thisUserHasTask = true
                        val convertedToJson = Gson().toJson(selectedWayInfo)
                        val wayTaskEntityFromJson = viewModel.createObjectFromJson(WayTaskEntity::class.java, convertedToJson)
                        Log.d(TAG, "1 CONVERT $convertedToJson")
                        Log.d(TAG, "2 CONVERT ${Gson().toJson(wayTaskEntityFromJson)}")
                        viewModel.insertWayTask(wayTaskEntityFromJson)
                        viewModel.commitTransaction()
                        startActivity(Intent(this, MapActivity::class.java))
                        finish()
                    }
                    Status.ERROR -> {
                        toast(result.msg)
                        loadingHide()
                    }
                }
            })
    }

    override fun selectedWayTask(model: WayInfo) {
        selectedWayInfo = model
    }
}