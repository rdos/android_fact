package ru.smartro.worknote.base

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class AbstractBottomDialog : BottomSheetDialogFragment() {
    protected var TAG : String = "--Aaa${this::class.simpleName}"

    init {
        Log.i(TAG, "init AbstractBottomDialog")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

    }
}