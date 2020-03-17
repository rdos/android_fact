package ru.smartro.worknote.ui.workFlow.waybillHead


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_waybill.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.domain.models.WaybillHeadModel

class WaybillRecyclerViewAdapter(
    private val onSelectListener: (WaybillHeadModel) -> Unit,
    private val onDeselectListener: () -> Unit,
    private val viewModel: WaybillHeadViewModel
) : RecyclerView.Adapter<WaybillRecyclerViewAdapter.ViewHolder>() {

    var enabled = false

    var waybillHeadModels: List<WaybillHeadModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var lastCheckBox: CheckBox? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_waybill, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = waybillHeadModels[position]
        holder.checkBox.text = item.number
        if (item.id == viewModel.lastSelected.value) {
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

    override fun getItemCount(): Int = waybillHeadModels.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val checkBox: CheckBox = mView.checkBox
    }
}
