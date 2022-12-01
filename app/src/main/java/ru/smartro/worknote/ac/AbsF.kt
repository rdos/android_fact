package ru.smartro.worknote.ac

import android.os.Bundle
import android.view.View
import ru.smartro.worknote.abs.AF
import ru.smartro.worknote.abs.AUF


abstract class AbsF : AF(){
    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todo: wtf??:(
        AUF.onViewCreated(this, view, savedInstanceState)
//        todo:end!:)
        //        onCreate()
    }

}
