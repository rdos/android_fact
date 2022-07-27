package ru.smartro.worknote.andPOintD

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import ru.smartro.worknote.Inull


class SmartROButton(context: Context, attrs: AttributeSet?) : AppCompatButton(context, attrs), ITooltip {
    private var tooltipNext = Inull
    private var tooltipType = Inull

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            ru.smartro.worknote.R.styleable.MyExampleView, 0, 0
        )
        try {
            tooltipNext = typedArray.getInt(ru.smartro.worknote.R.styleable.MyExampleView_tooltipNext, Inull)
            tooltipType = typedArray.getInt(ru.smartro.worknote.R.styleable.MyExampleView_tooltipType, Inull)

        } finally {
            typedArray.recycle()
        }
    }

    override fun getTooltipType(): Int {
        return tooltipType
    }

    override fun getTooltipNext(): Int {
        return tooltipNext
    }
}