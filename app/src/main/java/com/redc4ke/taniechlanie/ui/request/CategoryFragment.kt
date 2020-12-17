package com.redc4ke.taniechlanie.ui.request

import android.os.Bundle
import androidx.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.data.CategoryViewModel
import com.redc4ke.taniechlanie.data.request.CategoryListAdapter
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : Fragment() {

    private var parentFrag: RequestFragment? = null
    private lateinit var categoryViewModel: CategoryViewModel

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
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        categoryViewModel = requireActivity().run {
            ViewModelProvider(this).get(CategoryViewModel::class.java)
        }
        val recyclerView = category_fragment_RV

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        categoryViewModel.get().observe(viewLifecycleOwner, Observer {
            val adapter = CategoryListAdapter(it, this)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        })
    }

    fun onItemClick(cat: Category?) {
        if (cat in parentFrag!!.categoryList) {
            Toast.makeText(requireContext(), "Już dodałeś tę kategorię!", Toast.LENGTH_LONG).show()
        } else {
            parentFrag!!.onCategoryResult(cat)
        }
        requireActivity().onBackPressed()
    }

}