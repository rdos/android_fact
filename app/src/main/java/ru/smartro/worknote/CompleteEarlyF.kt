package ru.smartro.worknote

import android.content.Intent
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
import kotlinx.android.synthetic.main.f_complete_success___act_map__rv_item.view.*
import ru.smartro.worknote.awORKOLDs.extensions.showSuccessComplete
import ru.smartro.worknote.awORKOLDs.service.network.Status
import ru.smartro.worknote.awORKOLDs.service.network.body.complete.CompleteWayBody
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
//            data as Int
            val fragment = CompleteEarlyF()
//            fragment.addArgument(data)
            return fragment
        }
    }

    override fun onGetLayout(): Int {
        return R.layout.f_complete___map_act
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
        RecyclerView.Adapter<ReasonAdapter.BaseCompleteEarlyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseCompleteEarlyViewHolder {

            lateinit var viewHolder: RecyclerView.ViewHolder

            when (viewType) {
                1 -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.f_complete_success___act_map__rv_item, parent, false)
                    viewHolder = SuccessAdapterViewHolder(view)
                }
                0 -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.f_complete_early__act_map__rv_item, parent, false)
                    viewHolder = EarlyAdapterViewHolder(view)
                }
                else -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.start_act__rv_item_know1, parent, false)
                    viewHolder = EarlyAdapterViewHolder(view)
                }
            }

            return viewHolder as BaseCompleteEarlyViewHolder
        }

        override fun getItemCount(): Int {
            return workOrderS.size
        }

        override fun onBindViewHolder(holder: BaseCompleteEarlyViewHolder, position: Int) {
            val workOrderEntity = workOrderS[position]
            holder.bind(workOrderEntity)
        }


        override fun getItemViewType(position: Int): Int {
            val itemViewType = if (workOrderS[position].isComplete()) 1 else 0
            return itemViewType
        }

        abstract inner class BaseCompleteEarlyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            abstract fun bind(workOrderEntity: WorkOrderEntity)
        }

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

        inner class EarlyAdapterViewHolder(itemView: View) : BaseCompleteEarlyViewHolder(itemView) {
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

            override fun bind(workOrderEntity: WorkOrderEntity) {
                val totalContainersVolume = mDatabase.findContainersVolume(workOrderEntity.id)
                this.tiedTotalVolume.setText("$totalContainersVolume")
                this.actvReason/** причина by lazy*/
                this.tbVolume
                this.tbWeight
                //todo:
                val view = this.itemView
                val acbFinalResultsWorkOrderName = view.findViewById<AppCompatTextView>(R.id.acb_f_complete_early__final_results_workorder_name)
                acbFinalResultsWorkOrderName.text = "Итоговые показатели рейса ${workOrderEntity.id}(${workOrderEntity.name})"

                val acbAccept = view.findViewById<AppCompatButton>(R.id.acb_f_complete_early__accept)
                acbAccept.setOnClickListener {
                    val hold = this
                    val workOrderId = workOrderEntity.id
                    val workOrder = workOrderEntity

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
        }






        inner class SuccessAdapterViewHolder(itemView: View) : BaseCompleteEarlyViewHolder(itemView) {
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

            override fun bind(workOrderEntity: WorkOrderEntity) {
                val database = App.getAppliCation().getDB()
                val totalVolume = App.getAppliCation().getDB().findContainersVolume(workOrderEntity.id)
                this.itemView.comment_et.setText("$totalVolume")
                this.itemView.accept_btn.setOnClickListener {

                    if (this.itemView.weight_tg.isChecked || this.itemView.volume_tg.isChecked) {
                        //TODO:!r_dos надо проверить!unloadType!!unloadType
                        val unloadType = if (this.itemView.volume_tg.isChecked) 1 else 2
                        val unloadValue = round(this.itemView.comment_et.text.toString().toDouble() * 100) / 100
                        val body = CompleteWayBody(
                            finishedAt = MyUtil.timeStampInSec(),
                            unloadType = unloadType, unloadValue = unloadValue.toString()
                        )

                        App.getAppliCation().getNetwork().completeWay(workOrderEntity.id, body)
                            .observe(viewLifecycleOwner) { result ->
                                when (result.status) {
                                    Status.SUCCESS -> {
                                        hideProgress()
                                        database.setCompleteWorkOrderData(workOrderEntity)
                                        if (database.hasWorkOrderInNotProgress()) {
                                            finishTask_know()
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
                            }
                    } else {
                        toast("Выберите тип показателей")
                    }
                }
            }


        }

    }
}