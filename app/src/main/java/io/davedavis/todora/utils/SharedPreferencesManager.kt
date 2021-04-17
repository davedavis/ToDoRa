package io.davedavis.todora.utils


import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import io.davedavis.todora.R


// Shared preferences manager to let me check for settings from the shared preferences object in
// the retrofit interface definitions and okHTTP interceptor.

// Class defines a globally accessible set of SharedPreference settings once it's initialized
// in the application class. Not an memory leak issue as SharedPreferences doesn't contain a context.
class SharedPreferencesManager {
    companion object {

        lateinit var sharedPreferences: SharedPreferences
        lateinit var applicationContext: Context

        fun init(context: Context) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            applicationContext = context
        }

        fun getUserBaseUrl(): String? {
            return sharedPreferences.getString(
                applicationContext.getString(R.string.prefs_jira_url_key),
                "www"
            )!!
        }

        fun getUserApiKey(): String? {
            return sharedPreferences.getString(
                applicationContext.getString(R.string.prefs_api_key_key),
                "Settings is empty!"
            )!!
        }

        fun getUserLogin(): String? {
            return sharedPreferences.getString(
                applicationContext.getString(R.string.prefs_jira_email),
                "Settings is empty!"
            )!!
        }

        fun getUserProject(): String? {
            return sharedPreferences.getString(
                applicationContext.getString(R.string.prefs_project_id),
                "Settings is empty!"
            )!!
        }
    }
}