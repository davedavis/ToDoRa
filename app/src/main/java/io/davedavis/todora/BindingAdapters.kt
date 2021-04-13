package io.davedavis.todora

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import io.davedavis.todora.network.JiraIssue
import io.davedavis.todora.network.JiraIssueResponse
import io.davedavis.todora.ui.home.IssueAdapter
import io.davedavis.todora.ui.home.JiraAPIStatus


/**
 * When there is no [JiraIssue] data, hide the [RecyclerView], otherwise show it.
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, apiData: List<JiraIssue>?) {
    val adapter = recyclerView.adapter as IssueAdapter
    adapter.submitList(apiData)
}

/**
 * As there's a delay between when the [JiraIssueResponse] response is received and when the
 * recyclerview is shown, show a status icon from the built in material icons drawables.
 * This doubles as an error handling feedback for the user if there's an issue with the network.
 * This uses the [JiraAPIStatus] set during the Retrofit call in getJiraIssues().
 */
@BindingAdapter("jiraApiStatus")
fun bindJiraApiStatus(statusImageView: ImageView, status: JiraAPIStatus?) {
    when (status) {
        JiraAPIStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.network_loading)

        }
        JiraAPIStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.network_error)
        }
        JiraAPIStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}