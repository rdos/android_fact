package ru.smartro.worknote.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.airbnb.lottie.LottieAnimationView
import com.masoudss.lib.WaveformSeekBar
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import java.io.File

@SuppressLint("ClickableViewAccessibility")
class VoiceCommentPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
): LinearLayoutCompat(context, attrs, defStyleAttrs) {

    private val ANIMATION_SPEED = 1.5f

    private var vibrator: Vibrator? = null
    private var vibrationEffect = VibrationEffect.createOneShot(100, 128)

    private var recordTime: AppCompatTextView? = null
    private var playButton: LottieAnimationView? = null
    private var trashButton: AppCompatImageView? = null
    private var waveformSeekBar: WaveformSeekBar? = null

    private var state = VoiceCommentContentState.IDLE

    var listener: VoiceCommentPlayerEvents? = null

    fun setTime(time: String) {
        recordTime?.text = time
    }

    fun setAudio(uriString: String) {
        waveformSeekBar?.setSampleFrom(uriString)
    }

    fun setAudio(resource: Int) {
        waveformSeekBar?.setSampleFrom(resource)
    }

    fun setAudio(file: File) {
        waveformSeekBar?.setSampleFrom(file)
    }

    fun setAudio(uri: Uri) {
        waveformSeekBar?.setSampleFrom(uri)
    }

    fun stop() {
        LOG.debug("STOP")
        vibrator?.vibrate(vibrationEffect)
        playButton?.pauseAnimation()
        playButton?.progress = 0f
        waveformSeekBar?.progress = 0f
        state = VoiceCommentContentState.IDLE
    }

    init {
        inflate(getContext(), R.layout.custom_view__voice_comment_player, this)

        vibrator = getContext().applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        recordTime = findViewById(R.id.actv__voice_comment_player__time)
        playButton = findViewById(R.id.lav__voice_comment_player__play_button)
        playButton?.setMaxProgress(0.5f)
        trashButton = findViewById(R.id.aciv__voice_comment_player__remove_button)
        waveformSeekBar = findViewById(R.id.wsb__voice_comment_player__waveform)

        playButton?.setOnClickListener {
            if(state == VoiceCommentContentState.PLAY) {
                listener?.onPause()
                playButton?.speed = -ANIMATION_SPEED
                playButton?.playAnimation()
                state = VoiceCommentContentState.PAUSE
                return@setOnClickListener
            }
            if(state == VoiceCommentContentState.IDLE) {
                listener?.onStart()
            }
            if(state == VoiceCommentContentState.PAUSE) {
                listener?.onResume()
            }
            playButton?.speed = ANIMATION_SPEED
            playButton?.playAnimation()
            state = VoiceCommentContentState.PLAY
        }

        trashButton?.setOnClickListener {
            listener?.onDelete()
            playButton?.speed = 1f
            playButton?.progress = 0f
            state = VoiceCommentContentState.IDLE
        }

    }

    enum class VoiceCommentContentState {
        IDLE,
        PLAY,
        PAUSE
    }

    interface VoiceCommentPlayerEvents {
        fun onStart()
        fun onPause()
        fun onResume()
        fun onDelete()
    }
}
