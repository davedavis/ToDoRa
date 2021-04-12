package io.davedavis.todora.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.davedavis.todora.databinding.FragmentEditBinding

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
        val jiraIssueDescription =
            EditFragmentArgs.fromBundle(requireArguments()).selectedIssueDescription
        val jiraIssuePriority =
            EditFragmentArgs.fromBundle(requireArguments()).selectedIssuePriority
        val jiraIssueTimeSpent =
            EditFragmentArgs.fromBundle(requireArguments()).selectedIssueTimeSpent

        val viewModelFactory = EditViewModelFactory(
            jiraIssueId, jiraIssueSummary, jiraIssueDescription,
            jiraIssuePriority, jiraIssueTimeSpent, application
        )

        binding.viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(EditViewModel::class.java)

        return binding.root
    }
}