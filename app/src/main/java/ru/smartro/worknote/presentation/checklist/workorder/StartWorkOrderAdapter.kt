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

class StartWorkOrderAdapter(private val listener: (WoRKoRDeR_know1, Int) -> Unit): RecyclerView.Adapter<StartWorkOrderAdapter.WorkOrderViewHolder>() {

    private val mItems: MutableList<WoRKoRDeR_know1> = mutableListOf()

    fun setItems(workOrders: List<WoRKoRDeR_know1>) {
        mItems.clear()
        mItems.addAll(workOrders)
        notifyDataSetChanged()
    }

    fun updateItem(woId: Int, isSelected: Boolean) {
        notifyItemChanged(mItems.indexOf(mItems.find { el -> el.id == woId }), isSelected)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkOrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.f_start_workorder__rv_item, parent, false)
        return WorkOrderViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: WorkOrderViewHolder, position: Int) {
        holder.bind(mItems[position], position)
    }

    override fun onBindViewHolder(holder: WorkOrderViewHolder, position: Int, payloads: List<Any>) {
        Log.d("TEST::::", "ON BIND VIEWHOLDER PAYLOADS")
        if(payloads.isNotEmpty()) {
            Log.d("TEST::::", "ON BIND VIEWHOLDER PAYLOADS is NOT EMPTY")
            if (payloads[0] is Boolean) {
                holder.updateItem(payloads[0] is Boolean)
                Log.d("TEST::::", "ON BIND VIEWHOLDER FIRST PAYLOAD IN LIST IS BOOLEAN")
            } else {
                Log.d("TEST::::", "ON BIND VIEWHOLDER FIRST PAYLOAD IN LIST is NOT BOOLEAN")
            }
        } else {
            Log.d("TEST::::", "ON BIND VIEWHOLDER PAYLOADS is EMPTY")
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    override fun getItemCount(): Int = mItems.size

    class WorkOrderViewHolder(val itemView: View, val listener: (WoRKoRDeR_know1, Int) -> Unit): RecyclerView.ViewHolder(itemView) {
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

            itemView.setOnClickListener { listener(workOrder, position) }
        }

        fun updateItem(isSelected: Boolean = false) {
            if (isSelected) {
                itemView.setBackgroundDrawable(ContextCompat.getDrawable(
                    itemView.context, R.drawable.bg_button_green__usebutton
                ))
            } else {
                itemView.setBackgroundResource(0)
            }
        }
    }
}