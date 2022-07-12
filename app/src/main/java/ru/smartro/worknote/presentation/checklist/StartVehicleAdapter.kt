package ru.smartro.worknote.presentation.checklist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.f_start_vehicle__rv_item.view.*
import kotlinx.android.synthetic.main.item_container_adapter.view.*
import kotlinx.android.synthetic.main.start_act__rv_item_know1.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.service.network.response.organisation.Organisation
import ru.smartro.worknote.awORKOLDs.service.network.response.vehicle.Vehicle

class StartVehicleAdapter(private val listener: (Vehicle) -> Unit): RecyclerView.Adapter<StartVehicleAdapter.VehicleViewHolder>() {

    private val mItems: MutableList<Vehicle> = mutableListOf()
    private var mFilteredItems: MutableList<Vehicle> = mutableListOf()
    fun setItems(vehicleList: List<Vehicle>) {
        mItems.clear()
        mItems.addAll(vehicleList)
        mFilteredItems.clear()
        mFilteredItems.addAll(vehicleList)
        notifyDataSetChanged()
    }

    fun updateList(filterText: String) {
        mFilteredItems.clear()
        mFilteredItems.addAll(mItems.filter { el ->
            Log.d("TEST:::", "ELELELLE ${el}")
            if(el.name != null) {
                val name = el.name.lowercase()
                name.startsWith(filterText) || name.contains(filterText)
            } else {
                false
            }
        })
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.f_start_vehicle__rv_item, parent, false)
        return VehicleViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.bind(mFilteredItems[position])
    }

    override fun getItemCount(): Int = mItems.size

    class VehicleViewHolder(val itemView: View, val listener: (Vehicle) -> Unit): RecyclerView.ViewHolder(itemView) {
        fun bind(vehicle: Vehicle) {
            itemView.findViewById<TextView>(R.id.vehicle_name).text = vehicle.name
            itemView.setOnClickListener {
                listener(vehicle)
            }
        }
    }
}