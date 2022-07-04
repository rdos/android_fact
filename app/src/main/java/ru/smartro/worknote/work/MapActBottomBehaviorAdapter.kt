package ru.smartro.worknote.work

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.geometry.Point
import kotlinx.android.synthetic.main.act_map__bottom_behavior__rv_item.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.BaseAdapter
import ru.smartro.worknote.andPOintD.PoinT
import ru.smartro.worknote.isShowForUser
import ru.smartro.worknote.awORKOLDs.util.StatusEnum


class MapActBottomBehaviorAdapter(
    private val listener: PlatformClickListener,
    mItemS: List<PlatformEntity>,
    private val mFilteredWayTaskIds: MutableList<Int>
) : BaseAdapter<PlatformEntity, MapActBottomBehaviorAdapter.PlatformViewHolder>(mItemS) {
    private var mOldQueryText: String? = null
    private var lastHolder: PlatformViewHolder? = null

    override fun onGetViewHolder(view: View): PlatformViewHolder {
        return PlatformViewHolder(view)
    }

    override fun onGetLayout(): Int {
        return R.layout.act_map__bottom_behavior__rv_item
    }

    fun filter(platformList: List<PlatformEntity>, filterText: String): List<PlatformEntity> {
        val query = filterText.lowercase()
        val filteredModeList = platformList.filter {
            try {
//                    it.javaClass.getField("address")
                val text = it.address?.lowercase()
                var res = true
                text?.let {
                    res = (text.startsWith(query) || (text.contains(query)))
                }
                res
            } catch (ex: Exception) {
                true
            }
        }
        //            val sYsTEM = mutableListOf<Vehicle>()
        return filteredModeList
    }

    fun filteredList(queryText: String?) {
        // TODO: !R_dos queryText == Snull??
        super.setQueryText(queryText)
        if(queryText.isNullOrEmpty()) {
            super.reset()
            return
        }
        val mItemsAfter = filter(super.getItemsForFilter(), queryText)
        super.set(mItemsAfter)
    }

    fun updateItemS(newItemS: List<PlatformEntity>) {
//        logSentry(filterText)
        super.setItems(newItemS)
        lastHolder?.collapseOld()
        if (mOldQueryText == null) {
            notifyDataSetChanged()
            return
        }
        filteredList(mOldQueryText)
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
        v.setBackgroundDrawable(ContextCompat.getDrawable(v.context, R.drawable.bg_button_yellow__usebutton))
    }

    private fun setUseButtonStyleBackgroundOrange(v: View) {
        v.setBackgroundDrawable(ContextCompat.getDrawable(v.context, R.drawable.bg_button_orange__usebutton))
    }

    override fun bind(item: PlatformEntity, holder: PlatformViewHolder) {
        holder.itemView.alpha = 1f
        //фильрация
        if (item.workOrderId in mFilteredWayTaskIds) {
            holder.itemView.alpha = 0.1f
        }
        if (lastHolder?.platformId == item.platformId) {
            holder.itemView.map_behavior_expl.expand(false)
        } else {
            holder.itemView.map_behavior_expl.collapse(false)
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

        val tvCurrentStatus = holder.itemView.findViewById<TextView>(R.id.tv_item_map_behavior__status)
        val status = when(item.getPlatformStatus()) {
            StatusEnum.NEW -> "Новое"
            StatusEnum.UNFINISHED -> "Не завершено"
            StatusEnum.SUCCESS -> "Завершено: успешно"
            StatusEnum.PARTIAL_PROBLEMS -> "Завершено: частичный невывоз"
            StatusEnum.ERROR -> "Завершено: невывоз"
            else -> null
        }
        if(status != null)
            tvCurrentStatus.text =status


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
        holder.itemView.setOnClickListener {
            // TODO:
            //nothing
        }

        val currentStatus = item.getPlatformStatus()
        if(currentStatus == StatusEnum.NEW || currentStatus == StatusEnum.UNFINISHED) {
            holder.itemView.setOnClickListener {
                if (!holder.itemView.map_behavior_expl.isExpanded) {
                    holder.itemView.map_behavior_expl.expand()
                    holder.itemView.map_behavior_start_service.setOnClickListener {
                        listener.startPlatformService(item)
                    }
                    holder.itemView.map_behavior_fire.setOnClickListener {
                        listener.startPlatformProblem(item)
                    }
                    if (lastHolder?.platformId != item.platformId) {
                        lastHolder?.collapseOld()
                    }
                    lastHolder = holder
                    lastHolder?.platformId = item.platformId
                } else {
                    holder.itemView.map_behavior_expl.collapse(true)
                }
            }
        } else {
            holder.itemView.setOnClickListener(null)
        }

        when (currentStatus) {
            StatusEnum.NEW -> {
                setDefButtonStyleBackground(holder.itemView)
            }
            StatusEnum.UNFINISHED -> {
                setUseButtonStyleBackgroundYellow(holder.itemView)
                holder.itemView.map_behavior_start_service.setText(R.string.start_serve_again)
            }
            StatusEnum.PARTIAL_PROBLEMS -> {
                setUseButtonStyleBackgroundOrange(holder.itemView)
            }
            StatusEnum.SUCCESS -> {
                setUseButtonStyleBackgroundGreen(holder.itemView)
            }
            StatusEnum.ERROR -> {
                setUseButtonStyleBackgroundRed(holder.itemView)
            }
        }
    }

    class PlatformViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun collapseOld() {
            if (platformId == null) {
                return
            }
            itemView.map_behavior_expl?.collapse()
            platformId = null
        }
        var platformId: Int? = null
    }

    interface PlatformClickListener {
        fun startPlatformService(item: PlatformEntity)
        fun startPlatformProblem(item: PlatformEntity)
        fun moveCameraPlatform(point: PoinT)
        fun navigatePlatform(checkPoint: Point)
    }
}