//curl -D- \
//-u dave@davedavis.io:MpkWzHiXp3QdnrtdSZdqF38A \
//-X GET \
//-H "Content-Type: application/json" \
//https://davedavis.atlassian.net/rest/api/2/search?jql=project="TODORA"

package io.davedavis.todora.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
        val jiraSubdomain: String? = SharedPreferencesManager.getUserBaseUrl()
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
private const val BASE_URL = "https://davedavis.atlassian.net/rest/api/latest/"
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
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()




private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface JiraApiService {
    //    @Headers("Authorization: Basic ZGF2ZUBkYXZlZGF2aXMuaW86TXBrV3pIaVhwM1FkbnJ0ZFNaZHFGMzhB")
    @GET("search")
    suspend fun getIssues(@Header("Authorization") encodedAuth: String?, @Query("jql") type: String): JiraIssueResponse

    // curl -D-    -u dave@davedavis.io:MpkWzHiXp3QdnrtdSZdqF38A    -X PUT    -H "Content-Type: application/json"
    // https://davedavis.atlassian.net/rest/api/2/issue/TODORA-10 --data '{ "fields": {"summary": "new summary"} }'

    @PUT("issue")
//    suspend fun updateJiraIssue(@Header("Authorization") encodedAuth: String?, @Body issue : JiraIssue): Response
    fun updateJiraIssue(@Header("Authorization") encodedAuth: String?, @Body issue: JiraIssue): Response


}

//interface JiraApiService {
//    @Headers("Authorization: Basic ZGF2ZUBkYXZlZGF2aXMuaW86TXBrV3pIaVhwM1FkbnJ0ZFNaZHFGMzhB")
//    @GET("search")
//    suspend fun getIssues(@Query("jql") type: String): JiraIssueResponse
//}

//Passing parameters: https://stackoverflow.com/questions/54338671/send-a-parameter-to-kotlin-retrofit-call
//
//interface JiraApiService {
//    @Headers("Authorization: Basic ZGF2ZUBkYXZlZGF2aXMuaW86TXBrV3pIaVhwM1FkbnJ0ZFNaZHFGMzhB")
//    @GET()
//    suspend fun getIssues(@Url url: String?, @Query("jql\"=\"") type: String): JiraIssueResponse
//}

// Create a singleton API service as it will only be used by a single user in a single app instance.
object JiraApi {
    val retrofitService: JiraApiService by lazy {
        retrofit.create(JiraApiService::class.java)
    }
}