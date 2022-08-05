package ru.smartro.worknote.presentation.platform_serve.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.platform_serve.TypeGroupedContainers
import ru.smartro.worknote.work.ServedContainers

// TODO VLAD
data class TCAdata(
    var servedContainers: List<ServedContainers> = listOf(),
    var group: List<TypeGroupedContainers> = listOf(),
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
        val typeGroup = data.group[position]
        Log.d("SHEESH :::", "typeGroup: ${typeGroup}")
        Log.d("SHEESH :::", "servedContainers: ${data.servedContainers}")
        val servedContainers = data.servedContainers.find { el -> typeGroup.typeName == el.typeName }
        if(servedContainers != null)
            holder.bind(typeGroup, servedContainers)
        else
            holder.bind(typeGroup, ServedContainers("", "", 0))
    }

    class TypeGroupedViewHolder(
        val view: View,
        val context: Context,
        val listener: TypedContainerListener
    ) : RecyclerView.ViewHolder(view) {
        fun bind(typeGroup: TypeGroupedContainers, servedGroup: ServedContainers) {

            val tvTypeName = view.findViewById<TextView>(R.id.container_type)
            val bDecrease = view.findViewById<AppCompatButton>(R.id.button_decrease_cont)
            val tvCount = view.findViewById<TextView>(R.id.containers_count)
            val bIncrease = view.findViewById<AppCompatButton>(R.id.button_increase_cont)
            val bAddPhoto = view.findViewById<AppCompatButton>(R.id.button_add_photo)
            val tvContSize = view.findViewById<TextView>(R.id.containers_size)

            tvTypeName.text = typeGroup.typeName.ifEmpty { "Тип не указан" }
            tvCount.text = servedGroup.servedCount.toString()
            tvContSize.text = typeGroup.containersIds.size.toString()


            bDecrease.setOnClickListener {
                listener.onDecrease(typeGroup.typeName)
            }

            bIncrease.setOnClickListener {
                listener.onIncrease(typeGroup.typeName)
            }

            bAddPhoto.setOnClickListener {
                listener.onAddPhoto(typeGroup.typeName)
            }
        }
    }

    interface TypedContainerListener {
        fun onDecrease(typeName: String)
        fun onIncrease(typeName: String)
        fun onAddPhoto(typeName: String)
    }
}