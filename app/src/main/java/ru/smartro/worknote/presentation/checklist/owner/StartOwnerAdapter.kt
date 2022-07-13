package ru.smartro.worknote.presentation.checklist.owner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.service.network.response.organisation.Organisation

class StartOwnerAdapter(private val listener: (Organisation) -> Unit): RecyclerView.Adapter<StartOwnerAdapter.OwnerViewHolder>() {

    private val mItems: MutableList<Organisation> = mutableListOf()
    fun setItems(ownersList: List<Organisation>) {
        mItems.clear()
        mItems.addAll(ownersList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.f_start_owner__rv_item, parent, false)
        return OwnerViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        holder.bind(mItems[position])
    }

    override fun getItemCount(): Int = mItems.size

    class OwnerViewHolder(val itemView: View, val listener: (Organisation) -> Unit): RecyclerView.ViewHolder(itemView) {
        fun bind(owner: Organisation) {
            itemView.findViewById<TextView>(R.id.owner_name).text = owner.name
            itemView.setOnClickListener {
                listener(owner)
            }
        }
    }
}