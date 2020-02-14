package ru.smartro.worknote.ui.workFlow.selectVehicle


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_vehicle.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.domain.models.VehicleModel
import ru.smartro.worknote.ui.workFlow.selectVehicle.VehicleFragment.OnListFragmentInteractionListener


/**
 * [RecyclerView.Adapter] that can display a [VehicleModel] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyVehicleRecyclerViewAdapter(
    private val selected: MutableLiveData<Int?>
) : RecyclerView.Adapter<MyVehicleRecyclerViewAdapter.ViewHolder>() {

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
        with(holder.mView) {
            this.checkBox.setOnClickListener {
                if (this.checkBox.isChecked) {
                    selected.value = item.id
                    lastCheckBox?.isChecked = false
                    lastCheckBox = this.checkBox
                } else {
                    lastCheckBox = null
                    selected.value = null
                }
            }
        }
    }

    override fun getItemCount(): Int = vehiclesModels.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val checkBox: CheckBox = mView.checkBox
    }
}
