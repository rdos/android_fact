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
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R

@SuppressLint("ClickableViewAccessibility")
class CommentInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
): ConstraintLayout(context, attrs, defStyleAttrs) {

    private var vibrator: Vibrator? = null
    private var vibrationEffect = VibrationEffect.createOneShot(100, 128)

    private var DRAG_EDGE_LEFT = 0f
    private var DRAG_EDGE_TOP = 0f

    private var messageInput: AppCompatEditText? = null
    private var recordInfo: LinearLayoutCompat? = null
    private var recordTime: AppCompatTextView? = null
    private var recordButton: AppCompatImageView? = null
    private var lockWrapper: RelativeLayout? = null
    private var lockBackground: View? = null
    private var lockButton: LottieAnimationView? = null
    private var swipeLeftHint: AppCompatTextView? = null
    private var recordButtonWrapper: CoordinatorLayout? = null
    private var buttonStop: AppCompatImageView? = null
    private var buttonCancel: AppCompatTextView? = null

    private var movement = MovementEnum.NONE
    private var currentState = VoiceCommentState.IDLE

    private var recordButtonX1 = 0f
    private var recordButtonY1 = 0f
    private var recordButtonX2 = 0f
    private var recordButtonY2 = 0f

    private var lockButtonY1 = 0f

    private var lockButtonDistance = 0f

    private var dx = 0f
    private var dy = 0f

    var listener: CommentInputEvents? = null

    fun setTime(time: String) {
        recordTime?.text = time
    }

    private fun initState() {
        recordButtonWrapper?.visibility = VISIBLE
        recordInfo?.visibility = INVISIBLE
        swipeLeftHint?.visibility = INVISIBLE
        lockWrapper?.visibility = INVISIBLE
        messageInput?.visibility = VISIBLE
        buttonStop?.visibility = GONE
        buttonCancel?.visibility = GONE
    }

    init {
        inflate(getContext(), R.layout.custom_view__comment_input, this)

        vibrator = getContext().applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        messageInput = findViewById(R.id.acet__comment_input__message_input)
        recordInfo = findViewById(R.id.llc__comment_input__record_info)
        recordTime = findViewById(R.id.actv__comment_input__record_time)
        lockButton = findViewById(R.id.lav__comment_input__lock_animated_icon)
        lockWrapper = findViewById(R.id.rl__comment_input__lock_button_wrapper)
        lockBackground = findViewById<View?>(R.id.v__comment_input__lock_button_background).apply {
            pivotX = 0.5f
            pivotY = 0f
        }
        recordButton = findViewById(R.id.aciv__comment_input__record_button)
        swipeLeftHint = findViewById(R.id.actv__comment_input__swipe_left_hint)
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
                    DRAG_EDGE_LEFT = -(recordButtonSize * 3.5f)
                    DRAG_EDGE_TOP = -(recordButtonSize * 2f)

                    val location = IntArray(2)
                    recordButton?.getLocationOnScreen(location)
                    recordButtonX1 = location[0].toFloat()
                    recordButtonX2 = recordButtonX1 + recordButtonSize
                    recordButtonY1 = location[1].toFloat()
                    recordButtonY2 = recordButtonY1 + recordButtonSize

                    lockButton?.getLocationOnScreen(location)
                    lockButtonY1 = location[1].toFloat()

                    lockButtonDistance = recordButtonY1 - lockButtonY1

                    swipeLeftHint?.translationX = 0f
                    swipeLeftHint?.translationY = 0f
                    recordButton?.translationY = 0f
                    recordButton?.apply {
                        translationX = 0f
                        animate().scaleX(1.8f).scaleY(1.8f).setDuration(200).start()
                    }
                    
                    lockButton?.progress = 0f
                    lockButton?.scaleY = 1f
                    lockButton?.scaleX = 1f
                    lockBackground?.scaleY = 1f

                    vibrator?.vibrate(vibrationEffect)

                    recordInfo?.visibility = VISIBLE
                    messageInput?.visibility = INVISIBLE
                    swipeLeftHint?.visibility = VISIBLE
                    swipeLeftHint?.visibility = VISIBLE
                    lockWrapper?.visibility = VISIBLE

                    val params = swipeLeftHint!!.layoutParams as MarginLayoutParams
                    val marginRight = (recordButton?.width ?: 0) / 2
                    params.setMargins(0, 0, marginRight, 0)
                    swipeLeftHint!!.layoutParams = params

                    currentState = VoiceCommentState.RECORDING

                    // TODO::: DELAY?
                    listener?.onStart()

                    return@setOnTouchListener true
                }
                
                MotionEvent.ACTION_MOVE -> {
                    if (currentState == VoiceCommentState.RECORDING) {
                        
                        val rawX = event.rawX
                        val rawY = event.rawY

                        if (movement == MovementEnum.NONE) {
                            if (rawY > recordButtonY1 && rawY < recordButtonY2 && rawX < recordButtonX1) {
                                lockWrapper?.visibility = INVISIBLE
                                swipeLeftHint?.visibility = VISIBLE
                                recordButton?.translationX = 0f
                                movement = MovementEnum.HORIZONTAL
                            } else if (rawX > recordButtonX1 && rawX < recordButtonX2 && rawY < recordButtonY1) {
                                lockWrapper?.visibility = VISIBLE
                                swipeLeftHint?.visibility = INVISIBLE
                                recordButton?.translationY = 0f
                                lockButton?.progress = 0f
                                lockButton?.scaleY = 1f
                                lockButton?.scaleX = 1f
                                lockBackground?.scaleY = 1f

                                movement = MovementEnum.VERTICAL
                            }
                        }
                        
                        if (movement == MovementEnum.HORIZONTAL) {
                            dx = rawX - recordButtonX1
                            
                            if (dx < 10f) {
                                recordButton?.translationX = dx
                            }
                            
                            if (dx < DRAG_EDGE_LEFT) {
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
                        
                        if (movement == MovementEnum.VERTICAL) {
                            dy = rawY - recordButtonY1
                            
                            if (dy < 10f) {
                                recordButton?.translationY = dy
                            }

                            if(dy < -60f) {
                                val diff = (lockButtonDistance - (rawY - lockButtonY1)) / lockButtonDistance
                                val scale = 1 + diff

                                lockButton?.scaleX = scale
                                lockButton?.scaleY = scale
                                lockButton?.progress = diff

                                lockBackground?.scaleY = 1 - diff
                            }
                            
                            if (dy < DRAG_EDGE_TOP) {
                                listener?.onLock()
                                currentState = VoiceCommentState.LOCK

                                vibrator!!.vibrate(vibrationEffect)

                                recordButtonWrapper?.visibility = INVISIBLE
                                buttonStop?.visibility = VISIBLE
                                buttonCancel?.visibility = VISIBLE

                                return@setOnTouchListener false
                            }
                            
                            if (rawY > recordButtonY1 && rawY < recordButtonY2 && rawX < recordButtonX1) {
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
        HORIZONTAL, VERTICAL, NONE
    }

    interface CommentInputEvents {
        fun onStart()
        fun onStop()
        fun onCancel()
        fun onLock()
    }
}
