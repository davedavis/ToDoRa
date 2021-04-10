package io.davedavis.todora.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import io.davedavis.todora.R
import io.davedavis.todora.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: HomeViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // Set up ViewBinding
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this.requireActivity())

        viewModelFactory = HomeViewModelFactory(prefs)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        // Grab references to the views, using ViewBinding.
        val textView: TextView = binding.textHome
        val userNameTV: TextView = binding.homePrefUserName
        val userEmailTV: TextView = binding.homePrefUserEmail
        val userURLTV: TextView = binding.homePrefUrl
        val userProjectIdTV: TextView = binding.homePrefProjectId
        val userAPIKeyTV: TextView = binding.homePrefApiKey

        // To Observe LiveData
        binding.lifecycleOwner = this



        // Set up livedata observers.
//        viewModel.issues.observe(viewLifecycleOwner, Observer {
//            for (issue in it.issues)
//                textView.append(issue.fields.summary + issue.fields.description + issue.fields.priority.name
//                        + issue.fields.status.name + System.lineSeparator())
//        })


        viewModel.issues.observe(viewLifecycleOwner, {
            for (issue in it)
                textView.append(issue.fields.summary + issue.fields.description + issue.fields.priority.name
                        + issue.fields.status.name + System.lineSeparator())
        })


        viewModel.userNameTVText.observe(viewLifecycleOwner, {
            userNameTV.text = it
        })

        viewModel.userEmailTVText.observe(viewLifecycleOwner, {
            userEmailTV.text = it
        })

        viewModel.userURLTVText.observe(viewLifecycleOwner, {
            userURLTV.text = it
        })

        viewModel.userProjectIdTVText.observe(viewLifecycleOwner, {
            userProjectIdTV.text = it
        })

        viewModel.userAPIKeyTVText.observe(viewLifecycleOwner, {
            userAPIKeyTV.text = it
        })

        return binding.root
    }
}