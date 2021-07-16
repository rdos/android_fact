package ru.smartro.worknote.adapter

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ru.smartro.worknote.base.BaseFragment


class ViewPagerAdapter(fragmentManager: FragmentManager?, myFrags: List<BaseFragment>) : FragmentPagerAdapter(fragmentManager!!) {
    private val fragments: List<BaseFragment> = myFrags

    override fun getItem(position: Int): BaseFragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}