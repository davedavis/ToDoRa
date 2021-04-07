package io.davedavis.todora.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.davedavis.todora.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }
}
