//curl -D- \
//-u dave@davedavis.io:MpkWzHiXp3QdnrtdSZdqF38A \
//-X GET \
//-H "Content-Type: application/json" \
//https://davedavis.atlassian.net/rest/api/2/search?jql=project="TODORA"


// API DOCUMENTATION https://developer.atlassian.com/cloud/jira/platform/rest/v3/api-group-issues/#api-rest-api-3-issue-issueidorkey-put

package io.davedavis.todora.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.davedavis.todora.model.NewIssue
import io.davedavis.todora.model.ParcelableIssue
import io.davedavis.todora.model.WorkLog
import io.davedavis.todora.utils.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

enum class JiraApiFilter(val value: String) {
    // ToDo - Dynamically generate this.
    SHOW_BACKLOG("status=\"Backlog\""),
    SHOW_SELECTED_FOR_DEVELOPMENT("status=\"Selected for Development\""),
    SHOW_IN_PROGRESS("status=\"In Progress\""),
    SHOW_DONE("status=\"Done\""),
    SHOW_ALL("") }


// Host interceptor to enable dynamic subdomain substitution as Jira uses subdomains for APU calls.
class HostSelectionInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val jiraSubdomain: String = SharedPreferencesManager.getUserBaseUrl() ?: "www"
        val host = "$jiraSubdomain.atlassian.net"
        val newUrl = request.url.newBuilder()
            .host(host)
            .build()

        request = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(request)
    }

}


//private const val BASE_URL = "https://davedavis.atlassian.net/rest/api/latest/"
private const val BASE_URL = "https://www.atlassian.net/"
//private const val BASE_URL = "http://192.168.1.144/"

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

// Set up interceptor for logging
// https://stackoverflow.com/questions/37105278/httplogginginterceptor-not-logging-with-retrofit-2
// https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
// ToDo: Remove logging after push to production as tokens/paswords will leak.
private val interceptor = run {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.apply {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    }
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .addInterceptor(HostSelectionInterceptor())
    .connectTimeout(5, TimeUnit.SECONDS)
    .writeTimeout(5, TimeUnit.SECONDS)
    .readTimeout(5, TimeUnit.SECONDS)
    .build()

// Possible create the client: https://stackoverflow.com/questions/58038917/unable-to-successfully-send-a-post-request-using-retrofit-2-6-1-problems-with


private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface JiraApiService {

    // For listing issues.
    @GET("rest/api/2/search")
    suspend fun getIssues(
        @Header("Authorization") encodedAuth: String?,
        @Query("jql") type: String
    ): JiraIssueResponse


    // For editing an issue (uses old API)
    @PUT("rest/api/2/issue/{jiraIssueKey}")
    suspend fun updateJiraIssue(
        @Header("Authorization") encodedAuth: String?,
        @Body issue: ParcelableIssue?,
        @Path("jiraIssueKey") jiraIssueKey: String?
    ): retrofit2.Response<Unit>


    // For creating a new issue
    @POST("rest/api/3/issue")
    suspend fun newJiraIssue(
        @Header("Authorization") encodedAuth: String?,
        @Body issue: NewIssue?
    ): retrofit2.Response<Unit>


//    curl --request POST \
//    --url 'https://davedavis.atlassian.net/rest/api/3/issue/TODORA-44/worklog' \
//    --user 'dave@davedavis.io:MpkWzHiXp3QdnrtdSZdqF38A' \
//    --header 'Accept: application/json' \
//    --header 'Content-Type: application/json' \
//    --data '{
//    "timeSpentSeconds": 12000,
//    "started": "2021-04-27T06:55:51.036+0000"
//     }'

    // For submitting the Timelogs
    @POST("rest/api/2/issue/{jiraIssueKey}/worklog")
    suspend fun submitTimeLog(
        @Header("Authorization") encodedAuth: String?,
        @Body workLog: WorkLog?,
        @Path("jiraIssueKey") jiraIssueKey: String?
    ): retrofit2.Response<Unit>

}

// Create a singleton API service as it will only be used by a single user in a single app instance.
object JiraApi {
    val retrofitService: JiraApiService by lazy {
        retrofit.create(JiraApiService::class.java)
    }
}