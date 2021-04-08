package io.davedavis.todora.ui.backlog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.davedavis.todora.ui.network.JiraApi
import io.davedavis.todora.ui.network.JiraApiFilter
import io.davedavis.todora.ui.network.JiraIssue
import io.davedavis.todora.ui.network.JiraIssueResponse
import kotlinx.coroutines.launch

enum class JiraApiStatus { LOADING, ERROR, DONE }

class BacklogViewModel : ViewModel() {

    private val _status = MutableLiveData<JiraApiStatus>()

    val status: LiveData<JiraApiStatus>
        get() = _status


    private val _issues = MutableLiveData<JiraIssueResponse>()

    val issues: LiveData<JiraIssueResponse>
        get() = _issues

    private val _navigateToSelectedIssue = MutableLiveData<JiraIssueResponse>()
    val navigateToSelectedIssue: LiveData<JiraIssueResponse>
        get() = _navigateToSelectedIssue


    init {
        getJiraIssues(JiraApiFilter.SHOW_ALL)
    }


    private fun getJiraIssues(filter: JiraApiFilter) {
        viewModelScope.launch {
            _status.value = JiraApiStatus.LOADING
            try {
                _issues.value = JiraApi.retrofitService.getIssues(filter.value)
                Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", _issues.toString())
                Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", _issues.value.toString())

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