package ru.smartro.worknote.presentation.platform_serve.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.R
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.awORKOLDs.util.MyUtil.toStr

// TODO: 22.10.2021 !!!когда?
class ExtendedContainerAdapter(
    private val activity: Context,
    private val listener: ContainerPointClickListener,
    private val containers: List<ContainerEntity>
) : RecyclerView.Adapter<ExtendedContainerAdapter.OwnerViewHolder>() {
    // TODO: 22.10.2021  item_container_adapter !!!
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_container_adapter, parent, false)
        return OwnerViewHolder(view, activity)
    }

    override fun getItemCount(): Int {
        return containers.size
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        val container = containers[position]

        holder.itemView.findViewById<TextView>(R.id.choose_title).text = container.number
        holder.itemView.findViewById<TextView>(R.id.tv_item_container_adapter__type_name).text = container.typeName
        // TODO: 25.10.2021 add getString() + format
        holder.itemView.findViewById<TextView>(R.id.tv_item_container_adapter__constructiveVolume).text = container.constructiveVolume.toStr("м³")
        holder.itemView.setOnClickListener {
            if(!container.isActiveToday && container.volume == null) {
                showTakeInactiveContainerAlert(holder.activity) {
                    listener.startContainerService(item = containers[position])
                }
            } else {
                listener.startContainerService(item = containers[position])
            }
            Log.d("ContainerPointAdapter", "onBindViewHolder: true")
        }
        val tvVolume = holder.itemView.findViewById<TextView>(R.id.tv_item_container_adapter__volume)
        tvVolume.text =  "${container.getVolumeInPercent()}%"
        //2&
        tvVolume.setTextColor(container.getVolumePercentColor(holder.itemView.context))

        if(!container.isActiveToday && container.volume == null) {
            holder.itemView.findViewById<TextView>(R.id.choose_title).setTextColor(ContextCompat.getColor(holder.activity, R.color.light_gray))
            holder.itemView.findViewById<TextView>(R.id.tv_item_container_adapter__type_name).setTextColor(ContextCompat.getColor(holder.activity, R.color.light_gray))
            holder.itemView.findViewById<TextView>(R.id.tv_item_container_adapter__constructiveVolume).setTextColor(ContextCompat.getColor(holder.activity, R.color.light_gray))
            tvVolume.setTextColor(ContextCompat.getColor(holder.activity, R.color.light_gray))
        }

        if (container.isFailureNotEmpty()) {
            holder.itemView.findViewById<CardView>(R.id.choose_cardview).setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red_cool))
        }

        if(container.volume != null && !container.isFailureNotEmpty()) {
            holder.itemView.findViewById<CardView>(R.id.choose_cardview).setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green_cool))
        }
    }

    private fun showTakeInactiveContainerAlert(context: Context, next: () -> Any) {
        try {
            lateinit var alertDialog: AlertDialog
            val builder = AlertDialog.Builder(context)
            val view = (context as Activity).layoutInflater.inflate(R.layout.alert_take_inactive_container, null)
            view.findViewById<AppCompatButton>(R.id.acb_alert_inactive_container___accept).setOnClickListener {
                next()
                alertDialog.dismiss()
            }
            view.findViewById<AppCompatButton>(R.id.acb_alert_inactive_container___decline).setOnClickListener {
                alertDialog.dismiss()
            }
            builder.setView(view)
            alertDialog = builder.create()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.show()
        } catch (e: Exception) {
            Log.e("ALERT INACTIVE CONTAINER :::", e.stackTraceToString())
        }
    }

    class OwnerViewHolder(itemView: View, val activity: Context) : RecyclerView.ViewHolder(itemView)

    interface ContainerPointClickListener {
        fun startContainerService(item: ContainerEntity)
    }
}