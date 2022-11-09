package ru.smartro.worknote.andPOintD.swipebtn

import android.content.Context
import android.util.AttributeSet
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.airbnb.lottie.LottieAnimationView
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import kotlin.math.min
//todo: ::: https://github.com/ebanx/swipe-button.git
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

    private val rlPathCancelX: Int by lazy { ViewUtil(rlPathCancel!!).getXStart() }
    private val rlPathLockX: Int by lazy { ViewUtil(rlPathLock!!).getXStart() }

    private var movableView: MovableView? = null

        //todo: мне кажется
    private var mIsLockReady = false
    private var mIsStop = false

    var mOnStartRecording: (() -> Unit)? = null
    var mOnLockRecording: (() -> Unit)? = null
    // stop)
    var mOnEndRecording: (() -> Unit)? = null
    // cancel))
    var mOnStopRecording: (() -> Unit)? = null

    var mOnTextCommentChange: ((newText: String) -> Unit)? = null

    fun setTextComment(comment: String?) {
        if(comment != null) {
            acetMessageInput?.setText(comment)
        }
    }

    init {
        inflate(getContext(), R.layout.smartro_view_voicewhatsup, this)

        lavMicrophone = findViewById(R.id.lav_sview_voicewhatsup_recording_animated_icon)
        rlPathCancel = findViewById(R.id.rl_sview_voicewhatsup_path_cancel)
        rlPathLock = findViewById(R.id.rl_sview_voicewhatsup_path_lock)
        acetMessageInput = findViewById(R.id.acet_sview_voicewhatsup_message_input)
        llcRecordInfo = findViewById(R.id.llc_sview_voicewhatsup_record_info)
        actvRecordTime = findViewById(R.id.actv_sview_voicewhatsup_record_time)
        acivRecordButton = findViewById(R.id.aciv_sview_voicewhatsup_rec_start)
        flRecordButtonWrapper = findViewById(R.id.cl_sview_voicewhatsup_record_button_wrapper)
        acivButtonStop = findViewById(R.id.aciv_sview_voicewhatsup_stop_button)
        actvButtonCancel = findViewById(R.id.actv_sview_voicewhatsup_cancel_button)

        acetMessageInput?.addTextChangedListener {
            mOnTextCommentChange?.invoke(it?.toString() ?: "")
        }

        initialState()

        acivButtonStop?.setOnClickListener {
            mOnEndRecording?.invoke()
        }

        actvButtonCancel?.setOnClickListener {
            mOnStopRecording?.invoke()
        }

        movableView = MovableView()
            .setTargetView(acivRecordButton!!)
            .setMovementRules(MovableView.MovementRule.HORIZONTAL)
            .setDelay(200)
            .onMoveStart {
                LOG.debug("MOVABLE ON START")
                mOnStartRecording?.invoke()
                acivRecordButton!!.animate()?.scaleX(1.6f)?.scaleY(1.6f)?.setDuration(200)?.start()
            }
            .onMoveHorizontally { view, absoluteX ->
                if(!mIsLockReady && absoluteX > (rlPathLockX - 30f) && absoluteX < (rlPathLockX + 30f)) {
                    LOG.debug("LOCK")
                    mIsLockReady = true
                    App.getAppliCation().startVibrateServiceHaptic()
                }

                if(mIsLockReady && absoluteX < (rlPathLockX - 50f) || absoluteX > (rlPathLockX + 50f)) {
                    LOG.debug("UNLOCK")
                    mIsLockReady = false
                }

                if(absoluteX < rlPathCancelX) {
                    LOG.debug("stop")
                    mIsStop = true

                    movableView?.stopMove()
                }
            }
            .onMoveEnd {
                LOG.debug("MOVABLE ON STOP")
                acivRecordButton?.animate()?.translationX(0f)?.translationY(0f)?.start()
                acivRecordButton?.animate()?.scaleX(1f)?.scaleY(1f)?.setDuration(200)?.start()
                if(mIsLockReady) {
                    lockState()
                    mOnLockRecording?.invoke()
                    return@onMoveEnd
                }
                if(mIsStop) {
                    initialState()
                    mOnStopRecording?.invoke()
                    return@onMoveEnd
                }
                initialState()
                mOnEndRecording?.invoke()
            }
        movableView?.apply()
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