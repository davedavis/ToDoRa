package io.davedavis.todora.model


import android.annotation.SuppressLint
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@SuppressLint("ParcelCreator")
@Parcelize
@JsonClass(generateAdapter = true)
data class ParcelableIssue(
        @Json(name = "fields")
        val fields: Fields?
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
@JsonClass(generateAdapter = true)
data class Fields(
    @Json(name = "summary")
    var summary: String?,
    @Json(name = "description")
    var description: String?,
    @Json(name = "priority")
    val priority: Priority?,
) : Parcelable


@SuppressLint("ParcelCreator")
@Parcelize
@JsonClass(generateAdapter = true)
data class Priority(
        @Json(name = "name")
        var name: String?
) : Parcelable

