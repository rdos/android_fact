package ru.smartro.worknote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.geometry.Point
import io.realm.RealmList
import kotlinx.android.synthetic.main.item_map_behavior.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.util.StatusEnum


class PlatformAdapter(
    private val listener: PlatformClickListener,
    private val items: RealmList<PlatformEntity>
) : RecyclerView.Adapter<PlatformAdapter.PlatformViewHolder>() {
    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatformViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_map_behavior, parent, false)
        return PlatformViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PlatformViewHolder, position: Int) {
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

        holder.itemView.tv_item_map_behavior__address.text = item!!.address
        holder.itemView.tv_item_map_behavior__name.text = item.name
        holder.itemView.map_behavior_scrp_id.text = item.srpId.toString()
        val containerString: String = holder.itemView.context.resources.getQuantityString(R.plurals.container_count, item.containers.size)
        holder.itemView.map_behavior_container_count.text = "${item.containers.size} $containerString"

        holder.itemView.map_behavior_coordinate.setOnClickListener {
            listener.moveCameraPlatform(Point(item.coords[0]!!, item.coords[1]!!))
        }
        holder.itemView.map_behavior_location.setOnClickListener {
            listener.navigatePlatform(Point(item.coords[0]!!, item.coords[1]!!))
        }

        val tvPlatformContact = holder.itemView.findViewById<TextView>(R.id.tv_item_map_behavior__platform_contact)
        val contactsInfo = item.getContactsInfo()
        tvPlatformContact.text = contactsInfo
        tvPlatformContact.isVisible = contactsInfo.isNotEmpty()
        when (item.status) {
            StatusEnum.NEW -> {
                holder.itemView.map_behavior_status.isVisible = false
                holder.itemView.setOnClickListener {
                    if (checkedPosition != holder.adapterPosition) {
                        holder.itemView.map_behavior_expl.expand()
                        holder.itemView.map_behavior_start_service.setOnClickListener {
                            listener.startPlatformService(item)
                        }
                        holder.itemView.map_behavior_fire.setOnClickListener {
                            listener.startPlatformProblem(item)
                        }
                        notifyItemChanged(checkedPosition)
                        checkedPosition = holder.adapterPosition
                    }
                }
            }
            StatusEnum.SUCCESS -> {
                holder.itemView.map_behavior_status.isVisible = true
                holder.itemView.map_behavior_status.setImageResource(R.drawable.ic_check)
                holder.itemView.setOnClickListener {
                    //nothing
                }
            }
            StatusEnum.ERROR -> {
                holder.itemView.map_behavior_status.isVisible = true
                holder.itemView.map_behavior_status.setImageResource(R.drawable.ic_red_check)
                holder.itemView.setOnClickListener {
                    //nothing
                }
            }
            StatusEnum.UNFINISHED -> {
                holder.itemView.map_behavior_status.isVisible = true
                holder.itemView.map_behavior_status.setImageResource(R.drawable.ic_orange_check)
                holder.itemView.setOnClickListener {
                    //nothing
                }
            }
        }

    }

    class PlatformViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    interface PlatformClickListener {
        fun startPlatformService(item: PlatformEntity)
        fun startPlatformProblem(item: PlatformEntity)
        fun moveCameraPlatform(point: Point)
        fun navigatePlatform(checkPoint: Point)
    }
}