package com.redc4ke.taniechlanie.ui.request

import android.os.Bundle
import androidx.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.ItemCategory
import com.redc4ke.taniechlanie.data.request.CategoryListAdapter

class CategoryFragment : Fragment() {

    private var parentFrag: RequestFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_from_left)
        exitTransition = inflater.inflateTransition(R.transition.slide_to_left)
        allowEnterTransitionOverlap = true
        allowReturnTransitionOverlap = true

        parentFrag = arguments?.getSerializable("RequestFragment") as RequestFragment

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

    fun onItemClick(cat: ItemCategory?) {
        if (cat in parentFrag!!.categoryList) {
            Toast.makeText(requireContext(), "Już dodałeś tę kategorię!", Toast.LENGTH_LONG).show()
        } else {
            parentFrag!!.onCategoryResult(cat)
        }
        requireActivity().onBackPressed()
    }

}