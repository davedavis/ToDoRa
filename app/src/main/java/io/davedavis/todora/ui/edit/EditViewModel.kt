package io.davedavis.todora.ui.edit

import android.app.Application
import android.text.Editable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.davedavis.todora.database.TimeLog
import io.davedavis.todora.database.TimeLogDatabaseDAO
import io.davedavis.todora.model.JiraAPIStatus
import io.davedavis.todora.model.ParcelableIssue
import io.davedavis.todora.model.PriorityOptions
import io.davedavis.todora.network.Auth
import io.davedavis.todora.network.JiraApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

// API docs: https://square.github.io/retrofit/2.x/retrofit/retrofit2/Response.html


class EditViewModel(
    val database: TimeLogDatabaseDAO,
    application: Application,
    issueKey: String,
    jiraIssueObject: ParcelableIssue,
) : AndroidViewModel(application) {


    // String to present info to the user
    var responseMessage: String = "No response yet"

    // Set up Room stuff.
    private var timeLogDbJob = Job()

    // Set up scope for coroutines.
//    private val ioScope = CoroutineScope(Dispatchers.IO + timeLogDbJob)

    // The issueKey from args as it's not in the parcelable issue.
    private var key = issueKey

    // This is livedata, room handles it for us.
//    private val unsentTimeLogs = database.getAllIssueTimeLogs(key)


    // Get un-submitted time logs for the issue if there's any.
//    private val selectedIssueTimeLogs = database.getAllIssueTimeLogs(key)

    private val _selectedIssueTimeLogs = MutableLiveData<List<TimeLog>>()
    val selectedIssueTimeLogs: LiveData<List<TimeLog>>
        get() = _selectedIssueTimeLogs


    // LiveData of the Parcelable issue (the summary and description Moshi object)
    private val _selectedIssue = MutableLiveData<ParcelableIssue>()
    val selectedIssue: LiveData<ParcelableIssue>
        get() = _selectedIssue

    // API status livedata
    private val _status = MutableLiveData<JiraAPIStatus>()
    val status: LiveData<JiraAPIStatus>
        get() = _status

    // The priority livedata
    private val _priority = MutableLiveData<PriorityOptions>()
    val priority: LiveData<PriorityOptions>
        get() = _priority


    // Observable to monitor for any changes so that the button can be enabled. We need this because
    // the init block creates a new observable instance that can't be monitored from the view/fragment.
    private val _issueEdited = MutableLiveData<Boolean>()
    val issueEdited: LiveData<Boolean>
        get() = _issueEdited



    // Initialize the _selectedIssue MutableLiveData so that the view can populate the edit fields.
    init {

        _selectedIssue.value = jiraIssueObject
        _priority.value = PriorityOptions.valueOf(jiraIssueObject.fields?.priority?.name.toString())

        // Also init the timeLogs for the issue. Will be null if there's no issues logged (hide TV)
        initTimeLogs()
        Timber.i(
            "DDDDDDDDDDDDD >> selectedIssueTimeLogs: %s",
            selectedIssueTimeLogs.value?.get(0)?.issueKey.toString()
        )

    }


//    private fun initTimeLogs() {
//        ioScope.launch {
//            val testIssue = TimeLog(issueKey = key)
//            database.insert(testIssue)
//            val pendingTimeLogs = database.getAllIssueTimeLogs(key)
//            _selectedIssueTimeLogs.postValue(pendingTimeLogs)
//            Timber.i("DDDDDDDDDDDDD >> pendingTimeLogs : %s", pendingTimeLogs.get(0).issueKey.toString())
//
//
//            // Get all timelogs for the isue.
////            _selectedIssueTimeLogs.value = database.getAllIssueTimeLogs(key)
//        }
//        Timber.i("DDDDDDDDDDDDD >> selectedIssueTimeLogs: %s", selectedIssueTimeLogs.value?.get(0)?.issueKey.toString())
//
//    }


    private fun initTimeLogs() {
        viewModelScope.launch {
            _selectedIssueTimeLogs.value = getUnsentTimeLogsFromDb()
        }
    }

    private suspend fun getUnsentTimeLogsFromDb(): List<TimeLog>? {
        return withContext(Dispatchers.IO) {
            val unsentTimeLogs = database.getAllIssueTimeLogs(key)
            unsentTimeLogs
        }

    }


    fun submitPendingTimeLogs() {
//        Timber.i("DDDDDDDDDDDDD >> %s", selectedIssueTimeLogs.value?.get(0)?.startTime.toString())
//        Timber.i("DDDDDDDDDDDDD >> %s", selectedIssueTimeLogs.value?.get(0)?.endTime.toString())
//        Timber.i("DDDDDDDDDDDDD >> %s", selectedIssueTimeLogs.value?.get(0)?.issueKey.toString())

        for (pendingLog in selectedIssueTimeLogs.value!!)
            Log.i(
                "DDD >> LOOP :",
                pendingLog.issueKey + " " + pendingLog.startTime + " " + pendingLog.endTime
            )


    }


    // Inserts a new TmeLog into the DB with the issue key of the issue we're editing.
    fun onStartTimeLogTracking() {
        viewModelScope.launch {
            insertNewTimeLog(TimeLog(issueKey = key))

        }
    }

    private suspend fun insertNewTimeLog(newLog: TimeLog): Long {
        return withContext(Dispatchers.IO) {
            val insertedTimeLogId = database.insert(newLog)
            insertedTimeLogId
        }

    }


    // Grabs the latest TimeLog created and updates the end_time on it.
    fun onStopTimeLogTracking() {
        viewModelScope.launch {
            database.getCurrentTimeLog()?.let { updateNewTimeLog(it) }

        }
    }


    // Extension function to notify observer when a new timelog is added by the user.
    // As it can't be called from inside the IO coroutine.
    fun <T> MutableLiveData<List<T>>.add(item: T) {
        this.postValue(listOf(item))
    }

    private suspend fun updateNewTimeLog(updatedLog: TimeLog) {
        return withContext(Dispatchers.IO) {
            updatedLog.endTime = System.currentTimeMillis()
            database.update(updatedLog)
            // I'm cheating a bit here as I ran out of time. Updating the livedata manually.
//            _selectedIssueTimeLogs.postValue(listOf(updatedLog))
            _selectedIssueTimeLogs.add(updatedLog)

        }

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
                responseMessage =
                    "I don't know how to handle this error. Please report to owner : $e"
                _status.value = JiraAPIStatus.ERROR
            }
        }
    }


    //ToDo: Share livedata between fragments.
    // https://bladecoder.medium.com/advanced-json-parsing-techniques-using-moshi-and-kotlin-daf56a7b963d


    override fun onCleared() {
        super.onCleared()
        timeLogDbJob.cancel()
    }


}
