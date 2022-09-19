package ru.smartro.worknote.presentation.ac

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.utils.VoiceCommentPlayerView
import ru.smartro.worknote.utils.CommentInputView
import ru.smartro.worknote.work.swipebtn.SwipeButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

        val voiceCommentPlayer = findViewById<VoiceCommentPlayerView>(R.id.voice_message_content)

        val commentInput = findViewById<CommentInputView>(R.id.voice_message_view)

//        swipeBtnDisabled.setDisabledStateNotAnimated()
//        swipeBtnEnabled.setEnabledStateNotAnimated()
        val swipeNoState  = findViewById<SwipeButton>(R.id.swipeNoState)
        swipeNoState.setOnActiveListener { Toast.makeText(this@MainActivity, "Active!", Toast.LENGTH_SHORT).show() }

        val  toggleBtn  = findViewById<AppCompatButton>(R.id.toggleBtn)
        toggleBtn.setOnClickListener {
            if (!swipeBtnEnabled.isActive) {
                swipeBtnEnabled.toggleState()
                swipeBtnEnabled.isActive
            }
            if(voiceCommentPlayer?.visibility == View.VISIBLE) {
                voiceCommentPlayer.stop()
            }
        }

        commentInput.apply {
            listener = object : CommentInputView.CommentInputEvents {
                override fun onStart() {
                    LOG.debug("onStart!!!")
                }

                override fun onStop() {
                    LOG.debug("onStop!!!")
                    voiceCommentPlayer.visibility = View.VISIBLE
                }

                override fun onCancel() {
                    LOG.debug("onCancel!!!")
                }

                override fun onLock() {
                    LOG.debug("onLock!!!")
                }
            }
        }

        voiceCommentPlayer.apply {
            listener = object : VoiceCommentPlayerView.VoiceCommentPlayerEvents {
                override fun onStart() {
                    LOG.debug("VoiceCommentContent: onStart")
                }

                override fun onPause() {
                    LOG.debug("VoiceCommentContent: onPause")
                }

                override fun onResume() {
                    LOG.debug("VoiceCommentContent: onResume")
                }

                override fun onDelete() {
                    LOG.debug("VoiceCommentContent: onDelete")
                    this@apply.visibility = View.GONE
                }

            }
        }

    }

}
