package com.redc4ke.taniechlanie.ui.menu

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.menu.CategoryPickAdapter
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.FilterViewModel
import com.redc4ke.taniechlanie.databinding.FragmentCategoryPickBinding
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class CategoryPickFragment : BaseFragment<FragmentCategoryPickBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCategoryPickBinding
        get() = FragmentCategoryPickBinding::inflate
    private var isMajor = false
    private var allSelected = false
    private  lateinit var adapter: CategoryPickAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isMajor = arguments?.getBoolean("isMajor") ?: false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val filterViewModel = ViewModelProvider(requireActivity())[FilterViewModel::class.java]
        val categoryViewModel = ViewModelProvider(requireActivity())[CategoryViewModel::class.java]

        adapter = CategoryPickAdapter(requireContext())
        binding.catPickRV.layoutManager = LinearLayoutManager(requireContext())
        binding.catPickRV.adapter = adapter

        if (isMajor) {
            allSelected = filterViewModel.allTypesSelected
            categoryViewModel.getAllMajor().observe(viewLifecycleOwner, {
                adapter.update(
                    it.values.toList(),
                    filterViewModel.getTypeFilter().value ?: listOf()
                )
            })
        } else {
            allSelected = filterViewModel.allCategoriesSelected
            categoryViewModel.getAllNotMajor().observe(viewLifecycleOwner, {
                adapter.update(
                    it.values.toList(),
                    filterViewModel.getCategoryFilter().value ?: listOf()
                )
            })
        }

        binding.catPickCancelBT.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.catPickApplyBT.setOnClickListener {
            if (isMajor) filterViewModel.allTypesSelected = allSelected
            else filterViewModel.allCategoriesSelected = allSelected

            val selected = adapter.getSelected()
            if (isMajor) {
                filterViewModel.setTypeFilter(selected)
            } else {
                filterViewModel.setCategoryFilter(selected)
            }
            findNavController().popBackStack()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.selectall_menu, menu)

        val icon =
            if (allSelected) R.drawable.ic_baseline_check_box_24
            else R.drawable.ic_baseline_check_box_outline_blank_24

        menu.findItem(R.id.selectallBT).setIcon(icon)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (allSelected) {
            allSelected = false
            item.setIcon(R.drawable.ic_baseline_check_box_outline_blank_24)
            adapter.deselectAll()
        } else {
            allSelected = true
            item.setIcon(R.drawable.ic_baseline_check_box_24)
            adapter.selectAll()
        }

        return super.onOptionsItemSelected(item)
    }
}