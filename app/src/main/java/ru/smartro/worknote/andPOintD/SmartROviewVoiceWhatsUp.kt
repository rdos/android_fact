package ru.smartro.worknote.andPOintD

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R

class SmartROviewVoiceWhatsUp @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
): ConstraintLayout(context, attrs, defStyleAttrs) {

//    var currentState = VoiceCommentState.IDLE

    private var isLockReady: Boolean = false

    private var DRAG_EDGE_LOCK = 0f
    private var DRAG_EDGE_CANCEL = 0f

    private var messageInput: AppCompatEditText? = null
    private var recordInfo: LinearLayoutCompat? = null
    private var recordTime: AppCompatTextView? = null
    private var recordButton: AppCompatImageView? = null
    private var recordButtonWrapper: FrameLayout? = null
    private var buttonStop: AppCompatImageView? = null
    private var buttonCancel: AppCompatTextView? = null
    private var rlPathCancel: RelativeLayout? = null
    private var rlPathLock: RelativeLayout? = null

    var listener: CommentInputEvents? = null

    fun setTime(timeInMS: Long) {
        val minutes = timeInMS / 60_000
        val seconds = (timeInMS - (minutes * 60_000)) / 1000
        val minutesShowForUser = if(minutes < 10) "0${minutes}" else minutes.toString()
        val secondsShowForUser = if(seconds < 10) "0${seconds}" else seconds.toString()
        recordTime?.text = "${minutesShowForUser}:${secondsShowForUser}"
    }

    private fun initState() {
        recordButtonWrapper?.visibility = VISIBLE
        messageInput?.visibility = VISIBLE

        recordInfo?.visibility = INVISIBLE
        rlPathCancel?.visibility = INVISIBLE
        rlPathLock?.visibility = INVISIBLE

        buttonStop?.visibility = GONE
        buttonCancel?.visibility = GONE
    }

    init {
        inflate(getContext(), R.layout.custom_view__comment_input, this)

        rlPathCancel = findViewById(R.id.rl__comment_input__path_cancel)
        rlPathLock = findViewById(R.id.rl__comment_input__path_lock)

        messageInput = findViewById(R.id.acet__comment_input__message_input)
        recordInfo = findViewById(R.id.llc__comment_input__record_info)
        recordTime = findViewById(R.id.actv__comment_input__record_time)
        recordButton = findViewById(R.id.aciv__comment_input__rec_start)
        recordButtonWrapper = findViewById(R.id.cl__comment_input__record_button_wrapper)
        buttonStop = findViewById(R.id.aciv__comment_input__stop_button)
        buttonCancel = findViewById(R.id.actv__comment_input__cancel_button)

        initState()

        val movableView = MovableViewBuilder()
            .setTargetView(recordButton!!)
            .setMovementRules(MovableViewBuilder.MovementRule.LEFT)
            .setDelay(200)
            .onStart {
                LOG.debug("MOVABLE ON START")
                recordButton?.animate()?.scaleX(1.6f)?.scaleY(1.6f)?.setDuration(200)?.start()
            }
            .onMoveHorizontally { view, absoluteX ->
                LOG.debug("MOVABLE ON MOVE HORIZONTALLY: ${absoluteX}")
            }
            .onStop {
                LOG.debug("MOVABLE ON STOP")
                recordButton?.animate()?.scaleX(1f)?.scaleY(1f)?.start()
            }
            .build()
    }

    interface CommentInputEvents {
        fun onStart()
        fun onStop()
        fun onCancel()
        fun onLock()
    }

//    override fun onTouch(v: View?, event: MotionEvent): Boolean {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                // Corners of button has the same size
//                val recordButtonSize = recordButton?.width ?: 0
//                val location = IntArray(2)
//                recordButton?.getLocationOnScreen(location)
//                recordButtonX1 = location[0].toFloat()
//                recordButtonX2 = recordButtonX1 + recordButtonSize
//                recordButtonY1 = location[1].toFloat()
//                recordButtonY2 = recordButtonY1 + recordButtonSize
//
//                rlPathCancel?.visibility = View.VISIBLE
//                rlPathLock?.visibility = View.VISIBLE
//
//                recordButton?.translationY = 0f
//                recordButton?.apply {
//                    translationX = 0f
//                    animate().scaleX(1.8f).scaleY(1.8f).setDuration(200).start()
//                }
//
//                vibrator?.vibrate(vibrationEffect)
//
//                recordInfo?.visibility = VISIBLE
//                messageInput?.visibility = INVISIBLE
//
//                rlPathCancel?.getLocationOnScreen(location)
//                DRAG_EDGE_CANCEL = location[0].toFloat()
//
//                rlPathLock?.getLocationOnScreen(location)
//                DRAG_EDGE_LOCK = location[0].toFloat()
//
//                currentState = VoiceCommentState.RECORDING
//                listener?.onStart()
//
//                return true
//            }
//
//            MotionEvent.ACTION_MOVE -> {
//                if (currentState == VoiceCommentState.RECORDING) {
//
//                    val rawX = event.rawX
//                    val rawY = event.rawY
//
//                    if (movement == MovementEnum.NONE) {
//                        if (rawY > recordButtonY1 && rawY < recordButtonY2 && rawX < recordButtonX1) {
//                            recordButton?.translationX = 0f
//                            movement = MovementEnum.HORIZONTAL
//                        }
//                    }
//
//                    if (movement == MovementEnum.HORIZONTAL) {
//                        dx = rawX - recordButtonX1
//
//                        if (dx < 10f) {
//                            recordButton?.translationX = dx
//                        }
//
//                        if ((rawX > (DRAG_EDGE_LOCK - 30f) && rawX <= DRAG_EDGE_LOCK) && !isLockReady) {
//                            isLockReady = true
//                            vibrator!!.vibrate(vibrationEffect)
//                        }
//
//                        if((rawX < (DRAG_EDGE_LOCK - 60f) || rawX > (DRAG_EDGE_LOCK + 30f)) && isLockReady) {
//                            isLockReady = false
//                        }
//
//                        if (rawX < DRAG_EDGE_CANCEL) {
//                            currentState = VoiceCommentState.CANCEL
//
//                            vibrator!!.vibrate(vibrationEffect)
//
//                            val cancelEvent = MotionEvent.obtain(event)
//                            cancelEvent.action = MotionEvent.ACTION_UP
//                            recordButton?.dispatchTouchEvent(cancelEvent)
//
//                            return false
//                        }
//
//                        if (rawX > recordButtonX1 && rawX < recordButtonX2 && rawY < recordButtonY1) {
//                            movement = MovementEnum.NONE
//                        }
//
//                        return false
//                    }
//                }
//                return false
//            }
//
//            MotionEvent.ACTION_UP -> {
//                if(isLockReady) {
//                    listener?.onLock()
//                    currentState = VoiceCommentState.LOCK
//
//                    recordButtonWrapper?.visibility = INVISIBLE
//                    buttonStop?.visibility = VISIBLE
//                    buttonCancel?.visibility = VISIBLE
//                }
//
//                recordButton?.apply {
//                    animate()
//                        .setDuration(200)
//                        .scaleX(1f).scaleY(1f)
//                        .translationX(0f).translationY(0f)
//                        .start()
//                }
//
//                movement = MovementEnum.NONE
//
//                rlPathCancel?.visibility = View.INVISIBLE
//                rlPathLock?.visibility = View.INVISIBLE
//
//                if(currentState == VoiceCommentState.CANCEL) {
//                    // animate trashcan
//                    // and then ->
//                    listener?.onCancel()
//                    currentState = VoiceCommentState.IDLE
//
//                    initState()
//                    return false
//                }
//
//                if(currentState == VoiceCommentState.RECORDING) {
//                    listener?.onStop()
//                    currentState = VoiceCommentState.IDLE
//
//                    initState()
//                }
//
//                return false
//            }
//        }
//        return false
//    }
}

