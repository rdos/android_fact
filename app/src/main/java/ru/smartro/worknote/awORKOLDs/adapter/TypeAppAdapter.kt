package ru.smartro.worknote.awORKOLDs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_container_adapter.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.service.network.body.TypeAppBody

class TypeAppAdapter(private val items: ArrayList<TypeAppBody>) :
    RecyclerView.Adapter<TypeAppAdapter.OwnerViewHolder>() {
    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.start_act__rv_item_know1, parent, false)
        return OwnerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        val organisation = items[position]

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

            holder.itemView.choose_title.text = organisation.name
            holder.itemView.setOnClickListener {
                holder.itemView.choose_cardview.isVisible = true
                if (checkedPosition != holder.adapterPosition){
                    holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
                    holder.itemView.choose_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                    notifyItemChanged(checkedPosition)
                    checkedPosition = holder.adapterPosition
                }
            }
    }

    class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}