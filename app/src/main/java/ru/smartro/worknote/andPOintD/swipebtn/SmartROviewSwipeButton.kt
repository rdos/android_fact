package ru.smartro.worknote.andPOintD.swipebtn

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.getColorOrThrow
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R

//todo: ::: https://github.com/ebanx/swipe-button.git
class SmartROviewSwipeButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
): ConstraintLayout(context, attrs, defStyleAttrs) {
//?1)
    private var rlBackground: View? = null
    private var vLayer: View? = null
    private var acibDraggableButton: AppCompatImageButton? = null
    private var actvInnerText: AppCompatTextView? = null

    private val rlBackgroundReachPoint: Int by lazy { ViewUtil(rlBackground!!).getXEnd() - (acibDraggableButtonWidth / 2.5f).toInt()  }
    private val acibDraggableButtonWidth: Int by lazy { acibDraggableButton?.width ?: 0 }

    private var movableView: MovableView? = null

        //todo: мне кажется
    private var mIsLockReady = false

    var onSwipe: (() -> Unit)? = null

    init {
        inflate(getContext(), R.layout.sview_swipe_button, this)

        rlBackground = findViewById(R.id.v__sview_swipe_button__background)
        acibDraggableButton = findViewById(R.id.aciv__sview_swipe_button__draggable)
        actvInnerText = findViewById(R.id.actv__sview_swipe_button__inner_text)
        vLayer = findViewById(R.id.v__sview_swipe_button__layer)
        vLayer?.pivotX = 0f

        if(attrs != null) {
            val parsedAttributes = context.obtainStyledAttributes(attrs, R.styleable.sViewSwipeButton, defStyleAttrs, 0)


            val buttonSrc = parsedAttributes.getDrawable(R.styleable.sViewSwipeButton_buttonSrc)
            if (buttonSrc != null)
                acibDraggableButton?.background = buttonSrc

            val buttonIcon = parsedAttributes.getDrawable(R.styleable.sViewSwipeButton_buttonIcon)
            if (buttonIcon != null)
                acibDraggableButton?.setImageDrawable(buttonIcon)

            val innerTextResource = parsedAttributes.getString(R.styleable.sViewSwipeButton_innerText)
            if (innerTextResource != null) {
                actvInnerText?.text = innerTextResource
            } else {
                val innerTextPlain = parsedAttributes.getNonResourceString(R.styleable.sViewSwipeButton_innerText)
                if(innerTextPlain != null)
                    actvInnerText?.text = innerTextPlain
            }

            parsedAttributes.recycle()
        }


        initialState()

        movableView = MovableView()
            .setTargetView(acibDraggableButton!!)
            .setMovementRules(MovableView.MovementRule.HORIZONTAL)
            .onMoveHorizontally { view, absoluteX ->

                if(absoluteX > acibDraggableButtonWidth)
                    vLayer?.scaleX = absoluteX / acibDraggableButtonWidth
                else
                    vLayer?.scaleX = -(acibDraggableButtonWidth / absoluteX)

                if(!mIsLockReady && absoluteX > (rlBackgroundReachPoint - 50f)) {
                    LOG.debug("LOCK")
                    mIsLockReady = true
                    App.getAppliCation().startVibrateServiceHaptic()
                    movableView?.stopMove()
                }

                if(mIsLockReady && absoluteX < (rlBackgroundReachPoint - 50f)) {
                    LOG.debug("UNLOCK")
                    mIsLockReady = false
                }
            }
            .onMoveEnd {
                LOG.debug("MOVABLE ON STOP")
                if(mIsLockReady) {
                    onSwipe?.invoke()
                }
                initialState()
            }

        movableView?.apply()
    }

    private fun initialState() {
        mIsLockReady = false
        vLayer?.scaleX = 0f
        acibDraggableButton?.translationX = 0f
    }

}