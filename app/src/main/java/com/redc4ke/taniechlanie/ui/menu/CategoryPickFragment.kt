package com.redc4ke.taniechlanie.ui.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.data.menu.CategoryPickAdapter
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.FilterViewModel
import com.redc4ke.taniechlanie.databinding.FragmentCategoryPickBinding
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class CategoryPickFragment : BaseFragment<FragmentCategoryPickBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCategoryPickBinding
        get() = FragmentCategoryPickBinding::inflate
    private var isMajor = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isMajor = arguments?.getBoolean("isMajor") ?: false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filterViewModel = ViewModelProvider(requireActivity())[FilterViewModel::class.java]
        val categoryViewModel = ViewModelProvider(requireActivity())[CategoryViewModel::class.java]

        val adapter = CategoryPickAdapter(requireContext())
        binding.catPickRV.layoutManager = LinearLayoutManager(requireContext())
        binding.catPickRV.adapter = adapter

        if (isMajor) {
            categoryViewModel.getAllMajor().observe(viewLifecycleOwner, {
                adapter.update(it.values.toList())
            })
        } else {
            categoryViewModel.getAllNotMajor().observe(viewLifecycleOwner, {
                adapter.update(it.values.toList())
            })
        }

        binding.catPickCancelBT.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.catPickApplyBT.setOnClickListener {
            val selected = adapter.getSelected()
            if (isMajor) {
                filterViewModel.setTypeFilter(selected)
            } else {
                filterViewModel.setCategoryFilter(selected)
            }
            findNavController().popBackStack()
        }
    }
}