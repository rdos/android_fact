package ru.smartro.worknote.work.ac.choose

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmModel
import kotlinx.android.synthetic.main.activity_choose.*
import kotlinx.android.synthetic.main.item_choose.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.base.AbstractAct
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.loadingShow
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.work.Workorder
import ru.smartro.worknote.work.ac.map.MapAct
import ru.smartro.worknote.work.WorkOrderResponse

class TaskWorkorderAct : AbstractAct() {

    private val vm: WayTaskViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)
        supportActionBar?.title = "Выберите сменное задание"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loadingShow()
        vm.getWorkOrder(AppPreferences.organisationId, AppPreferences.wayBillId)
            .observe(this, Observer { result ->
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        val workorders = data!!.data.workorders
                        vm.db.deleteWorkOrders()
                        vm.insertWayTask(workorders)
                        choose_rv.adapter = WayTaskAdapter(workorders)
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

        act_choose_select_all.setOnClickListener {
            // TODO: добавить логирование
            gotoShowMapAct(null)
        }
    }

    fun gotoShowMapAct(workorderId: Int?) {
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        workorderId?.let {
            intent.putExtra( ACT_PUT_EXTRA_NAME, workorderId)
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

    inner class WayTaskAdapter(private val p_workorderList: List<Workorder>) :
        RecyclerView.Adapter<WayTaskAdapter.OwnerViewHolder>() {
        private var checkedPosition = -1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_choose, parent, false)
            return OwnerViewHolder(view)
        }

        override fun getItemCount(): Int {
            return p_workorderList.size
        }

        override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
            val workorder = p_workorderList[position]

            if (checkedPosition == -1) {
                holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.itemView.choose_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))

            } else {
                if (checkedPosition == holder.adapterPosition) {
                    holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
                    holder.itemView.choose_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                } else {
                    holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                    holder.itemView.choose_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                }
            }

            holder.itemView.choose_title.text = workorder.name
            holder.itemView.setOnClickListener {
                holder.itemView.choose_cardview.isVisible = true
                if (checkedPosition != holder.adapterPosition) {
                    holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
                    holder.itemView.choose_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                    notifyItemChanged(checkedPosition)
                    checkedPosition = holder.adapterPosition

                    val workorderList = mutableListOf<Int>()
                    workorderList.add(workorder.id)
                    gotoShowMapAct(null)

                }
            }
        }

//        fun getSelectedId(): Int {
//            return if (checkedPosition != -1) {
//                p_workorderList[checkedPosition].id
//            } else {
//                -1
//            }
//        }

        inner class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }


    }

    open class WayTaskViewModel(application: Application) : BaseViewModel(application) {

/*
    fun getWayTask(wayId: Int, wayTaskBody: WayTaskBody): LiveData<Resource<WayTaskResponse>> {
        return network.getWayTask(wayId, wayTaskBody)
    }

*/

        fun insertWayTask(workorderList: List<Workorder>) {
            db.deleteWorkOrders()
            for (workorder in workorderList) {
                try {
                    db.insertWayTask(workorder)
                } catch (ex: Exception) {
//                    logSentry("insertWayTask.ex.Exception" + ex.message)
                }
            }
        }
        fun getWorkOrder(organisationId: Int, wayId: Int): LiveData<Resource<WorkOrderResponse>> {
            Log.d("AAA", "getWorkOrder")
            return network.getWorkOder(organisationId, wayId)
        }

        fun <E : RealmModel?> createObjectFromJson(clazz: Class<E>, json: String): E {
            return db.createObjectFromJson(clazz, json)
        }



    }


}