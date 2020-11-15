package com.redc4ke.jabotmobile.ui.request

import android.os.Bundle
import androidx.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.jabotmobile.R
import com.redc4ke.jabotmobile.data.request.CategoryListAdapter

class CategoryFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_from_left)
        exitTransition = inflater.inflateTransition(R.transition.slide_to_left)
        allowEnterTransitionOverlap = true
        allowReturnTransitionOverlap = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_category, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.category_fragment_RV)

        recyclerView.adapter = CategoryListAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return rootView
    }

}