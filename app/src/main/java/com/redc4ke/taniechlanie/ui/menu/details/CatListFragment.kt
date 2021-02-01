package com.redc4ke.taniechlanie.ui.menu.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.menu.DetailsCategoryAdapter
import com.redc4ke.taniechlanie.databinding.FragmentCatListBinding
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class CatListFragment(private val alcoObject: AlcoObject) : BaseFragment<FragmentCatListBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCatListBinding
        get() = FragmentCatListBinding::inflate
    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val recyclerView = binding.catlistRV
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