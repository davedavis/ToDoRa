package io.davedavis.todora.network

import android.util.Base64
import io.davedavis.todora.utils.SharedPreferencesManager


// Just my util class using companion objects as static methods so this doesn't have to be done
// in ViewModels.

class Auth {


    // Base64 Encode Auth Parameter Payload
    // https://developer.android.com/reference/kotlin/android/util/Base64
    companion object {
        fun getAuthHeaders() : String {
            val jiraApiKey: String? = SharedPreferencesManager.getUserApiKey()
            val jiraLogin: String? = SharedPreferencesManager.getUserLogin()
            val decodedAuth = "$jiraLogin:$jiraApiKey"
            val encodedAuth = Base64.encodeToString(decodedAuth.toByteArray(charset("UTF-8")), Base64.NO_WRAP)
            return "Basic " + encodedAuth.trim()
        }
    }



}