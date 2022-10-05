package ru.smartro.worknote.andPOintD

import android.content.Context
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
import androidx.core.view.MotionEventCompat
import com.yandex.runtime.view.internal.TouchEvent
import ru.smartro.worknote.App
import ru.smartro.worknote.R

class SmartROviewVoiceWhatsUp @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
): ConstraintLayout(context, attrs, defStyleAttrs), View.OnTouchListener {

    private var mCurrentEvent: MotionEvent? = null
    private var mCurrentState: Int? = null

    private var mIsLockReady: Boolean = false

    private var DRAG_EDGE_LOCK = 0f
    private var DRAG_EDGE_CANCEL = 0f

    private var messageInput: AppCompatEditText? = null
    private var recordInfo: LinearLayoutCompat? = null
    private var recordTime: AppCompatTextView? = null
    private var acivRecStart: AppCompatImageView? = null
    private var recordButtonWrapper: FrameLayout? = null
    private var buttonStop: AppCompatImageView? = null
    private var buttonCancel: AppCompatTextView? = null

    private var rlPathCancel: RelativeLayout? = null
    private var rlPathLock: RelativeLayout? = null

    private var movement = MovementEnum.NONE

    private var XRecButtonStart__Save = 0f
    private var recordButtonY1 = 0f
    private var XRecordButtonEnd = 0f
    private var recordButtonY2 = 0f


    private var dx = 0f
    private var dy = 0f

    //todo: on
    var mCallBack: CommentInputEvents? = null

    fun setIdle() {
        mCurrentState = VoiceCommentState.IDLE
        setViewState(mCurrentState)
    }

    fun setTime(timeInMS: Long) {
        val minutes = timeInMS / 60_000
        val seconds = (timeInMS - (minutes * 60_000)) / 1000
        val minutesShowForUser = if(minutes < 10) "0${minutes}" else minutes.toString()
        val secondsShowForUser = if(seconds < 10) "0${seconds}" else seconds.toString()
        recordTime?.text = "${minutesShowForUser}:${secondsShowForUser}"
    }


    init {
        inflate(getContext(), R.layout.custom_view__comment_input, this)


        rlPathCancel = findViewById(R.id.rl__comment_input__path_cancel)
        rlPathLock = findViewById(R.id.rl__comment_input__path_lock)

        messageInput = findViewById(R.id.acet__comment_input__message_input)
        recordInfo = findViewById(R.id.llc__comment_input__record_info)
        recordTime = findViewById(R.id.actv__comment_input__record_time)
        acivRecStart = findViewById(R.id.aciv__comment_input__rec_start)

        recordButtonWrapper = findViewById(R.id.cl__comment_input__record_button_wrapper)
        buttonStop = findViewById(R.id.aciv__comment_input__stop_button)
        buttonCancel = findViewById(R.id.actv__comment_input__cancel_button)

        initViewsss()

        buttonStop?.setOnClickListener {
            mCallBack?.onStop()
            mIsLockReady = false
            App.getAppliCation().startVibrateService()
            cleanState()
        }

        buttonCancel?.setOnClickListener {
            mCurrentState = VoiceCommentState.CANCEL
            setViewState(mCurrentState)
        }

        acivRecStart?.setOnTouchListener(this)
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
        //onResume()
        fun onCancel()
        fun onLock()
    }

    class UtilsAcivRecStart(private val p_view: View) {
        init {

        }
        private var mLocationOnScreen: IntArray? = null

        private fun getLocationOnScreenForView(): IntArray {
            if (mLocationOnScreen == null) {
                mLocationOnScreen = IntArray(2)
                p_view.getLocationOnScreen(mLocationOnScreen)
            }
            return mLocationOnScreen!!
        }

        fun getX(): Float {
            val locationOnScreen = getLocationOnScreenForView()
            // TODO: )) LOG
            return locationOnScreen[0].toFloat()
        }

        fun getYAsFloat(): Float {
            val locationOnScreen = getLocationOnScreenForView()
            // TODO: )) LOG
            return locationOnScreen[1].toFloat()
        }
    }
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        val result = false
        mCurrentEvent = event
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                // Corners of button has the same size
                val sizeAcivRecStart = acivRecStart?.width ?: 0

                val extAcivRecStart = UtilsAcivRecStart(acivRecStart!!)
                XRecButtonStart__Save = extAcivRecStart.getX()

                XRecordButtonEnd = XRecButtonStart__Save + sizeAcivRecStart
                recordButtonY1 = extAcivRecStart.getYAsFloat()
                recordButtonY2 = recordButtonY1 + sizeAcivRecStart

                acivRecStart?.translationY = 0f
                acivRecStart?.apply {
                    translationX = 0f
                    animate().scaleX(1.8f).scaleY(1.8f).setDuration(200).start()
                }

                App.getAppliCation().startVibrateService()

                val extAcivRecCancel = UtilsAcivRecStart(rlPathCancel!!)
                DRAG_EDGE_CANCEL = extAcivRecCancel.getX()

                DRAG_EDGE_LOCK = extAcivRecCancel.getYAsFloat()

                mCurrentState = VoiceCommentState.RECORDING
                setViewState(mCurrentState)

                return true
            }

            MotionEvent.ACTION_MOVE -> {
                return setViewState(mCurrentState)
            }

            MotionEvent.ACTION_UP -> {
                blablablaTODO()
                setNextState()
                return result
            }
        }
        return result
    }


    private fun cleanState() {
        recordButtonWrapper?.visibility = VISIBLE
        recordInfo?.visibility = VISIBLE
        messageInput?.visibility = VISIBLE
        buttonStop?.visibility = VISIBLE
        buttonCancel?.visibility = VISIBLE
        rlPathCancel?.visibility = VISIBLE
        rlPathLock?.visibility = VISIBLE
    }

    private fun stopStateInit(){
        cleanState()
        initViewsss()
    }

    private fun cancelStateInit(){
        cleanState()
        initViewsss()
    }

    private fun startStateInit(){
        cleanState()
    }

    private fun lockStateInit(){
        cleanState()
        recordButtonWrapper?.visibility = INVISIBLE
        buttonStop?.visibility = VISIBLE
        buttonCancel?.visibility = VISIBLE
    }

    private fun recordStateInit(){
        cleanState()
        recordInfo?.visibility = VISIBLE
        messageInput?.visibility = INVISIBLE
        rlPathCancel?.visibility = View.VISIBLE
        rlPathLock?.visibility = View.VISIBLE
    }

    private fun idleStateInit(){
        cleanState()
        initViewsss()
    }

    private fun initViewsss() {
        recordButtonWrapper?.visibility = VISIBLE
        recordInfo?.visibility = INVISIBLE
        messageInput?.visibility = VISIBLE
        buttonStop?.visibility = GONE
        buttonCancel?.visibility = GONE
        rlPathCancel?.visibility = INVISIBLE
        rlPathLock?.visibility = INVISIBLE
    }


    private fun blablablaTODO(){
        rlPathCancel?.visibility = View.INVISIBLE
        rlPathLock?.visibility = View.INVISIBLE

        acivRecStart?.apply {
            animate()
                .setDuration(200)
                .scaleX(1f).scaleY(1f)
                .translationX(0f).translationY(0f)
                .start()
        }

        movement = MovementEnum.NONE
    }


    private fun setNextState() {
        val stateNext = getNextState()
        setViewState(stateNext)
    }

    private fun getNextState(): VoiceCommentState {
        if (mCurrentState == null) {
            mCurrentState = VoiceCommentState.IDLE
        }
        if (mCurrentEvent!!.action == MotionEvent.ACTION_UP) {
            if(mIsLockReady) {
                mCurrentState = VoiceCommentState.LOCK
                return setViewState(mCurrentState)
            }
            if(mCurrentState == VoiceCommentState.CANCEL) {
                mCurrentState = VoiceCommentState.IDLE
                return setViewState(mCurrentState)
            }
            if(mCurrentState == VoiceCommentState.RECORDING) {
                mCurrentState = VoiceCommentState.IDLE
                return setViewState(mCurrentState)
            }

        }



        return mCurrentState

    }

    private fun setViewState(state: VoiceCommentState): Boolean {
        if(state == VoiceCommentState.IDLE) {
            setTime(0)
            movement = MovementEnum.NONE
            mIsLockReady = false
            idleStateInit()
        }


        if(state == VoiceCommentState.CANCEL) {
            // animate trashcan
            // and then ->
            mCallBack?.onCancel()
            App.getAppliCation().startVibrateService()
            cancelStateInit()
            return false
        }

        if( state == VoiceCommentState.LOCK) {
            mCallBack?.onLock()
            lockStateInit()

        }

        if (state == VoiceCommentState.RECORDING) {
            if (mCurrentEvent == null) {
                return false
            }
            recordStateInit()
            mCallBack?.onStart()
            val rawX = mCurrentEvent?.rawX!!
            val rawY = mCurrentEvent?.rawY!!

            if (movement == MovementEnum.NONE) {
                if (rawY > recordButtonY1 && rawY < recordButtonY2 && rawX < XRecButtonStart__Save) {
                    acivRecStart?.translationX = 0f
                    movement = MovementEnum.HORIZONTAL
                }
            }

            if (movement == MovementEnum.HORIZONTAL) {
                dx = rawX - XRecButtonStart__Save

                if (dx < 10f) {
                    acivRecStart?.translationX = dx
                }

                if ((rawX > (DRAG_EDGE_LOCK - 30f) && rawX <= DRAG_EDGE_LOCK) && !mIsLockReady) {
                    mIsLockReady = true
                    App.getAppliCation().startVibrateService()
                }

                if((rawX < (DRAG_EDGE_LOCK - 60f) || rawX > (DRAG_EDGE_LOCK + 30f)) && mIsLockReady) {
                    mIsLockReady = false
                }

                if (rawX < DRAG_EDGE_CANCEL) {
                    mCurrentState = VoiceCommentState.CANCEL

                    App.getAppliCation().startVibrateService()

                    val cancelEvent = MotionEvent.obtain(mCurrentEvent)
                    cancelEvent.action = MotionEvent.ACTION_UP
                    acivRecStart?.dispatchTouchEvent(cancelEvent)

                    return false
                }

                if (rawX > XRecButtonStart__Save && rawX < XRecordButtonEnd && rawY < recordButtonY1) {
                    movement = MovementEnum.NONE
                }

                return false
            }
        }
        return false
    }

}
