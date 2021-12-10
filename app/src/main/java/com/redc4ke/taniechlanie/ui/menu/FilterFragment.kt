package com.redc4ke.taniechlanie.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.FilterViewModel
import com.redc4ke.taniechlanie.databinding.FragmentFilterBinding
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class FilterFragment() :
    BaseFragment<FragmentFilterBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFilterBinding
        get() = FragmentFilterBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val provider = ViewModelProvider(requireActivity())
        val filterViewModel = provider[FilterViewModel::class.java]
        val categoryViewModel = provider[CategoryViewModel::class.java]

        with(binding) {
            filterViewModel.getMaxPrice().observe(viewLifecycleOwner, {
                filterPriceSLD.valueFrom = 0f
                filterPriceSLD.valueTo = it.toFloat()
            })

            filterViewModel.getMaxVolume().observe(viewLifecycleOwner, {
                filterVolumeSLD.valueFrom = 0f
                filterVolumeSLD.valueTo = it.toFloat()
            })

            filterVoltageSLD.valueFrom = 0f
            filterVoltageSLD.valueTo = 100f

            filterViewModel.getMaxValue().observe(viewLifecycleOwner, {
                filterValueSLD.valueFrom = 0f
                filterValueSLD.valueTo = it.toFloat()
            })

            val priceFilter = filterViewModel.getPriceFilter().value
            filterPriceSLD.setValues(priceFilter?.first, priceFilter?.second)

            val volumeFilter = filterViewModel.getVolumeFilter().value
            filterVolumeSLD.setValues(volumeFilter?.first?.toFloat(), volumeFilter?.second?.toFloat())

            val voltageFilter = filterViewModel.getVoltageFilter().value
            filterVoltageSLD.setValues(voltageFilter?.first, voltageFilter?.second)

            val valueFilter = filterViewModel.getValueFilter().value
            filterValueSLD.setValues(valueFilter?.first, valueFilter?.second)

            filterTypeEditBT.setOnClickListener {
                val directions = FilterFragmentDirections.pickCategory(true)
                findNavController().navigate(directions)
            }
            filterViewModel.getTypeFilter().observe(viewLifecycleOwner, {
                var typeString = ""
                it?.forEach {
                    
                }
                filterTypeRemoveBT.visibility =
                    if (!it.isNullOrEmpty()) View.VISIBLE
                    else View.GONE
            })

            filterCategoryEditBT.setOnClickListener {
                val directions = FilterFragmentDirections.pickCategory(false)
                findNavController().navigate(directions)
            }
            filterViewModel.getCategoryFilter().observe(viewLifecycleOwner, {
                filterCategoryRemoveBT.visibility =
                    if (!it.isNullOrEmpty()) View.VISIBLE
                    else View.GONE
            })
        }
    }
}