package ru.smartro.worknote.abs

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import org.slf4j.LoggerFactory
import ru.smartro.worknote.LoG
import ru.smartro.worknote.log

abstract class AbstractDialog : DialogFragment() {

    init {
        LoG.info( "init AbstractDialog")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("onViewCreated")
    }
}