package io.davedavis.todora.ui.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.davedavis.todora.network.JiraIssue

class EditViewModel(
    jiraIssueId: String,
    jiraIssueSummary: String,
    jiraIssueDescription: String,
    jiraIssuePriority: String,
    jiraIssueStatus: String,
    jiraIssueTimeSpent: Long,
    app: Application
) : AndroidViewModel(app) {

    // The internal MutableLiveData for the selected property
    private val _selectedProperty = MutableLiveData<JiraIssue>()
    val selectedProperty: LiveData<JiraIssue>
        get() = _selectedProperty

    // Initialize the _selectedProperty MutableLiveData
    init {
//        _selectedProperty.value = jiraIssue
    }


    val issueSummary = jiraIssueSummary
    val issueDescription = jiraIssueDescription

}