class MovableView(
    val targetView: View,
    private var mOnStop: ((view: View) -> Unit)? = null
) {
    fun stopMovement() {
        mOnStop?.invoke(targetView)
    }
}

class MovableViewBuilder {

    private var mTargetView: View? = null

    private var mOnStart: ((view: View) -> Unit)? = null
    private var mOnMoveLeft: ((view: View, absoluteX: Float) -> Unit)? = null
    private var mOnMoveRight: ((view: View, absoluteX: Float) -> Unit)? = null
    private var mOnMoveHorizontally: ((view: View, absoluteX: Float) -> Unit)? = null
    private var mOnMoveTop: ((view: View, absoluteX: Float) -> Unit)? = null
    private var mOnMoveBottom: ((view: View, absoluteX: Float) -> Unit)? = null
    private var mOnMoveVertically: ((view: View, absoluteX: Float) -> Unit)? = null
    private var mOnStop: ((view: View) -> Unit)? = null

    private var mDelay: Long? = null

    private var mIsRuleLeftInitialized: Boolean = false
    private var mIsRuleTopInitialized: Boolean = false
    private var mIsRuleRightInitialized: Boolean = false
    private var mIsRuleBottomInitialized: Boolean = false

    private val mVerticalEdges = mutableListOf<EdgeSetting>()

