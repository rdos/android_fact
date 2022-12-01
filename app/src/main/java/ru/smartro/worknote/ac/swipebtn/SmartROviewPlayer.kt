package ru.smartro.worknote.ac.swipebtn

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.net.toUri
import com.airbnb.lottie.LottieAnimationView
import com.masoudss.lib.SeekBarOnProgressChanged
import com.masoudss.lib.WaveformSeekBar
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import java.io.File
import java.util.*

@SuppressLint("ClickableViewAccessibility")
//название из-за future(;)
//todo: ::: https://github.com/ebanx/swipe-button.git
class SmartROviewPlayer(context: Context, attrs: AttributeSet?, defStyleAttrs: Int)  : LinearLayoutCompat(context, attrs, defStyleAttrs) {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val ANIMATION_SPEED = 2.2f
    private var mediaPlayer: MediaPlayer? = null

    private var timer = Timer()

    private var recordTime: AppCompatTextView? = null
    private var playButton: LottieAnimationView? = null
    private var trashButton: AppCompatImageView? = null
    private var waveformSeekBar: WaveformSeekBar? = null

    private var state = VoiceCommentPlayerState.IDLE

    var listener: VoiceCommentPlayerEvents? = null

    private var countDownTimer: CountDownTimer? = null

    fun setTime(timeInMS: Long) {
        val minutes = timeInMS / 60_000
        val seconds = (timeInMS - (minutes * 60_000)) / 1000
        val minutesShowForUser = if(minutes < 10) "0${minutes}" else minutes.toString()
        val secondsShowForUser = if(seconds < 10) "0${seconds}" else seconds.toString()
        recordTime?.text = "${minutesShowForUser}:${secondsShowForUser}"
    }

    fun setAudio(context: Context, file: File) {
        waveformSeekBar?.progress = 0f
        waveformSeekBar?.setSampleFrom(file)
        mediaPlayer = MediaPlayer.create(context, file.toUri())
        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        setTime(mediaPlayer?.duration?.toLong() ?: 0)
        mediaPlayer?.setOnCompletionListener {
            stop()
        }
    }

    private fun stop() {
        LOG.debug("STOP")
        App.getAppliCation().startVibrateService()
        timer.cancel()
        setTime(mediaPlayer?.duration?.toLong() ?: 0)
        playButton?.pauseAnimation()
        playButton?.progress = 0f
        waveformSeekBar?.progress = 0f
        state = VoiceCommentPlayerState.IDLE
    }

    init {
        inflate(getContext(), R.layout.sview_player, this)

        recordTime = findViewById(R.id.actv__sview_player__time)
        playButton = findViewById(R.id.lav__sview_player__play_button)
        playButton?.setMaxProgress(0.5f)
        trashButton = findViewById(R.id.aciv__sview_player__remove_button)
        waveformSeekBar = findViewById(R.id.wsb__sview_player__waveform)

        waveformSeekBar?.onProgressChanged = object : SeekBarOnProgressChanged {
            override fun onProgressChanged(waveformSeekBar: WaveformSeekBar, progress: Float, fromUser: Boolean) {
                if(fromUser) {
                    val duration = mediaPlayer?.duration ?: 0
                    val currentMS = (duration * progress) / 100
                    countDownTimer?.cancel()
                    if(mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.pause()
                        mediaPlayer?.seekTo(currentMS.toInt())
                        setTime(currentMS.toLong())
                        mediaPlayer?.start()
                        getTimer().start()
                    } else {
                        mediaPlayer?.seekTo(currentMS.toInt())
                        setTime(currentMS.toLong())
                    }
                }
            }
        }

        playButton?.setOnClickListener {
            // PAUSE
            if(state == VoiceCommentPlayerState.PLAY) {
                mediaPlayer?.pause()
                playButton?.speed = -ANIMATION_SPEED
                playButton?.playAnimation()
                state = VoiceCommentPlayerState.PAUSE
                return@setOnClickListener
            }

            mediaPlayer?.start()

            getTimer().start()

            playButton?.speed = ANIMATION_SPEED
            playButton?.playAnimation()
            state = VoiceCommentPlayerState.PLAY
        }

        trashButton?.setOnTouchListener { v, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    trashButton?.setImageResource(R.drawable.ic_trashcan_red)
                }
                MotionEvent.ACTION_UP -> {
                    trashButton?.setImageResource(R.drawable.ic_trashcan)

                    val trashButtonUtil = ViewUtil(trashButton!!)
                    val trashButtonXStart = trashButtonUtil.getXStart()
                    val trashButtonXEnd = trashButtonUtil.getXEnd()
                    val trashButtonYStart = trashButtonUtil.getYStart()
                    val trashButtonYEnd = trashButtonUtil.getYEnd()
                    // Если палец подняли над иконкой - произвести нажатие
                    if(
                        event.rawX >= trashButtonXStart && event.rawX <= trashButtonXEnd &&
                        event.rawY >= trashButtonYStart && event.rawY <= trashButtonYEnd
                    ) {
                        App.getAppliCation().startVibrateServiceHaptic()
                        listener?.onClickDelete()
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        playButton?.speed = 1f
                        playButton?.progress = 0f
                        state = VoiceCommentPlayerState.IDLE
                    }
                }
            }
            false
        }

    }

    private fun getTimer(): CountDownTimer {
        if(countDownTimer != null) {
            countDownTimer?.cancel()
        }

        val duration = mediaPlayer?.duration ?: 1000

        countDownTimer = object : CountDownTimer(duration.toLong() + 500, 50) {
            override fun onTick(millisUntilFinished: Long) {
                val currentPos = mediaPlayer?.currentPosition ?: 0
                waveformSeekBar?.progress = ((currentPos / duration.toFloat()) * 105)
                setTime(currentPos.toLong())
            }

            override fun onFinish() {

            }
        }
        return countDownTimer!!
    }

    fun release() {
        countDownTimer?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    enum class VoiceCommentPlayerState {
        IDLE,
        PLAY,
        PAUSE
    }

    interface VoiceCommentPlayerEvents {
        fun onClickDelete()
    }
}
