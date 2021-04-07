package io.davedavis.todora.ui.debug

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


// Receives shared preferences instance from the ViewModelFactory
class DebugViewModel (prefs: SharedPreferences) : ViewModel() {

    init {
        Log.i("Dave", "Received Preferences object from Factory ${prefs.toString()}")
    }


    private val _text = MutableLiveData<String>().apply {
        value = "This is the Debug Fragment"
    }
    val text: LiveData<String> = _text


    private val _userNameTVText = MutableLiveData<String>().apply {
        value = prefs.getString("user_name", "No Name Found")
    }
    val userNameTVText: LiveData<String> = _userNameTVText


    private val _userEmailTVText = MutableLiveData<String>().apply {
        value = prefs.getString("user_email", "No Email Found")
    }
    val userEmailTVText: LiveData<String> = _userEmailTVText

    private val _userURLTVText = MutableLiveData<String>().apply {
        value = prefs.getString("user_project_url", "No Project URL Found")
    }
    val userURLTVText: LiveData<String> = _userURLTVText

    private val _userProjectIdTVText = MutableLiveData<String>().apply {
        value = prefs.getString("user_project_id", "No Project ID Found")
    }
    val userProjectIdTVText: LiveData<String> = _userProjectIdTVText


    private val _userAPIKeyTVText = MutableLiveData<String>().apply {
        value = prefs.getString("user_api_key", "No API Key Found")
    }
    val userAPIKeyTVText: LiveData<String> = _userAPIKeyTVText


}