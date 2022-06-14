package ru.smartro.worknote

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ToggleButton
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.alert_successful_complete.view.*
import ru.smartro.worknote.awORKOLDs.extensions.showSuccessComplete
import ru.smartro.worknote.awORKOLDs.service.network.Status
import ru.smartro.worknote.awORKOLDs.util.MyUtil

import ru.smartro.worknote.work.RealmRepository
import ru.smartro.worknote.work.WorkOrderEntity
import ru.smartro.worknote.work.ac.StartAct
import ru.smartro.worknote.work.net.CancelWayReasonEntity
import ru.smartro.worknote.work.net.EarlyCompleteBody
import kotlin.math.round


class CompleteEarlyF : AFragment() {
    private lateinit var mReasonAdapter: ReasonAdapter

    private lateinit var mDatabase: RealmRepository

    companion object {
        fun newInstance(data: Any?): CompleteEarlyF {
            data as Int
            val fragment = CompleteEarlyF()
            fragment.addArgument(data)
            return fragment
        }
    }

    override fun onGetLayout(): Int {
        return R.layout.f_complete_early__map_act
    }

    /**Activity = это просто view + context + EVENT?*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val workOrderId = getArgumentID()
        mDatabase = App.getAppliCation().getDB()
        val workOrderS = mDatabase.getWorkOrderEntityS(true)

        val cancelWayReasonS = mDatabase.findCancelWayReasonEntity()

        val rvReason = view.findViewById<RecyclerView>(R.id.rv_f_complete_early__reason)
        rvReason.layoutManager = LinearLayoutManager(getAct())
        mReasonAdapter = ReasonAdapter(workOrderS, cancelWayReasonS)
        rvReason.adapter = mReasonAdapter

    }

    inner class ReasonAdapter(private val workOrderS: MutableList<WorkOrderEntity>,
                              private val cancelWayReasonS: List<CancelWayReasonEntity>) :
        RecyclerView.Adapter<ReasonAdapter.ReasonAdapterViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReasonAdapterViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.f_complete_early__map_act__rv_item, parent, false)
            return ReasonAdapterViewHolder(view)
        }

        override fun getItemCount(): Int {
            return workOrderS.size
        }

        override fun onBindViewHolder(holder: ReasonAdapterViewHolder, position: Int) {
            val workOrderEntity = workOrderS[position]
            val totalContainersVolume = mDatabase.findContainersVolume(workOrderEntity.id)

            holder.tiedTotalVolume.setText("$totalContainersVolume")
            holder.actvReason/** причина by lazy*/
            holder.tbVolume
            holder.tbWeight
            //todo:
            val view = holder.itemView
            val acbFinalResultsWorkOrderName = view.findViewById<AppCompatTextView>(R.id.acb_f_complete_early__final_results_workorder_name)
            acbFinalResultsWorkOrderName.text = "Итоговые показатели рейса ${workOrderEntity.id}(${workOrderEntity.name})"

            val acbAccept = view.findViewById<AppCompatButton>(R.id.acb_f_complete_early__accept)
            acbAccept.setOnClickListener {
                val hold = holder
                val workOrderId = workOrderEntity.id
                val workOrder = workOrderEntity
                //todo:!r_dos
                fun finishTask_know() {
                    Log.i(TAG, "finishTask")
                    getAct().modeSyNChrON_off()
                    mDatabase.clearDataBase()

                    getAct().showSuccessComplete().let {
                        it.finish_accept_btn.setOnClickListener {
                            getAct().startActivity(Intent(getAct(), StartAct::class.java))
                            getAct().finish()
                        }
                        it.exit_btn.setOnClickListener {
                            getAct().logout()
                        }
                    }
                }

                val reasonText = hold.actvReason.text
                val totalVolumeText = hold.tiedTotalVolume.text
                if (reasonText.isNotEmpty() && !totalVolumeText.isNullOrEmpty() ) {
                    showingProgress()
                    val failureId = mDatabase.findCancelWayReasonIdByValue(reasonText.toString())
                    val totalValue = round(hold.tiedTotalVolume.text.toString().toDouble() * 100) / 100
                    val totalType = if (hold.tbWeight.isChecked) 2 else 1
                    val body = EarlyCompleteBody(failureId, MyUtil.timeStampInSec(), totalType, totalValue)

                    App.getAppliCation().getNetwork().earlyComplete(workOrderId, body)
                        .observe(viewLifecycleOwner) { result ->

                            when (result.status) {
                                Status.SUCCESS -> {
                                    mDatabase.setCompleteWorkOrderData(workOrder)
                                    if (mDatabase.hasWorkOrderInNotProgress()) {
                                        finishTask_know()
                                    }
                                    hideProgress()
                                }
                                Status.ERROR -> {
                                    hideProgress()
                                    toast(result.msg)
                                }
                                Status.NETWORK -> {
                                    hideProgress()
                                    toast("Проблемы с интернетом")
                                }
                            }
                        }
                } else {
                    toast("Заполните все поля")
                }
            }
        }

        inner class ReasonAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tiedTotalVolume: TextInputEditText by lazy {
                val view = itemView.findViewById<TextInputEditText>(R.id.tiet_f_complete_early__total_volume)
                view
            }
            val actvReason: AutoCompleteTextView by lazy {
                val view = itemView.findViewById<AutoCompleteTextView>(R.id.actv_f_complete_early__reason)
                val reasonsString = cancelWayReasonS.map { it.problem }
                view.setAdapter(ArrayAdapter(getAct(), android.R.layout.simple_dropdown_item_1line, android.R.id.text1, reasonsString))
                view.setOnClickListener {
                    view.showDropDown()
                }
                view.setOnFocusChangeListener { _, _ ->
                    view.showDropDown()
                }
                view
            }

            val tbVolume: ToggleButton by lazy {
                val view = itemView.findViewById<ToggleButton>(R.id.tb_f_complete_early__type_volume)
                //https://jira.smartro.ru/browse/SR-2625
                view.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        tbWeight.isChecked = !b
                        tilTotalVolume.hint = (getString(R.string.enter_volume_hint))
                    }
                }
                view?.isChecked = true
                view
            }
            val tbWeight: ToggleButton by lazy {
                val view = itemView.findViewById<ToggleButton>(R.id.tb_f_complete_early__type_weight)
                view.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        tbVolume.isChecked = !b
                        tilTotalVolume.hint = (getString(R.string.enter_weight_hint))
                    }
                }
                view
            }

            val tilTotalVolume: TextInputLayout by lazy {
                val view = itemView.findViewById<TextInputLayout>(R.id.til_f_complete_early__total_volume)
                view
            }
            public fun setBlueAllIn() {

            }
        }
    }
}