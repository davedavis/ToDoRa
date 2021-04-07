package io.davedavis.todora.ui.debug

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
import io.davedavis.todora.databinding.FragmentDebugBinding


class DebugFragment : Fragment() {

    // Set up Viewodel
//    private lateinit var debugViewModel: DebugViewModel

    private lateinit var viewModel: DebugViewModel
    private lateinit var viewModelFactory: DebugViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // Set up ViewBinding
        val binding: FragmentDebugBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_debug, container, false)

//        debugViewModel = ViewModelProvider(this).get(DebugViewModel::class.java)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this.requireActivity())

        viewModelFactory = DebugViewModelFactory(prefs)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DebugViewModel::class.java)

        // Grab references to the views, using ViewBinding.
        val textView: TextView = binding.textDebug
        val userNameTV: TextView = binding.prefUserName
        val userEmailTV: TextView = binding.prefUserEmail
        val userURLTV: TextView = binding.prefUrl
        val userProjectIdTV: TextView = binding.prefProjectId
        val userAPIKeyTV: TextView = binding.prefApiKey

        // To Observe LiveData
        binding.lifecycleOwner = this

        // Set up observers.
        viewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
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