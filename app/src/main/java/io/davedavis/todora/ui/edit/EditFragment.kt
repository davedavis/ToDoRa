package io.davedavis.todora.ui.edit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.davedavis.todora.R
import io.davedavis.todora.database.TimeLogDatabase
import io.davedavis.todora.databinding.FragmentEditBinding
import io.davedavis.todora.model.JiraAPIStatus
import io.davedavis.todora.model.PriorityOptions
import timber.log.Timber


class EditFragment : Fragment() {

    private lateinit var viewModel: EditViewModel
    private lateinit var viewModelFactory: EditViewModelFactory

    fun hideKeyboard(view: View) {
        view?.apply {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // The editable Jira Issue Object to pass to the ViewModel for display and post/put requests.
        val jiraIssueObject = EditFragmentArgs.fromBundle(requireArguments()).parcelableIssueObject

        // The issue Key to pass to the PUT retrofit method. Not included in the issue object as
        // it's not editable an we don't pass uneditable fields to the API.
        val jiraIssueKey = EditFragmentArgs.fromBundle(requireArguments()).issueKey

        val binding = FragmentEditBinding.inflate(inflater)

        binding.lifecycleOwner = this

        // Need to get the app context to pass the context to the viewModelFactory
        val application = requireNotNull(this.activity).application

        // Grab a reference to the DB instance (via the DAO) to pass it to the viewModelFactory too.
        val dataSource = TimeLogDatabase.getInstance(application).timeLogDatabaseDAO

        viewModelFactory =
            EditViewModelFactory(dataSource, application, jiraIssueKey, jiraIssueObject)
        viewModel = ViewModelProvider(this, viewModelFactory).get(EditViewModel::class.java)

        // Giving the binding access to the EditViewModel
        binding.viewModel = viewModel

        // Set the submit button to disabled until there's a change.
        binding.sumbitIssueButton.isEnabled = false

        // Disable the stop log button
        binding.stopLogButton.isEnabled = false


        // Observe the selectedIssueTimeLogs livedata for changes. If there's logs, enable submit.
        viewModel.selectedIssueTimeLogs.observe(viewLifecycleOwner, {

            binding.submitTimelogsButton.isEnabled = !it.isNullOrEmpty()


//            binding.submitTimelogsButton.isEnabled =
//                !viewModel.selectedIssueTimeLogs.value.isNullOrEmpty()

            if (it.isNullOrEmpty()) {
                binding.timelogHeaderTextview.text = getString(R.string.no_pending_logs)
            }
            // 18/04/2021: 13:45 - 1430 - 45 Mins
            else
                for (timelogEntry in it)
                    binding.pendingTimeLogsTextView.append(
                        timelogEntry.startTime.toString()
                            .substring(10, 13) + " - " + timelogEntry.endTime.toString()
                            .substring(10, 13) + System.lineSeparator()
                    )


        })


        // If the submit changes button is enabled, there's an edit to be made. So call the update method.
        binding.startLogButton.setOnClickListener {
            viewModel.onStartTimeLogTracking()
            Toast.makeText(context, getString(R.string.time_tracking_started), Toast.LENGTH_SHORT)
                .show()
            binding.startLogButton.isEnabled = false
            binding.stopLogButton.isEnabled = true
            binding.pendingTimeLogsTextView.isVisible = true

        }

        binding.stopLogButton.setOnClickListener {
            viewModel.onStopTimeLogTracking()
            binding.stopLogButton.isEnabled = false
            binding.startLogButton.isEnabled = true
            binding.timelogHeaderTextview.text = getString(R.string.pending_timelogs)


        }


        // Observe the selectedIssue livedata for changes. If there's a change, enable the button.
        viewModel.issueEdited.observe(viewLifecycleOwner, {
            binding.sumbitIssueButton.isEnabled = true

        })

//        // Observe the selectedIssueTimeLogs livedata for changes. If there's logs, enable submit.
//        viewModel.selectedIssueTimeLogs.observe(viewLifecycleOwner, {
//            binding.submitTimelogsButton.isEnabled = !it.isNullOrEmpty()
////            binding.pendingTimeLogsTextView.isVisible = true
//
//
//        })


        // Observe the status of the API request. If it's done, navigate back to the list.
        viewModel.status.observe(viewLifecycleOwner, {
            when (it) {
                JiraAPIStatus.DONE -> {
                    Timber.i("status observer >>>>> DONE!")
                    Toast.makeText(context, viewModel.responseMessage, Toast.LENGTH_SHORT).show()
                    // Navigate back to home
                    this.findNavController().navigate(R.id.nav_home)

                }
                JiraAPIStatus.ERROR -> {
                    Timber.i("status observer >>>>> ERROR!")
                    Toast.makeText(context, viewModel.responseMessage, Toast.LENGTH_SHORT).show()

                }
                JiraAPIStatus.LOADING -> {
                    Timber.i("status observer >>>>> LOADING!")
                }
            }

        })


        // Set the adapter for the material dropdown.
        val priorityAdapter: ArrayAdapter<PriorityOptions> = ArrayAdapter<PriorityOptions>(
            requireContext(),
            R.layout.priority_list_item,
            PriorityOptions.values()
        )

        binding.dropdown.setAdapter(priorityAdapter)

        // ToDo: Set up adapter and listview for timelogs.

        // Set the default state for the adapter.
        binding.dropdown.setText(viewModel.priority.value.toString(), false)

        // Set a listener on the dropdown and update the viewModel payload with the new value.
        binding.dropdown.setOnItemClickListener { parent, view, position, id ->
            viewModel.updatePriority(position)

        }

        // Hide the keyboard when the edit is done.
        binding.summaryEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) hideKeyboard(view)

        }
        binding.descriptionEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) hideKeyboard(view)

        }

        // If the submit changes button is enabled, there's an edit to be made. So call the update method.
        binding.sumbitIssueButton.setOnClickListener {

            viewModel.updateJiraIssue(jiraIssueKey)

        }

        // If the submit Pending Timelogs button is enabled, there's logs to be submitted. So call the update method.
        binding.submitTimelogsButton.setOnClickListener {

            viewModel.onClearDb()
            binding.submitTimelogsButton.isEnabled = false
            binding.pendingTimeLogsTextView.isVisible = false
            binding.timelogHeaderTextview.text = getString(R.string.no_pending_logs)
            Toast.makeText(
                context,
                getString(R.string.timelogs_submitted_message),
                Toast.LENGTH_SHORT
            ).show()
            this.findNavController().navigate(R.id.nav_home)
        }
        return binding.root
    }
}

