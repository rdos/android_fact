package ru.smartro.worknote.ui.workFlow.onTheRoute

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_route_platform.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.utils.Animations


class RoutePlatformRecyclerViewAdapter(private val model: RoutePlatformShowViewModel) :
    RecyclerView.Adapter<RoutePlatformRecyclerViewAdapter.ViewHolder>() {

    val lastExpended: MutableLiveData<LinearLayout?> = MutableLiveData(null)

    var platforms: List<PlatformToShow> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_route_platform, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = platforms[position]
        holder.number.text = item.name
        holder.address.text = item.address
        holder.count.text = item.containersCount.toString()
        if (position == model.lastExpendedPosition.value) {
            lastExpended.value = holder.view.layout_expand
            holder.view.layout_expand.visibility = View.VISIBLE
        } else {
            holder.view.layout_expand.visibility = View.GONE
        }
        holder.view.setOnClickListener {
            if (model.lastExpendedPosition.value == position) {
                lastExpended.value = null
                model.lastExpendedPosition.value = null
                toggleLayout(false, holder.view.layout_expand)
            } else {
                lastExpended.value?.let {
                    toggleLayout(false, it)
                }
                lastExpended.value = holder.view.layout_expand
                model.lastExpendedPosition.value = position
                lastExpended.value?.let {
                    toggleLayout(true, it)
                }
            }
        }

    }

    private fun toggleLayout(
        isExpanded: Boolean,
        // v: View,
        layoutExpand: LinearLayout
    ): Boolean {
        if (isExpanded) {
            Animations.expand(layoutExpand)
        } else {
            Animations.collapse(layoutExpand)
        }
        return isExpanded
    }

    override fun getItemCount(): Int = platforms.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val number: TextView = mView.item_number
        val address: TextView = mView.address
        val count: TextView = mView.cont_count

        //val collapse: LinearLayout = mView.layout_expand
        val view: View = mView
    }
}
