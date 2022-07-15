package ru.smartro.worknote.presentation.checklist.workorder

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.start_act__rv_item.view.*
import org.w3c.dom.Text
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.service.network.response.way_list.WayBillDto
import ru.smartro.worknote.work.WoRKoRDeR_know1

class StartWorkOrderAdapter(): RecyclerView.Adapter<StartWorkOrderAdapter.WorkOrderViewHolder>() {

    private val mItems: MutableList<WoRKoRDeR_know1> = mutableListOf()
    private var listener: ((Int) -> Unit)? = null
    fun setItems(workOrders: List<WoRKoRDeR_know1>) {
        mItems.clear()
        mItems.addAll(workOrders)
        notifyDataSetChanged()
    }
    fun isItemsEmpty() = mItems.isEmpty()

    fun setListener(_listener: (Int) -> Unit) {
        listener = _listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkOrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.f_start_workorder__rv_item, parent, false)
        return WorkOrderViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: WorkOrderViewHolder, position: Int) {
        holder.bind(mItems[position], position)
    }

    override fun onBindViewHolder(holder: WorkOrderViewHolder, position: Int, payloads: List<Any>) {
        if(payloads.isNotEmpty()) {
            when {
                // Обнови один элемент
                payloads[0] is Boolean -> holder.updateItem(payloads[0] as Boolean)
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    override fun getItemCount(): Int = mItems.size

    fun updateItemSelection(indexes: List<Int>, isSelected: Boolean) {
        for(ind in indexes) {
            notifyItemChanged(ind, isSelected)
        }
    }

    fun clearSelections() {
        if(mItems.isNotEmpty()) {
            notifyItemRangeChanged(0, mItems.size, false)
        }
    }

    class WorkOrderViewHolder(val itemView: View, val listener: ((Int) -> Unit)?): RecyclerView.ViewHolder(itemView) {
        fun bind(workOrder: WoRKoRDeR_know1, position: Int){
            itemView.findViewById<TextView>(R.id.wo_name).text = workOrder.name

            if (workOrder.waste_type != null) {
                itemView.findViewById<TextView>(R.id.choose_st).apply {
                    text = workOrder.waste_type.name
                    setTextColor(Color.parseColor("#${workOrder.waste_type.color.hex}"))
                }
            }

            itemView.findViewById<TextView>(R.id.wo_status).apply {
                if(workOrder.beginnedAt != null && workOrder.finishedAt == null) {
                    text = "В работе"
                    setTextColor(itemView.context.getColor(R.color.yellow))
                } else if (workOrder.finishedAt != null) {
                    text = "Завершено"
                    setTextColor(itemView.context.getColor(R.color.green))
                } else {
                    text = "Новое"
                }
            }

            itemView.setOnClickListener { if(listener != null) listener!!(position) }
        }

        fun updateItem(isSelected: Boolean = false) {
            itemView.setBackgroundDrawable(ContextCompat.getDrawable(
                itemView.context,
                if (isSelected)
                    R.drawable.bg_button_green__usebutton
                else
                    R.drawable.bg_button_green__default
            ))
        }
    }
}