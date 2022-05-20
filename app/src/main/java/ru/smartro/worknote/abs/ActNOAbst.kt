package ru.smartro.worknote.abs

import android.os.Bundle
import android.util.Log
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.log.AAct

abstract class ActNOAbst : AAct() {

    protected fun paramS() = paramS
    override fun onNewGPS() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(ActNOAbst)")
// TODO: !r_dos 
//        AppliCation().stopWorkERS()
     
    }

    override fun onPause() {
        super.onPause()
        showingProgress()
    }

    override fun onStop() {
        super.onStop()
        hideProgress()
    }
}