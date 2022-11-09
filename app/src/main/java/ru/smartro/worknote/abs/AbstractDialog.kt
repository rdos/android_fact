package ru.smartro.worknote.abs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import ru.smartro.worknote.LOG
import ru.smartro.worknote.LOG

abstract class AbstractDialog : DialogFragment() {

    init {
        LOG.info( "init AbstractDialog")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LOG.debug("onViewCreated")
    }
}