package ru.smartro.worknote.presentation.platform_serve.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.platform_serve.TypeGroupedContainers

class TypedContainerAdapter(
    private val context: Context,
    private val listener: TypedContainerListener
) : RecyclerView.Adapter<TypedContainerAdapter.TypeGroupedViewHolder>() {

    var containers: List<TypeGroupedContainers> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeGroupedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_simplified_serve_typed_containers, parent, false)
        return TypeGroupedViewHolder(view, context, listener)
    }

    override fun getItemCount(): Int {
        return containers.size
    }

    override fun onBindViewHolder(holder: TypeGroupedViewHolder, position: Int) {
        holder.bind(containers[position])
    }

    class TypeGroupedViewHolder(
        val view: View,
        val context: Context,
        val listener: TypedContainerListener
    ) : RecyclerView.ViewHolder(view) {
        fun bind(typeGroup: TypeGroupedContainers) {
            val tvTypeName = view.findViewById<TextView>(R.id.container_type)
            val bDecrease = view.findViewById<AppCompatButton>(R.id.button_decrease_cont)
            val tvCount = view.findViewById<TextView>(R.id.containers_count)
            val bIncrease = view.findViewById<AppCompatButton>(R.id.button_increase_cont)
            val bAddPhoto = view.findViewById<AppCompatButton>(R.id.button_add_photo)
            val tvContSize = view.findViewById<TextView>(R.id.containers_size)
            val containersIds = typeGroup.containers.map { it.containerId ?: -1 }

            tvTypeName.text = typeGroup.typeName.ifEmpty { "Тип не указан" }

            bDecrease.setOnClickListener {
                listener.onDecrease(containersIds)
            }

            tvCount.text =
                typeGroup.containers.count {
                    it.volume != null && !it.isFailureNotEmpty()
                }.toString()

            bIncrease.setOnClickListener {
                listener.onIncrease(containersIds)
            }

            bAddPhoto.setOnClickListener {
                listener.onAddPhoto(containersIds)
            }

            tvContSize.text = typeGroup.containers.size.toString()
        }
    }

    interface TypedContainerListener {
        fun onDecrease(containersIds: List<Int>)
        fun onIncrease(containersIds: List<Int>)
        fun onAddPhoto(containersIds: List<Int>)
    }
}