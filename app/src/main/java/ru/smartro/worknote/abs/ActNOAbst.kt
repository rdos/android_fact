package ru.smartro.worknote.abs

import android.os.Bundle
import android.util.Log
import ru.smartro.worknote.log.AAct

abstract class ActNOAbst : AAct() {
    override fun onNewGPS() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
// TODO: !r_dos 
//        AppliCation().stopWorkERS()
     
    }
}