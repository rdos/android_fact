package ru.smartro.worknote.work

import android.content.Context
import android.media.MediaRecorder
import ru.smartro.worknote.Inull
import ru.smartro.worknote.LOG
import java.io.File

// TODO: !!? mv to APP
class RecordMan(private val p_outputF: File) {

    private var mAudioRecorder: MediaRecorder? = null

    fun start() {
        LOG.trace("absolutePath=${p_outputF.absolutePath}")
        try {
            mAudioRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(p_outputF)
                prepare()
                start()
            }
        } catch (ex: Exception) {
            LOG.error("mAudioRecorder = MediaRecorder().apply {", ex)
        }
        LOG.debug("after")
    }

    fun stop() {
        try {
            if (mAudioRecorder == null) {
                LOG.trace("if (audioRecorder == null) {")
                return
            }
            mAudioRecorder?.stop()
            mAudioRecorder?.release()
            mAudioRecorder = null
            LOG.debug("after")
        } catch(e: Exception) {
            LOG.error(e.stackTraceToString())
            mAudioRecorder?.release()
            mAudioRecorder = null
        }
    }

    fun isAudioRecording(): Boolean {
        val result = mAudioRecorder != null
        LOG.debug("result=${result}")
        return result
    }

    fun getVolume() = mAudioRecorder?.maxAmplitude ?: Inull

    private companion object {

    }

}