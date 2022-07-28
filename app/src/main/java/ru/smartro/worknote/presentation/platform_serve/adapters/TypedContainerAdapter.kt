package ru.smartro.worknote.presentation.platform_serve.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.platform_serve.ServedContainers
import ru.smartro.worknote.presentation.platform_serve.TypeGroupedContainers

// TODO VLAD
data class TCAdata(
    var servedContainers: List<ServedContainers> = listOf(),
    var group: List<TypeGroupedContainers> = listOf(),
    var clientIndex: Int = -1
)

class TypedContainerAdapter(
    private val context: Context,
    private val listener: TypedContainerListener
) : RecyclerView.Adapter<TypedContainerAdapter.TypeGroupedViewHolder>() {

    var data: TCAdata = TCAdata()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeGroupedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.f_pserve_simplify__rv_conainers_typed, parent, false)
        return TypeGroupedViewHolder(view, context, listener)
    }

    override fun getItemCount(): Int {
        return data.group.size
    }

    override fun onBindViewHolder(holder: TypeGroupedViewHolder, position: Int) {
        data.servedContainers.find { it.clientGroupIndex == data.clientIndex && it.typeGroupIndex == position }
            ?.let {
                holder.bind(
                    position,
                    data.group[position],
                    it,
                    data.clientIndex
                )
            }
    }

    class TypeGroupedViewHolder(
        val view: View,
        val context: Context,
        val listener: TypedContainerListener
    ) : RecyclerView.ViewHolder(view) {
        fun bind(index: Int, typeGroup: TypeGroupedContainers, servedGroup: ServedContainers, clientInd: Int) {

            val tvTypeName = view.findViewById<TextView>(R.id.container_type)
            val bDecrease = view.findViewById<AppCompatButton>(R.id.button_decrease_cont)
            val tvCount = view.findViewById<TextView>(R.id.containers_count)
            val bIncrease = view.findViewById<AppCompatButton>(R.id.button_increase_cont)
            val bAddPhoto = view.findViewById<AppCompatButton>(R.id.button_add_photo)
            val tvContSize = view.findViewById<TextView>(R.id.containers_size)

            tvTypeName.text = typeGroup.typeName.ifEmpty { "Тип не указан" }

            bDecrease.setOnClickListener {
                listener.onDecrease(clientInd, index)
            }


            bIncrease.setOnClickListener {
                listener.onIncrease(clientInd, index)
            }

            bAddPhoto.setOnClickListener {
                listener.onAddPhoto(clientInd, index)
            }

            tvCount.text = if(servedGroup.count == -1) typeGroup.containersIds.size.toString() else servedGroup.count.toString()
            tvContSize.text = typeGroup.containersIds.size.toString()
        }
    }

    interface TypedContainerListener {
        fun onDecrease(clientGroupInd: Int, typeGroupInd: Int)
        fun onIncrease(clientGroupInd: Int, typeGroupInd: Int)
        fun onAddPhoto(clientGroupInd: Int, typeGroupInd: Int)
    }
}