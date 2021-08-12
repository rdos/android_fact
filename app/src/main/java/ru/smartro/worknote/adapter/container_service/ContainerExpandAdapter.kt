package ru.smartro.worknote.adapter.container_service

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_choose.view.choose_status
import kotlinx.android.synthetic.main.item_choose.view.choose_title
import kotlinx.android.synthetic.main.item_container.view.*
import net.cachapa.expandablelayout.ExpandableLayout
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup
import ru.smartro.worknote.R
import ru.smartro.worknote.service.database.entity.work_order.ContainerEntity
import ru.smartro.worknote.util.StatusEnum

class ContainerExpandAdapter(
    private val listener: ContainerPointClickListener,
    private val items: ArrayList<ContainerEntity>
) : RecyclerView.Adapter<ContainerExpandAdapter.OwnerViewHolder>() {
    private var checkedPosition = -1

    private var volume: Double = 0.0
    private var comment: String = ""
    private var lastExpandableLayout: ExpandableLayout? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container, parent, false)
        return OwnerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        val item = items[position]

        if (items.lastIndex == position) {
            lastExpandableLayout = holder.itemView.enter_info_exp
        }

        if (checkedPosition == -1) {
            holder.itemView.enter_info_exp.hide()
        } else {
            if (checkedPosition == holder.adapterPosition) {
                holder.itemView.enter_info_exp.show()
            } else {
                holder.itemView.enter_info_exp.hide()
            }
        }

        holder.itemView.comment_clear.setOnClickListener {
            holder.itemView.comment_et.setText("")
        }

        holder.itemView.enter_info_exp.setOnExpansionUpdateListener { _, state ->
            when (state) {
                0 -> {
                    Log.d("ContainerExpandAdapter", "position:${position} closed")
                    Log.d("ContainerExpandAdapter", "selectedVolume:${volume} comment: $comment")
                    if (isNotDefault(volume, holder.itemView.comment_et.toString())) {
                        listener.saveContainerInfo(
                            containerId = item.containerId!!,
                            volume = volume, comment = comment
                        )
                    }


                }
                3 -> Log.d("ContainerExpandAdapter", "position:${position} expanded")
            }

        }

        holder.itemView.comment_et.setText(item.comment)

        holder.itemView.back_button.setOnClickListener {
            holder.itemView.enter_info_exp.hide()
            notifyItemChanged(position)
            checkedPosition = -1
        }

        if (item.volume == null) {
            holder.itemView.enter_info_percent_rg.selectButton(holder.itemView.percent_100)
        } else {
            setPercent(
                holder.itemView.enter_info_percent_rg, holder.itemView.percent_0, holder.itemView.percent_25,
                holder.itemView.percent_50, holder.itemView.percent_75,
                holder.itemView.percent_100, holder.itemView.percent_125, item.volume!!
            )
        }

        holder.itemView.enter_info_percent_rg.setOnSelectListener {
            volume = toPercent(it.text.replace("%", "").toInt())
        }

        holder.itemView.comment_et.addTextChangedListener {
            comment = it.toString()
        }

        holder.itemView.choose_title.text = item.number
        if (item.isActiveToday!!) {
            when (item.status) {
                StatusEnum.NEW -> {
                    holder.itemView.choose_status.isVisible = false
                    holder.itemView.setOnClickListener {
                        if (checkedPosition != holder.adapterPosition) {
                            holder.itemView.enter_info_exp.show()

                            Log.d("ContainerPointAdapter", "onBindViewHolder: true")
                            notifyItemChanged(checkedPosition)
                            checkedPosition = holder.adapterPosition
                        } else {
                            holder.itemView.enter_info_exp.hide()
                        }
                    }
                }
                StatusEnum.SUCCESS -> {
                    holder.itemView.choose_status.setImageResource(R.drawable.ic_check)
                    holder.itemView.choose_status.isVisible = true
                    holder.itemView.setOnClickListener {
                        if (checkedPosition != holder.adapterPosition) {
                            holder.itemView.enter_info_exp.show()
                            notifyItemChanged(checkedPosition)
                            checkedPosition = holder.adapterPosition
                        } else {
                            holder.itemView.enter_info_exp.hide()
                        }
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
            when (item.status) {
                StatusEnum.NEW -> {
                    holder.itemView.choose_status.setImageResource(R.drawable.ic_cancel)
                    holder.itemView.choose_status.isVisible = true
                    holder.itemView.setOnClickListener {
                        if (checkedPosition != holder.adapterPosition) {
                            holder.itemView.enter_info_exp.show()
                            notifyItemChanged(checkedPosition)
                            checkedPosition = holder.adapterPosition
                        } else {
                            holder.itemView.enter_info_exp.hide()
                        }
                    }

                }
                StatusEnum.SUCCESS -> {
                    holder.itemView.choose_status.setImageResource(R.drawable.ic_cancel_green)
                    holder.itemView.choose_status.isVisible = true
                    holder.itemView.setOnClickListener {
                        if (checkedPosition != holder.adapterPosition) {
                            holder.itemView.enter_info_exp.show()
                            notifyItemChanged(checkedPosition)
                            checkedPosition = holder.adapterPosition
                        } else {
                            holder.itemView.enter_info_exp.hide()
                        }
                    }
                }
                StatusEnum.ERROR -> {
                    holder.itemView.choose_status.isVisible = true
                    holder.itemView.choose_status.setImageResource(R.drawable.ic_cancel_red)
                    holder.itemView.setOnClickListener {
                        Log.d("ContainerPointAdapter", "onBindViewHolder: true")
                    }
                }
            }
        }
    }

    class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface ContainerPointClickListener {
        fun saveContainerInfo(containerId: Int, volume: Double, comment: String)
    }

    private fun toPercent(percent: Int): Double {
        return when (percent) {
            0 -> 0.00
            25 -> 0.25
            50 -> 0.50
            75 -> 0.75
            100 -> 1.00
            else -> 1.25
        }
    }

    private fun setPercent(
        group: ThemedToggleButtonGroup, view0: ThemedButton, view25: ThemedButton, view50:
        ThemedButton, view75: ThemedButton, view100: ThemedButton, view125: ThemedButton, percent: Double
    ) {
        when (percent) {
            0.00 -> {
                group.selectButton(view0)
            }
            0.25 -> {
                group.selectButton(view25)
            }
            0.50 -> {
                group.selectButton(view50)
            }
            0.75 -> {
                group.selectButton(view75)
            }
            1.00 -> {
                group.selectButton(view100)
            }
            else -> {
                group.selectButton(view125)
            }
        }
    }


    private fun ExpandableLayout.hide() {
        this.collapse()
    }

    private fun ExpandableLayout.show() {
        this.expand()
    }

    fun collapseAllItems() {
        lastExpandableLayout?.hide()
        checkedPosition = -1
    }

    fun isNotDefault(volume: Double, comment: String): Boolean {
        Log.d("ContainerExpandAdapter", "volumeIsNotO:${volume != 0.0} commentIsNotNullOrEmpty:${comment.isNullOrEmpty()} ")
        return volume != 0.0 || !comment.isNullOrEmpty()
    }
}