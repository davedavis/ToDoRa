package io.davedavis.todora.ui.debug

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


// This factory is used to pass parameters/objects in to my view models.
// Shared preferences in view models need to be accessed this way.

@SuppressWarnings("unchecked")
class DebugViewModelFactory(private val prefs: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DebugViewModel::class.java)) {
            return DebugViewModel(prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}