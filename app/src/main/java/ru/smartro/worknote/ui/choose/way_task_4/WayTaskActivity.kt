package ru.smartro.worknote.ui.choose.way_task_4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_choose.*
import kotlinx.android.synthetic.main.item_choose.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.base.AbstractAct
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.loadingShow
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.ProgressBody
import ru.smartro.worknote.service.network.response.EmptyResponse
import ru.smartro.worknote.work.Workorder
import ru.smartro.worknote.ui.map.MapActivity
import ru.smartro.worknote.util.MyUtil

class WayTaskActivity : AbstractAct() {
    private var mWorkorders: List<Workorder> = emptyList()
    private val viewModel: WayTaskViewModel by viewModel()

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
                        mWorkorders = data!!.data.workorders
                        choose_rv.adapter = WayTaskAdapter(mWorkorders)
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
            gotoProgressWorkOrder(mWorkorders)
        }
    }

    fun gotoProgressWorkOrder(workorders: List<Workorder>) {
//        AppPreferences.wayTaskId = workorder.id
        loadingShow()
        try {
            saveFailReason()
            saveCancelWayReason()
            saveBreakDownTypes()
//                    val hand = Handler(Looper.getMainLooper())
            for (workorder in workorders) {
                logSentry(workorder.name)
                val result = acceptProgress(workorder)
                    when (result.status) {
                        Status.SUCCESS -> {
                            logSentry("acceptProgress Status.SUCCESS ")
                            AppPreferences.isHasTask = true
                            viewModel.insertWayTask(workorder)
                        }
                        else -> {
                            logSentry( "acceptProgress Status.ERROR")
                            toast(result.msg)
                            AppPreferences.isHasTask = false
                            break
                        }
                    }
            }
        } finally {
            loadingHide()
            if (AppPreferences.isHasTask) {
                val intent = Intent(this, MapActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun saveBreakDownTypes() {
        viewModel.getBreakDownTypes().observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    // TODO: ПО голове себе постучи
                    Log.d(TAG, "saveBreakDownTypes. Status.SUCCESS")
                }
                Status.ERROR -> {
                    toast(result.msg)
                }
                else -> Log.d(TAG, "saveBreakDownTypes:")
            }

        })
    }

    private fun saveFailReason() {
        Log.i(TAG, "saveFailReason.before")
        viewModel.getFailReason().observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "saveFailReason. Status.SUCCESS")
                }
                Status.ERROR -> {
                    toast(result.msg)
                }
            }
        })
    }

    private fun saveCancelWayReason() {
        Log.d(TAG, "saveCancelWayReason.before")
        viewModel.getCancelWayReason().observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "saveCancelWayReason. Status.SUCCESS")
                }
                Status.ERROR -> {
                    Log.d(TAG, "saveCancelWayReason. Status.ERROR")
                    toast(result.msg)
                }
            }
        })
    }

    private fun acceptProgress(workorder: Workorder): Resource<EmptyResponse> {
        Log.d(TAG, "acceptProgress.before")
        val res = viewModel.progress(workorder.id, ProgressBody(MyUtil.timeStamp()))
        return res

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

                    val workorderList = mutableListOf<Workorder>()
                    workorderList.add(workorder)
                    gotoProgressWorkOrder(workorderList)

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

}