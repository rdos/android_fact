package ru.smartro.worknote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_choose.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.service.network.response.way_list.Data

class WayListAdapter(private val items: ArrayList<Data>) :
    RecyclerView.Adapter<WayListAdapter.OwnerViewHolder>() {
    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_choose, parent, false)
        return OwnerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        val data = items[position]

        if (checkedPosition == -1){
            holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            holder.itemView.choose_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))

        }else{
            if (checkedPosition == holder.adapterPosition){
                holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
                holder.itemView.choose_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))

            }
            else {
                holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.itemView.choose_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))

            }
        }

        holder.itemView.choose_title.text = data.number
        holder.itemView.setOnClickListener {
            holder.itemView.choose_cardview.isVisible = true
            if (checkedPosition != holder.adapterPosition) {
                holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
                holder.itemView.choose_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                notifyItemChanged(checkedPosition)
                checkedPosition = holder.adapterPosition
            }
        }
    }

    fun getSelectedId(): Int {
        return if (checkedPosition != -1) {
            items[checkedPosition].id
        } else {
            -1
        }

    }

    class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}