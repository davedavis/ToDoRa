package io.davedavis.todora.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.davedavis.todora.R
import io.davedavis.todora.databinding.ListItemBinding
import io.davedavis.todora.network.JiraIssue
import io.davedavis.todora.utils.getTimeAgo
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


class IssueAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<JiraIssue, IssueAdapter.IssueViewHolder>(DiffCallback) {

    /**
     * The [IssueViewHolder] constructor takes the binding variable from the associated
     * ListItem, lets us access all [JiraIssue] information. Current and future.
     */
    class IssueViewHolder(private var binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jiraIssue: JiraIssue) {
            binding.issueSummary.text = jiraIssue.fields.summary
            binding.issueDescription.text = jiraIssue.fields.description


            // Thanks to desugaring: https://developer.android.com/studio/write/java8-support#library-desugaring
            // and some help: https://stackoverflow.com/questions/35858608/how-to-convert-time-to-time-ago-in-android
            // And here: https://www.programiz.com/kotlin-programming/examples/string-date
            val dateAgo = LocalDateTime
                .parse(jiraIssue.fields.created.toString().substring(0, 19))
                .toLocalDate()
            val dateView = Date.from(dateAgo.atStartOfDay(ZoneId.systemDefault()).toInstant())
            val timeAgo = getTimeAgo(dateView)

            binding.issueDate.text = timeAgo

            if (jiraIssue.fields.timespent?.toInt() ?: 0 > 1) {
                binding.issueTime.setImageResource(R.drawable.list_view_time_logged)

            }


            /**
             * Sets the image depending on the priority/severity of the issue.
             */
            binding.issuePriority.setImageResource(
                when (jiraIssue.fields.priority.name) {
                    "Highest" -> R.drawable.highest
                    "High" -> R.drawable.high
                    "Low" -> R.drawable.low
                    "Lowest" -> R.drawable.lowest
                    "Medium" -> R.drawable.medium
                    "Minor" -> R.drawable.minor
                    "Major" -> R.drawable.major
                    "Blocker" -> R.drawable.blocker
                    "Critical" -> R.drawable.critical
                    else -> R.drawable.trivial
                }
            )

            /**
             * This makes data binding trigger immediately so the recyclerview can get the size of
             * the Issue List immediately. If it doesn't, getItemCount will return zero and the
             * onCreateViewHolder method will never be called.
             */
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [JiraIssue]
     * has been updated. This will prevent a new call to the API when an issue is edited as it will
     * already have the edited information.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<JiraIssue>() {
        override fun areItemsTheSame(oldItem: JiraIssue, newItem: JiraIssue): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: JiraIssue, newItem: JiraIssue): Boolean {
            return oldItem.id == newItem.id
        }
    }

    /**
     * Create new [RecyclerView] item views
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IssueViewHolder {
        return IssueViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    /**
     * Replaces the contents of a view. RequiresAPI is needed as a lint removal. Bind method is
     * handled by compat libraries, but Android Studio isn't up to date yet.
     */
    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        val jiraIssue = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(jiraIssue)
        }
        holder.bind(jiraIssue)
    }

    /**
     * Custom listener that handles clicks on [RecyclerView] items. Passes the [JiraIssue]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [JiraIssue]
     */
    class OnClickListener(val clickListener: (jiraIssue: JiraIssue) -> Unit) {
        fun onClick(jiraissue: JiraIssue) = clickListener(jiraissue)
    }
}


