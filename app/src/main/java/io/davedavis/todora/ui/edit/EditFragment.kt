package io.davedavis.todora.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.davedavis.todora.databinding.FragmentEditBinding
import timber.log.Timber

class EditFragment : Fragment() {

    private lateinit var viewModel: EditViewModel
    private lateinit var viewModelFactory: EditViewModelFactory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val jiraIssueObject = EditFragmentArgs.fromBundle(requireArguments()).parcelableIssueObject

        val binding = FragmentEditBinding.inflate(inflater)

        binding.lifecycleOwner = this

        viewModelFactory = EditViewModelFactory(jiraIssueObject)
        viewModel = ViewModelProvider(this, viewModelFactory).get(EditViewModel::class.java)

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel


        viewModel.selectedIssue.observe(viewLifecycleOwner, {
            Timber.i("Observing successfully !%s", it.toString())
            // ToDo:
        })

        binding.sumbitIssueButton.setOnClickListener {

            Timber.i("Issue Object !%s", jiraIssueObject.toString())
            // ToDo Send the observed issue
            viewModel.updateJiraIssue()

        }
        return binding.root
    }
}

