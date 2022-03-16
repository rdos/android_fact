package ru.smartro.worknote.work.platform_serve

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_container_adapter.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.util.MyUtil.toStr

// TODO: 22.10.2021 !!!когда?
class ContainerAdapter(private val listener: ContainerPointClickListener, private val containers: ArrayList<ContainerEntity>) :
    RecyclerView.Adapter<ContainerAdapter.OwnerViewHolder>() {
    // TODO: 22.10.2021  item_container_adapter !!!
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_container_adapter, parent, false)
        return OwnerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return containers.size
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        val container = containers[position]

        holder.itemView.choose_title.text = container.number
        holder.itemView.tv_item_container_adapter__type_name.text = container.typeName
        // TODO: 25.10.2021 add getString() + format
        holder.itemView.tv_item_container_adapter__constructiveVolume.text = "${container.constructiveVolume.toStr("м³")}"

        holder.itemView.setOnClickListener {
            listener.startContainerService(item = containers[position])
            Log.d("ContainerPointAdapter", "onBindViewHolder: true")
        }
        val tvVolume = holder.itemView.findViewById<TextView>(R.id.tv_item_container_adapter__volume)
        tvVolume.text =  "${container.getVolumeInPercent().toString().dropLast(2)}%"
        //2&
        tvVolume.setTextColor(container.getVolumePercentColor(holder.itemView.context))

        if (container.isFailureNotEmpty()) {
            holder.itemView.choose_cardview.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red_cool))
        }
        if (container.isBreakdownNotEmpty()) {
            holder.itemView.choose_cardview.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red_cool))
        }
    }

    class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface ContainerPointClickListener {
        fun startContainerService(item: ContainerEntity)
    }

    fun updateData(newData: ArrayList<ContainerEntity>) {
        containers.clear()
        containers.addAll(newData)
        notifyDataSetChanged()
    }
}