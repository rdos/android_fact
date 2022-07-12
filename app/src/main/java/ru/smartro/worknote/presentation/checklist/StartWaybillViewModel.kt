package ru.smartro.worknote.presentation.checklist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel

class StartWaybillViewModel(application: Application) : BaseViewModel(application) {

    private val _wayBillList: MutableLiveData<Int> = MutableLiveData()

//    fun getWayBillList(): LiveData<> {
//
//        return _wayBillList
//    }


}