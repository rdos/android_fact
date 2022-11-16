package ru.smartro.worknote.presentation.ac

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ToggleButton
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.smartro.worknote.abs.FragmentA
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.presentation.work.Status
import ru.smartro.worknote.awORKOLDs.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.toast

import ru.smartro.worknote.presentation.work.RealmRepository
import ru.smartro.worknote.presentation.work.WorkOrderEntity
import ru.smartro.worknote.presentation.work.net.CancelWayReasonEntity
import ru.smartro.worknote.presentation.work.net.EarlyCompleteBody
import kotlin.math.round


class CompleteF : FragmentA() {

    private lateinit var mReasonAdapter: ReasonAdapter
    private lateinit var mDatabase: RealmRepository

    private val viewModel: CompleteViewModel by viewModels()

    override fun onGetLayout(): Int {
        return R.layout.f_complete___map_act
    }


    private fun setUseButtonStyleBackgroundGreen(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(getAct(), R.drawable.bg_button_green__usebutton))
    }

    private fun setStyleBackgroundGreen(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(getAct(), R.drawable.bg_button_green__default))
    }


    private fun setUseButtonStyleBackgroundRed(appCompatButton: AppCompatButton) {
        appCompatButton.setBackgroundDrawable(ContextCompat.getDrawable(getAct(), R.drawable.bg_button_red__usebutton))
    }


    /**Activity = это просто view + context + EVENT?*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val workOrderId = getArgumentID()
        mDatabase = App.getAppliCation().getDB()
        val workOrderS = mDatabase.getWorkOrderEntityS(true)

        val cancelWayReasonS = mDatabase.findCancelWayReasonEntity()

        val rvReason = view.findViewById<RecyclerView>(R.id.rv_f_complete)
        val llm = LinearLayoutManager(getAct())
//        llm.
        rvReason.layoutManager = llm
        mReasonAdapter = ReasonAdapter(workOrderS, cancelWayReasonS, object : OnSuccessListener {
            override fun onSuccess() {
                viewModel.increaseCounter()
            }
        })
        rvReason.adapter = mReasonAdapter

        viewModel.mServedCounter.observe(viewLifecycleOwner) {
            if(it == workOrderS.size) {
                if(mDatabase.hasWorkOrderInProgress() == false) {
                    finishTask_know()
                } else {
                    navigateBack(R.id.MapPlatformsF)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateBack()
    }

    interface OnSuccessListener {
        fun onSuccess()
    }

    //todo:!r_dos
    fun finishTask_know() {
        LOG.info( "finishTask")
        getAct().modeSyNChrON_off()
        mDatabase.clearDataBase()

        navigateNext(R.id.FinishCompleteF)
    }

    inner class ReasonAdapter(private val workOrderS: MutableList<WorkOrderEntity>,
                              private val cancelWayReasonS: List<CancelWayReasonEntity>,
                              private val listener: OnSuccessListener
    ) :
        RecyclerView.Adapter<ReasonAdapter.BaseCompleteViewHolder>() {

        override fun getItemCount(): Int {
            return workOrderS.size
        }

        override fun onBindViewHolder(holder: BaseCompleteViewHolder, position: Int) {
            val workOrderEntity = workOrderS[position]
            holder.bind(workOrderEntity)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseCompleteViewHolder {

            val viewHolder = when (viewType) {
                1 -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.f_complete_success___act_map__rv_item, parent, false)
                    SuccessAdapterViewHolder(view, listener)
                }
                0 -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.f_complete_early__act_map__rv_item, parent, false)
                    EarlyAdapterViewHolder(view, listener)
                }
                else -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.start_act__rv_item_know1, parent, false)
                    EarlyAdapterViewHolder(view, listener)
                }
            }

            return viewHolder as BaseCompleteViewHolder
        }


        override fun getItemViewType(position: Int): Int {
            val itemViewType = if (workOrderS[position].isComplete()) 1 else 0
            return itemViewType
        }

        abstract inner class BaseCompleteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            abstract fun bind(workOrderEntity: WorkOrderEntity)
        }

        inner class EarlyAdapterViewHolder(itemView: View, listener: OnSuccessListener) : BaseCompleteViewHolder(itemView) {
            val tiedTotalVolume: TextInputEditText by lazy {
                val view = itemView.findViewById<TextInputEditText>(R.id.tiet_f_complete_early__total_volume)
                view
            }
            val tilTotalVolume: TextInputLayout by lazy {
                val view = itemView.findViewById<TextInputLayout>(R.id.til_f_complete_early__total_volume)
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

            val tbTypeVolume: ToggleButton by lazy {
                val view = itemView.findViewById<ToggleButton>(R.id.tb_f_complete_early__type_volume)
                //https://jira.smartro.ru/browse/SR-2625
                view.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        tbTypeWeight.isChecked = !b
                        tilTotalVolume.hint = (getString(R.string.enter_volume_hint))
                    }
                }
                view?.isChecked = true
                view
            }
            val tbTypeWeight: ToggleButton by lazy {
                val view = itemView.findViewById<ToggleButton>(R.id.tb_f_complete_early__type_weight)
                view.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        tbTypeVolume.isChecked = !b
                        tilTotalVolume.hint = (getString(R.string.enter_weight_hint))
                    }
                }
                view
            }
            val acbAccept: AppCompatButton by lazy {
                val view = itemView.findViewById<AppCompatButton>(R.id.acb_f_complete_early__accept)
                view
            }
            public fun setBlueAllIn() {

            }

            override fun bind(workOrderEntity: WorkOrderEntity) {
                val totalContainersVolume = mDatabase.findContainersVolume(workOrderEntity.id)
                this.tiedTotalVolume.setText("${totalContainersVolume}")
                this.actvReason/** причина by lazy*/
                this.tbTypeVolume
                this.tbTypeWeight
                //todo:
                val view = this.itemView
                val acbFinalResultsWorkOrderName = view.findViewById<AppCompatTextView>(R.id.acb_f_complete_early__final_results_workorder_name)
                acbFinalResultsWorkOrderName.text = "Итоговые показатели рейса ${workOrderEntity.id}(${workOrderEntity.name})"


                acbAccept.setOnClickListener {
                    if (workOrderEntity.isShowForUser == false) {
                        toast("Вы завершили Сменного Задания")
                        return@setOnClickListener
                    }
                    val hold = this
                    val workOrderId = workOrderEntity.id
                    val workOrder = workOrderEntity

                    val reasonText = hold.actvReason.text
                    val totalVolumeText = hold.tiedTotalVolume.text
                    if (reasonText.isNotEmpty() && !totalVolumeText.isNullOrEmpty() ) {
                        showingProgress()
                        val failureId = mDatabase.findCancelWayReasonIdByValue(reasonText.toString())
                        val totalValue = round(hold.tiedTotalVolume.text.toString().toDouble() * 100) / 100
                        val totalType = if (hold.tbTypeVolume.isChecked) 1 else 2
                        val body = EarlyCompleteBody(failureId, MyUtil.timeStampInSec(), totalType, totalValue)

                        App.getAppliCation().getNetwork().earlyComplete(workOrderId, body)
                            .observe(viewLifecycleOwner) { result ->

                                when (result.status) {
                                    Status.SUCCESS -> {
                                        mDatabase.setCompleteWorkOrderData(workOrder)
                                        setUseButtonStyleBackgroundRed(acbAccept)
                                        hold.itemView.isEnabled = false
                                        workOrderEntity.isShowForUser = false
                                        hideProgress()
                                        listener.onSuccess()
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

        inner class SuccessAdapterViewHolder(itemView: View, listener: OnSuccessListener) : BaseCompleteViewHolder(itemView) {
            val tiedTotalVolume: TextInputEditText by lazy {
                val view = itemView.findViewById<TextInputEditText>(R.id.tiet_f_complete_success__total_volume)
                view
            }
            val tilTotalVolume: TextInputLayout by lazy {
                val view = itemView.findViewById<TextInputLayout>(R.id.til_f_complete_success__total_volume)
                view
            }
            val tbTypeVolume: ToggleButton by lazy {
                val view = itemView.findViewById<ToggleButton>(R.id.tb_f_complete_success__type_volume)
                //https://jira.smartro.ru/browse/SR-2625
                view.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        tbTypeWeight.isChecked = !b
                        tilTotalVolume.hint = (getString(R.string.enter_volume_hint))
                    }
                }
                view?.isChecked = true
                view
            }
            val tbTypeWeight: ToggleButton by lazy {
                val view = itemView.findViewById<ToggleButton>(R.id.tb_f_complete_success__type_weight)
                view.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        tbTypeVolume.isChecked = !b
                        tilTotalVolume.hint = (getString(R.string.enter_weight_hint))
                    }
                }
                view
            }
            val acbAccept: AppCompatButton by lazy {
                val view = itemView.findViewById<AppCompatButton>(R.id.acb_f_complete_success__accept)
                view
            }


            override fun bind(workOrderEntity: WorkOrderEntity) {
                val totalContainersVolume = mDatabase.findContainersVolume(workOrderEntity.id)
                this.tiedTotalVolume.setText("${totalContainersVolume}")
                /** причина by lazy*/
                this.tbTypeVolume
                this.tbTypeWeight
                this.acbAccept.setOnClickListener {
                    val hold = this
                    if (hold.tbTypeWeight.isChecked || hold.tbTypeVolume.isChecked) {
                        val totalValue = round(hold.tiedTotalVolume.text.toString().toDouble() * 100) / 100
                        val totalType = if (hold.tbTypeVolume.isChecked) 1 else 2
                        val body = CompleteWayBody(
                            finishedAt = MyUtil.timeStampInSec(),
                            unloadType = totalType, unloadValue = totalValue.toString()
                        )

                        App.getAppliCation().getNetwork().completeWay(workOrderEntity.id, body)
                            .observe(viewLifecycleOwner) { result ->
                                when (result.status) {
                                    Status.SUCCESS -> {
                                        mDatabase.setCompleteWorkOrderData(workOrderEntity)
                                        setUseButtonStyleBackgroundGreen(it as AppCompatButton)
                                        hold.itemView.isEnabled = false
                                        hideProgress()
                                        listener.onSuccess()
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
                        toast("Выберите тип показателей")
                    }
                }
            }
        }
    }

    open class CompleteViewModel(app: Application) : AViewModel(app) {

        private val _servedCounter: MutableLiveData<Int> = MutableLiveData(0)
        val mServedCounter: LiveData<Int>
            get() = _servedCounter


        fun increaseCounter() {
            _servedCounter.postValue((mServedCounter.value ?: 0) + 1)
        }
    }
}