package ru.smartro.worknote.presentation.checklist.vehicle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.VehicleBodyOutVehicle

class StartVehicleAdapter(private val listener: (VehicleBodyOutVehicle) -> Unit): RecyclerView.Adapter<StartVehicleAdapter.VehicleViewHolder>() {

    private val mItems: MutableList<VehicleBodyOutVehicle> = mutableListOf()
    private var mFilteredItems: MutableList<VehicleBodyOutVehicle> = mutableListOf()
    fun setItems(vehicleList: List<VehicleBodyOutVehicle>) {
        mItems.clear()
        mItems.addAll(vehicleList)
        mFilteredItems.clear()
        mFilteredItems.addAll(vehicleList)
        notifyDataSetChanged()
    }

    fun clearItems() {
        mItems.clear()
        notifyDataSetChanged()
    }

    fun updateList(_filterText: String) {
        val filterText = _filterText.lowercase()
        mFilteredItems.clear()
        mFilteredItems.addAll(mItems.filter { el ->
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
        // TODO TEST I THINK THAT IF GETITEMCOUNT RETURNS FILTEREDITEMS SIZE THEN WE CAN GET RID OF IF HERE
//        if(mFilteredItems.isNotEmpty() && position < mFilteredItems.size) {
            holder.bind(mFilteredItems[position])
//        }
    }

    override fun getItemCount(): Int = mFilteredItems.size

    class VehicleViewHolder(val itemView: View, val listener: (VehicleBodyOutVehicle) -> Unit): RecyclerView.ViewHolder(itemView) {
        fun bind(vehicle: VehicleBodyOutVehicle) {
            itemView.findViewById<TextView>(R.id.vehicle_name).text = vehicle.name
            itemView.setOnClickListener {
                listener(vehicle)
            }
        }
    }
}