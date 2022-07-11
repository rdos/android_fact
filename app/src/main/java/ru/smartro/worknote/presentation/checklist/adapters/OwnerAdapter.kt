package ru.smartro.worknote.presentation.checklist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.start_act__rv_item_know1.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.service.network.response.organisation.Organisation

class OwnerAdapter(val listener: (Int) -> Unit): RecyclerView.Adapter<OwnerAdapter.OwnerViewHolder>() {

    private val mItems: MutableList<Organisation> = mutableListOf()
    fun setItems(ownersList: List<Organisation>) {
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

    class OwnerViewHolder(val itemView: View, val listener: (Int) -> Unit): RecyclerView.ViewHolder(itemView) {
        fun bind(owner: Organisation) {
            itemView.choose_title.text = owner.name
            itemView.setOnClickListener {
                listener(owner.id)
            }
        }
    }
}