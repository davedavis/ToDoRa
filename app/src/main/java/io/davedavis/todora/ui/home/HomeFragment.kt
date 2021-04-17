package io.davedavis.todora.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import io.davedavis.todora.MainActivity
import io.davedavis.todora.R
import io.davedavis.todora.databinding.FragmentHomeBinding
import io.davedavis.todora.model.Fields
import io.davedavis.todora.model.ParcelableIssue
import io.davedavis.todora.model.Priority
import io.davedavis.todora.network.JiraApiFilter
import io.davedavis.todora.ui.edit.EditFragmentDirections
import timber.log.Timber


class HomeFragment : Fragment() {

    /**
     * Lazily initialize our [HomeViewModel].
     */
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentHomeBinding.inflate(inflater)
        setHasOptionsMenu(true)


        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

        if (sharedPrefs.all.containsValue("") || sharedPrefs.all.isNullOrEmpty()) {
            this.findNavController().navigate(R.id.settings)
            Timber.i(" >>>>> zzz >>>>> Settings not complete")
            Toast.makeText(
                context,
                "You need to enter all settings to use this app",
                Toast.LENGTH_LONG
            ).show()
        }


        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        // Sets the adapter of the issuesGrid RecyclerView with clickHandler lambda that
        // tells the viewModel when the property is clicked
        binding.issuesGrid.adapter = IssueAdapter(IssueAdapter.OnClickListener {
            viewModel.displayIssueDetail(it)
        })

        // Observe the navigateToSelectedIssue LiveData and Navigate when it isn't null
        // After navigating, call displayIssueDetailsComplete() so that the ViewModel is ready
        // for another navigation event.

        viewModel.navigateToSelectedIssue.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                Timber.i(it.key)

                val parcelableEditIssue = ParcelableIssue(
                    Fields(
                        it.fields.summary.toString(),
                        it.fields.description.toString(),
                        Priority(it.fields.priority.name.toString()),
//                        it.fields.timespent ?: 0
                    )
                )




                this.findNavController()
                    .navigate(
                        EditFragmentDirections.actionShowEdit(parcelableEditIssue, it.key)
                    )

                // Reset the Issue so navigation is released and works again. Otherwise, stuck on the issue.
                viewModel.displayIssueDetailComplete()
            }
        })

        return binding.root
    }

    /**
     * Inflates the overflow menu that contains filtering options.
     */
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.main, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    /**
     * Updates the filter in the [HomeViewModel] when the menu items are selected from the
     * overflow menu.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Timber.i(">>>>>onOPtionsItemSelected Called")
        var mainActivity: MainActivity = activity as MainActivity

        when (item.itemId) {
            R.id.show_backlog -> viewModel.updateFilter(JiraApiFilter.SHOW_BACKLOG)
            R.id.show_selected -> viewModel.updateFilter(JiraApiFilter.SHOW_SELECTED_FOR_DEVELOPMENT)
            R.id.show_wip -> viewModel.updateFilter(JiraApiFilter.SHOW_IN_PROGRESS)
            R.id.show_done -> viewModel.updateFilter(JiraApiFilter.SHOW_DONE)
            R.id.show_all_issues -> viewModel.updateFilter(JiraApiFilter.SHOW_ALL)
            else -> mainActivity.openDrawer()
        }


        return true
    }


}
