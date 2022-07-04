package ru.smartro.worknote.andPOintD

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.Inull
import ru.smartro.worknote.Snull

abstract class BaseAdapter<T,D : RecyclerView.ViewHolder>(private var mItems: List<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mItemsBefore = mItems
    //var mItemsAfter
    abstract fun onGetLayout(): Int
    abstract fun onGetViewHolder(view: View): D

    private var lenQueryText = Inull
    private var mQueryText: String? = Snull
    private var lenQueryTextOld = Inull
    private var mQueryTextOld: String? = Snull

    fun getQueryTextOld(): String? {
        return mQueryText
    }
    fun setQueryText(text: String?) {
        mQueryTextOld = mQueryText
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

    private fun getItemsBefore(): List<T> {
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
    }
    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(onGetLayout(), parent, false)
        return onGetViewHolder(view)
    }


}

