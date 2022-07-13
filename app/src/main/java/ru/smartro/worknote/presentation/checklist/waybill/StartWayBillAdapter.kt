package ru.smartro.worknote.presentation.checklist.waybill

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.service.network.response.way_list.WayBillDto

class StartWayBillAdapter(private val listener: (WayBillDto) -> Unit): RecyclerView.Adapter<StartWayBillAdapter.WayBillViewHolder>() {

    private val mItems: MutableList<WayBillDto> = mutableListOf()

    fun setItems(wayBillsList: List<WayBillDto>) {
        mItems.clear()
        mItems.addAll(wayBillsList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WayBillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.f_start_waybill__rv_item, parent, false)
        return WayBillViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: WayBillViewHolder, position: Int) {
        holder.bind(mItems[position])
    }

    override fun getItemCount(): Int = mItems.size

    class WayBillViewHolder(val itemView: View, val listener: (WayBillDto) -> Unit): RecyclerView.ViewHolder(itemView) {
        fun bind(wayBill: WayBillDto) {
            itemView.findViewById<TextView>(R.id.waybill_number).text = wayBill.number
            itemView.setOnClickListener {
                listener(wayBill)
            }
        }
    }
}