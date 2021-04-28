package io.davedavis.todora.model


import android.annotation.SuppressLint
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


/**
 * Object that represents a Jira worklog. Time spent in seconds for the entry and the start time
 * as a sting representation in the format "2021-04-27T06:55:51.036+0000"
 */
@SuppressLint("ParcelCreator")
@Parcelize
@JsonClass(generateAdapter = true)
data class WorkLog(
    @Json(name = "timeSpentSeconds")
    var timeSpentSeconds: Int? = 0,

    @Json(name = "started")
    var started: String? = "",

    ) : Parcelable

