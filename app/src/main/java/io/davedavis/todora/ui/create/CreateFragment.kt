package io.davedavis.todora.ui.create

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
import io.davedavis.todora.databinding.FragmentCreateBinding
import io.davedavis.todora.model.JiraAPIStatus
import io.davedavis.todora.model.PriorityOptions
import timber.log.Timber


class CreateFragment : Fragment() {


    private lateinit var viewModel: CreateViewModel
    private lateinit var viewModelFactory: CreateViewModelFactory

    fun hideKeyboard(view: View) {
        view.apply {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val binding = FragmentCreateBinding.inflate(inflater)

        binding.lifecycleOwner = this


        viewModelFactory = CreateViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(CreateViewModel::class.java)

        // Giving the binding access to the EditViewModel
        binding.viewModel = viewModel


        // Set the submit button to disabled until there's a change.
        binding.sumbitIssueButton.isEnabled = false

        // Monitor for a valid issue so the button can be reenabled.
        viewModel.issueValid.observe(viewLifecycleOwner, {
            if (it) {
                binding.sumbitIssueButton.isEnabled = true
            }
        })


        // Observe the status of the API request. If it's done, navigate back to the list.
        viewModel.status.observe(viewLifecycleOwner, {
            when (it) {
                JiraAPIStatus.DONE -> {
                    Timber.i("status observer >>>>> DONE!")
                    Toast.makeText(context, viewModel.responseMessage, Toast.LENGTH_SHORT).show()
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
        // ToDo: Move this to the bindingadapter class.
        val adapter: ArrayAdapter<PriorityOptions> = ArrayAdapter<PriorityOptions>(
            requireContext(),
            R.layout.priority_list_item,
            PriorityOptions.values()
        )
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

        binding.descriptionCreateText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) hideKeyboard(view)

        }

        // If the button is visible, it's valud and can be submitted.
        binding.sumbitIssueButton.setOnClickListener {

            viewModel.submitNewJiraIssue()

        }
        return binding.root
    }
}