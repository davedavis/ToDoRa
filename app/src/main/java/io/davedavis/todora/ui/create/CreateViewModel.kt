package io.davedavis.todora.ui.create

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.davedavis.todora.model.*
import io.davedavis.todora.network.Auth
import io.davedavis.todora.network.JiraApi
import io.davedavis.todora.network.JiraApiService
import io.davedavis.todora.utils.SharedPreferencesManager
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber

/**
 * ViewModel class that holds all the data and logic for the create issue flow.
 */
class CreateViewModel : ViewModel() {

    // Response from the API as a string as it's only in English so can't be localized.
    var responseMessage: String = "No response yet"

    // The internal MutableLiveData for the selected issue
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

    /**
     * When the description is changed, set the livedata of [newSummary] to the updated value
     * so when there's a lifecycle change, the user won't lose any input.
     */
    fun onSummaryTextChange(newSummary: Editable?) {
        _newIssue.value?.fields?.summary = newSummary.toString()
        if (!_newIssue.value?.fields?.description?.content?.get(0)?.content?.get(0)?.actualDescriptionText.isNullOrEmpty()) {
            _issueValid.value = true
        }

    }

    /**
     * When the description is changed, set the livedata of [newDescription] to the updated value
     * so when there's a lifecycle change, the user won't lose any input.
     */
    fun onDescriptionTextChange(newDescription: Editable?) {
        _newIssue.value?.fields?.description?.content?.get(0)?.content?.get(0)?.actualDescriptionText =
            newDescription.toString()
        if (!_newIssue.value?.fields?.summary.isNullOrEmpty()) {
            _issueValid.value = true
        }

    }

    /**
     * When the issue/to-do is updated, set the [priority] [MutableLiveData]
     * @param updatedPriority to the updated one in the view.
     */
    fun updatePriority(updatedPriority: Int) {
        _newIssue.value?.fields?.priority?.name =
            PriorityOptions.values()[updatedPriority].toString()

    }

    /**
     * Submits new issue to the [JiraApiService] by calling [submitNewJiraIssue] through a coroutine.
     * Returns a string response.
     */
    fun submitNewJiraIssue() {
        Timber.i(">>> updateJiraIssue in ViewModel Called")
        viewModelScope.launch {
            _status.value = JiraAPIStatus.LOADING
            try {
                Timber.i(">>> Trying ...")
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
