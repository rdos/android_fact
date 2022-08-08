package ru.smartro.worknote.abs

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import org.slf4j.LoggerFactory

abstract class AbstractDialog : DialogFragment() {
    protected val log = LoggerFactory.getLogger("${this::class.simpleName}")
    protected var TAG : String = "--Aaa${this::class.simpleName}"

    init {
        log.info( "init AbstractDialog")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log.debug("onViewCreated")
    }
}