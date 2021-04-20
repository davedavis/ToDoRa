package io.davedavis.todora.ui.edit

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.davedavis.todora.database.TimeLogDatabaseDAO
import io.davedavis.todora.model.ParcelableIssue

/**
 * I'm constructing a viewModel here with all the issue parameters passed in individually, instead
 * of an object as I'm finding it difficult/impossible to parcelize such a complex nested object.
 */
class EditViewModelFactory(
    private val datasource: TimeLogDatabaseDAO,
    private val application: Application,
    private val issueKey: String,
    private val parcelableIssue: ParcelableIssue
) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditViewModel::class.java)) {
            return EditViewModel(datasource, application, issueKey, parcelableIssue) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

