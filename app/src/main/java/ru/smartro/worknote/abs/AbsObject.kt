package ru.smartro.worknote.abs

import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.TAG

//todo:!!? this is interface
abstract class AbsObject(val tagName: String?=null, val valueName: String?=null) {
    public var TAGObj : String = tagName?:valueName?: "${this::class.simpleName}"

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
