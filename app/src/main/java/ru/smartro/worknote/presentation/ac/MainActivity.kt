package ru.smartro.worknote.presentation.ac

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.SmartROviewVoicePlayer
import ru.smartro.worknote.andPOintD.SmartROviewVoiceWhatsUp
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

        val voiceCommentPlayer = findViewById<SmartROviewVoicePlayer>(R.id.voice_message_content)
        val commentInput = findViewById<SmartROviewVoiceWhatsUp>(R.id.voice_message_view)


        val  toggleBtn  = findViewById<AppCompatButton>(R.id.toggleBtn)
        toggleBtn.setOnClickListener {
            if (!swipeBtnEnabled.isActive) {
                swipeBtnEnabled.toggleState()
                swipeBtnEnabled.isActive
            }
        }

        commentInput.apply {
//            mCallBack = object : SmartROviewVoiceWhatsUp.CommentInputEvents {
//                override fun onStart() {
//                    if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.RECORD_AUDIO), 101)
//                    } else {
//                        voiceCommentHandler.startRecording()
//                    }
//                }
//
//                override fun onStop() {
//                    voiceCommentHandler.end()
//                }
//
//                override fun onCancel() {
//                    voiceCommentHandler.stop()
//                }
//
//                override fun onLock() {
//                    LOG.debug("onLock!!!")
//                    // TODO::: INCREASE ALLOWED RECORD TIME
//                }
//            }
        }

        voiceCommentPlayer.apply {
            listener = object : SmartROviewVoicePlayer.VoiceCommentPlayerEvents {

                override fun onClickDelete() {
                    LOG.debug("onDelete")
                    this@apply.visibility = View.GONE
                    // TODO::: Remove file??
                }

            }
        }

    }

}
