package ru.smartro.worknote.presentation.ac

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import ru.smartro.worknote.App
import ru.smartro.worknote.R
import ru.smartro.worknote.work.swipebtn.SwipeButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val  swipeBtnEnabled  = findViewById<SwipeButton>(R.id.swipeBtnEnabled)
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
        val  swipeNoState  = findViewById<SwipeButton>(R.id.swipeNoState)
        swipeNoState.setOnActiveListener { Toast.makeText(this@MainActivity, "Active!", Toast.LENGTH_SHORT).show() }
        val  toggleBtn  = findViewById<AppCompatButton>(R.id.toggleBtn)
        toggleBtn.setOnClickListener {
            if (!swipeBtnEnabled.isActive) {
                swipeBtnEnabled.toggleState()
                swipeBtnEnabled.isActive
            }
        }

    }

}
