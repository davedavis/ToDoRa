package io.davedavis.todora.ui.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.davedavis.todora.model.ParcelableIssue
import io.davedavis.todora.network.JiraIssue

class EditViewModel(
    jiraIssueId: String,
    jiraIssueSummary: String,
    jiraIssueDescription: String,
    jiraIssuePriority: String,
    jiraIssueTimeSpent: Long,
    jiraIssueObject: ParcelableIssue,
    app: Application
) : AndroidViewModel(app) {

    // The internal MutableLiveData for the selected property
    private val _selectedProperty = MutableLiveData<JiraIssue>()
    val selectedProperty: LiveData<JiraIssue>
        get() = _selectedProperty

    // Initialize the _selectedProperty MutableLiveData
//    init {
////        _selectedProperty.value = jiraIssue
//    }


    val issueSummary = jiraIssueSummary
    val issueDescription = jiraIssueDescription
    val issuePriority = jiraIssuePriority
    val issueTimeSpent = jiraIssueTimeSpent.toString()


    //ToDo: Share livedata between fragments.
    // https://bladecoder.medium.com/advanced-json-parsing-techniques-using-moshi-and-kotlin-daf56a7b963d
}
