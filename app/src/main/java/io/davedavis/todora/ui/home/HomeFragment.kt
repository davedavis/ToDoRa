package io.davedavis.todora.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.davedavis.todora.R
import io.davedavis.todora.databinding.FragmentHomeBinding
import io.davedavis.todora.model.ParcelableIssue
import io.davedavis.todora.ui.edit.EditFragmentDirections
import io.davedavis.todora.utils.SharedPreferencesManager
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

                val parcelableEditIssue = ParcelableIssue(
                    it.fields.summary.toString(),
                    it.fields.description.toString(),
                    it.id,
                    SharedPreferencesManager.getUserProject(),
                    it.fields.priority.name.toString(),
                    it.fields.timespent ?: 0,
                    "self"
                )

                Timber.i(parcelableEditIssue.toString())

                this.findNavController()
                    // Create a parcelable issue so we can pass and use it in EditFragment.


                    .navigate(
                        EditFragmentDirections.actionShowEdit(parcelableEditIssue)
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Updates the filter in the [HomeViewModel] when the menu items are selected from the
     * overflow menu.
     */
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        viewModel.updateFilter(
//                when (item.itemId) {
//                    R.id.show_rent_menu -> MarsApiFilter.SHOW_RENT
//                    R.id.show_buy_menu -> MarsApiFilter.SHOW_BUY
//                    else -> MarsApiFilter.SHOW_ALL
//                }
//        )
//        return true
//    }
}
