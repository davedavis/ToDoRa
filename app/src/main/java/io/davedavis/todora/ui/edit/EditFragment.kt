package io.davedavis.todora.ui.edit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.davedavis.todora.R
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

        Timber.i("Received the issue key: %s", jiraIssueKey)

        val binding = FragmentEditBinding.inflate(inflater)

        binding.lifecycleOwner = this

        viewModelFactory = EditViewModelFactory(jiraIssueObject)
        viewModel = ViewModelProvider(this, viewModelFactory).get(EditViewModel::class.java)

        // Giving the binding access to the EditViewModel
        binding.viewModel = viewModel

        // Set the submit button to disabled until there's a change.
        binding.sumbitIssueButton.isEnabled = false

        // Observe the selectedIssue livedata for changes. If there's a change, enable the button.
        viewModel.issueEdited.observe(viewLifecycleOwner, {
            Timber.i("issueEdited Change Detected, enabling submit button. ")
            binding.sumbitIssueButton.isEnabled = true

        })


        // Observe the status of the API request. If it's done, navigate back to the list.
        viewModel.status.observe(viewLifecycleOwner, {
            when (it) {
                JiraAPIStatus.DONE -> {
                    Timber.i("status observer >>>>> DONE!")
                    Toast.makeText(context, viewModel.responseMessage, Toast.LENGTH_SHORT).show()
                    // ToDo: Navigate back to home
                    this.findNavController().navigate(R.id.nav_home)

                }
                JiraAPIStatus.ERROR -> {
                    Timber.i("status observer >>>>> ERROR!")
                    Toast.makeText(context, viewModel.responseMessage, Toast.LENGTH_SHORT).show()

                }
                JiraAPIStatus.LOADING -> {
                    Timber.i("status observer >>>>> LOADING!")
                    // ToDo: Disable the submit button while sending

                }
            }

        })


        // Set the adapter for the material dropdown.
        val adapter: ArrayAdapter<PriorityOptions> = ArrayAdapter<PriorityOptions>(requireContext(), R.layout.priority_list_item, PriorityOptions.values())
        binding.dropdown.setAdapter(adapter)

        // Set the default state for the adapter.
        binding.dropdown.setText(viewModel.priority.value.toString(), false)

        // Set a listener on the dropdown and update the viewmodel payload with the new value.
        binding.dropdown.setOnItemClickListener { parent, view, position, id ->
            viewModel.updatePriority(position)

        }

        // Hide teh keyboard when the edit is done.
        binding.summaryEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) hideKeyboard(view)

        }

        binding.descriptionEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) hideKeyboard(view)

        }

        // If the button us visible, there's an edit to be made. So call the update method.
        binding.sumbitIssueButton.setOnClickListener {

            Timber.i("Issue Object !%s", jiraIssueObject.toString())
            // ToDo Send the observed issue
            viewModel.updateJiraIssue(jiraIssueKey)

        }
        return binding.root
    }
}

