package ru.smartro.worknote.ui.workFlow.maintenance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.maintenance_list_item.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.domain.models.complex.SrpContainerWithRelations

class MaintenanceRecyclerViewAdapter :
    RecyclerView.Adapter<MaintenanceRecyclerViewAdapter.ViewHolder>() {

    var containers: List<SrpContainerWithRelations> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.maintenance_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = containers[position]
        holder.name.text = item.srpType.name
    }

    override fun getItemCount(): Int = containers.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val name: TextView = mView.container_type
    }
}
