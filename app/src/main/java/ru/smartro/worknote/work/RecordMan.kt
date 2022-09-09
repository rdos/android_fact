package ru.smartro.worknote.work

import android.content.Context
import android.media.MediaRecorder
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.OvershootInterpolator
import java.io.File
import kotlin.math.min

class RecordMan(private val context: Context) {

    private var audioRecorder: MediaRecorder? = null
    private var countDownTimer: CountDownTimer? = null
    fun start() {
        Log.d(TAG, "Start")
        val path = getAudioPath()
        Log.d(TAG, "path=${path}")
        audioRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(path)
            prepare()
            start()
        }
    }

    private fun getAudioPath(): String {
        return "${context.filesDir.absolutePath}${File.pathSeparator}${System.currentTimeMillis()}.wav"
    }

    fun stop() {
        audioRecorder?.let {
            Log.d(TAG, "Stop")
            it.stop()
            it.release()
        }
        audioRecorder = null
    }

    fun isAudioRecording() = audioRecorder != null

    fun getVolume() = audioRecorder?.maxAmplitude ?: 0

    private fun onButtonClicked() {
        if (this.isAudioRecording()) {
            this.stop()
            countDownTimer?.cancel()
            countDownTimer = null
        } else {
            this.start()
            val duration = 0
            countDownTimer = object : CountDownTimer(60_000, VOLUME_UPDATE_DURATION) {
                override fun onTick(p0: Long) {

                    val volume = this@RecordMan.getVolume()
                    Log.d(TAG, "Volume = $volume")
                    Log.d(TAG, "p0 = $p0")
                    handleVolume(volume)
                }

                override fun onFinish() {
                }
            }.apply {
                start()
            }
        }
    }

    private fun handleVolume(volume: Int) {
        val scale = min(11.0, volume / MAX_RECORD_AMPLITUDE + 1.0).toFloat()
        Log.d(TAG, "Scale = $scale")

//        audioButton.animate()
//            .scaleX(scale)
//            .scaleY(scale)
//            .setInterpolator(interpolator)
//            .duration = VOLUME_UPDATE_DURATION
    }

    private companion object {
//        private val TAG = MainActivity::class.java.name
        private const val MAX_RECORD_AMPLITUDE = 32768.0
        private const val VOLUME_UPDATE_DURATION = 100L
        private val interpolator = OvershootInterpolator()
        private val TAG = RecordMan::class.java.name
    }

}