package ru.smartro.worknote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.f_complete_early__map_act__rv_item.view.*

import ru.smartro.worknote.work.RealmRepository
import ru.smartro.worknote.work.WorkOrderEntity


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

        val rvReason = view.findViewById<RecyclerView>(R.id.rv_f_complete_early__reason)
        rvReason.layoutManager = LinearLayoutManager(getAct())
        mReasonAdapter = ReasonAdapter(workOrderS)
        rvReason.adapter = mReasonAdapter


        val acbAccept = view.findViewById<AppCompatButton>(R.id.acb_f_complete_early__accept)
//        acbAccept.setOnClickListener {
//            //todo:!r_dos
//            fun finishTask_know() {
//                Log.i(TAG, "finishTask")
//                getAct().modeSyNChrON_off()
//                mDatabase.clearDataBase()
//
//                getAct().showSuccessComplete().let {
//                    it.finish_accept_btn.setOnClickListener {
//                        getAct().startActivity(Intent(getAct(), StartAct::class.java))
//                        getAct().finish()
//                    }
//                    it.exit_btn.setOnClickListener {
//                        getAct().logout()
//                    }
//                }
//            }
//
//            val reasonText = actvReason.text
//            val totalVolumeText = tiedTotalVolume.text
//            if (reasonText.isNotEmpty() && !totalVolumeText.isNullOrEmpty() ) {
//                showingProgress()
//                val failureId = mDatabase.findCancelWayReasonIdByValue(reasonText.toString())
//                val totalValue = round(tiedTotalVolume.text.toString().toDouble() * 100) / 100
//                val totalType = if (tbWeight.isChecked) 2 else 1
//                val body = EarlyCompleteBody(failureId, MyUtil.timeStamp(), totalType, totalValue)
//
//                App.getAppliCation().getNetwork().earlyComplete(workOrderId, body)
//                    .observe(viewLifecycleOwner) { result ->
//                        when (result.status) {
//                            Status.SUCCESS -> {
//                                hideProgress()
//                                mDatabase.setCompleteWorkOrderData(mWorkOrderEntity)
//                                if (mDatabase.hasWorkOrderInNotProgress()) {
//                                    finishTask_know()
//                                }
//                            }
//                            Status.ERROR -> {
//                                toast(result.msg)
//                                hideProgress()
//                            }
//                            Status.NETWORK -> {
//                                toast("Проблемы с интернетом")
//                                hideProgress()
//                            }
//                        }
//                    }
//            } else {
//                toast("Заполните все поля")
//            }
//        }

    }

    inner class ReasonAdapter(private val workOrderS: MutableList<WorkOrderEntity>) :
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
//            holder.accbCheckBox.text = "${workOrder.id} ${workOrder.name}"
//            holder.accbCheckBox.isChecked = workOrder.isShowForUser


            val totalContainersVolume = mDatabase.findContainersVolume(workOrderEntity.id)

            holder.tiedTotalVolume.setText("$totalContainersVolume")

            //todo:
            holder.itemView.workorder_name.text = "Итоговые показатели рейса \n${workOrderEntity.id}(${workOrderEntity.name})"

    //            val tbWeight = view.findViewById<ToggleButton>(R.id.tb_f_complete_early__type_weight)
    //            val tbVolume = view.findViewById<ToggleButton>(R.id.tb_f_complete_early__type_volume)
    //
    //            val cancelWayReasonS = mDatabase.findCancelWayReasonEntity()
    //            val reasonsString = cancelWayReasonS.map { it.problem }
    //            val actvReason = view.findViewById<AutoCompleteTextView>(R.id.actv_f_complete_early__reason)
    //            actvReason.setAdapter(ArrayAdapter(getAct(), android.R.layout.simple_dropdown_item_1line, android.R.id.text1, reasonsString))
    //            actvReason.setOnClickListener {
    //                actvReason.showDropDown()
    //            }
    //            actvReason.setOnFocusChangeListener { _, _ ->
    //                actvReason.showDropDown()
    //            }
    //
    //            tbWeight.setOnCheckedChangeListener { _, b ->
    //                if (b) {
    //                    tbVolume.isChecked = !b
    //                    tbWeight.setTextColor(Color.WHITE)
    //                    view.til_f_complete_early__total_volume.hint = (getString(R.string.enter_weight_hint))
    //                } else {
    //                    tbWeight.setTextColor(Color.BLACK)
    //                }
    //            }
    //            tbVolume.setOnCheckedChangeListener { _, b ->
    //                if (b) {
    //                    tbWeight.isChecked = !b
    //                    tbVolume.setTextColor(Color.WHITE)
    //                    view.til_f_complete_early__total_volume.hint = (getString(R.string.enter_volume_hint))
    //                } else {
    //                    tbWeight.setTextColor(Color.BLACK)
    //                }
    //            }

        }

        inner class ReasonAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tiedTotalVolume: TextInputEditText by lazy {
              itemView.findViewById<TextInputEditText>(R.id.tiet_f_complete_early__total_volume)
            }
            val tbVolume: ToggleButton by lazy {
                val view = itemView.findViewById<ToggleButton>(R.id.tb_f_complete_early__type_volume)
                //https://jira.smartro.ru/browse/SR-2625
                view?.isChecked = true
                view
            }
        }
    }
}