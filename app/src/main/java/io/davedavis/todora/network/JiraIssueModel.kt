package io.davedavis.todora.network

import com.squareup.moshi.Json



// Model for the response object.
data class JiraIssueResponse(
    val expand: String?,
    val startAt: Int,
    val maxResults: Int,
    val total: Int,
    // Not declaring this as a field as we want an object with an iterator for the RecyclerView.
    val issues: List<JiraIssue>
)


// The nested Issue objects.
// @field annotation used as Jira has some pretty deep nesting and this is how Moshi knows
// how to create objects from the nested objects.
data class JiraIssue (
        val expand: String,
        val id: String,
        val self: String,
        val key: String,
        @field:Json (name = "fields") val fields: Fields
)

// Each issue has fields with the data we want. Each field also has nested fields. Like priority.
data class Fields(
    val summary: String?,
    val description: String?,
    val created: String?,
    @field:Json(name = "timespent") var timespent: Long?,
    @field:Json(name = "status") val status: Status,
    @field:Json(name = "priority") val priority: Priority

)

data class Priority (
        val self : String?,
        val iconUrl : String?,
        val name : String?,
        val id : Int?
)


data class Status (

    val self : String,
    val description : String?,
    val iconUrl : String?,
    val name : String,
    val id : Int,
    @field:Json(name = "statusCategory") val statusCategory: StatusCategory
)

data class StatusCategory(

    val self: String,
    val id: Int,
    val key: String,
    val colorName: String,
    val name: String
)
