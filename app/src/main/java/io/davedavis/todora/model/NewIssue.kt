package io.davedavis.todora.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@JsonClass(generateAdapter = true)
data class NewIssue(
    @Json(name = "fields")
    val fields: NewFields?

) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
@JsonClass(generateAdapter = true)
data class NewFields(
    @Json(name = "summary")
    var summary: String? = "",
    @Json(name = "issuetype")
    var issueType: IssueType?,
    @Json(name = "project")
    var project: Project?,
    @Json(name = "priority")
    val priority: NewPriority?,
    @Json(name = "description")
    val description: Description?,


    ) : Parcelable


@SuppressLint("ParcelCreator")
@Parcelize
@JsonClass(generateAdapter = true)
data class IssueType(
    @Json(name = "name")
    var name: String? = "Task"

) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
@JsonClass(generateAdapter = true)
data class Project(
    @Json(name = "key")
    var key: String? = "key"

) : Parcelable


@SuppressLint("ParcelCreator")
@Parcelize
@JsonClass(generateAdapter = true)
data class NewPriority(
    @Json(name = "name")
    var name: String? = "Medium"

) : Parcelable


//        "content": [
//        {
//            "type": "paragraph",
//            "content": [
//            {
//                "text": "Order entry fails when selecting supplier.",
//                "type": "text"
//            }
//            ]
//        }
//        ]
@SuppressLint("ParcelCreator")
@Parcelize
@JsonClass(generateAdapter = true)
data class Description(
    @Json(name = "type")
    var type: String? = "doc",
    @Json(name = "version")
    var version: Int? = 1,
    @Json(name = "content")
    var content: List<Content>
//    @Json(name = "content")
//    var content: List<Map<String, String>>?
//    @Json(name = "content")
//    var content: ArrayList<ArrayList<Map<String, String>>>


) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "type")
    var contentType: String = "paragraph",
    var content: List<ContentDescription>
) : Parcelable


@SuppressLint("ParcelCreator")
@Parcelize
@JsonClass(generateAdapter = true)
data class ContentDescription(
    @Json(name = "text")
    var actualDescriptionText: String = "",
    @Json(name = "type")
    var contentType: String? = "text"
) : Parcelable

