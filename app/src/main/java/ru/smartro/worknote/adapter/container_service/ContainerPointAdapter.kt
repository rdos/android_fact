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
import ru.smartro.worknote.service.database.entity.way_task.ContainerInfoEntity
import ru.smartro.worknote.util.StatusEnum

class ContainerPointAdapter(private val listener: ContainerPointClickListener, private val items: RealmList<ContainerInfoEntity>) :
    RecyclerView.Adapter<ContainerPointAdapter.OwnerViewHolder>() {

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
        when (data.status) {
            StatusEnum.empty -> {
                holder.itemView.setOnClickListener {
                    listener.startContainerPointService(item = items[position]!!)
                    Log.d("ContainerPointAdapter", "onBindViewHolder: false")
                }
            }
            StatusEnum.completed -> {
                holder.itemView.choose_status.isVisible = true
                holder.itemView.choose_status.setImageResource(R.drawable.ic_check)
                holder.itemView.setOnClickListener {
                    Log.d("ContainerPointAdapter", "onBindViewHolder: true")
                }
            }
            StatusEnum.breakDown -> {
                holder.itemView.choose_status.isVisible = true
                holder.itemView.choose_status.setImageResource(R.drawable.ic_red_check)
                holder.itemView.setOnClickListener {
                    listener.startContainerPointService(item = items[position]!!)
                    Log.d("ContainerPointAdapter", "onBindViewHolder: true")
                }
            }
            StatusEnum.failure -> {
                holder.itemView.choose_status.isVisible = true
                holder.itemView.choose_status.setImageResource(R.drawable.ic_cancel)
                holder.itemView.setOnClickListener {
                    Log.d("ContainerPointAdapter", "onBindViewHolder: true")
                }
            }
        }
    }

    class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface ContainerPointClickListener {
        fun startContainerPointService(item: ContainerInfoEntity)
    }
}