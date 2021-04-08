//curl -D- \
//-u dave@davedavis.io:MpkWzHiXp3QdnrtdSZdqF38A \
//-X GET \
//-H "Content-Type: application/json" \
//https://davedavis.atlassian.net/rest/api/2/search?jql=project="TODORA"

package io.davedavis.todora.ui.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

enum class JiraApiFilter(val value: String) {
    SHOW_RENT("rent"),
    SHOW_BUY("buy"),
    SHOW_ALL("all") }

private const val BASE_URL = "https://davedavis.atlassian.net/rest/api/2/"
//private const val BASE_URL = "http://192.168.1.144/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface JiraApiService {
    @Headers("Authorization: Basic ZGF2ZUBkYXZlZGF2aXMuaW86TXBrV3pIaVhwM1FkbnJ0ZFNaZHFGMzhB")
    @GET("search?jql=project=\"TODORA\"")
//    @GET("jira")
    suspend fun getIssues(@Query("filter") type: String): JiraIssueResponse

}

// Create a singleton API service as it will only be used by a single user in a single app instance.

object JiraApi {
    val retrofitService: JiraApiService by lazy {
        retrofit.create(JiraApiService::class.java)
    }
}