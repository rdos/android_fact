package ru.smartro.worknote.abs

import android.os.Bundle
import android.view.View


abstract class AbsFragment : FragmentA(){
    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todo: wtf??:(
        AUF.onViewCreated(this, view, savedInstanceState)
//        todo:end!:)
        //        onCreate()
    }

}
