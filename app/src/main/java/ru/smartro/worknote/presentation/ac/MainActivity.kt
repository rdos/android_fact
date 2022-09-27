package ru.smartro.worknote.presentation.ac

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.Voice
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.PERMISSIONS
import ru.smartro.worknote.R
import ru.smartro.worknote.utils.VoiceCommentPlayerView
import ru.smartro.worknote.utils.CommentInputView
import ru.smartro.worknote.work.VoiceComment
import ru.smartro.worknote.work.swipebtn.SwipeButton
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.RECORD_AUDIO), 101)
        }

        val swipeBtnEnabled = findViewById<SwipeButton>(R.id.swipeBtnEnabled)
        swipeBtnEnabled.background = ContextCompat.getDrawable(this, R.drawable.shape_button2)
        swipeBtnEnabled.setSlidingButtonBackground(ContextCompat.getDrawable(this, R.drawable.shape_rounded2))

        swipeBtnEnabled.setOnStateChangeListener { active ->
            Toast.makeText(this@MainActivity, "State: $active", Toast.LENGTH_SHORT).show()
            if (active) App.getAppliCation().startVibrateService()
            if (active) {
                swipeBtnEnabled.setButtonBackground(ContextCompat.getDrawable(this@MainActivity, R.drawable.shape_button))
            } else {
                swipeBtnEnabled.setButtonBackground(ContextCompat.getDrawable(this@MainActivity, R.drawable.shape_button3))
            }
        }

//        swipeBtnDisabled.setDisabledStateNotAnimated()
//        swipeBtnEnabled.setEnabledStateNotAnimated()
        val swipeNoState  = findViewById<SwipeButton>(R.id.swipeNoState)
        swipeNoState.setOnActiveListener { Toast.makeText(this@MainActivity, "Active!", Toast.LENGTH_SHORT).show() }

        val voiceCommentPlayer = findViewById<VoiceCommentPlayerView>(R.id.voice_message_content)
        val commentInput = findViewById<CommentInputView>(R.id.voice_message_view)
        val voiceCommentHandler = VoiceComment(123, object : VoiceComment.IVoiceComment {
            override fun onStartVoiceComment() {

            }

            override fun onStopVoiceComment() {

            }

            override fun onVoiceCommentShowForUser(volume: Int, timeInMS: Long) {
                commentInput.setTime(timeInMS)
            }

            override fun onVoiceCommentSave(soundF: File) {
                voiceCommentPlayer?.visibility = View.VISIBLE
                voiceCommentPlayer?.setAudio(this@MainActivity, soundF)
            }
        })

        val  toggleBtn  = findViewById<AppCompatButton>(R.id.toggleBtn)
        toggleBtn.setOnClickListener {
            if (!swipeBtnEnabled.isActive) {
                swipeBtnEnabled.toggleState()
                swipeBtnEnabled.isActive
            }
        }

        commentInput.apply {
            listener = object : CommentInputView.CommentInputEvents {
                override fun onStart() {
                    if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.RECORD_AUDIO), 101)
                    } else {
                        voiceCommentHandler.startRecording()
                    }
                }

                override fun onStop() {
                    voiceCommentHandler.end()
                }

                override fun onCancel() {
                    voiceCommentHandler.stop()
                }

                override fun onLock() {
                    LOG.debug("onLock!!!")
                    // TODO::: INCREASE ALLOWED RECORD TIME
                }
            }
        }

        voiceCommentPlayer.apply {
            listener = object : VoiceCommentPlayerView.VoiceCommentPlayerEvents {
                override fun onStart() {
                    LOG.debug("onStart")
                }

                override fun onPause() {
                    LOG.debug("onPause")
                }

                override fun onResume() {
                    LOG.debug("onResume")
                }

                override fun onDelete() {
                    LOG.debug("onDelete")
                    this@apply.visibility = View.GONE
                    // TODO::: Remove file??
                }

            }
        }

    }

}
