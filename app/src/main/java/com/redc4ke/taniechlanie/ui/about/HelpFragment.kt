package com.redc4ke.taniechlanie.ui.about

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.about.HelpRecyclerViewAdapter
import com.redc4ke.taniechlanie.ui.setTransitions

class HelpFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitions(this, R.transition.slide_up_menu, R.transition.slide_down_menu)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_help, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.help_RV)

        recyclerView.adapter = HelpRecyclerViewAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return root
    }
}