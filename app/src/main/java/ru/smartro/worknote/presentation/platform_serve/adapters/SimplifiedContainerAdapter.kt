package ru.smartro.worknote.presentation.platform_serve.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.platform_serve.ClientGroupedContainers

class SimplifiedContainerAdapter(
    private val context: Context,
    private val listener: ClientContainerListener
) : RecyclerView.Adapter<SimplifiedContainerAdapter.ClientGroupViewHolder>() {

    var containers: List<ClientGroupedContainers> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientGroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_platform_serve_client_cluster, parent, false)
        return ClientGroupViewHolder(view, context, listener)
    }

    override fun getItemCount(): Int {
        return containers.size
    }

    override fun onBindViewHolder(holder: ClientGroupViewHolder, position: Int) {
        holder.bind(containers[position])
    }

    class ClientGroupViewHolder(
        val view: View,
        val context: Context,
        val listener: ClientContainerListener
    ) : RecyclerView.ViewHolder(view) {
        fun bind(clientGroup: ClientGroupedContainers) {
            val tvClient = view.findViewById<TextView>(R.id.client_label)
            val rvGroupedContainers = view.findViewById<RecyclerView>(R.id.typed_containers)

            tvClient.text = clientGroup.client.ifEmpty { "Клиент не указан" }
            rvGroupedContainers.layoutManager = LinearLayoutManager(context)
            rvGroupedContainers.adapter = TypedContainerAdapter(
                context,
                object : TypedContainerAdapter.TypedContainerListener {
                    override fun onDecrease(typeGroupId: Int) {
                        listener.onDecrease(clientGroup.id, typeGroupId)
                    }

                    override fun onIncrease(typeGroupId: Int) {
                        listener.onIncrease(clientGroup.id, typeGroupId)
                    }

                    override fun onAddPhoto(typeGroupId: Int) {
                        listener.onAddPhoto(clientGroup.id, typeGroupId)
                    }

                }
            ).apply {
                containers = clientGroup.groupedContainers
            }
        }
    }

    interface ClientContainerListener {
        fun onDecrease(clientGroupId: Int, typeGroupId: Int)
        fun onIncrease(clientGroupId: Int, typeGroupId: Int)
        fun onAddPhoto(clientGroupId: Int, typeGroupId: Int)
    }
}