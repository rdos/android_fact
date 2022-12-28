package ru.smartro.worknote.ac

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.Inull

// TODO: add filtered field for table    clone(address= filter_field
abstract class BaseAdapter<T,D : RecyclerView.ViewHolder>(private var mItems: List<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mItemsBefore = mItems
    //var mItemsAfter
    abstract fun onGetLayout(): Int
    abstract fun onGetViewHolder(view: View): D

    private var lenQueryText = Inull
    private var mQueryText: String? = null
    private var lenQueryTextOld = Inull
    private var mQueryTextOld: String? = null

    fun getQueryTextOld(): String? {
        return mQueryText
    }
    fun setQueryText(text: String?) {
        mQueryTextOld = mQueryText
        lenQueryTextOld = Inull
        lenQueryText = Inull
        getQueryTextOld()?.let {
            lenQueryTextOld = it.length
        }
        mQueryText = text
        mQueryText?.let {
            lenQueryText = it.length
        }
    }

    fun getItemsForFilter(): List<T> {
        if (lenQueryText < lenQueryTextOld) {
            return mItemsBefore
        }
        return mItems
    }

    private fun getItemsBefore(): List<T>? {
        return mItemsBefore
    }



    fun getItems(): List<T> {
        return mItems
    }
    abstract fun bind(item: T, holder: D)

    fun reset() {
        this.mItems = mItemsBefore
        notifyDataSetChanged()
    }

    fun setItemsBefore(newItemS: List<T>){
        this.mItemsBefore = newItemS
    }
    fun setItems(newItemS: List<T>) {
        this.mItems = newItemS
    }

    fun set(items: List<T>) {
        setItems(items)
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

    final override fun getItemCount(): Int = mItems.count()

    fun position(position: Int): T {
        return mItems[position]
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bind(mItems[position], holder as D)
        bindWithPos(mItems[position], holder as D, position)
    }

    open fun bindWithPos(item: T, holder: D, position: Int) {

    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(onGetLayout(), parent, false)
        return onGetViewHolder(view)
    }


}

