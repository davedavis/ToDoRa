package io.davedavis.todora.ui.edit

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.davedavis.todora.model.ParcelableIssue

/**
 * I'm constructing a viewModel here with all the issue parameters passed in individually, instead
 * of an object as I'm finding it difficult/impossible to parcelize such a complex nested object.
 */
class EditViewModelFactory(
    private val jiraIssueId: String,
    private val jiraIssueSummary: String,
    private val jiraIssueDescription: String,
    private val jiraIssuePriority: String,
    private val jiraIssueTimeSpent: Long,
    private val parcelableIssue: ParcelableIssue,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditViewModel::class.java)) {
            return EditViewModel(
                jiraIssueId, jiraIssueSummary, jiraIssueDescription,
                jiraIssuePriority, jiraIssueTimeSpent, parcelableIssue, application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

