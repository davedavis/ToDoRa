package io.davedavis.todora.ui.backlog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.davedavis.todora.network.*
import kotlinx.coroutines.launch

enum class JiraApiStatus { LOADING, ERROR, DONE }

class BacklogViewModel : ViewModel() {

    // Status LiveData
    private val _status = MutableLiveData<JiraApiStatus>()
    val status: LiveData<JiraApiStatus>
        get() = _status

    // Actual Issues received from API LiveData
    private val _jiraApiResponse = MutableLiveData<JiraIssueResponse>()
    val jiraApiResponse: LiveData<JiraIssueResponse>
        get() = _jiraApiResponse

    // Actual Issues received from API LiveData
    private val _issues = MutableLiveData<List<JiraIssue>>()
    val issues: MutableLiveData<List<JiraIssue>>
        get() = _issues

    // Navigation LiveData
    private val _navigateToSelectedIssue = MutableLiveData<JiraIssueResponse>()
    val navigateToSelectedIssue: LiveData<JiraIssueResponse>
        get() = _navigateToSelectedIssue

    // Immediate init call to the API
    init {
        getJiraIssues(JiraApiFilter.SHOW_BACKLOG)
    }


    private fun getJiraIssues(filter: JiraApiFilter) {
        viewModelScope.launch {
            _status.value = JiraApiStatus.LOADING
            try {
                _jiraApiResponse.value =
                    JiraApi.retrofitService.getIssues(Auth.getAuthHeaders(), filter.value)
                _issues.value = _jiraApiResponse.value!!.issues

                Log.i(">>>>>>>>>>>>>>", _jiraApiResponse.value.toString())
                Log.i(">>>>>>>>>>>>>>", _issues.value.toString())
                _status.value = JiraApiStatus.DONE
            } catch (e: Exception) {
                _status.value = JiraApiStatus.ERROR

            }
        }
    }


    fun updateFilter(filter: JiraApiFilter) {
        getJiraIssues(filter)
    }

    fun displayIssueDetails(jiraIssue: JiraIssue) {
//        _navigateToSelectedIssue.value = jiraIssue
    }


    fun displayIssueDetailsComplete() {
        this._navigateToSelectedIssue.value = null
    }
}