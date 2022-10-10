package ru.smartro.worknote.andPOintD

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import kotlin.math.min

class SmartROviewVoiceWhatsUp @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
): ConstraintLayout(context, attrs, defStyleAttrs) {

    private var lavMicrophone: LottieAnimationView? = null
    private var acetMessageInput: AppCompatEditText? = null
    private var llcRecordInfo: LinearLayoutCompat? = null
    private var actvRecordTime: AppCompatTextView? = null
    private var acivRecordButton: AppCompatImageView? = null
    private var flRecordButtonWrapper: FrameLayout? = null
    private var acivButtonStop: AppCompatImageView? = null
    private var actvButtonCancel: AppCompatTextView? = null
    private var rlPathCancel: RelativeLayout? = null
    private var rlPathLock: RelativeLayout? = null

    private var movableView: MovableView? = null

    private val rlPathCancelX: Int by lazy { ViewUtil(rlPathCancel!!).getXStart() }
    private val rlPathLockX: Int by lazy { ViewUtil(rlPathLock!!).getXStart() }


        //todo: мне кажется
    private var mIsLockReady = false
    private var mIsStop = false

    var mOnStart: (() -> Unit)? = null
    var mOnLock: (() -> Unit)? = null
    var mOnEnd: (() -> Unit)? = null
    var mOnStop: (() -> Unit)? = null

    init {
        inflate(getContext(), R.layout.custom_view__comment_input, this)

        lavMicrophone = findViewById<LottieAnimationView>(R.id.lav__comment_input__recording_animated_icon)
        rlPathCancel = findViewById(R.id.rl__comment_input__path_cancel)
        rlPathLock = findViewById(R.id.rl__comment_input__path_lock)
        acetMessageInput = findViewById(R.id.acet__comment_input__message_input)
        llcRecordInfo = findViewById(R.id.llc__comment_input__record_info)
        actvRecordTime = findViewById(R.id.actv__comment_input__record_time)
        acivRecordButton = findViewById(R.id.aciv__comment_input__rec_start)
        flRecordButtonWrapper = findViewById(R.id.cl__comment_input__record_button_wrapper)
        acivButtonStop = findViewById(R.id.aciv__comment_input__stop_button)
        actvButtonCancel = findViewById(R.id.actv__comment_input__cancel_button)

        initialState()

        acivButtonStop?.setOnClickListener {
            mOnEnd?.invoke()
        }

        actvButtonCancel?.setOnClickListener {
            mOnStop?.invoke()
        }

        movableView = MovableViewBuilder()
            .setTargetView(acivRecordButton!!)
            .setMovementRules(MovableViewBuilder.MovementRule.LEFT)
            .setDelay(200)
            .onStart {
                LOG.debug("MOVABLE ON START")
                mOnStart?.invoke()
                acivRecordButton!!.animate()?.scaleX(1.6f)?.scaleY(1.6f)?.setDuration(200)?.start()
            }
            .onMoveHorizontally { view, absoluteX ->
                LOG.debug("MOVABLE ON MOVE HORIZONTALLY: ${absoluteX}")
                if(!mIsLockReady && absoluteX > (rlPathLockX - 30f) && absoluteX < (rlPathLockX + 30f)) {
                    LOG.debug("LOCK")
                    mIsLockReady = true
                }

                if(mIsLockReady && absoluteX < (rlPathLockX - 50f) || absoluteX > (rlPathLockX + 50f)) {
                    LOG.debug("UNLOCK")
                    mIsLockReady = false
                }

                if(absoluteX < rlPathCancelX) {
                    LOG.debug("stop")
                    mIsStop = true

                    movableView?.stopMovement()
                }
            }
            .onStop {
                LOG.debug("MOVABLE ON STOP")
                acivRecordButton?.animate()?.translationX(0f)?.translationY(0f)?.start()
                acivRecordButton?.animate()?.scaleX(1f)?.scaleY(1f)?.setDuration(200)?.start()
                if(mIsLockReady) {
                    lockState()
                    mOnLock?.invoke()
                    return@onStop
                }
                if(mIsStop) {
                    initialState()
                    mOnStop?.invoke()
                    return@onStop
                }
                initialState()
                mOnEnd?.invoke()
            }

            .build()
    }

    fun setTime(timeInMS: Long) {
        val minutes = timeInMS / 60_000
        val seconds = (timeInMS - (minutes * 60_000)) / 1000
        val minutesShowForUser = if(minutes < 10) "0${minutes}" else minutes.toString()
        val secondsShowForUser = if(seconds < 10) "0${seconds}" else seconds.toString()
        actvRecordTime?.text = "${minutesShowForUser}:${secondsShowForUser}"
    }

    fun stop() {
//        movableView?.stopMovement()
        initialState()
    }


    private fun initialState() {
        mIsStop = false
        mIsLockReady = false
        setTime(0)
        flRecordButtonWrapper?.visibility = VISIBLE
        acetMessageInput?.visibility = VISIBLE

        llcRecordInfo?.visibility = INVISIBLE
        rlPathCancel?.visibility = INVISIBLE
        rlPathLock?.visibility = INVISIBLE

        acivButtonStop?.visibility = GONE
        actvButtonCancel?.visibility = GONE
    }

    private fun recordingState() {
        flRecordButtonWrapper?.visibility = VISIBLE
        acetMessageInput?.visibility = INVISIBLE

        llcRecordInfo?.visibility = VISIBLE
        rlPathCancel?.visibility = VISIBLE
        rlPathLock?.visibility = VISIBLE

        acivButtonStop?.visibility = GONE
        actvButtonCancel?.visibility = GONE
    }

    private fun lockState() {
        mIsLockReady = false
        flRecordButtonWrapper?.visibility = INVISIBLE
        acetMessageInput?.visibility = INVISIBLE

        llcRecordInfo?.visibility = VISIBLE
        rlPathCancel?.visibility = INVISIBLE
        rlPathLock?.visibility = INVISIBLE

        acivButtonStop?.visibility = VISIBLE
        actvButtonCancel?.visibility = VISIBLE
    }

    fun start() {
        recordingState()
    }
    private val MAX_RECORD_AMPLITUDE = 32768.0
    private val interpolator = OvershootInterpolator()
    fun setVolumeEffect(volume: Int, interValInMS: Long) {

        val scale = min(11.0, volume / MAX_RECORD_AMPLITUDE + 1.0).toFloat()
        LOG.debug("Scale = $scale")
            lavMicrophone!!.animate()
                    .scaleX(scale)
                    .scaleY(scale)
                    .setInterpolator(interpolator)
                    .duration = interValInMS
    }
}

