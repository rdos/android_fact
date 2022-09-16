package ru.smartro.worknote.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.airbnb.lottie.LottieAnimationView
import com.masoudss.lib.WaveformSeekBar
import ru.smartro.worknote.R

@SuppressLint("ClickableViewAccessibility")
class VoiceCommentContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
): LinearLayoutCompat(context, attrs, defStyleAttrs) {

    private var vibrator: Vibrator? = null
    private var vibrationEffect = VibrationEffect.createOneShot(100, 128)

    private var recordTime: AppCompatTextView? = null
    private var playButton: LottieAnimationView? = null
    private var trashButton: AppCompatImageView? = null
    private var waveformSeekBar: WaveformSeekBar? = null

    private var state = VoiceCommentContentState.IDLE

    var listener: VoiceCommentContentViewEvents? = null

    fun setTime(time: String) {
        recordTime?.text = time
    }

    fun setAudio() {

    }

    init {
        inflate(getContext(), R.layout.f_pserve__voice_comment__content, this)
        recordTime = findViewById(R.id.time)
        playButton = findViewById(R.id.play)
        playButton?.setMaxProgress(0.5f)
        trashButton = findViewById(R.id.trash)
        waveformSeekBar = findViewById(R.id.waveform)

        playButton?.setOnClickListener {

            when(state) {
                VoiceCommentContentState.IDLE -> {
                    listener?.onStart()
                    playButton?.speed = 1f
                    playButton?.playAnimation()
                    state = VoiceCommentContentState.PLAY
                }
                VoiceCommentContentState.PLAY -> {
                    listener?.onPause()
                    playButton?.speed = -1f
                    playButton?.playAnimation()
                    state = VoiceCommentContentState.PAUSE
                }
                VoiceCommentContentState.PAUSE -> {
                    listener?.onResume()
                    playButton?.speed = 1f
                    playButton?.playAnimation()
                    state = VoiceCommentContentState.PLAY

                }
            }
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

    interface VoiceCommentContentViewEvents {
        fun onStart()
        fun onPause()
        fun onResume()
        fun onStop()
        fun onDelete()
    }
}
