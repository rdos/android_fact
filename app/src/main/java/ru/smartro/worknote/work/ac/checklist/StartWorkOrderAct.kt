package ru.smartro.worknote.work.ac.checklist

import android.app.Application
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmModel
import kotlinx.android.synthetic.main.item_container_adapter.view.choose_title
import kotlinx.android.synthetic.main.start_act__rv_item.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.Status
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.work.WoRKoRDeR_know1
import ru.smartro.worknote.work.ac.PERMISSIONS
import ru.smartro.worknote.MapAct
import ru.smartro.worknote.abs.ActAbstract

class StartWorkOrderAct : ActAbstract() {

    private var workOrders: List<WoRKoRDeR_know1>? = null
    private val vm: WayTaskViewModel by viewModel()

    override fun onNewGPS() {
        // TODO: r_dos!!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MyUtil.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }
        setContentView(R.layout.act_start_workorder)
        supportActionBar?.title = "Сменное Задание"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val rv = findViewById<RecyclerView>(R.id.rv_act_start_workorder)
        rv.layoutManager = LinearLayoutManager(this)
        val acbSelectAll = findViewById<AppCompatButton>(R.id.acb_act_start_workorder__select_all)
        acbSelectAll.setOnClickListener {
            
            if (workOrders!!.isEmpty()) {
                return@setOnClickListener
            }
            var checkName: String? = workOrders!![0].waste_type?.name
            for(workOrder in workOrders!!) {
                if (checkName == workOrder.waste_type?.name) {
                    checkName = workOrder.waste_type?.name
                } else {
                    toast("Нельзя одновременно взять два задания с разными типами отходов")
                    return@setOnClickListener
                }
            }
            gotoNextAct(null)
        }
        showingProgress(getPutExtraParam_NAME())
        vm.networkDat.getWorkOrder(paramS().getOwnerId(), paramS().wayBillId)
            .observe(this, Observer { result ->
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        workOrders = data!!.dataKnow100.woRKoRDeRknow1s
                        insertWayTask(workOrders!!)
                        rv.adapter = WayTaskAdapter(workOrders!!)
                        if (workOrders!!.size == 1) {
                            gotoNextAct(workOrders!![0].id)
                        }
                        hideProgress()
                    }
                    Status.ERROR -> {
                        toast(result.msg)
                        hideProgress()
                    }
                    Status.NETWORK -> {
                        toast("Проблемы с интернетом")
                        hideProgress()
                    }
                }
            })
    }

    fun insertWayTask(workOrderList: List<WoRKoRDeR_know1>?) {
        if (workOrderList == null) {
            return
        }
        vm.baseDat.clearDataBase()
        for (workOrder in workOrderList) {
            try {
                vm.baseDat.insertWayTask(workOrder)
            } catch (ex: Exception) {
                Log.e(TAG, "insertWayTask", ex)
                oops()
            }
        }
    }

    fun gotoNextAct(workorderId: Int?) {
        val intent = Intent(this, MapAct::class.java)
        //or
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        workorderId?.let {
            intent.putExtra(PUT_EXTRA_PARAM_ID, workorderId)
        }
        startActivity(intent)
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

    inner class WayTaskAdapter(private val p_woRKoRDeRknow1List: List<WoRKoRDeR_know1>) :
        RecyclerView.Adapter<WayTaskAdapter.OwnerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.start_act__rv_item, parent, false)
            return OwnerViewHolder(view)
        }

        override fun getItemCount(): Int {
            return p_woRKoRDeRknow1List.size
        }

        override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
            val workOrder = p_woRKoRDeRknow1List[position]

            holder.itemView.choose_title.text = workOrder.name

            if (workOrder.waste_type != null) {
                holder.itemView.choose_st.text = workOrder.waste_type.name
                holder.itemView.choose_st.setTextColor(Color.parseColor("#${workOrder.waste_type.color.hex}"))
            }

            when {
                workOrder.beginnedAt != null && workOrder.finishedAt == null -> holder.itemView.wo_status.apply {
                    text = "В работе"
                    setTextColor(getColor(R.color.yellow))
                }
                workOrder.finishedAt != null -> holder.itemView.wo_status.apply {
                    text = "Завершено"
                    setTextColor(getColor(R.color.green))
                }
                else -> holder.itemView.wo_status.apply {
                    text = "Новое"
                }
            }

            holder.itemView.setOnClickListener {
                if(workOrder.finishedAt == null) {
                    setAntiErrorClick(holder.itemView)
                    gotoNextAct(workOrder.id)
                } else {
                    toast("Задание завершено")
                }
            }
        }
        inner class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    }

    open class WayTaskViewModel(application: Application) : BaseViewModel(application) {

        fun <E : RealmModel?> createObjectFromJson(clazz: Class<E>, json: String): E {
            return baseDat.createObjectFromJson(clazz, json)
        }


    }


}