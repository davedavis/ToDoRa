package io.davedavis.todora.ui.home

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.davedavis.todora.network.JiraApi
import io.davedavis.todora.network.JiraApiFilter
import io.davedavis.todora.network.JiraIssueResponse
import io.davedavis.todora.ui.home.AllApiStatus.*
import kotlinx.coroutines.launch

enum class AllApiStatus { LOADING, ERROR, DONE }

class HomeViewModel (prefs: SharedPreferences) : ViewModel() {


    // Status LiveData
    private val _status = MutableLiveData<AllApiStatus>()
    val status: LiveData<AllApiStatus>
        get() = _status

    // Actual Issues received from API LiveData
    private val _issues = MutableLiveData<JiraIssueResponse>()
    val issues: LiveData<JiraIssueResponse>
        get() = _issues


    private val _text = MutableLiveData<String>().apply {
        value = "This is the Home Fragment"
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

    // Immediate init call to the APU
    init {
        getJiraIssues(JiraApiFilter.SHOW_ALL)
    }

    private fun getJiraIssues(filter: JiraApiFilter) {
        viewModelScope.launch {
            _status.value = LOADING
            try {
                _issues.value = JiraApi.retrofitService.getIssues(filter.value)
                Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", _issues.value.toString())
                _status.value = DONE
            } catch (e: Exception) {
                Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", e.toString())
                _status.value = ERROR

            }
        }
    }



}