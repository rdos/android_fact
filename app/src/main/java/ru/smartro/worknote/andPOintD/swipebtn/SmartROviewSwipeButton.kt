package ru.smartro.worknote.andPOintD.swipebtn

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R

//todo: ::: https://github.com/ebanx/swipe-button.git
class SmartROviewSwipeButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
): ConstraintLayout(context, attrs, defStyleAttrs) {

    private var rlBackground: RelativeLayout? = null
    private var acibDraggableButton: AppCompatImageButton? = null
    private var vLayer: View? = null

    private val rlBackgroundReachPoint: Int by lazy { ViewUtil(rlBackground!!).getXEnd() }
    private val acibDraggableButtonWidth: Int by lazy { acibDraggableButton?.width ?: 0 }

    private var movableView: MovableView? = null

        //todo: мне кажется
    private var mIsLockReady = false

    var mOnReachEnd: (() -> Unit)? = null

    init {
        inflate(getContext(), R.layout.sview_swipe_button, this)

        rlBackground = findViewById(R.id.rl__sview_swipe_button__background)
        acibDraggableButton = findViewById(R.id.aciv__sview_swipe_button__draggable)
        vLayer = findViewById(R.id.v__sview_swipe_button__layer)

        initialState()

        vLayer?.pivotX = 0f

        movableView = MovableView()
            .setTargetView(acibDraggableButton!!)
            .setMovementRules(MovableView.MovementRule.HORIZONTAL)
            .onMoveHorizontally { view, absoluteX ->
                LOG.debug("MOVABLE ON MOVE HORIZONTALLY: ${absoluteX}")

                vLayer?.scaleX = absoluteX / acibDraggableButtonWidth

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
                    mOnReachEnd?.invoke()
                } else {
                    initialState()
                }
            }

        movableView?.apply()
    }

    private fun initialState() {
        mIsLockReady = false
        vLayer?.scaleX = 0f
    }

}