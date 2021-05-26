package ru.smartro.worknote.adapter.container_service

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import kotlinx.android.synthetic.main.item_choose.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.service.database.entity.work_order.ContainerEntity
import ru.smartro.worknote.util.StatusEnum

class ContainerAdapter(private val listener: ContainerPointClickListener, private val items: RealmList<ContainerEntity>) :
    RecyclerView.Adapter<ContainerAdapter.OwnerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_choose, parent, false)
        return OwnerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        val data = items[position]
        holder.itemView.choose_title.text = data!!.number
        if (data.isActiveToday!!){
            when (data.status) {
                StatusEnum.NEW -> {
                    holder.itemView.setOnClickListener {
                        listener.startContainerService(item = items[position]!!)
                        Log.d("ContainerPointAdapter", "onBindViewHolder: true")
                    }
                }
                StatusEnum.SUCCESS -> {
                    holder.itemView.choose_status.isVisible = true
                    holder.itemView.choose_status.setImageResource(R.drawable.ic_check)
                    holder.itemView.setOnClickListener {
                        Log.d("ContainerPointAdapter", "onBindViewHolder: true")
                    }
                }
                StatusEnum.ERROR -> {
                    holder.itemView.choose_status.isVisible = true
                    holder.itemView.choose_status.setImageResource(R.drawable.ic_red_check)
                    holder.itemView.setOnClickListener {
                        listener.startContainerService(item = items[position]!!)
                        Log.d("ContainerPointAdapter", "onBindViewHolder: true")
                    }
                }
            }
        }else{
            when (data.status) {
                StatusEnum.NEW -> {
                    holder.itemView.choose_status.isVisible = true
                    holder.itemView.choose_status.setImageResource(R.drawable.ic_cancel)
                    holder.itemView.setOnClickListener {
                        listener.startContainerService(item = items[position]!!)
                        Log.d("ContainerPointAdapter", "onBindViewHolder: false")
                    }
                }
                StatusEnum.SUCCESS -> {
                    holder.itemView.choose_status.isVisible = true
                    holder.itemView.choose_status.setImageResource(R.drawable.ic_cancel_green)
                    holder.itemView.setOnClickListener {
                        listener.startContainerService(item = items[position]!!)
                        Log.d("ContainerPointAdapter", "onBindViewHolder: false")
                    }
                }
                StatusEnum.ERROR -> {
                    holder.itemView.choose_status.isVisible = true
                    holder.itemView.choose_status.setImageResource(R.drawable.ic_cancel_red)
                    holder.itemView.setOnClickListener {
                        listener.startContainerService(item = items[position]!!)
                        Log.d("ContainerPointAdapter", "onBindViewHolder: true")
                    }
                }
            }

        }
    }

    class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface ContainerPointClickListener {
        fun startContainerService(item: ContainerEntity)
    }
}