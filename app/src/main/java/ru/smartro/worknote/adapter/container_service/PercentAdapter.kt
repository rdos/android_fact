package ru.smartro.worknote.adapter.container_service

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_percent.view.*
import ru.smartro.worknote.R

class PercentAdapter(
    private val context: Context,
    private val items: ArrayList<PercentModel>
) : RecyclerView.Adapter<PercentAdapter.OwnerViewHolder>() {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val outMetrics = DisplayMetrics()
    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_percent, parent, false)
        return OwnerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        @Suppress("DEPRECATION")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = context.display
            display?.getRealMetrics(outMetrics)
        } else {
            val display = windowManager.defaultDisplay
            display.getMetrics(outMetrics)
        }
        holder.itemView.percent_cardview.layoutParams.height = outMetrics.widthPixels / 7
        holder.itemView.percent_cardview.layoutParams.width = outMetrics.widthPixels / 4
        if (checkedPosition == -1) {
            holder.itemView.percent_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.light_green))
            holder.itemView.item_percent_tv.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
            if (items[position].selected) {
                holder.itemView.percent_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
                holder.itemView.item_percent_tv.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                checkedPosition = holder.adapterPosition
            }
        } else {
            if (checkedPosition == holder.adapterPosition) {
                holder.itemView.percent_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
                holder.itemView.item_percent_tv.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            } else {
                holder.itemView.percent_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.light_green))
                holder.itemView.item_percent_tv.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
            }
        }
        holder.itemView.item_percent_tv.text = "${toPercent(items[position].volume)} %"
        holder.itemView.setOnClickListener {
            holder.itemView.percent_cardview.isVisible = true
            if (checkedPosition != holder.adapterPosition) {
                holder.itemView.percent_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
                holder.itemView.item_percent_tv.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                notifyItemChanged(checkedPosition)
                checkedPosition = holder.adapterPosition
            }
        }
    }

    fun getSelectedCount(): Double {
        return (if (checkedPosition != -1) {
            items[checkedPosition].volume
        } else {
            -1.00
        })
    }

    private fun toPercent(percent: Double): Int {
        return when (percent) {
            0.00 -> 0
            0.25 -> 25
            0.50 -> 50
            0.75 -> 75
            1.00 -> 100
            else -> 125
        }
    }

    class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}