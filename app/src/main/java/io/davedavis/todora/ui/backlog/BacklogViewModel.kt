package io.davedavis.todora.ui.backlog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.davedavis.todora.ui.network.JiraApi
import io.davedavis.todora.ui.network.JiraApiFilter
import io.davedavis.todora.ui.network.JiraIssue
import kotlinx.coroutines.launch

enum class JiraApiStatus { LOADING, ERROR, DONE }

class BacklogViewModel : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<JiraApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<JiraApiStatus>
        get() = _status


    // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
    // with new values
    private val _properties = MutableLiveData<List<JiraIssue>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val properties: LiveData<List<JiraIssue>>
        get() = _properties

    // LiveData to handle navigation to the selected property
    private val _navigateToSelectedProperty = MutableLiveData<JiraIssue>()
    val navigateToSelectedProperty: LiveData<JiraIssue>
        get() = _navigateToSelectedProperty

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getJiraIssues(JiraApiFilter.SHOW_ALL)
    }


    /**
     * Sets the value of the response LiveData to the Mars API status or the successful number of
     * Mars properties retrieved.
     */

    // Left off here. Receiving back an object, not a list.
    // ToDo: https://stackoverflow.com/questions/45646188/how-can-i-debug-my-retrofit-api-call
    // ToDo: https://stackoverflow.com/questions/62837201/retrofit-error-expected-begin-array-but-was-begin-object-at-path

    private fun getJiraIssues(filter: JiraApiFilter) {
        viewModelScope.launch {
            _status.value = JiraApiStatus.LOADING
            try {
                _properties.value = JiraApi.retrofitService.getIssues(filter.value)
                Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", _properties.value.toString())
                _status.value = JiraApiStatus.DONE
            } catch (e: Exception) {
                _status.value = JiraApiStatus.ERROR
                _properties.value = ArrayList()
                Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", e.toString())

            }
        }
    }

    /**
     * Updates the data set filter for the web services by querying the data with the new filter
     * by calling [getMarsRealEstateProperties]
     * @param filter the [MarsApiFilter] that is sent as part of the web server request
     */
    fun updateFilter(filter: JiraApiFilter) {
        getJiraIssues(filter)
    }

    /**
     * When the property is clicked, set the [_navigateToSelectedProperty] [MutableLiveData]
     * @param marsProperty The [MarsProperty] that was clicked on.
     */
    fun displayPropertyDetails(jiraIssue: JiraIssue) {
        _navigateToSelectedProperty.value = jiraIssue
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayPropertyDetailsComplete() {
        this._navigateToSelectedProperty.value = null
    }
}