package io.davedavis.todora.ui.create

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.davedavis.todora.model.*
import io.davedavis.todora.network.Auth
import io.davedavis.todora.network.JiraApi
import io.davedavis.todora.utils.SharedPreferencesManager
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber

class CreateViewModel : ViewModel() {

    var responseMessage: String = "No response yet"

    // The internal MutableLiveData for the selected property
    private val _newIssue = MutableLiveData<NewIssue>()
    val newIssue: LiveData<NewIssue>
        get() = _newIssue

    // The internal MutableLiveData that stores the status of the API request
    private val _status = MutableLiveData<JiraAPIStatus>()
    val status: LiveData<JiraAPIStatus>
        get() = _status

    // The internal MutableLiveData that stores the status of the most recent request
    private val _priority = MutableLiveData<PriorityOptions>()
    val priority: LiveData<PriorityOptions>
        get() = _priority

    // Is it ready to submit?
    private val _issueValid = MutableLiveData<Boolean>()
    val issueValid: LiveData<Boolean>
        get() = _issueValid


    // Initialize the _selectedIssue MutableLiveData so that the view can populate the edit fields.
    init {
        _priority.value = PriorityOptions.Medium
        _issueValid.value = false
        _newIssue.value =
            NewIssue(
                NewFields(
                    "",
                    IssueType(),
                    Project(SharedPreferencesManager.getUserProject()),
                    NewPriority(),
                    Description(
                        "doc", 1, listOf(Content("paragraph", listOf(ContentDescription(""))))

                    )
                )
            )

    }


    // Set up the edit fields on changed listeners (Bound in the XML)
    // Thanks https://stackoverflow.com/questions/33798426/how-to-databind-to-ontextchanged-for-an-edittext-on-android
    // ToDo: Move this to the Binding Adapter Class.
    fun onSummaryTextChange(newSummary: Editable?) {
        _newIssue.value?.fields?.summary = newSummary.toString()
        if (!_newIssue.value?.fields?.description?.content?.get(0)?.content?.get(0)?.actualDescriptionText.isNullOrEmpty()) {
            _issueValid.value = true
        }

    }

    fun onDescriptionTextChange(newDescription: Editable?) {
        _newIssue.value?.fields?.description?.content?.get(0)?.content?.get(0)?.actualDescriptionText =
            newDescription.toString()
        if (!_newIssue.value?.fields?.summary.isNullOrEmpty()) {
            _issueValid.value = true
        }

    }

    fun updatePriority(updatedPriority: Int) {
        _newIssue.value?.fields?.priority?.name =
            PriorityOptions.values()[updatedPriority].toString()

    }


    fun submitNewJiraIssue() {
        Timber.i(">>> updateJiraIssue in ViewModel Called")
        viewModelScope.launch {
            _status.value = JiraAPIStatus.LOADING
            try {
                Timber.i(">>> Trying ...")
                Timber.i(newIssue.value.toString())
                val response: Response<Unit> = JiraApi.retrofitService.newJiraIssue(
                    Auth.getAuthHeaders(),
                    newIssue.value

                )
                // ToDo; Extract all these strings
                when (response.code()) {

                    // API V2
                    200 -> {
                        Timber.i("Request is successful.")

                        responseMessage = "Request is successful. Issue Created"
                        _status.value = JiraAPIStatus.DONE
                    }
                    // API V3
                    201 -> {
                        Timber.i("Request is successful.")

                        responseMessage = "Request is successful. Issue Created"
                        _status.value = JiraAPIStatus.DONE
                    }
                    400 -> {
                        Timber.i("Body missing, missing permissions on some fields or invalid transition")
                        responseMessage =
                            "Body missing, missing permissions on some fields or invalid transition"
                        _status.value = JiraAPIStatus.ERROR
                    }
                    401 -> {
                        Timber.i("Authentication credentials are incorrect or missing.")
                        responseMessage = "Authentication credentials are incorrect or missing."
                        _status.value = JiraAPIStatus.ERROR
                    }
                    403 -> {
                        Timber.i("No Permission to override security screen or hidden fields")
                        responseMessage =
                            "No Permission to override security screen or hidden fields"
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
                responseMessage =
                    "I don't know how to handle this error. Please report to owner : $e"
                _status.value = JiraAPIStatus.ERROR
            }
        }
    }

}
