package ru.smartro.worknote.ui.workFlow.workorder


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_work_order.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.domain.models.WorkOrderModel

class WorkOrderRecyclerViewAdapter(
    private val onSelectListener: (WorkOrderModel) -> Unit,
    private val onDeselectListener: () -> Unit,
    private val onClickInfo: (WorkOrderModel) -> Unit,
    private val viewModel: WorkOrderViewModel
) : RecyclerView.Adapter<WorkOrderRecyclerViewAdapter.ViewHolder>() {

    var enabled = false

    var workOrders: List<WorkOrderModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var lastCheckBox: CheckBox? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_work_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = workOrders[position]
        holder.checkBox.text = item.srpId.toString()
        if (item.srpId == viewModel.lastSelected.value) {
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
            this.imageView2.setOnClickListener {
                onClickInfo(item)
            }
        }
    }

    override fun getItemCount(): Int = workOrders.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val checkBox: CheckBox = mView.checkBox
    }
}
