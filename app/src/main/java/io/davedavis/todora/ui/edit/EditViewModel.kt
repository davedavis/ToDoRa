package io.davedavis.todora.ui.edit

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.davedavis.todora.model.JiraAPIStatus
import io.davedavis.todora.model.ParcelableIssue
import io.davedavis.todora.model.PriorityOptions
import io.davedavis.todora.network.Auth
import io.davedavis.todora.network.JiraApi
import kotlinx.coroutines.launch
import timber.log.Timber

// API docs: https://square.github.io/retrofit/2.x/retrofit/retrofit2/Response.html


class EditViewModel(
    jiraIssueObject: ParcelableIssue,
) : ViewModel() {

    // The internal MutableLiveData for the selected property
    private val _selectedIssue = MutableLiveData<ParcelableIssue>()
    val selectedIssue: LiveData<ParcelableIssue>
        get() = _selectedIssue

    // The internal MutableLiveData that stores the status of the API request
    private val _status = MutableLiveData<JiraAPIStatus>()
    val status: LiveData<JiraAPIStatus>
        get() = _status

    // The internal MutableLiveData that stores the status of the most recent request
    private val _priority = MutableLiveData<PriorityOptions>()
    val priority: LiveData<PriorityOptions>
        get() = _priority


    // The internal MutableLiveData that stores the status of the API request
    private val _updateResponse = MutableLiveData<okhttp3.Response>()
    val updateResponse: LiveData<okhttp3.Response>
        get() = _updateResponse

    // Initialize the _selectedProperty MutableLiveData
    init {
        _selectedIssue.value = jiraIssueObject
        _priority.value = PriorityOptions.valueOf(jiraIssueObject.fields?.priority?.name.toString())

    }


    // Set up the edit fields on changed listeners (Bound in the XML)
    // Thanks https://stackoverflow.com/questions/33798426/how-to-databind-to-ontextchanged-for-an-edittext-on-android
    // ToDo: Move this to the Binding Adapter Class.
    fun onSummaryTextChange(updatedSummary: Editable?) {
        Log.d("TAG", "New text: ${updatedSummary.toString()}")
        _selectedIssue.value?.fields?.summary = updatedSummary.toString()

    }

    fun updatePriority(updatedPriority: Int) {
        Log.d("PRIORITY TEST", PriorityOptions.values()[updatedPriority].toString())
        _selectedIssue.value?.fields?.priority?.name = PriorityOptions.values()[updatedPriority].toString()

    }

    fun onDescriptionTextChange(updatedDescription: Editable?) {
        Log.d("TAG", "New text: ${updatedDescription.toString()}")
        _selectedIssue.value?.fields?.description = updatedDescription.toString()
    }


    fun updateJiraIssue(jiraIssueKey: String) {
        Timber.i(">>> updateJiraIssue in ViewModel Called")
        viewModelScope.launch {
            _status.value = JiraAPIStatus.LOADING
            try {
                Timber.i(">>> Trying ...")
//                JiraApi.retrofitService.updateJiraIssue(Auth.getAuthHeaders(), selectedIssue.value)
                val rsp = JiraApi.retrofitService.updateJiraIssue(
                        Auth.getAuthHeaders(),
                        selectedIssue.value,
                        jiraIssueKey
                )
                Timber.i(">>> updateResponse %s", rsp.toString())
                _status.value = JiraAPIStatus.DONE
            } catch (e: Exception) {
                _status.value = JiraAPIStatus.ERROR
            }
        }
    }


    //ToDo: Share livedata between fragments.
    // https://bladecoder.medium.com/advanced-json-parsing-techniques-using-moshi-and-kotlin-daf56a7b963d
}
