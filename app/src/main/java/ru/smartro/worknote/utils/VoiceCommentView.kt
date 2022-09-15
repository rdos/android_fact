package ru.smartro.worknote.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R

@SuppressLint("ClickableViewAccessibility")
class VoiceCommentView @JvmOverloads constructor(
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
    private var lockButton: LottieAnimationView? = null
    private var swipeLeftHint: AppCompatTextView? = null
    private var messageInputHint: String? = null

    private var movement = MovementEnum.NONE
    private var currentState = VoiceCommentState.IDLE

    private var recordButtonX1 = 0f
    private var recordButtonY1 = 0f
    private var recordButtonX2 = 0f
    private var recordButtonY2 = 0f

    private var lockButtonY1 = 0f

    private var lockDistance = 0f

    private var dx = 0f
    private var dy = 0f

    init {
        inflate(getContext(), R.layout.f_pserve__voice_message_view, this)

        vibrator = getContext().applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        messageInput = findViewById(R.id.acet__f_pserve__voice_message_view__message_input)
        recordInfo = findViewById(R.id.llc__f_pserve__voice_message_view__record_info)
        recordTime = findViewById(R.id.actv__f_pserve__voice_message_view__record_time)
        lockButton = findViewById(R.id.lottie_lock)
        recordButton = findViewById(R.id.aciv__f_pserve__voice_message_view__record_button)
        swipeLeftHint = findViewById(R.id.actv__f_pserve__voice_message_view__swipe_left_hint)

        messageInputHint = messageInput?.hint.toString()

        recordInfo?.visibility = INVISIBLE
        swipeLeftHint?.visibility = INVISIBLE
        lockButton?.visibility = INVISIBLE

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

                    lockDistance = recordButtonY1 - lockButtonY1

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

                    vibrator?.vibrate(vibrationEffect)

                    recordInfo?.visibility = VISIBLE
                    messageInput?.visibility = INVISIBLE
                    swipeLeftHint?.visibility = VISIBLE
                    swipeLeftHint?.visibility = VISIBLE
                    lockButton?.visibility = VISIBLE

                    val params = swipeLeftHint!!.layoutParams as MarginLayoutParams
                    val marginRight = (recordButton?.width ?: 0) / 2
                    LOG.debug("MARGIN RIGHT: $marginRight")
                    params.setMargins(0, 0, marginRight, 0)
                    swipeLeftHint!!.layoutParams = params

                    currentState = VoiceCommentState.RECORDING
                    return@setOnTouchListener true
                }
                
                MotionEvent.ACTION_MOVE -> {
                    if (currentState == VoiceCommentState.RECORDING) {
                        
                        val rawX = event.rawX
                        val rawY = event.rawY

                        if (movement == MovementEnum.NONE) {
                            if (rawY > recordButtonY1 && rawY < recordButtonY2 && rawX < recordButtonX1) {
                                lockButton?.visibility = INVISIBLE
                                swipeLeftHint?.visibility = VISIBLE
                                recordButton?.translationX = 0f
                                movement = MovementEnum.HORIZONTAL
                            } else if (rawX > recordButtonX1 && rawX < recordButtonX2 && rawY < recordButtonY1) {
                                lockButton?.visibility = VISIBLE
                                swipeLeftHint?.visibility = INVISIBLE
                                recordButton?.translationY = 0f
                                lockButton?.progress = 0f
                                lockButton?.scaleY = 1f
                                lockButton?.scaleX = 1f
                                movement = MovementEnum.VERTICAL
                            }
                        }
                        
                        if (movement == MovementEnum.HORIZONTAL) {
                            
                            Log.d("TEST :::", "left")
                            dx = rawX - recordButtonX1
                            Log.d("TEST :::", dx.toString())
                            
                            if (dx < 10f) {
                                recordButton?.translationX = dx
                            }
                            
                            if (dx < DRAG_EDGE_LEFT) {
                                Log.d("TEST :::", "SWIPED LEFT")
                                vibrator!!.vibrate(vibrationEffect)
                                currentState = VoiceCommentState.LOCK
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
                            
                            Log.d("TEST :::", "top")
                            dy = rawY - recordButtonY1
                            Log.d("TEST :::", dy.toString())
                            
                            if (dy < 10f) {
                                recordButton?.translationY = dy
                            }

                            if(dy < -60f) {
                                lockButton?.apply {
                                    val diff = ((rawY + 40) - lockButtonY1) / lockDistance
                                    val scale = 2 - diff
                                    scaleX = scale
                                    scaleY = scale
                                    progress = 1 - diff
                                }
                            }
                            
                            if (dy < DRAG_EDGE_TOP) {
                                Log.d("TEST :::", "SWIPED TOP")
                                vibrator!!.vibrate(vibrationEffect)
                                currentState = VoiceCommentState.LOCK
                                val cancelEvent = MotionEvent.obtain(event)
                                cancelEvent.action = MotionEvent.ACTION_UP
                                recordButton?.dispatchTouchEvent(cancelEvent)
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

                    recordInfo?.visibility = INVISIBLE
                    swipeLeftHint?.visibility = INVISIBLE
                    messageInput?.visibility = VISIBLE
                    lockButton?.visibility = INVISIBLE

                    return@setOnTouchListener false
                }
            }
            
            false
        }
    }

    enum class VoiceCommentState {
        IDLE, RECORDING, LOCK
    }

    enum class MovementEnum {
        HORIZONTAL, VERTICAL, NONE
    }
}