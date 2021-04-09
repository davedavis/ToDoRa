package io.davedavis.todora

import android.app.Application
import io.davedavis.todora.utils.SharedPreferencesManager
import timber.log.Timber

class TodoraApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        SharedPreferencesManager.init(this)

    }
}