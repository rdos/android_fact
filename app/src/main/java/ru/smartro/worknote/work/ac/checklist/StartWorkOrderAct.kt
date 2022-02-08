package ru.smartro.worknote.work.ac.checklist

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmModel
import kotlinx.android.synthetic.main.item_container_adapter.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.base.AbstractAct
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.loadingShow
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.work.AppPreferences
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.work.WoRKoRDeR_know1
import ru.smartro.worknote.work.ac.map.MapAct

class StartWorkOrderAct : AbstractAct() {

    private val vm: WayTaskViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_start_workorder)
        supportActionBar?.title = "Выберите сменное задание"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val rv = findViewById<RecyclerView>(R.id.rv_act_start_workorder)
        rv.layoutManager = LinearLayoutManager(this)
        val acbSelectAll = findViewById<AppCompatButton>(R.id.acb_act_start_workorder__select_all)
        acbSelectAll.setOnClickListener {
            // TODO: добавить логирование
            gotoNextAct(null)
        }
        loadingShow(getPutExtraParam_NAME())
        // TODO:r_dos!!пох их код
        vm.networkDat.getWorkOrder(AppPreferences.organisationId, AppPreferences.wayBillId)
            .observe(this, Observer { result ->
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        loadingHide()
                        val workOrders = data!!.dataKnow100.woRKoRDeRknow1s
                        insertWayTask(workOrders)
                        rv.adapter = WayTaskAdapter(workOrders)
                        if (workOrders.size == 1) {
                            gotoNextAct(workOrders[0].id)
                        }
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
//        AppPreferences.wayTaskId = workorder.id
        //        r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!
        // r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!
        // r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!
        // r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!
        // r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!
        // r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!
        // r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!
        // r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!
//        todo:show must go on ))
        val intent = Intent(this, MapAct::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        workorderId?.let {
            intent.putExtra(PUT_EXTRA_PARAM_ID, workorderId)
        }
        startActivity(intent)
        finish()
        // r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!
        // r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!
        // r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!
        // r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!
        // r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!
        // r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!
        // r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos! r_dos!

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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.start_act__rv_item_know1, parent, false)
            return OwnerViewHolder(view)
        }

        override fun getItemCount(): Int {
            return p_woRKoRDeRknow1List.size
        }

        override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
            val workOrder = p_woRKoRDeRknow1List[position]

            holder.itemView.choose_title.text = workOrder.name
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