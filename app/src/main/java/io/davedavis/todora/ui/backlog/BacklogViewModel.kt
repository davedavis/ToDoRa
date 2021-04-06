package io.davedavis.todora.ui.backlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BacklogViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Backlog Fragment"
    }
    val text: LiveData<String> = _text
}