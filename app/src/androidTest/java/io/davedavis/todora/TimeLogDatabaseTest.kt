package io.davedavis.todora


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.davedavis.todora.database.TimeLog
import io.davedavis.todora.database.TimeLogDatabase
import io.davedavis.todora.database.TimeLogDatabaseDAO
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


// Just some basic tests (well, one basic test!) to make sure the DB is working!

@RunWith(AndroidJUnit4::class)
//@RunWith(AndroidJUnit4ClassRunner::class)
class TimeLogDatabaseTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    private lateinit var timelogDao: TimeLogDatabaseDAO
    private lateinit var db: TimeLogDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Not clogging up my AVD with actual DB files, so using in-memory.
        db = Room.inMemoryDatabaseBuilder(context, TimeLogDatabase::class.java)
            // Use main thread for test. In code, I'll use Coroutines.
            .allowMainThreadQueries()
            .build()
        timelogDao = db.timeLogDatabaseDAO
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


    // Basic functionality
    @Test
    @Throws(Exception::class)
    fun insertTimeLog() {
        val timelogEntry = TimeLog()
        timelogDao.insert(timelogEntry)
        val allPendingTimeLogEntries = timelogDao.getPendingTimeLogs("")
        assertEquals(timelogEntry.issueKey, "")
        assertEquals(allPendingTimeLogEntries.issueKey, "")

    }

    // LiveData test. Can't figure out how to test liveData.

    @Test
    @Throws(Exception::class)
    fun insertLiveData() {
        val timelogEntry = TimeLog(issueKey = "key1")
        timelogDao.insert(timelogEntry)
        val allTimeLogEntries = timelogDao.getAllIssueTimeLogs("key1")
        assertEquals(allTimeLogEntries.issueKey, "key1")

    }


}