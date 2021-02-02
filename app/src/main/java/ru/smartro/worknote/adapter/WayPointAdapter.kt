package ru.smartro.worknote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.map_behavior_item.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.service.db.entity.container_info.ContainerInfoEntity
import ru.smartro.worknote.service.response.way_task.WayPoint

class WayPointAdapter(private val listener: ContainerClickListener, private val items: ArrayList<WayPoint>, private val filledContainers: List<ContainerInfoEntity>) :
    RecyclerView.Adapter<WayPointAdapter.OwnerViewHolder>() {
    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.map_behavior_item, parent, false)
        return OwnerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        val item = items[position]

        if (checkedPosition == -1) {
            holder.itemView.map_behavior_expl.collapse()
        } else {
            if (checkedPosition == holder.adapterPosition) {
                holder.itemView.map_behavior_expl.expand(true)
            } else {
                holder.itemView.map_behavior_expl.collapse()
            }
        }

        holder.itemView.map_behavior_address.text = item.name
        holder.itemView.map_behavior_scrp_id.text = item.srpId.toString()
        holder.itemView.map_behavior_container_count.text = "${item.containerInfo.size} контейнер"

        //получаем все контейнеры по этой точке
        val currentPointFilledContainers = ArrayList<ContainerInfoEntity>()
        for (filledContainer in filledContainers) {
            if (filledContainer.wayPointId == items[position].id)
                currentPointFilledContainers.add(filledContainer)
        }

        //сравниваем заполненные контейнеры с сохраненными с сервера. Если кол-во совпадает, значит данная точка заполнена
        if (currentPointFilledContainers.size == items[position].containerInfo.size) {
            holder.itemView.map_behavior_status.isVisible = true
            holder.itemView.setOnClickListener {
                //nothing
            }
        } else {
            holder.itemView.setOnClickListener {
                if (checkedPosition != holder.adapterPosition) {
                    holder.itemView.map_behavior_expl.expand()
                    holder.itemView.map_behavior_start_service.setOnClickListener {
                        listener.startPointService(item)
                    }
                    notifyItemChanged(checkedPosition)
                    checkedPosition = holder.adapterPosition
                }
            }
        }
    }

    class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


    interface ContainerClickListener {
        fun startPointService(item: WayPoint)
    }
}