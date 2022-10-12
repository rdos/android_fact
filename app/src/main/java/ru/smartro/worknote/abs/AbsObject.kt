package ru.smartro.worknote.abs

import android.util.Log
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.TAG

//todo:!!? this is interface
abstract class AbsObject(val valueName: String?=null) {


    protected fun LOGWork(value: String) {
        val method =  App.getMethodMan()
        method?.let {
            valueName?.let {
                LOG.info("${method}.${valueName}=${value}")
                return@LOGWork
            }
            LOG.info("${method}.valueName=${value}")
            return@LOGWork
        }
        LOG.info("${TAG}:.valueName=${value}")
    }

    interface ABS{
        fun yes()
    }
}
