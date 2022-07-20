package ru.smartro.worknote.presentation.terminate

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel

class TerminateViewModel(application: Application) : BaseViewModel(application) {

    private val _servedCounter: MutableLiveData<Int> = MutableLiveData(0)
    val mServedCounter: LiveData<Int>
        get() = _servedCounter


    fun increaseCounter() {
        _servedCounter.postValue((mServedCounter.value ?: 0) + 1)
    }
}