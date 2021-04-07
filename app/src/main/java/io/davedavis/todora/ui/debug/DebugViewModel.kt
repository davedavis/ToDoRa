package io.davedavis.todora.ui.debug

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DebugViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Debug Fragment"
    }
    val text: LiveData<String> = _text
}