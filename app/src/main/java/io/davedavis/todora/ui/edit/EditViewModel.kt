package io.davedavis.todora.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.davedavis.todora.model.ParcelableIssue
import io.davedavis.todora.network.Auth
import io.davedavis.todora.network.JiraApi
import io.davedavis.todora.ui.home.JiraAPIStatus
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


    // The internal MutableLiveData that stores the status of the API request
    private val _updateResponse = MutableLiveData<okhttp3.Response>()
    val updateresponse: LiveData<okhttp3.Response>
        get() = _updateResponse

    // Initialize the _selectedProperty MutableLiveData
    init {
        _selectedIssue.value = jiraIssueObject
    }


    // ToDo: Update Object when edittext changes.
    // ToDo: CreateMethod to send the object with a put
    // ToDo: Navigate back on success


    fun updateJiraIssue() {
        Timber.i(">>> updateJiraIssue in ViewModel Called")
        viewModelScope.launch {
            _status.value = JiraAPIStatus.LOADING
            try {
                Timber.i(">>> Trying ...")
//                JiraApi.retrofitService.updateJiraIssue(Auth.getAuthHeaders(), selectedIssue.value)
                val rsp = JiraApi.retrofitService.updateJiraIssue(
                    Auth.getAuthHeaders(),
                    selectedIssue.value
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
