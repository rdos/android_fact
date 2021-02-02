package ru.smartro.worknote.adapter.container_service

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.choose_item.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.service.db.entity.container_info.ContainerInfoEntity
import ru.smartro.worknote.service.response.way_task.ContainerInfo

class ContainerPointAdapter(private val listener: ContainerPointClickListener, private val items: ArrayList<ContainerInfo>, private val filletContainers: List<ContainerInfoEntity>) :
    RecyclerView.Adapter<ContainerPointAdapter.OwnerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.choose_item, parent, false)
        return OwnerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        val data = items[position]
        holder.itemView.choose_title.text = data.number

        if (equal(filletContainers, items[position])) {
            holder.itemView.choose_status.isVisible = true
            holder.itemView.setOnClickListener {
                Log.d("ContainerPointAdapter", "onBindViewHolder: true")
            }
        } else {
            holder.itemView.setOnClickListener {
                listener.startContainerPointService(item = items[position])
                Log.d("ContainerPointAdapter", "onBindViewHolder: false")
            }
        }

    }

    class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    private fun equal(filletContainers: List<ContainerInfoEntity>, item: ContainerInfo): Boolean {
        var bool = false
        for (filletContainer in filletContainers) {
            bool = filletContainer.containerId == item.id
        }
        return bool
    }

    interface ContainerPointClickListener {
        fun startContainerPointService(item: ContainerInfo)
    }
}