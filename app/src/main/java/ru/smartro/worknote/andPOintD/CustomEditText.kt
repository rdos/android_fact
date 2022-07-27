package ru.smartro.worknote.andPOintD

import android.content.Context
import android.widget.RelativeLayout
import android.content.res.TypedArray
import android.util.AttributeSet
import ru.smartro.worknote.R

class CustomEditText(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private var panCount = 0

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MyExampleView, 0, 0
        )
        try {
            panCount = typedArray.getInt(R.styleable.MyExampleView_tooltipNext, 16)
        } finally {
            typedArray.recycle()
        }
    }
}