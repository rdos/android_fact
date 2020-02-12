package ru.smartro.worknote.ui.workFlow.selectVehicle


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
    val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyVehicleRecyclerViewAdapter.ViewHolder>() {

    var vehiclesModels: List<VehicleModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val mOnClickListener: View.OnClickListener


    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as VehicleModel

            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_vehicle, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = vehiclesModels[position]
        holder.checkBox.text = item.name
//        holder.mIdView.text = item.id
//        holder.mContentView.text = item.content

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = vehiclesModels.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val checkBox: CheckBox = mView.checkBox
//        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '"/* + mContentView.text + "'"*/
        }
    }
}
