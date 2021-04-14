package io.davedavis.todora.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.davedavis.todora.R
import io.davedavis.todora.databinding.FragmentEditBinding
import timber.log.Timber

class EditFragment : Fragment() {

    private lateinit var viewModel: EditViewModel
    private lateinit var viewModelFactory: EditViewModelFactory


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

//        // Grab references to the edit views so we can monitor any changes.
//        val priorityTextDropDown: TextInputEditText = binding.dropdown

        val items = listOf("High", "Medium", "Low", "Lowest")
        val adapter = ArrayAdapter(requireContext(), R.layout.priority_list_item, items)
        (binding.dropdown as? AutoCompleteTextView)?.setAdapter(adapter)


        viewModel.selectedIssue.observe(viewLifecycleOwner, {
            Timber.i("Observing successfully !%s", it.toString())
            // ToDo:
        })

        binding.sumbitIssueButton.setOnClickListener {

            Timber.i("Issue Object !%s", jiraIssueObject.toString())
            // ToDo Send the observed issue
            viewModel.updateJiraIssue(jiraIssueKey)

        }
        return binding.root
    }
}

