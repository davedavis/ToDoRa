package io.davedavis.todora.ui.settings

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import io.davedavis.todora.R

/**
 * Creates an instance of the PreferenceFragmentCompat class to auto save settings in shared
 * preferences. Validates email and provides hints in a listener when editing each item.
 */
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)


        // ToDo: Finish validation
        // Let's do some validation. First, let's grab the fields.
        val userNameField = preferenceScreen.findPreference<EditTextPreference>("user_name")
        val userEmailField = preferenceScreen.findPreference<EditTextPreference>("user_email")
        val userProjectUrlField = preferenceScreen.findPreference<EditTextPreference>("user_project_url")
        val userProjectIdField = preferenceScreen.findPreference<EditTextPreference>("user_project_id")
        val userAPIKeyField = preferenceScreen.findPreference<EditTextPreference>("user_api_key")

        // Now some specific length and format validations custom to each field.
        userEmailField?.setOnPreferenceChangeListener { preference, newValue ->
            // Check that it's a valid email address using the built in android patterns class.
            if (Patterns.EMAIL_ADDRESS.matcher(newValue as CharSequence).matches()) {
                Toast.makeText(
                    context,
                    "Valid Email. Setting updated.",
                    Toast.LENGTH_SHORT
                ).show()
                // If it's valid, return true (update the setting)
                true
            } else {
                Toast.makeText(
                    context,
                    "Invalid Email Address Entered. Setting not updated. Please try again",
                    Toast.LENGTH_LONG
                ).show()
                false
            }
        } // End userEmail Validation changelistener.

        userNameField?.setOnBindEditTextListener { editText -> editText.hint = "Your name. " }
        userEmailField?.setOnBindEditTextListener { editText ->
            editText.hint = "Your Jira Login Email"
        }
        userProjectUrlField?.setOnBindEditTextListener { editText ->
            editText.hint =
                "Your Jira subdomain before the dot. E.g: The 'davedavis' in  https://davedavis.atlassian.net"
        }
        userProjectIdField?.setOnBindEditTextListener { editText ->
            editText.hint = "Your project key where you want todos posted. E.g: TODORA"
        }
        userAPIKeyField?.setOnBindEditTextListener { editText ->
            editText.hint = "Your Jira API Key"
        }


    } // End onCreatePreferences

    // Basic validation
    // https://stackoverflow.com/questions/61628130/url-and-email-validation-api-in-kotlin

    // Refresh
    // https://stackoverflow.com/questions/11903346/refresh-android-preferencefragment/11903467


}