    private var mIsReadyForMove = false

    fun setTargetView(targetView: View): MovableViewBuilder {
        mTargetView = targetView
        return this
    }

    fun setDelay(delay: Long): MovableViewBuilder {
        mDelay = delay
        return this
    }

    fun setMovementRules(vararg rules: MovementRule): MovableViewBuilder {
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
        return this
    }

    fun onStart(callback: (view: View) -> Unit): MovableViewBuilder {
        mOnStart = callback
        return this
    }

    fun onMoveHorizontally(callback: (view: View, absoluteX: Float) -> Unit): MovableViewBuilder {
        mOnMoveHorizontally = callback
        return this
    }

    fun onStop(callback: (view: View) -> Unit): MovableViewBuilder {
        mOnStop = callback
        return this
    }

    private fun start() {
        mIsReadyForMove = true
        mOnStart?.invoke(mTargetView!!)
    }

    private fun stop() {
        mIsReadyForMove = false
        mTargetView?.animate()?.translationX(0f)?.translationY(0f)?.start()
        mOnStop?.invoke(mTargetView!!)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun build(): MovableView {
        if(mTargetView == null)
            throw Exception("Target View was not initialized")

        var viewXStart: Int? = null
//            var viewXEnd: Int

        mTargetView!!.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mTargetView?.isPressed = true
                    var viewUtil = ViewUtil(mTargetView!!)
                    viewXStart = viewUtil.getXStart()
//                        viewXEnd = viewUtil.getXEnd()

                    if(mDelay != null) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            if(mTargetView?.isPressed == true) {
                                start()
                            }
                        }, mDelay!!)
                    } else {
                        start()
                    }

                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    if(mIsReadyForMove) {
                        val rawX = event.rawX
                        var dx: Float

                        if(mIsRuleLeftInitialized) {
                            dx = rawX - (viewXStart ?: 0)

                            if (dx < 10f) {
                                mTargetView?.translationX = dx
                                mOnMoveHorizontally?.invoke(mTargetView!!, rawX)
                            }
                        }
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    mTargetView?.isPressed = false
                    if(mIsReadyForMove) {
                        stop()
                    }
                    false
                }

                else -> false
            }
        }

        return MovableView(mTargetView!!, mOnStop)
    }

    data class EdgeSetting (
        val edge: Int,
        val isDeadly: Boolean = false,
        val expandTo: Int? = null
    )

    enum class MovementRule {
        LEFT, TOP, RIGHT, BOTTOM
    }
}

class ViewUtil(val targetView: View) {
    private var mLocation: IntArray? = null

    private fun getLocationOnScreen(): IntArray {
        if(mLocation != null) {
            return mLocation!!
        }
        mLocation = IntArray(2)
        targetView.getLocationOnScreen(mLocation)
        return mLocation!!
    }

    fun getXStart(): Int {
        return getLocationOnScreen()[0]
    }

    fun getXEnd(): Int {
        return getXStart() + targetView.width
    }
}