package io.davedavis.todora.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TimeLogDatabaseDAO {

    @Insert
    fun insert(timelog: TimeLog): Long

    @Update
    fun update(timelog: TimeLog)

    @Query(value = "SELECT * FROM timelog_table WHERE issue_key = :issueKey ")
    fun getPendingTimeLogs(issueKey: String): TimeLog


    // Get all TimeLogs as LiveData so we can filter on a single issue and observe a single set.
    @Query(value = "SELECT * FROM timelog_table WHERE issue_key = :issueKey ")
    fun getAllIssueTimeLogs(issueKey: String): List<TimeLog>


    /**
     * Selects and returns the latest timelog for a specific issue.
     */
    @Query("SELECT * FROM timelog_table ORDER BY logId DESC LIMIT 1")
    suspend fun getCurrentTimeLog(): TimeLog?

//    @Query(value = "SELECT * FROM timelog_table WHERE issue_key = :issueKey ")
//    fun getAllIssueTimeLogs(issueKey: String): LiveData<List<TimeLog>>

    // Get all TimeLogs as LiveData so we only have to load it once (V2 "Submit all pending" feature)
    @Query("SELECT * FROM timelog_table ")
    fun getAllTimeLogs(): LiveData<List<TimeLog>>


    // For deleting a group of TimeLogs once submitted, or to remove one added in error.
    @Delete
    fun deleteIndividualIssueTimeLogs(timeLogs: List<TimeLog>): Int


    // Util query for clearing the DB completely.
    @Query("DELETE FROM timelog_table")
    fun clear()


}