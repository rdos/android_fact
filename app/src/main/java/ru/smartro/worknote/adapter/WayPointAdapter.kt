package ru.smartro.worknote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import kotlinx.android.synthetic.main.map_behavior_item.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.service.db.entity.way_task.WayPointEntity

class WayPointAdapter(private val listener: ContainerClickListener, private val items: RealmList<WayPointEntity>) : RecyclerView.Adapter<WayPointAdapter.OwnerViewHolder>() {
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

        holder.itemView.map_behavior_address.text = item!!.name
        holder.itemView.map_behavior_scrp_id.text = item.srpId.toString()
        holder.itemView.map_behavior_container_count.text = "${item!!.containerInfo!!.size} контейнер"


        //сравниваем заполненные контейнеры с сохраненными с сервера. Если кол-во совпадает, значит данная точка заполнена
        if (items[position]!!.isComplete) {
            holder.itemView.map_behavior_status.isVisible = true
            holder.itemView.setOnClickListener {
                //nothing
            }
        } else {
            holder.itemView.setOnClickListener {
                if (checkedPosition != holder.adapterPosition) {
                    holder.itemView.map_behavior_expl.expand()
                    holder.itemView.map_behavior_start_service.setOnClickListener {
                        listener.startPointService(item!!)
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
        fun startPointService(item: WayPointEntity)
    }
}