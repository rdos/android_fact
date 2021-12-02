package ru.smartro.worknote.ui.platform_serve

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_container_adapter.view.*
import kotlinx.android.synthetic.main.item_map_behavior.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.service.database.entity.work_order.ContainerEntity
import ru.smartro.worknote.util.MyUtil.toStr
import ru.smartro.worknote.util.StatusEnum

// TODO: 22.10.2021 !!!когда?
class ContainerAdapter(private val listener: ContainerPointClickListener, private val items: ArrayList<ContainerEntity>) :
    RecyclerView.Adapter<ContainerAdapter.OwnerViewHolder>() {
    // TODO: 22.10.2021  item_container_adapter !!!
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_container_adapter, parent, false)
        return OwnerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        val data = items[position]

        holder.itemView.choose_title.text = data.number
        holder.itemView.tv_item_container_adapter__type_name.text = data.typeName
        // TODO: 25.10.2021 add getString() + format
        holder.itemView.tv_item_container_adapter__constructiveVolume.text = "${data.constructiveVolume.toStr("м3")}"

        holder.itemView.map_behavior_expl11.collapse()
        holder.itemView.plus.setOnClickListener {
            holder.itemView.map_behavior_expl11.expand()
        }
        holder.itemView.choose_status.visibility = View.INVISIBLE

        if (data.isActiveToday!!) {
            when (data.status) {
                StatusEnum.NEW -> {
                    holder.itemView.setOnClickListener {
                        listener.startContainerService(item = items[position])
                        Log.d("ContainerPointAdapter", "onBindViewHolder: true")
                    }
                }
                StatusEnum.SUCCESS -> {
                    holder.itemView.choose_status.isVisible = true
                    holder.itemView.choose_status.setImageResource(R.drawable.ic_check)
                    holder.itemView.setOnClickListener {
                        listener.startContainerService(item = items[position])
                        Log.d("ContainerPointAdapter", "onBindViewHolder: true")
                    }
                }
                StatusEnum.ERROR -> {
                    holder.itemView.choose_status.isVisible = true
                    holder.itemView.choose_status.setImageResource(R.drawable.ic_red_check)
                    holder.itemView.setOnClickListener {
                        Log.d("ContainerPointAdapter", "onBindViewHolder: true")
                    }
                }
            }
        } else {
            when (data.status) {
                StatusEnum.NEW -> {
                    holder.itemView.choose_status.isVisible = true
                    holder.itemView.choose_status.setImageResource(R.drawable.ic_cancel)
                    holder.itemView.setOnClickListener {
                        listener.startContainerService(item = items[position])
                        Log.d("ContainerPointAdapter", "onBindViewHolder: false")
                    }
                }
                StatusEnum.SUCCESS -> {
                    holder.itemView.choose_status.isVisible = true
                    holder.itemView.choose_status.setImageResource(R.drawable.ic_cancel_green)
                    holder.itemView.setOnClickListener {
                        listener.startContainerService(item = items[position])
                        Log.d("ContainerPointAdapter", "onBindViewHolder: true")
                    }
                }
                StatusEnum.ERROR -> {
                    holder.itemView.choose_status.isVisible = true
                    holder.itemView.choose_status.setImageResource(R.drawable.ic_cancel_red)
                    holder.itemView.setOnClickListener {
                        listener.startContainerService(item = items[position])
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

    fun updateData(newData: ArrayList<ContainerEntity>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }
}