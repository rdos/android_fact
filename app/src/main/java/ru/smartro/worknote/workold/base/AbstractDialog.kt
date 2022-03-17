package ru.smartro.worknote.workold.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment

abstract class AbstractDialog : DialogFragment() {
    protected var TAG : String = "--Aaa${this::class.simpleName}"

    init {
        Log.i(TAG, "init AbstractDialog")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
    }
}