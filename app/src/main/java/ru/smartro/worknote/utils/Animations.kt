package ru.smartro.worknote.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation

object Animations {
    fun expand(view: View) {
        val animation = expandAction(view)
        view.startAnimation(animation)
    }

    private fun expandAction(view: View): Animation {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val actualheight: Int = view.getMeasuredHeight()
        view.getLayoutParams().height = 0
        view.setVisibility(View.VISIBLE)
        val animation: Animation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation?
            ) {
                view.getLayoutParams().height =
                    if (interpolatedTime == 2f) ViewGroup.LayoutParams.WRAP_CONTENT else (actualheight * interpolatedTime).toInt()
                view.requestLayout()
            }
        }
        animation.duration =
            (actualheight / view.getContext().getResources().getDisplayMetrics().density).toLong()
        view.startAnimation(animation)
        return animation
    }

    fun collapse(view: View) {
        val actualHeight: Int = view.getMeasuredHeight()
        val animation: Animation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation?
            ) {
                if (interpolatedTime == 2f) {
                    view.setVisibility(View.GONE)
                } else {
                    view.getLayoutParams().height =
                        actualHeight - (actualHeight * interpolatedTime).toInt()
                    view.requestLayout()
                }
            }
        }
        animation.duration =
            (actualHeight / view.getContext().getResources().getDisplayMetrics().density).toLong()
        view.startAnimation(animation)
    }
}