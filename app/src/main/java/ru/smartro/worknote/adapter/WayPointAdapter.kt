package ru.smartro.worknote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.geometry.Point
import io.realm.RealmList
import kotlinx.android.synthetic.main.map_behavior_item.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.service.database.entity.way_task.WayPointEntity
import ru.smartro.worknote.util.StatusEnum

class WayPointAdapter(private val listener: ContainerClickListener, private val items: RealmList<WayPointEntity>) : RecyclerView.Adapter<WayPointAdapter.WayPointViewHolder>() {
    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WayPointViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.map_behavior_item, parent, false)
        return WayPointViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: WayPointViewHolder, position: Int) {
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

        holder.itemView.map_behavior_address.text = item!!.address
        holder.itemView.map_behavior_scrp_id.text = item.srp_id.toString()
        holder.itemView.map_behavior_container_count.text = "${item!!.cs!!.size} контейнер"

        holder.itemView.map_behavior_coordinate.setOnClickListener {
            listener.moveCameraPoint(Point(item.co?.get(0)!!, item.co?.get(1)!!))
        }

        when (items[position]!!.status) {
            StatusEnum.empty -> {
                holder.itemView.map_behavior_status.isVisible = false
                holder.itemView.setOnClickListener {
                    if (checkedPosition != holder.adapterPosition) {
                        holder.itemView.map_behavior_expl.expand()
                        holder.itemView.map_behavior_start_service.setOnClickListener {
                            listener.startPointService(item)
                        }
                        holder.itemView.map_behavior_fire.setOnClickListener {
                            listener.startPointProblem(item)
                        }
                        notifyItemChanged(checkedPosition)
                        checkedPosition = holder.adapterPosition
                    }
                }
            }
            StatusEnum.completed -> {
                holder.itemView.map_behavior_status.isVisible = true
                holder.itemView.map_behavior_status.setImageResource(R.drawable.ic_check)
                holder.itemView.setOnClickListener {
                    //nothing
                }
            }
            StatusEnum.breakDown -> {
                holder.itemView.map_behavior_status.isVisible = true
                holder.itemView.map_behavior_status.setImageResource(R.drawable.ic_red_check)
                holder.itemView.setOnClickListener {
                    //nothing
                }
            }

            StatusEnum.failure -> {
                holder.itemView.map_behavior_status.isVisible = true
                holder.itemView.map_behavior_status.setImageResource(R.drawable.ic_cancel)
                holder.itemView.setOnClickListener {
                    //nothing
                }
            }
        }

    }

    class WayPointViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    interface ContainerClickListener {
        fun startPointService(item: WayPointEntity)
        fun startPointProblem(item: WayPointEntity)
        fun moveCameraPoint(point: Point)
    }
}