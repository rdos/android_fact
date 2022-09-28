package ru.smartro.worknote.work

import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import java.io.File

//class VoiceComment(private val p_callback: IVoiceComment) : AbsObject() {
class VoiceComment(private val p_callback: IVoiceComment) : CountDownTimer(SEC30_IN_MS, INTERVAL_IN_MS) {

    private var mRecordMan: RecordMan? = null
    private var startTimestamp: Long = 0

    fun startRecording() {
        super.start()
        getRecordM().start()
        startTimestamp = MyUtil.timeStampInMS()
        LOG.debug("onStartVoiceComment")
        p_callback.onStartVoiceComment()
        LOG.debug("onStartVoiceComment.after")

    }

    fun stop() {
        val diff = MyUtil.timeStampInMS() - startTimestamp
        if(getRecordM().isAudioRecording())
        if(diff > 1500) {
            stopRecording()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                stopRecording()
            }, 1000)
        }
        LOG.debug("after")
    }

    fun end() {
        val diff = MyUtil.timeStampInMS() - startTimestamp
        if(diff > 1500) {
            stopRecording()
            LOG.debug("onVoiceCommentSave")
            p_callback.onVoiceCommentSave(getSoundF())
            LOG.debug("onVoiceCommentSave.after")
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                stopRecording()
                if(diff > 1000) {
                    LOG.debug("onVoiceCommentSave")
                    p_callback.onVoiceCommentSave(getSoundF())
                    LOG.debug("onVoiceCommentSave.after")
                }
            }, 1000)
        }
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
        return App.getAppliCation().getF("sound", "y10.wav")
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

    fun release() {
        getRecordM().stop()
        super.cancel()
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