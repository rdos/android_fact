package ru.smartro.worknote.ac.swipebtn

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View

class MovableView {
    private var mTargetView: View? = null

    private var mOnMoveStart: ((view: View) -> Unit)? = null
    private var mOnMoveHorizontally: ((view: View, absoluteX: Float) -> Unit)? = null
    private var mOnMoveEnd: ((view: View) -> Unit)? = null

    // TODO:: VT : TO BE CONTINUED)
//    private var mOnMoveLeft: ((view: View, absoluteX: Float) -> Unit)? = null
//    private var mOnMoveRight: ((view: View, absoluteX: Float) -> Unit)? = null
//    private var mOnMoveTop: ((view: View, absoluteX: Float) -> Unit)? = null
//    private var mOnMoveBottom: ((view: View, absoluteX: Float) -> Unit)? = null
//    private var mOnMoveVertically: ((view: View, absoluteX: Float) -> Unit)? = null

    private var mDelay: Long = 0

    private var mIsRuleLeftInitialized: Boolean = false
    private var mIsRuleTopInitialized: Boolean = false
    private var mIsRuleRightInitialized: Boolean = false
    private var mIsRuleBottomInitialized: Boolean = false

    private var mIsRuleHorizontalInitialized: Boolean = false
    private var mIsRuleVerticalInitialized: Boolean = false

    private var mIsReadyForMove = false

    private var mCurrentMotionEvent: MotionEvent? = null

    fun setTargetView(targetView: View): MovableView {
        mTargetView = targetView
        return this
    }

    fun setDelay(delay: Long): MovableView {
        mDelay = delay
        return this
    }

    fun setMovementRules(vararg rules: MovementRule): MovableView {
        if(rules.contains(MovementRule.LEFT)) {
            mIsRuleLeftInitialized = true
        }
        if(rules.contains(MovementRule.TOP)) {
            mIsRuleTopInitialized = true
        }
        if(rules.contains(MovementRule.BOTTOM)) {
            mIsRuleBottomInitialized = true
        }
        if(rules.contains(MovementRule.RIGHT)) {
            mIsRuleRightInitialized = true
        }
        if(rules.contains(MovementRule.HORIZONTAL)) {
            mIsRuleHorizontalInitialized = true
        }
        return this
    }

    fun onMoveStart(callback: (view: View) -> Unit): MovableView {
        mOnMoveStart = callback
        return this
    }

    fun onMoveHorizontally(callback: (view: View, absoluteX: Float) -> Unit): MovableView {
        mOnMoveHorizontally = callback
        return this
    }

    fun onMoveEnd(callback: (view: View) -> Unit): MovableView {
        mOnMoveEnd = callback
        return this
    }

    private fun startMove() {
        mIsReadyForMove = true
        mOnMoveStart?.invoke(getTargetView())
    }

    fun stopMove() {
        mIsReadyForMove = false
        getTargetView().isPressed = false
        mOnMoveEnd?.invoke(getTargetView())
    }

    private fun getTargetView(): View {
        if(mTargetView == null)
            throw Exception("Target View was not initialized")
        return mTargetView!!
    }

    @SuppressLint("ClickableViewAccessibility")
    fun apply() {

        var viewXStart: Int? = null
//            var viewXEnd: Int

        getTargetView().setOnTouchListener { _, event ->
            mCurrentMotionEvent = event
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    getTargetView().isPressed = true

                    val viewUtil = ViewUtil(getTargetView())
                    viewXStart = viewUtil.getXStart()
//                        viewXEnd = viewUtil.getXEnd()

                    Handler(Looper.getMainLooper()).postDelayed({
                        if(getTargetView().isPressed) {
                            this.startMove()
                        }
                    }, mDelay)

                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    if(mIsReadyForMove) {
                        val rawX = event.rawX
                        var dx: Float

                        if(mIsRuleHorizontalInitialized) {
                            dx = rawX - (viewXStart ?: 0)
                            getTargetView().translationX = dx
                            mOnMoveHorizontally?.invoke(getTargetView(), rawX)
                        }

                        // TODO:: VT ONLY MOVE LEFT
                        if(mIsRuleLeftInitialized) {
                            dx = rawX - (viewXStart ?: 0)

                            // TODO:: VT: PARAM FOR THRESHOLD
                            if (dx < 10f) {
                                getTargetView().translationX = dx

                                mOnMoveHorizontally?.invoke(getTargetView(), rawX)
                            }
                        }

                        // TODO:: VT ONLY MOVE RIGHT
                        if(mIsRuleRightInitialized) {
                            dx = rawX - (viewXStart ?: 0)

                            // TODO:: VT: PARAM FOR THRESHOLD
                            if (dx > 10f) {
                                getTargetView().translationX = dx

                                mOnMoveHorizontally?.invoke(getTargetView(), rawX)
                            }
                        }
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    getTargetView().isPressed = false
                    getTargetView().translationX = 0f
                    if(mIsReadyForMove) {
                        stopMove()
                        // TODO: должно быть так//  mOnStop.invoke(mTargetView!!)
                    }
                    false
                }

                else -> false
            }
        }
    }

    enum class MovementRule {
        LEFT, TOP, RIGHT, BOTTOM, HORIZONTAL, VERTICAL
    }
}