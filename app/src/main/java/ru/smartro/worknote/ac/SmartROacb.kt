package ru.smartro.worknote.ac

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import ru.smartro.worknote.Inull
import ru.smartro.worknote.R


class SmartROacb(context: Context, attrs: AttributeSet?) : AppCompatButton(context, attrs), ITooltip {
    private var tooltipNext: String? = null
    private var tooltipType = Inull

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MyExampleView, 0, 0
        )
        try {
            tooltipNext = typedArray.getString(R.styleable.MyExampleView_tooltipNext)
            tooltipType = typedArray.getInt(R.styleable.MyExampleView_tooltipType, Inull)
        } finally {
            typedArray.recycle()
        }
    }

    override fun getTooltipType(): Int {
        return tooltipType
    }

    override fun getTooltipNext(): String? {
        return tooltipNext
    }

    override fun getIdText(): String {
        return if (this.id == View.NO_ID) "no-id" else this.resources.getResourceEntryName(this.id)
    }
}