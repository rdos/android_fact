package ru.smartro.worknote.andPOintD

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T>(private var items: List<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val mItemsBefore = items
    //var mItemsAfter
    fun getItems(): List<T> {
        return mItemsBefore
    }
    abstract fun bind(item: T, holder: ViewHolder)

    fun reset() {
        this.items = mItemsBefore
        notifyDataSetChanged()
    }

    fun set(items: List<T>) {
        this.items = items
        notifyDataSetChanged()
    }

//    fun add(items: ArrayList<T>) {
//        this.items.addAll(items)
//        notifyDataSetChanged()
//    }

//    fun clear() {
//        this.items.clear()
//        notifyDataSetChanged()
//    }

    override fun getItemCount(): Int = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    fun position(position: Int): T {
        return items[position]
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bind(items[position], holder as ViewHolder)
    }

    class BASEViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}
class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

}
