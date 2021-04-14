package io.davedavis.todora.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.davedavis.todora.model.JiraAPIStatus
import io.davedavis.todora.network.*
import kotlinx.coroutines.launch


//enum class JiraAPIStatus { LOADING, ERROR, DONE }

class HomeViewModel() : ViewModel() {


    /**
     * Actual response from the API, a single response object with nested [JiraIssue]
     * objects serialized within. Keeping it separate for future development.
     */
    private val _jiraApiResponse = MutableLiveData<JiraIssueResponse>()
    val jiraApiResponse: LiveData<JiraIssueResponse>
        get() = _jiraApiResponse

    // Actual Issues received from API LiveData
    private val _issues = MutableLiveData<List<JiraIssue>>()
    val issues: MutableLiveData<List<JiraIssue>>
        get() = _issues

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<JiraAPIStatus>()
    val status: LiveData<JiraAPIStatus>
        get() = _status


    // LiveData for navigating to the selected issue edit fragment.
    private val _navigateToSelectedIssue = MutableLiveData<JiraIssue?>()

    val navigateToSelectedIssue: MutableLiveData<JiraIssue?>
        get() = _navigateToSelectedIssue


    /**
     * Call getJiraIssues() on init so we can display issues immediately.
     */
    init {
        getJiraIssues(JiraApiFilter.SHOW_ALL)
    }

    /**
     * Gets filtered Jira Issue information from the Jira API Retrofit service and
     * updates the [JiraIssue] [List] and [JiraAPIStatus] [LiveData]. The Retrofit service
     * returns a coroutine Deferred, which we await to get the result of the transaction.
     * @param filter the [JiraApiFilter] that is sent as part of the web server request
     */
    private fun getJiraIssues(filter: JiraApiFilter) {
        viewModelScope.launch {
            _status.value = JiraAPIStatus.LOADING
            try {
                _jiraApiResponse.value =
                    JiraApi.retrofitService.getIssues(Auth.getAuthHeaders(), filter.value)
                _issues.value = _jiraApiResponse.value!!.issues
                _status.value = JiraAPIStatus.DONE
            } catch (e: Exception) {
                _status.value = JiraAPIStatus.ERROR
                _issues.value = ArrayList()
            }
        }
    }

    /**
     * Updates the data set filter for the web services by querying the data with the new filter
     * by calling [getJiraIssues]
     * @param filter the [JiraApiFilter] that is sent as part of the web server request
     */
    fun updateFilter(filter: JiraApiFilter) {
        getJiraIssues(filter)
    }

    /**
     * When the issue/to-do is clicked, set the [_navigateToSelectedIssue] [MutableLiveData]
     * @param jiraIssue The [JiraIssue] that was clicked on.
     */
    fun displayIssueDetail(jiraIssue: JiraIssue) {
        _navigateToSelectedIssue.value = jiraIssue
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedIssue is set to null
     */
    fun displayIssueDetailComplete() {
        _navigateToSelectedIssue.value = null
    }
}


