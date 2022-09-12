package ru.smartro.worknote.utils

import android.content.Context
import android.graphics.Canvas
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.coordinatorlayout.widget.CoordinatorLayout
import ru.smartro.worknote.R

class VoiceMessageRecordView(context: Context): CoordinatorLayout(context) {

    init {
        inflate(context, R.layout.f_pserve__voice_message_view, this)

    }

}