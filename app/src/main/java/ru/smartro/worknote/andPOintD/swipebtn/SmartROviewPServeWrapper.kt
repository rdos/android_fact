package ru.smartro.worknote.andPOintD.swipebtn

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.SmartROllc

//todo: ::: https://github.com/ebanx/swipe-button.git
class SmartROviewPServeWrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : SmartROllc(context, attrs, defStyleAttrs) {

    private var parent: ConstraintLayout? = null

    init {
        inflate(getContext(), R.layout.sview_pserve_wrapper, this)

        parent = findViewById(R.id.cl__sview_pserve_wrapper__parent)

        parent.putChildren()


    }

    fun toggleColors(flag: Boolean) {

    }
}