package ru.smartro.worknote.abs

import ru.smartro.worknote.andPOintD.SmartROllc

interface IAFragment {
    fun getAct(): AAct
    fun onInitLayoutView(sview: SmartROllc): Boolean
    fun onNewLiveData()
    fun onBackPressed()

}