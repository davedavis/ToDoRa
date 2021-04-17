package io.davedavis.todora.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// I'm only using a single table as I'm not storing the issues themselves in a DB, just the timelogs
// which a user should be able to record offline.

@Entity(tableName = "timelog_table")
data class TimeLog(
    @PrimaryKey(autoGenerate = true)
    var logId: Long = 0L,

    @ColumnInfo(name = "start_time")
    val startTime: Long = System.currentTimeMillis(),

    // Set this as start time initially so we know if there's a difference and if time was logged
    @ColumnInfo(name = "end_time")
    val endTime: Long = startTime,

    @ColumnInfo(name = "issue_key")
    val issueKey: String = ""
)