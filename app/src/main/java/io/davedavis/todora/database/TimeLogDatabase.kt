package io.davedavis.todora.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//This is the class that holds the DB. A holder as a singleton.
//Create an instance of the DB if it doesn't exist, or returns the existing instance.

@Database(entities = [TimeLog::class], version = 1, exportSchema = false)
abstract class TimeLogDatabase : RoomDatabase() {

    // We only have one table and one dao.
    abstract val timeLogDatabaseDAO: TimeLogDatabaseDAO

    // No need to instantiate, this is what makes it a singleton.
    companion object {

        // Volatile keeps things thread safe with the single instance.
        @Volatile
        // provide a way to reference the DB instance.
        private var INSTANCE: TimeLogDatabase? = null

        // Use a database builder.
        fun getInstance(context: Context): TimeLogDatabase {

            // Make sure the DB only gets initialized once. Pass us (doing the calling) into the
            // block so we have access to the context.
            synchronized(this) {

                // For teh smartcast to work at the end of this sync block, this needs to be a var.
                var instance = INSTANCE

                // If the instance is null, there's no DB (new install), so we create it.
                if (instance == null) {
                    // Sweet fluent DB builder API
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TimeLogDatabase::class.java,
                        "timelog_history_database"
                    )
                        // Not going to handle migration for a college project.
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance


                }

                // Smart cast
                return instance


            }
        }
    }
}