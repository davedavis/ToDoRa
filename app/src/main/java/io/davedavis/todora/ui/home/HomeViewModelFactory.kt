package io.davedavis.todora.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// This factory is used to pass parameters/objects in to my view models.
// Shared preferences in view models need to be accessed this way.

@SuppressWarnings("unchecked")
class HomeViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}