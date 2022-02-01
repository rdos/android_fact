package ru.smartro.worknote.base

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.sentry.Sentry

abstract class AbstractBottomDialog : BottomSheetDialogFragment() {
    protected var TAG : String = "${this::class.simpleName}"

    protected fun logSentry(text: String) {
        Sentry.addBreadcrumb("${TAG} : $text")
        Log.i(TAG, "onCreate")
    }

    init {
        Log.i(TAG, "init AbstractBottomDialog")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

    }
}