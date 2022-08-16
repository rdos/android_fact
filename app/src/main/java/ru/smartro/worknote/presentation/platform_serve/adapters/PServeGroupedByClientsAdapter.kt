package ru.smartro.worknote.presentation.platform_serve.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.slf4j.LoggerFactory
import ru.smartro.worknote.R
import ru.smartro.worknote.log
import ru.smartro.worknote.presentation.platform_serve.ClientGroupedContainers
import ru.smartro.worknote.work.ServedContainers

class PServeGroupedByClientsAdapter(
    private val context: Context,
    private val listener: SimplifyContainerServeListener
) : RecyclerView.Adapter<PServeGroupedByClientsAdapter.ClientGroupViewHolder>() {

    var served: List<ServedContainers> = listOf()
        set(value) {
            log("servedContainers: ${value}")
            field = value
            notifyDataSetChanged()
        }

    var containers: List<ClientGroupedContainers> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientGroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.f_pserve_by_types__rv_clientcluster, parent, false)
        return ClientGroupViewHolder(view, context, listener)
    }

    override fun getItemCount(): Int {
        return containers.size
    }

    override fun onBindViewHolder(holder: ClientGroupViewHolder, position: Int) {
        val clientGroup = containers[position]
        val servedContainersFilteredByClient = served.filter { el -> el.client == clientGroup.client }
        holder.bind(clientGroup, servedContainersFilteredByClient)
    }

    class ClientGroupViewHolder(
        val view: View,
        val context: Context,
        val listener: SimplifyContainerServeListener
    ) : RecyclerView.ViewHolder(view) {

        fun bind(clientGroup: ClientGroupedContainers, servedConts: List<ServedContainers>) {
            val tvClient = view.findViewById<TextView>(R.id.client_label)
            val rvGroupedContainers = view.findViewById<RecyclerView>(R.id.typed_containers)

            tvClient.text = clientGroup.client.ifEmpty { "Клиент не указан" }
            rvGroupedContainers.layoutManager = LinearLayoutManager(context)
            rvGroupedContainers.adapter = TypedContainerAdapter(
                context,
                object : TypedContainerAdapter.TypedContainerListener {
                    override fun onDecrease(typeName: String) {
                        listener.onDecrease(clientGroup.client, typeName)
                    }

                    override fun onIncrease(typeName: String) {
                        listener.onIncrease(clientGroup.client, typeName)
                    }

                    override fun onAddPhoto(typeName: String) {
                        listener.onAddPhoto(clientGroup.client, typeName)
                    }

                }
            ).apply {
                data = TCAdata(servedConts, clientGroup.typeGroupedContainers)
            }
        }

    }

    interface SimplifyContainerServeListener {
        fun onDecrease(clientName: String, typeName: String)
        fun onIncrease(clientName: String, typeName: String)
        fun onAddPhoto(clientName: String, typeName: String)
    }
}