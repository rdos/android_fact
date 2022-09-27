package ru.smartro.worknote.work

import android.os.CountDownTimer
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import java.io.File

//class VoiceComment(private val p_callback: IVoiceComment) : AbsObject() {
class VoiceComment(private val platformId: Int, private val p_callback: IVoiceComment) : CountDownTimer(SEC30_IN_MS, INTERVAL_IN_MS) {
    private var mRecordMan: RecordMan? = null

    fun startRecording() {
        super.start()
        getRecordM().start()
        LOG.debug("onStartVoiceComment")
        p_callback.onStartVoiceComment()
        LOG.debug("onStartVoiceComment.after")

    }

    fun stop() {
        stopRecording()
//        p_callback.onCancelVoiceComment()
        LOG.debug("after")
    }

    fun end() {
        stopRecording()
        LOG.debug("onVoiceCommentSave")
        p_callback.onVoiceCommentSave(getSoundF())
        LOG.debug("onVoiceCommentSave.after")
    }

    private fun stopRecording() {
        if (getRecordM().isAudioRecording()) {
            getRecordM().stop()
            super.cancel()
        }
        LOG.debug("onStopVoiceComment")
        p_callback.onStopVoiceComment()
        LOG.debug("onStopVoiceComment.after")
    }

    private fun getRecordM(): RecordMan {
        //mv to APP
        if (mRecordMan == null) {
            mRecordMan = RecordMan(getSoundF())
        }
        return mRecordMan!!
    }

    private fun getSoundF(): File {
        return App.getAppliCation().getF("sound", "${platformId}.wav")
    }

    companion object {
        private const val INTERVAL_IN_MS = 100L
        private const val SEC30_IN_MS: Long = 30_000
    }

    override fun onTick(millisUntilFinished: Long) {
//        LOG.trace("millisUntilFinished = $millisUntilFinished")
        val timeInMS = SEC30_IN_MS - millisUntilFinished
        val volume = getRecordM().getVolume()
//        LOG.trace("onVoiceCommentShowForUser.volume=${volume}, timeInMS=${timeInMS}")
        p_callback.onVoiceCommentShowForUser(volume, timeInMS)
//        LOG.trace("onVoiceCommentShowForUser.after")
    }

    override fun onFinish() {
        end()
        LOG.debug("after")
    }

    interface IVoiceComment {
        fun onStartVoiceComment()
        fun onStopVoiceComment()
                                    //        fun onCancelVoiceComment()
        /**VT, sorry!!! onHandleVoiceComment()    (;)=смайлик святошка*/
        fun onVoiceCommentShowForUser(volume: Int, timeInMS: Long)
        fun onVoiceCommentSave(soundF: File)
    }
}

/**
private fun handleVolume(volume: Int) {
val scale = min(11.0, volume / MAX_RECORD_AMPLITUDE + 1.0).toFloat()
LOG.debug("Scale = $scale")
//        audioButton.animate()
//            .scaleX(scale)
//            .scaleY(scale)
//            .setInterpolator(interpolator)
//            .duration = VOLUME_UPDATE_DURATION
}
private const val MAX_RECORD_AMPLITUDE = 32768.0
private val interpolator = OvershootInterpolator()

fun getRecordTimeInMS(): Long {
return Lnull
}
 */