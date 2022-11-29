package ru.smartro.worknote.abs

import android.os.Bundle
import ru.smartro.worknote.andPOintD.SmartROllc

interface FAI {
    fun onGetLayout(): Int
    fun onInitLayoutView(sview: SmartROllc): Boolean
    fun onLiveData()
    fun onBindLayoutState(): Boolean
    fun onBackPressed()
    fun getAct(): AAct
    fun getArgSBundle(argumentId: Int, argumentName: String ?= null): Bundle
}