package com.redc4ke.taniechlanie.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.CategoryViewModel
import com.redc4ke.taniechlanie.data.menu.DetailsCategoryAdapter
import kotlinx.android.synthetic.main.fragment_cat_list.*

class CatListFragment(private val alcoObject: AlcoObject) : Fragment() {

    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cat_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val recyclerView = catlist_RV
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        categoryViewModel = requireActivity().run {
            ViewModelProvider(this).get(CategoryViewModel::class.java)
        }

        categoryViewModel.get().observe(viewLifecycleOwner, Observer {
            val detailsCategoryAdapter = DetailsCategoryAdapter(alcoObject, it)
            recyclerView.adapter = detailsCategoryAdapter
            detailsCategoryAdapter.notifyDataSetChanged()
        })
    }

}