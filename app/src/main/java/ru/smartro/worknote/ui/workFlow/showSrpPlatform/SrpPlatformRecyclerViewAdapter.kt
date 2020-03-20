package ru.smartro.worknote.ui.workFlow.showSrpPlatform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_srp_platform_show.view.*
import ru.smartro.worknote.R

class SrpPlatformRecyclerViewAdapter :
    RecyclerView.Adapter<SrpPlatformRecyclerViewAdapter.ViewHolder>() {

    var platforms: List<PlatformToShow> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_srp_platform_show, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = platforms[position]
        holder.number.text = item.name
        holder.address.text = item.address
        holder.count.text = item.containersCount.toString()
    }

    override fun getItemCount(): Int = platforms.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val number: TextView = mView.item_number
        val address: TextView = mView.address
        val count: TextView = mView.cont_count
    }
}
