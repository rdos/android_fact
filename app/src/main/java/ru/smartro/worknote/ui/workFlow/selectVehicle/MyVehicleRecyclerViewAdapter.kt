package ru.smartro.worknote.ui.workFlow.selectVehicle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_vehicle.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.domain.models.VehicleModel

class MyVehicleRecyclerViewAdapter(
    private val onSelectListener: (VehicleModel) -> Unit,
    private val onDeselectListener: () -> Unit,
    private val viewModel: VehicleViewModel

) : RecyclerView.Adapter<MyVehicleRecyclerViewAdapter.ViewHolder>() {

    var enabled = false

    var vehiclesModels: List<VehicleModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var lastCheckBox: CheckBox? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_vehicle, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = vehiclesModels[position]
        holder.checkBox.text = item.name
        if (viewModel.lastSelected.value != null && item.id == viewModel.lastSelected.value) {
            lastCheckBox = holder.checkBox
            holder.checkBox.isChecked = true
        } else {
            holder.checkBox.isChecked = false
        }

        with(holder.mView) {
            this.checkBox.setOnClickListener {
                if (!enabled) {
                    return@setOnClickListener
                }
                if (this.checkBox.isChecked) {
                    onSelectListener(item)
                    lastCheckBox?.isChecked = false
                    lastCheckBox = this.checkBox
                } else {
                    lastCheckBox = null
                    onDeselectListener()
                }
            }
        }
    }

    override fun getItemCount(): Int = vehiclesModels.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val checkBox: CheckBox = mView.checkBox
    }
}
