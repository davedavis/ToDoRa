package io.davedavis.todora.ui.edit

import android.text.Editable
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
import retrofit2.Response
import timber.log.Timber

// API docs: https://square.github.io/retrofit/2.x/retrofit/retrofit2/Response.html


class EditViewModel(
    jiraIssueObject: ParcelableIssue,
) : ViewModel() {

    var responseMessage: String = "No response yet"

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


    // Observable to monitor for any changes so that the button can be enabled. We need this because
    // the init block creates a new observable instance that can't be monitored from the view/fragment.
    private val _issueEdited = MutableLiveData<Boolean>()
    val issueEdited: LiveData<Boolean>
        get() = _issueEdited

    // ToDo Remove this
    // The internal MutableLiveData that stores the status of the API request
    private val _updateResponse = MutableLiveData<String>()
    val updateResponse: MutableLiveData<String>
        get() = _updateResponse


    // Initialize the _selectedIssue MutableLiveData so that the view can populate the edit fields.
    init {
        _selectedIssue.value = jiraIssueObject
        _priority.value = PriorityOptions.valueOf(jiraIssueObject.fields?.priority?.name.toString())

    }


    // Set up the edit fields on changed listeners (Bound in the XML)
    // Thanks https://stackoverflow.com/questions/33798426/how-to-databind-to-ontextchanged-for-an-edittext-on-android
    // ToDo: Move this to the Binding Adapter Class.
    fun onSummaryTextChange(updatedSummary: Editable?) {
        _selectedIssue.value?.fields?.summary = updatedSummary.toString()
        _issueEdited.value = true
    }

    fun onDescriptionTextChange(updatedDescription: Editable?) {
        _selectedIssue.value?.fields?.description = updatedDescription.toString()
        _issueEdited.value = true
    }

    fun updatePriority(updatedPriority: Int) {
        _selectedIssue.value?.fields?.priority?.name = PriorityOptions.values()[updatedPriority].toString()
        _issueEdited.value = true

    }




    fun updateJiraIssue(jiraIssueKey: String) {
        Timber.i(">>> updateJiraIssue in ViewModel Called")
        viewModelScope.launch {
            _status.value = JiraAPIStatus.LOADING
            try {
                Timber.i(">>> Trying ...")
                val response: Response<Unit> = JiraApi.retrofitService.updateJiraIssue(
                        Auth.getAuthHeaders(),
                        selectedIssue.value,
                        jiraIssueKey
                )
                // Successful response is 204 (no content), handling the other possible responses here.
                // ToDo; Extract all these strings
                when (response.code()) {
                    204 -> {
                        Timber.i("Request is successful.")

                        responseMessage = "Request is successful. Issue Updated"
                        Timber.i("From ViewModel" + updateResponse.value)
                        _status.value = JiraAPIStatus.DONE
                    }
                    400 -> {
                        Timber.i("Body missing, missing permissions on some fields or invalid transition")
                        responseMessage = "Body missing, missing permissions on some fields or invalid transition"
                        _status.value = JiraAPIStatus.ERROR
                    }
                    401 -> {
                        Timber.i("Authentication credentials are incorrect or missing.")
                        responseMessage = "Authentication credentials are incorrect or missing."
                        _status.value = JiraAPIStatus.ERROR
                    }
                    403 -> {
                        Timber.i("No Permission to override security screen or hidden fields")
                        responseMessage = "No Permission to override security screen or hidden fields"
                        _status.value = JiraAPIStatus.ERROR
                    }
                    404 -> {
                        Timber.i("Issue Not Found")
                        responseMessage = "Issue not found. Has it been deleted on the server?."
                        _status.value = JiraAPIStatus.ERROR
                    }
                }
                Timber.i(">>> updateResponse %s", response.toString())

            } catch (e: Exception) {
                Timber.i(">>> Exception %s", e.toString())
                responseMessage = "I don't know how to handle this error. Please report to owner : $e"
                _status.value = JiraAPIStatus.ERROR
            }
        }
    }


    //ToDo: Share livedata between fragments.
    // https://bladecoder.medium.com/advanced-json-parsing-techniques-using-moshi-and-kotlin-daf56a7b963d
}
