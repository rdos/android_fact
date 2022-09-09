package ru.smartro.worknote.ui.platform_serve

import android.view.View
import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import android.view.MotionEvent

import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatButton
import ru.smartro.worknote.R

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible


class CustomBtn : FrameLayout, View.OnTouchListener, View.OnDragListener {
    private lateinit var btn: AppCompatButton
    private var xx = 0f
    private var yy = 0f
    private lateinit var tv: AppCompatTextView
    var sDown: String? = null
    var sMove: String? = null
    var sUp: String? = null
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    private fun initView() {
        inflate(context, R.layout.custom_btn, this)
        btn = findViewById<AppCompatButton>(R.id.acb_custom_btn)
        tv = findViewById(R.id.atv_custom_btn)
        btn.setOnTouchListener(this)
//        btn.setOnDragListener(this)
    }

    private fun moveLinear(xx: Float, yy: Float) {
        tv.x = xx
    }


    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        xx = event.x
        yy = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                sDown = "Down: $xx,   $yy"
                sMove = ""
                sUp = ""
                btn.isVisible = false
            }
            MotionEvent.ACTION_MOVE -> {
                sMove = "Move: $xx,  $yy"
                moveLinear(xx, yy)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                sMove = ""
                sUp = "Up: $xx,  $yy"
                btn.isVisible = true
            }
        }
        tv.setText(sDown.toString() + "\n" + sMove + "\n" + sUp)
        return true
    }

    override fun onDrag(v: View?, event: DragEvent?): Boolean {

       return false
    }
}
