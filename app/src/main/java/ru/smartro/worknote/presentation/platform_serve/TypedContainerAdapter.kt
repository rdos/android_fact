package ru.smartro.worknote.presentation.platform_serve

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.R

class TypedContainerAdapter(
    private val context: Context,
//    private val listener: X
) : RecyclerView.Adapter<TypedContainerAdapter.TypeGroupedViewHolder>() {

    var containers: List<TypeGroupedContainers> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeGroupedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_platform_serve_container_cluster, parent, false)
        return TypeGroupedViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return containers.size
    }

    override fun onBindViewHolder(holder: TypeGroupedViewHolder, position: Int) {
        holder.bind(containers[position])
    }

    class TypeGroupedViewHolder(val view: View, val context: Context) : RecyclerView.ViewHolder(view) {
        fun bind(typeGroup: TypeGroupedContainers) {
            val tvTypeName = view.findViewById<TextView>(R.id.container_type)
            val tvContSize = view.findViewById<TextView>(R.id.containers_size)
            tvTypeName.text = typeGroup.typeName.ifEmpty { "Тип не указан" }
            tvContSize.text = typeGroup.containers.size.toString()
        }
    }


}