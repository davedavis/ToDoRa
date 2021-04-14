package io.davedavis.todora

import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import io.davedavis.todora.model.JiraAPIStatus
import io.davedavis.todora.model.PriorityOptions
import io.davedavis.todora.network.JiraIssue
import io.davedavis.todora.network.JiraIssueResponse
import io.davedavis.todora.ui.home.IssueAdapter
import timber.log.Timber


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

// TODO: Continue from here: https://stackoverflow.com/questions/58737505/autocompletetextview-or-spinner-data-binding-in-android
// an here: https://gist.github.com/fonix232/6553f66d17746dca5d062eecf40e8b3b


// To Set Listner:
// https://stackoverflow.com/questions/58568335/material-exposed-dropdown-menu-onitemselectedlistener-not-being-called
@BindingAdapter("priorityOption")
fun bindPriorityOptions(priorityOption: AutoCompleteTextView, priority: PriorityOptions?) {
    when (priority) {
        PriorityOptions.Highest -> {
//            statusImageView.visibility = View.VISIBLE
            Timber.i("Highest Selected")
            priorityOption.setText("TEST")

        }
        PriorityOptions.High -> {
            Timber.i("High Selected")

        }
    }
}


@BindingAdapter("android:adapter")
fun setAutoCompleteAdapter(textView: AutoCompleteTextView, adapter: ArrayAdapter<*>) {
    textView.setAdapter(adapter)
}