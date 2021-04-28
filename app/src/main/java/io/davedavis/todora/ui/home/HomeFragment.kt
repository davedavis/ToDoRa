package io.davedavis.todora.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
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
     * Inflates the layout with Data Binding, sets its lifecycle owner to the Home Fragment
     * to enable Data Binding to observe LiveData, and sets up the Recycler with a databinding
     * adapter from the databinding file..
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentHomeBinding.inflate(inflater)
        setHasOptionsMenu(true)


        /**
         * Checks that all settings are set, otherwise, navigates to settings fragment.
         */
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        if (sharedPrefs.all.containsValue("") || sharedPrefs.all.isNullOrEmpty()) {
            this.findNavController().navigate(R.id.settings)
            Timber.i(" >>>>> zzz >>>>> Settings not complete")
            Toast.makeText(
                context,
                getString(R.string.mandatory_settings_message),
                Toast.LENGTH_LONG
            ).show()
        }


        /**
         * Provides Data Binding to Observe LiveData with the lifecycle of this Fragment
         */
        binding.lifecycleOwner = this

        /**
         * Defines an action when the pull to refresh gesture is performed.
         * We want to call the API again
         *
         * */

        binding.swipeToRefreshContainer.setOnRefreshListener {
            viewModel.filter.value?.let { viewModel.updateFilter(it) }
            Timber.i(">>>>> Update is happening in the recyclerview")

            binding.issuesGrid.adapter?.notifyDataSetChanged()
            binding.swipeToRefreshContainer.isRefreshing = false

        }

        /**
         * Giving the binding access to the HomeViewModel
         */
        binding.viewModel = viewModel

        /**
         * Sets the adapter of the issuesGrid RecyclerView with clickHandler lambda that
         * tells the viewModel when the issue is clicked
         */
        binding.issuesGrid.adapter = IssueAdapter(IssueAdapter.OnClickListener {
            viewModel.displayIssueDetail(it)
        })

        /**
         * Set a listener on the fab and navigate to the create fragment.
         */
        binding.fab.setOnClickListener {
            this.findNavController().navigate(R.id.nav_create)
        }

        /**
         * Observe the navigateToSelectedIssue LiveData and Navigate when it isn't null
         * After navigating, call displayIssueDetailsComplete() so that the ViewModel is ready
         * for another navigation event.
         */
        viewModel.navigateToSelectedIssue.observe(viewLifecycleOwner, {
            if (null != it) {
                Timber.i(it.key)

                val parcelableEditIssue = ParcelableIssue(
                    Fields(
                        it.fields.summary.toString(),
                        it.fields.description.toString(),
                        Priority(it.fields.priority.name.toString()),
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
     * Inflate the menu; this adds items to the action bar if it is present.
     * We want this here as it contains filters only applicable to home, not app wide.
     */

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    /**
     * Updates the filter in the [HomeViewModel] when the filter options are selected from the
     * overflow menu.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Timber.i(">>>>>onOPtionsItemSelected Called")
        val mainActivity: MainActivity = activity as MainActivity

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
