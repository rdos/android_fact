package ru.smartro.worknote.presentation

import android.os.Bundle
import ru.smartro.worknote.log.AAct

class SingleAct: AAct() {
    override fun onNewGPS() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.extras?.apply {
            when(get("DEST")) {
                "PLATFORM_SERVE" -> {

                }
                "TERMINATE" -> {

                }
            }
        }
    }
}