package ru.smartro.worknote.abs

import android.os.Bundle
import android.view.View
import ru.smartro.worknote.andPOintD.ANOFragment


abstract class AFragment : ANOFragment(){
    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todo: wtf??:(
        AUFragment.onViewCreated(this, view, savedInstanceState)
//        todo:end!:)
        //        onCreate()
    }

}
