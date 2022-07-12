package ru.smartro.worknote.presentation.checklist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel

class StartWorkOrderViewModel(application: Application) : BaseViewModel(application) {

    private val _workOrderList: MutableLiveData<Int> = MutableLiveData()

//    fun getWorkOrderList(): LiveData<> {
//
//        return mOwnerList
//    }

}