class MovableView(
    val targetView: View,
    private var onStop: (() -> Unit)
) {
    fun stopMovement() {
        onStop()
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

    private var mDelay: Long = 0

    private var mIsRuleLeftInitialized: Boolean = false
    private var mIsRuleTopInitialized: Boolean = false
    private var mIsRuleRightInitialized: Boolean = false
    private var mIsRuleBottomInitialized: Boolean = false

    private var mIsReadyForMove = false

    private var mCurrentMotionEvent: MotionEvent? = null

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
        mOnStart?.invoke(getTargetView())
    }

    private fun stop() {
        mIsReadyForMove = false
        getTargetView().isPressed = false
        mOnStop?.invoke(getTargetView())
    }

    private fun getTargetView(): View {
        if(mTargetView == null)
            throw Exception("Target View was not initialized")
        return mTargetView!!
    }

    @SuppressLint("ClickableViewAccessibility")
    fun build(): MovableView {

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
                            this.start()
                        }
                    }, mDelay)

                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    if(mIsReadyForMove) {
                        val rawX = event.rawX
                        var dx: Float

                        if(mIsRuleLeftInitialized) {
                            dx = rawX - (viewXStart ?: 0)

                            if (dx < 10f) {
                                getTargetView().translationX = dx

                                mOnMoveHorizontally?.invoke(getTargetView(), rawX)
                            }
                        }
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    getTargetView().isPressed = false
                    if(mIsReadyForMove) {
                        stop()
                        // TODO: должно быть так//                        mOnStop.invoke(mTargetView!!)
                    }
                    false
                }

                else -> false
            }
        }

        return MovableView(getTargetView(), ::stop)
    }

    enum class MovementRule {
        LEFT, TOP, RIGHT, BOTTOM
    }
}

class ViewUtil(val targetView: View) {
    private var mLocationOnScreen: IntArray? = null

    private fun getLocationOnScreen(): IntArray {
        if(mLocationOnScreen != null) {
            return mLocationOnScreen!!
        }
        mLocationOnScreen = IntArray(2)
        targetView.getLocationOnScreen(mLocationOnScreen)
        return mLocationOnScreen!!
    }

    fun getXStart(): Int {
        return getLocationOnScreen()[0]
    }

    fun getXEnd(): Int {
        return getXStart() + targetView.width
    }
}