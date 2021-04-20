package io.davedavis.todora.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class CreateViewModelFactory :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateViewModel::class.java)) {
            return CreateViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}