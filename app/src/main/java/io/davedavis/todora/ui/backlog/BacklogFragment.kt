package io.davedavis.todora.ui.backlog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.davedavis.todora.R


class BacklogFragment : Fragment() {

    private lateinit var backlogViewModel: BacklogViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        backlogViewModel =
            ViewModelProvider(this).get(BacklogViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_backlog, container, false)
        val textView: TextView = root.findViewById(R.id.text_backlog)
        backlogViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

}