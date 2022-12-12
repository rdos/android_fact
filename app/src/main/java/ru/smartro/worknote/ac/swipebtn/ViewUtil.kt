package ru.smartro.worknote.ac.swipebtn

import android.view.View

class ViewUtil(val targetView: View) {
    private var mLocationOnScreen: IntArray? = null

    private fun getLocationOnScreen(): IntArray {
        if(mLocationOnScreen != null) {
            return mLocationOnScreen!!
        }
        mLocationOnScreen = IntArray(2)
        targetView.getLocationOnScreen(mLocationOnScreen)
        return mLocationOnScreen!!
    }

    fun getXStart(): Int {
        return getLocationOnScreen()[0]
    }

    fun getXEnd(): Int {
        return getXStart() + targetView.width
    }

    fun getYStart(): Int {
        return getLocationOnScreen()[1]
    }

    fun getYEnd(): Int {
        return getYStart() + targetView.height
    }
}