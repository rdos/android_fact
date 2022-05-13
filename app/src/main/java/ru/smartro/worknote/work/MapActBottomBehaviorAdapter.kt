package ru.smartro.worknote.work

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.geometry.Point
import kotlinx.android.synthetic.main.act_map__bottom_behavior__rv_item.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.PoinT
import ru.smartro.worknote.isShowForUser
import ru.smartro.worknote.awORKOLDs.util.StatusEnum



class MapActBottomBehaviorAdapter(
    private val listener: PlatformClickListener,
    private val items: List<PlatformEntity>,
    private val mFilteredWayTaskIds: MutableList<Int>
) : RecyclerView.Adapter<MapActBottomBehaviorAdapter.PlatformViewHolder>() {
    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatformViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.act_map__bottom_behavior__rv_item, parent, false)
        return PlatformViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun setUseButtonStyleBackgroundGreen(view: View) {
//        appCompatButton.alpha = 1f
        view.setBackgroundDrawable(ContextCompat.getDrawable(view.context, R.drawable.bg_button_green__usebutton))
    }

    private fun setUseButtonStyleBackgroundRed(view: View) {
//        appCompatButton.alpha = 1f
        view.setBackgroundDrawable(ContextCompat.getDrawable(view.context, R.drawable.bg_button_red__usebutton))
    }

    // TODO: ну -Гляди_ держись)
    private fun setDefButtonStyleBackground(view: View) {
//        appCompatButton.alpha = 1f
        view.setBackgroundDrawable(ContextCompat.getDrawable(view.context, R.drawable.bg_button_green__default))
    }

    private fun setUseButtonStyleBackgroundYellow(v: View) {
        v.setBackgroundDrawable(ContextCompat.getDrawable(v.context, R.drawable.bg_button_orange__usebutton))
    }

    override fun onBindViewHolder(holder: PlatformViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.alpha = 1f
        //фильрация
        if (item.workOrderId in mFilteredWayTaskIds) {
            holder.itemView.alpha = 0.1f
        }
        if (checkedPosition == -1) {
            holder.itemView.map_behavior_expl.collapse()
        } else {
            if (checkedPosition == holder.adapterPosition) {
                holder.itemView.map_behavior_expl.expand(true)
            } else {
                holder.itemView.map_behavior_expl.collapse()
            }
        }

        holder.itemView.tv_item_map_behavior__address.text = item.address
        val tvName = holder.itemView.findViewById<TextView>(R.id.tv_item_map_behavior__name)
        tvName.isVisible = false
        if (item.name.isShowForUser()) {
            tvName.text = item.name
            tvName.isVisible = true
        }

        val tvOrderTime = holder.itemView.findViewById<TextView>(R.id.tv_item_map_behavior__order_time)
        val orderTime = item.getOrderTime()
        tvOrderTime.isVisible = false
        if (orderTime.isShowForUser()) {
            tvOrderTime.text = orderTime
            tvOrderTime.setTextColor(item.getOrderTimeColor(holder.itemView.context))
            tvOrderTime.isVisible = true
        }

        holder.itemView.map_behavior_scrp_id.text = item.srpId.toString()
        val containerString: String = holder.itemView.context.resources.getQuantityString(R.plurals.container_count, item.containers.size)
        holder.itemView.map_behavior_container_count.text = "${item.containers.size} $containerString"

        holder.itemView.map_behavior_coordinate.setOnClickListener {
            listener.moveCameraPlatform(PoinT(item.coords[0]!!, item.coords[1]!!))
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
                setDefButtonStyleBackground(holder.itemView)
                holder.itemView.setOnClickListener {
                    if (!holder.itemView.map_behavior_expl.isExpanded) {
                        listener.onPlatformClicked(position)
                        holder.itemView.postDelayed({
                            holder.itemView.map_behavior_expl.expand()
                        }, 500)
                        if (item.isStartServe()) {
                            holder.itemView.map_behavior_start_service.setText(R.string.start_serve_again)
                        }
                        holder.itemView.map_behavior_start_service.setOnClickListener {
                            listener.startPlatformService(item)
                        }
                        holder.itemView.map_behavior_fire.setOnClickListener {
                            listener.startPlatformProblem(item)
                        }
                        notifyItemChanged(checkedPosition)
                        checkedPosition = holder.adapterPosition
                    } else {
                        holder.itemView.map_behavior_expl.collapse(true)
                    }
                }
            }
            StatusEnum.SUCCESS -> {
                setUseButtonStyleBackgroundGreen(holder.itemView)
                //                clItemMapBehavior.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.pink))

//                holder.itemView.alpha = 0.6f
//                holder.itemView.map_behavior_status.setImageResource(R.drawable.ic_check)
//                holder.itemView.setOnClickListener {
//                    //nothing
//                }
            }
            StatusEnum.ERROR -> {
                setUseButtonStyleBackgroundRed(holder.itemView)
//                clItemMapBehavior.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.pink))

            }
            StatusEnum.UNFINISHED -> {
                setUseButtonStyleBackgroundYellow(holder.itemView)
//                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.orangeCool))
//                holder.itemView.alpha = 0.6f
//                holder.itemView.map_behavior_status.isVisible = true
//                holder.itemView.map_behavior_status.setImageResource(R.drawable.ic_orange_check)
//                holder.itemView.setOnClickListener {
//                    //nothing
//                }
            }
        }

    }

    class PlatformViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    interface PlatformClickListener {
        fun onPlatformClicked(position: Int)
        fun startPlatformService(item: PlatformEntity)
        fun startPlatformProblem(item: PlatformEntity)
        fun moveCameraPlatform(point: PoinT)
        fun navigatePlatform(checkPoint: Point)
    }
}