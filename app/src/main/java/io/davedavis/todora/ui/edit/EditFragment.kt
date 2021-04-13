package io.davedavis.todora.ui.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import io.davedavis.todora.databinding.FragmentEditBinding
import io.davedavis.todora.network.Auth
import io.davedavis.todora.network.JiraApi
import io.davedavis.todora.network.JiraIssue
import kotlinx.coroutines.launch

class EditFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(activity).application
        val binding = FragmentEditBinding.inflate(inflater)

        binding.lifecycleOwner = this

        val jiraIssueId = EditFragmentArgs.fromBundle(requireArguments()).selectedIssueId
        val jiraIssueSummary = EditFragmentArgs.fromBundle(requireArguments()).selectedIssueSummary
        val jiraIssueDescription = EditFragmentArgs.fromBundle(requireArguments()).selectedIssueDescription
        val jiraIssuePriority = EditFragmentArgs.fromBundle(requireArguments()).selectedIssuePriority
        val jiraIssueTimeSpent = EditFragmentArgs.fromBundle(requireArguments()).selectedIssueTimeSpent

        val viewModelFactory = EditViewModelFactory(
                jiraIssueId, jiraIssueSummary, jiraIssueDescription,
                jiraIssuePriority, jiraIssueTimeSpent, application
        )

        binding.viewModel = ViewModelProvider(
                this, viewModelFactory
        ).get(EditViewModel::class.java)

        binding.sumbitIssueButton.setOnClickListener {
            Log.i("EditFragment >>>>>", "Button Clicked!" +
                    System.lineSeparator() +
                    jiraIssueId +
                    System.lineSeparator() +
                    jiraIssueSummary +
                    System.lineSeparator() +
                    jiraIssueDescription +
                    System.lineSeparator() +
                    jiraIssuePriority +
                    System.lineSeparator() +
                    jiraIssueTimeSpent +
                    System.lineSeparator()
            )

            // ToDo: Create ViewModel for Edit.
            // ToDo: Create a Gson version of the updated object to pass
            // ToDo: Update the interface to accept the new body instead of an object.
//         updateIssue("test", JiraIssue(id = jiraIssueId, expand = "", key=""))

            // ToDo: Call the API with a PUT request containing the updated fields.
        }




        return binding.root
    }


    fun updateIssue(issueId: String, issueObject: JiraIssue) {
        lifecycleScope.launch {
            try {
                JiraApi.retrofitService.updateJiraIssue(Auth.getAuthHeaders(), issueObject)
            } catch (e: Exception) {
            }
        }
    }
}

