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
import ru.smartro.worknote.presentation.platform_serve.ServedContainers

class SimplifiedContainerAdapter(
    private val context: Context,
    private val listener: TypedContainerAdapter.TypedContainerListener
) : RecyclerView.Adapter<SimplifiedContainerAdapter.ClientGroupViewHolder>() {

    var served: List<ServedContainers> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var containers: List<ClientGroupedContainers> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientGroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.f_pserve_simplify__rv_clientcluster, parent, false)
        return ClientGroupViewHolder(view, context, listener)
    }

    override fun getItemCount(): Int {
        return containers.size
    }

    override fun onBindViewHolder(holder: ClientGroupViewHolder, position: Int) {
        holder.bind(position, containers[position], served)
    }

    class ClientGroupViewHolder(
        val view: View,
        val context: Context,
        val listener: TypedContainerAdapter.TypedContainerListener
    ) : RecyclerView.ViewHolder(view) {
        fun bind(clientGroupIndex: Int, clientGroup: ClientGroupedContainers, servedConts: List<ServedContainers>) {
            val tvClient = view.findViewById<TextView>(R.id.client_label)
            val rvGroupedContainers = view.findViewById<RecyclerView>(R.id.typed_containers)

            tvClient.text = clientGroup.client.ifEmpty { "Клиент не указан" }
            rvGroupedContainers.layoutManager = LinearLayoutManager(context)
            rvGroupedContainers.adapter = TypedContainerAdapter(
                context,
                listener
            ).apply {
                data = TCAdata(servedConts, clientGroup.typeGroupedContainers, clientGroupIndex)
            }
        }
    }
}