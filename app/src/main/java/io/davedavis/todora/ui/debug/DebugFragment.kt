package io.davedavis.todora.ui.debug

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.davedavis.todora.R

class DebugFragment : Fragment() {

    private lateinit var debugViewModel: DebugViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        debugViewModel =
            ViewModelProvider(this).get(DebugViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_backlog, container, false)
        val textView: TextView = root.findViewById(R.id.text_backlog)
        debugViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}