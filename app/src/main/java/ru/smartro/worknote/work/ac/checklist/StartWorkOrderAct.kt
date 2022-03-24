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
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.awORKOLDs.extensions.toast
import ru.smartro.worknote.awORKOLDs.service.network.Status
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.work.WoRKoRDeR_know1
import ru.smartro.worknote.work.ac.PERMISSIONS
import ru.smartro.worknote.work.MapAct

class StartWorkOrderAct : ActNOAbst() {

    private lateinit var workOrders: List<WoRKoRDeR_know1>
    private val vm: WayTaskViewModel by viewModel()

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
            // TODO: добавить логирование
            if (workOrders.size <= 0) {
                return@setOnClickListener
            }
            var checkName: String? = workOrders[0].waste_type?.name
            for(workOrder in workOrders) {
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
        // TODO:r_dos!!пох их код
        vm.networkDat.getWorkOrder(paramS().getOwnerId(), paramS().wayBillId)
            .observe(this, Observer { result ->
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        hideProgress()
                        workOrders = data!!.dataKnow100.woRKoRDeRknow1s
                        insertWayTask(workOrders)
                        rv.adapter = WayTaskAdapter(workOrders)
                        if (workOrders.size == 1) {
                            gotoNextAct(workOrders[0].id)
                        }
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

    fun insertWayTask(woRKoRDeRknow1List: List<WoRKoRDeR_know1>) {
        vm.baseDat.clearBase()
        for (workorder in woRKoRDeRknow1List) {
            try {
                vm.baseDat.insertWayTask(workorder)
            } catch (ex: Exception) {
                Log.e(TAG, "insertWayTask", ex)
                //ups должен быть один, иначе Инспектор скажт слово
                oops()
//                    logSentry("insertWayTask.ex.Exception" + ex.message)
            }
        }
    }

    fun gotoNextAct(workorderId: Int?) {
        if (isOopsMode()) {
            finish()
            return
        }
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
            holder.itemView.setOnClickListener {
                setAntiErrorClick(holder.itemView)
                gotoNextAct(workOrder.id)
            }
        }
        inner class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    }

    open class WayTaskViewModel(application: Application) : BaseViewModel(application) {

/*
        fun getWorkOrder(organisationId: Int, wayId: Int): LiveData<Resource<WorkOrderResponse_know1>> {
            Log.d("AAA", "getWorkOrder")
            return :r_dos)цент неОтроПа
        }


*/

        fun <E : RealmModel?> createObjectFromJson(clazz: Class<E>, json: String): E {
            return baseDat.createObjectFromJson(clazz, json)
        }


    }


}