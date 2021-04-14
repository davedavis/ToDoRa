package io.davedavis.todora.model


import android.annotation.SuppressLint
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

//@SuppressLint("ParcelCreator")
//@Parcelize
//@JsonClass(generateAdapter = true)
//data class ParcelableIssue(
//    @Json(name = "summary")
//    val summary: String?,
//    @Json(name = "description")
//    val description: String?,
//    @Json(name = "id")
//    val id: String?,
//    @Json(name = "key")
//    val key: String?,
//    @Json(name = "priority")
//    val priority: String?,
//    @Json(name = "timespent")
//    val timespent: Long?,
//    @Json(name = "self")
//    val self: String?
//
//) : Parcelable


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
        val description: String?,
        @Json(name = "priority")
        val priority: Priority?,


        ) : Parcelable


@SuppressLint("ParcelCreator")
@Parcelize
@JsonClass(generateAdapter = true)
data class Priority(
        @Json(name = "name")
        val name: String?

) : Parcelable

