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
import io.davedavis.todora.network.JiraIssue
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


    /**
     * String that stores the English only response from the API. Used for
     * providing more detailed error messages to the user.
     */
    var responseMessage: String = "No response yet"


    /**
     * Create a Room Job so we can group everything under it.
     */
    private var timeLogDbJob = Job()


    /**
     * The issueKey from nav args as it's not in the parcelable issue.
     */
    private var key = issueKey


    /**
     * LiveData list of [TimeLog] This is a list of the logs from the database.
     */
    private val _selectedIssueTimeLogs = MutableLiveData<List<TimeLog>>()
    val selectedIssueTimeLogs: LiveData<List<TimeLog>>
        get() = _selectedIssueTimeLogs


    /**
     * LiveData [ParcelableIssue] converted from the  [JiraIssue]. This is a simpler object
     * used to pass around the selected issue from the fragment as the Moshi API cannot convert
     * the Jira API to parcelable so we have to do it ourselves. Only contains the summary,
     * description and key of the object so it can be sent directly when edited using the Retrofit
     * @Put method.
     */
    private val _selectedIssue = MutableLiveData<ParcelableIssue>()
    val selectedIssue: LiveData<ParcelableIssue>
        get() = _selectedIssue

    /**
     * LiveData of the ENUM [JiraAPIStatus] which is used for user feedback during
     * network operations.
     */
    private val _status = MutableLiveData<JiraAPIStatus>()
    val status: LiveData<JiraAPIStatus>
        get() = _status

    /**
     * LiveData of the ENUM [PriorityOptions] which is used to ensure a user doesn't accidentally
     * set a priority that Jira doesn't support.
     */
    private val _priority = MutableLiveData<PriorityOptions>()
    val priority: LiveData<PriorityOptions>
        get() = _priority


    /**
     * LiveData observable to monitor for any changes so that the button can be enabled.
     * We need this because the init block creates a new observable instance that can't be
     * monitored from the view/fragment. So we create our own so we can enable the buttons
     * if the issue has any edits.
     */
    private val _issueEdited = MutableLiveData<Boolean>()
    val issueEdited: LiveData<Boolean>
        get() = _issueEdited


    /**
     * Initialize the _selectedIssue MutableLiveData so that the view can populate the edit fields.
     * Also init the database.
     */
    init {

        _selectedIssue.value = jiraIssueObject
        _priority.value = PriorityOptions.valueOf(jiraIssueObject.fields?.priority?.name.toString())

        // Also init the timeLogs for the issue. Will be null if there's no issues logged (hide TV)
        initTimeLogs()
    }


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
        for (pendingLog in selectedIssueTimeLogs.value!!)
            Log.i(
                "DDD >> LOOP :",
                pendingLog.issueKey + " " + pendingLog.startTime + " " + pendingLog.endTime
            )

        // For now, I'm just going to clear the timelogs to simulate a successful submission.
        onClearDb()


    }


    fun onClearDb() {
        viewModelScope.launch {
            clearDb()
        }
    }

    suspend fun clearDb() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
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
    // Mix of solutions from all here: https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
    fun <T> MutableLiveData<List<T>>.add(item: T) {
        this.postValue(listOf(item))
    }


    // for immutable list
    operator fun <T> MutableLiveData<List<T>>.plusAssign(item: T) {
        val value = this.value ?: emptyList()
        this.postValue(listOf(item))
    }

    private suspend fun updateNewTimeLog(updatedLog: TimeLog) {
        return withContext(Dispatchers.IO) {
            updatedLog.endTime = System.currentTimeMillis()
            database.update(updatedLog)
            // Use the operator extension function (add) above to notify the observer in the fragment of the newly recorded time log.
//            _selectedIssueTimeLogs.add(updatedLog)
            _selectedIssueTimeLogs += updatedLog

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
                        responseMessage = "Request is successful. Issue Updated"
                        _status.value = JiraAPIStatus.DONE
                    }
                    400 -> {
                        responseMessage = "Body missing, missing permissions on some fields or invalid transition"
                        _status.value = JiraAPIStatus.ERROR
                    }
                    401 -> {
                        responseMessage = "Authentication credentials are incorrect or missing."
                        _status.value = JiraAPIStatus.ERROR
                    }
                    403 -> {
                        responseMessage = "No Permission to override security screen or hidden fields"
                        _status.value = JiraAPIStatus.ERROR
                    }
                    404 -> {
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
