package ru.smartro.worknote.ui.choose.way_task_4

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_choose.*
import kotlinx.android.synthetic.main.alert_accept_task.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.WayTaskAdapter
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.loadingShow
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.Status
import ru.smartro.worknote.service.body.ProgressBody
import ru.smartro.worknote.service.body.WayTaskBody
import ru.smartro.worknote.service.db.entity.way_task.WayTaskJsonEntity
import ru.smartro.worknote.service.response.way_task.WayPoint
import ru.smartro.worknote.service.response.way_task.WayInfo
import ru.smartro.worknote.service.response.way_task.WayTaskResponse
import ru.smartro.worknote.ui.map.MapActivity

class WayTaskActivity : AppCompatActivity(), WayTaskAdapter.SelectListener {
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
                    viewModel.progress(AppPreferences.wayTaskId, ProgressBody(System.currentTimeMillis() / 1000L))
                        .observe(this, Observer { result ->
                            when (result.status) {
                                Status.SUCCESS -> {
                                    loadingHide()
                                    AppPreferences.thisUserHasTask = true
                                    val wayTaskJsonString = Gson().toJson(selectedWayInfo)
                                    val entity = WayTaskJsonEntity(AppPreferences.userLogin, wayTaskJsonString)
                                    viewModel.insertWayTaskJson(entity)
                                    dialog.dismiss()
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

                view.dismiss_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    override fun selectedWayTask(model: WayInfo) {
        selectedWayInfo = model
    }
}