package ru.smartro.worknote.presentation.platform_serve

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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.alert_take_inactive_container.view.*
import kotlinx.android.synthetic.main.item_container_adapter.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.awORKOLDs.util.MyUtil.toStr


class ContainerSimplifiedAdapter(
    private val context: Context,
//    private val listener: X
) : RecyclerView.Adapter<ContainerSimplifiedAdapter.ClientGroupViewHolder>() {

    var containers: List<ClientGroupedContainers> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientGroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_platform_serve_client_cluster, parent, false)
        return ClientGroupViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return containers.size
    }

    override fun onBindViewHolder(holder: ClientGroupViewHolder, position: Int) {
        holder.bind(containers[position])
    }

    class ClientGroupViewHolder(val view: View, val context: Context) : RecyclerView.ViewHolder(view) {
        fun bind(clientGroup: ClientGroupedContainers) {
            val tvClient = view.findViewById<TextView>(R.id.client_label)
            val rvGroupedContainers = view.findViewById<RecyclerView>(R.id.typed_containers)

            tvClient.text = clientGroup.client.ifEmpty { "Клиент не указан" }
            rvGroupedContainers.layoutManager = LinearLayoutManager(context)
            rvGroupedContainers.adapter = TypedContainerAdapter(context).apply {
                containers = clientGroup.groupedContainers
            }
//                adapter = TypedContainerAdapter(context, listener)
        }
    }


}