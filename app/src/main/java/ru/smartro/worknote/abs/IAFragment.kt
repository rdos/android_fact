package ru.smartro.worknote.abs

import ru.smartro.worknote.andPOintD.SmartROllc

interface IAFragment {
    fun onGetLayout(): Int
    fun onInitLayoutView(sview: SmartROllc): Boolean
    fun onNewLiveData()
    fun onBackPressed()
    fun getAct(): AAct

}