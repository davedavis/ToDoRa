package io.davedavis.todora

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import io.davedavis.todora.network.JiraIssue
import io.davedavis.todora.ui.home.IssueAdapter
import io.davedavis.todora.ui.home.JiraAPIStatus


/**
 * When there is no [JiraIssue] data (data is null), hide the [RecyclerView], otherwise show it.
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, apiData: List<JiraIssue>?) {
    val adapter = recyclerView.adapter as IssueAdapter
    adapter.submitList(apiData)
}

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