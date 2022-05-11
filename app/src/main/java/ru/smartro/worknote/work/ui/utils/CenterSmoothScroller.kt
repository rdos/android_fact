package ru.smartro.worknote.work.ui.utils

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller

class CenterSmoothScroller constructor(context: Context?) : LinearSmoothScroller(context) {
    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START
    }

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
        return 120f / (displayMetrics?.densityDpi ?: 160)
    }
}