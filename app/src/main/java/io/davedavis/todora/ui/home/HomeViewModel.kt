package io.davedavis.todora.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.davedavis.todora.model.JiraAPIStatus
import io.davedavis.todora.network.*
import kotlinx.coroutines.launch
import timber.log.Timber


class HomeViewModel() : ViewModel() {


    /**
     * Actual response from the API, a single response object with nested [JiraIssue]
     * objects serialized within. Keeping it separate for future development.
     */
    private val _jiraApiResponse = MutableLiveData<JiraIssueResponse>()
    val jiraApiResponse: LiveData<JiraIssueResponse>
        get() = _jiraApiResponse

    /**
     * LiveData list of [JiraIssue] from the [JiraIssueResponse]. This is a list of the
     * individual issues as Moshi objects.
     */
    private val _issues = MutableLiveData<List<JiraIssue>>()
    val issues: MutableLiveData<List<JiraIssue>>
        get() = _issues

    /**
     * LiveData of the ENUM [JiraAPIStatus] which is used for user feedback during
     * network operations.
     */
    private val _status = MutableLiveData<JiraAPIStatus>()
    val status: LiveData<JiraAPIStatus>
        get() = _status


    /**
     * LiveData of the user clicked/chosen issue, used for navigation to the selected
     * issue in it's click handler.
     */
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
     * updates the [JiraIssue] [List] and [JiraAPIStatus] [LiveData]. We use Retrofit
     * with coroutines so we need to handle the status updates while the network activity
     * is taking place, or indeed if there's an error.
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
     * Updates the issue filter for the Jira API by querying the data with the new filter
     * by calling [getJiraIssues]
     * @param filter the [JiraApiFilter] that goes along with the get request (there's only
     * one handler in the [JiraApiService]
     */
    fun updateFilter(filter: JiraApiFilter) {
        Timber.i(">>>>> UpdateFilter Called!")
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
     * After the nav has happened (user clicked), we need to be sure navigateToSelectedIssue
     * is set to null so we don't get stuck in a nav loop.
     */
    fun displayIssueDetailComplete() {
        _navigateToSelectedIssue.value = null
    }
}


