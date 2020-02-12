package ru.smartro.worknote.ui.references.index

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReferencesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dictonaries Fragment"
    }
    val text: LiveData<String> = _text
}