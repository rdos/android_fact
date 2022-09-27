package ru.smartro.worknote.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.airbnb.lottie.LottieAnimationView
import com.yandex.runtime.i18n.TimeFormat
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import java.time.format.DateTimeFormatter

@SuppressLint("ClickableViewAccessibility")
class CommentInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
): ConstraintLayout(context, attrs, defStyleAttrs) {

    private var isLockReady: Boolean = false
        set(value) {
            LOG.debug("IS LOCK SET ${value}")
        }

    private var vibrator: Vibrator? = null
    private var vibrationEffect = VibrationEffect.createOneShot(100, 128)

    private var DRAG_EDGE_LOCK = 0f
    private var DRAG_EDGE_CANCEL = 0f

    private var messageInput: AppCompatEditText? = null
    private var recordInfo: LinearLayoutCompat? = null
    private var recordTime: AppCompatTextView? = null
    private var recordButton: AppCompatImageView? = null
    private var recordButtonWrapper: CoordinatorLayout? = null
    private var buttonStop: AppCompatImageView? = null
    private var buttonCancel: AppCompatTextView? = null

    private var rlPathCancel: RelativeLayout? = null
    private var rlPathLock: RelativeLayout? = null

    private var movement = MovementEnum.NONE
    private var currentState = VoiceCommentState.IDLE

    private var recordButtonX1 = 0f
    private var recordButtonY1 = 0f
    private var recordButtonX2 = 0f
    private var recordButtonY2 = 0f


    private var dx = 0f
    private var dy = 0f

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
        recordInfo?.visibility = INVISIBLE
        messageInput?.visibility = VISIBLE
        buttonStop?.visibility = GONE
        buttonCancel?.visibility = GONE
        rlPathCancel?.visibility = View.INVISIBLE
        rlPathLock?.visibility = View.INVISIBLE
    }

    init {
        inflate(getContext(), R.layout.custom_view__comment_input, this)

        vibrator = getContext().applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        rlPathCancel = findViewById(R.id.rl__comment_input__path_cancel)
        rlPathLock = findViewById(R.id.rl__comment_input__path_lock)

        messageInput = findViewById(R.id.acet__comment_input__message_input)
        recordInfo = findViewById(R.id.llc__comment_input__record_info)
        recordTime = findViewById(R.id.actv__comment_input__record_time)
        recordButton = findViewById(R.id.aciv__comment_input__record_button)
        recordButtonWrapper = findViewById(R.id.cl__comment_input__record_button_wrapper)
        buttonStop = findViewById(R.id.aciv__comment_input__stop_button)
        buttonCancel = findViewById(R.id.actv__comment_input__cancel_button)

        initState()

        buttonStop?.setOnClickListener {
            listener?.onStop()
            vibrator!!.vibrate(vibrationEffect)
            initState()
        }

        buttonCancel?.setOnClickListener {
            listener?.onCancel()
            vibrator!!.vibrate(vibrationEffect)
            initState()
        }

        recordButton?.setOnTouchListener { _: View?, event: MotionEvent ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    // Corners of button has the same size
                    val recordButtonSize = recordButton?.width ?: 0
                    val location = IntArray(2)
                    recordButton?.getLocationOnScreen(location)
                    recordButtonX1 = location[0].toFloat()
                    recordButtonX2 = recordButtonX1 + recordButtonSize
                    recordButtonY1 = location[1].toFloat()
                    recordButtonY2 = recordButtonY1 + recordButtonSize

                    rlPathCancel?.visibility = View.VISIBLE
                    rlPathLock?.visibility = View.VISIBLE

                    recordButton?.translationY = 0f
                    recordButton?.apply {
                        translationX = 0f
                        animate().scaleX(1.8f).scaleY(1.8f).setDuration(200).start()
                    }

                    vibrator?.vibrate(vibrationEffect)

                    recordInfo?.visibility = VISIBLE
                    messageInput?.visibility = INVISIBLE

                    rlPathCancel?.getLocationOnScreen(location)
                    DRAG_EDGE_CANCEL = location[0].toFloat()

                    rlPathLock?.getLocationOnScreen(location)
                    DRAG_EDGE_LOCK = location[0].toFloat()

                    currentState = VoiceCommentState.RECORDING

                    // TODO::: DELAY?
                    listener?.onStart()

                    return@setOnTouchListener true
                }
                
                MotionEvent.ACTION_MOVE -> {
                    if (currentState == VoiceCommentState.RECORDING) {
                        
                        val rawX = event.rawX
                        val rawY = event.rawY

                        LOG.debug("RAW X: ${rawX}, DRAG_CANCEL: ${DRAG_EDGE_CANCEL}, DRAG_LOCK: ${DRAG_EDGE_LOCK}")

                        if (movement == MovementEnum.NONE) {
                            if (rawY > recordButtonY1 && rawY < recordButtonY2 && rawX < recordButtonX1) {
                                recordButton?.translationX = 0f
                                movement = MovementEnum.HORIZONTAL
                            }
                        }
                        
                        if (movement == MovementEnum.HORIZONTAL) {
                            dx = rawX - recordButtonX1
                            
                            if (dx < 10f) {
                                recordButton?.translationX = dx
                            }

                            if ((rawX > (DRAG_EDGE_LOCK - 15f) && rawX <= DRAG_EDGE_LOCK)) {
                                if(!isLockReady) {
                                    isLockReady = true
                                    vibrator!!.vibrate(vibrationEffect)
                                }
                            } else if(isLockReady) {
                                isLockReady = false
                            }
                            
                            if (rawX < DRAG_EDGE_CANCEL) {
                                currentState = VoiceCommentState.CANCEL

                                vibrator!!.vibrate(vibrationEffect)

                                val cancelEvent = MotionEvent.obtain(event)
                                cancelEvent.action = MotionEvent.ACTION_UP
                                recordButton?.dispatchTouchEvent(cancelEvent)

                                return@setOnTouchListener false
                            }
                            
                            if (rawX > recordButtonX1 && rawX < recordButtonX2 && rawY < recordButtonY1) {
                                movement = MovementEnum.NONE
                            }
                            
                            return@setOnTouchListener false
                        }
                    }
                    return@setOnTouchListener false
                }

                MotionEvent.ACTION_UP -> {
                    recordButton?.apply {
                        animate()
                            .setDuration(200)
                            .scaleX(1f).scaleY(1f)
                            .translationX(0f).translationY(0f)
                            .start()
                    }

                    movement = MovementEnum.NONE

                    rlPathCancel?.visibility = View.INVISIBLE
                    rlPathLock?.visibility = View.INVISIBLE

                    if(currentState == VoiceCommentState.CANCEL) {
                        // animate trashcan
                        // and then ->
                        listener?.onCancel()
                        currentState = VoiceCommentState.IDLE

                        initState()
                        return@setOnTouchListener false
                    }

                    if(currentState == VoiceCommentState.RECORDING) {
                        listener?.onStop()
                        currentState = VoiceCommentState.IDLE

                        initState()
                    }

                    return@setOnTouchListener false
                }
            }
            false
        }
    }

    enum class VoiceCommentState {
        IDLE, RECORDING, LOCK, CANCEL
    }

    enum class MovementEnum {
        NONE, HORIZONTAL
    }

    interface CommentInputEvents {
        fun onStart()
        fun onStop()
        fun onCancel()
        fun onLock()
    }
}
