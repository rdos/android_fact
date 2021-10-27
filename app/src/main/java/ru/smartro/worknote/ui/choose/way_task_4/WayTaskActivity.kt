package ru.smartro.worknote.ui.choose.way_task_4

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_choose.*
import kotlinx.android.synthetic.main.alert_accept_task.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.WayTaskAdapter
import ru.smartro.worknote.base.AbstractAct
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.loadingShow
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.problem.BreakDownEntity
import ru.smartro.worknote.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.ProgressBody
import ru.smartro.worknote.service.network.response.work_order.Workorder
import ru.smartro.worknote.ui.map.MapActivity
import ru.smartro.worknote.util.MyUtil

class WayTaskActivity : AbstractAct(), WayTaskAdapter.SelectListener {
    private val viewModel: WayTaskViewModel by viewModel()
    private lateinit var adapter: WayTaskAdapter
    private lateinit var mSelectedWayInfo: Workorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)
        supportActionBar?.title = "Выберите сменное задание"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loadingShow()
        viewModel.getWorkOrder(AppPreferences.organisationId, AppPreferences.wayBillId)
            .observe(this, Observer { result ->
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        adapter = WayTaskAdapter(data!!.data.workorders as ArrayList<Workorder>, this)
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
                    saveCancelWayReason()
                    saveBreakDownTypes()
                    acceptProgress()
                    dialog.dismiss()
                }

                view.dismiss_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }




    private fun saveBreakDownTypes() {
        viewModel.getBreakDownTypes().observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    val entities = result.data?.data?.filter {
                        it.attributes.organisationId == AppPreferences.organisationId
                    }?.map {
                        BreakDownEntity(it.attributes.id, it.attributes.name)
                    }
                    viewModel.insertBreakDown(entities!!)
                }
                Status.ERROR -> {
                    toast(result.msg)
                }
                else -> Log.d("dsadsa", "saveBreakDownTypes:")
            }

        })
    }

    private fun saveFailReason() {
        Log.i(TAG, "saveFailReason.before")
        viewModel.getFailReason().observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    val entities = result.data?.data?.filter {
                        it.oid == AppPreferences.organisationId
                    }!!.map {
                        FailReasonEntity(it.id, it.name)
                    }
                    viewModel.insertFailReason(entities)
                }
                Status.ERROR -> {
                    toast(result.msg)
                }
            }
        })
    }

    private fun saveCancelWayReason() {
        viewModel.getCancelWayReason().observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    val entities = result.data?.data?.filter {
                        it.attributes.organisationId == AppPreferences.organisationId
                    }!!.map { CancelWayReasonEntity(it.id, it.attributes.name) }
                    viewModel.insertCancelWayReason(entities)
                }
                Status.ERROR -> {
                    toast(result.msg)
                }
            }
        })
    }

    private fun acceptProgress() {
        viewModel.progress(AppPreferences.wayTaskId, ProgressBody(MyUtil.timeStamp()))
            .observe(this, Observer { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        loadingHide()
                        AppPreferences.isHasTask = true
                        viewModel.insertWayTask(mSelectedWayInfo)
                        val intent = Intent(this, MapActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else -> {
                        toast(result.msg)
                        loadingHide()
                    }
                }
            })
    }

    override fun selectedWayTask(model: Workorder) {
        mSelectedWayInfo = model
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