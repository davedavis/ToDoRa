package io.davedavis.todora.ui.backlog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.davedavis.todora.R


class BacklogFragment : Fragment() {

    private lateinit var backlogViewModel: BacklogViewModel

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        backlogViewModel = ViewModelProvider(this).get(BacklogViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_backlog, container, false)

        val textView: TextView = root.findViewById(R.id.text_backlog)


        backlogViewModel.issues.observe(viewLifecycleOwner, {
            for (issue in it)

                textView.append(issue.fields.summary + issue.fields.description + issue.fields.priority.name
                        + issue.fields.status.name + System.lineSeparator())
        })


        return root
    }

}