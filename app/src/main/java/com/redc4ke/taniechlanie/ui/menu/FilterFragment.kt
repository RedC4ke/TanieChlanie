package com.redc4ke.taniechlanie.ui.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.FilterViewModel
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
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
        val userViewModel = provider[UserViewModel::class.java]

        with(binding) {

            // Favorite
            userViewModel.getFavourites().observe(viewLifecycleOwner) {
                filterViewModel.setFavList(it)
            }
            filterFavCHB.isChecked = filterViewModel.getFavFilter()
            filterFavCHB.setOnCheckedChangeListener { _, b ->
                filterViewModel.setFavFilter(b)
            }

            // Price
            filterViewModel.getMaxPrice().observe(viewLifecycleOwner) {
                filterPriceSLD.valueFrom = 0f
                filterPriceSLD.valueTo = it.toFloat()
            }
            val priceFilter = filterViewModel.getPriceFilter().value
            filterPriceSLD.setValues(priceFilter?.first, priceFilter?.second)

            // Volume
            filterViewModel.getMaxVolume().observe(viewLifecycleOwner) {
                filterVolumeSLD.valueFrom = 0f
                filterVolumeSLD.valueTo = it.toFloat()
            }
            val volumeFilter = filterViewModel.getVolumeFilter().value
            filterVolumeSLD.setValues(
                volumeFilter?.first?.toFloat(),
                volumeFilter?.second?.toFloat()
            )

            // Voltage
            filterVoltageSLD.valueFrom = 0f
            filterVoltageSLD.valueTo = 100f
            val voltageFilter = filterViewModel.getVoltageFilter().value
            filterVoltageSLD.setValues(voltageFilter?.first, voltageFilter?.second)

            // Value
            filterViewModel.getMaxValue().observe(viewLifecycleOwner) {
                filterValueSLD.valueFrom = 0f
                filterValueSLD.valueTo = it.toFloat()
            }
            val valueFilter = filterViewModel.getValueFilter().value
            filterValueSLD.setValues(valueFilter?.first, valueFilter?.second)

            // Type
            filterTypeEditBT.setOnClickListener {
                val directions = FilterFragmentDirections.pickCategory(true)
                findNavController().navigate(directions)
            }
            filterViewModel.getTypeFilter().observe(viewLifecycleOwner) {
                var typeString = ""
                it?.forEach { category ->
                    typeString += category.name + ", "
                }
                typeString = typeString.dropLast(2)
                filterTypeTV.text = typeString

                filterTypeRemoveBT.visibility =
                    if (!it.isNullOrEmpty()) View.VISIBLE
                    else View.GONE
            }
            filterTypeRemoveBT.setOnClickListener {
                filterViewModel.setTypeFilter(listOf())
            }

            // Category
            filterCategoryEditBT.setOnClickListener {
                val directions = FilterFragmentDirections.pickCategory(false)
                findNavController().navigate(directions)
            }
            filterViewModel.getCategoryFilter().observe(viewLifecycleOwner) {
                var categoryString = ""
                it?.forEach { category ->
                    categoryString += category.name + ", "
                }
                categoryString = categoryString.dropLast(2)
                filterCategoryTV.text = categoryString

                filterCategoryRemoveBT.visibility =
                    if (!it.isNullOrEmpty()) View.VISIBLE
                    else View.GONE
            }
            filterCategoryRemoveBT.setOnClickListener {
                filterViewModel.setCategoryFilter(listOf())
            }

            //Buttons
            filterCancelBT.setOnClickListener {
                filterViewModel.clear()
                findNavController().popBackStack()
            }
            filterApplyBT.setOnClickListener {
                with(filterPriceSLD) {
                    filterViewModel.setPriceFilter(Pair(this.values[0], this.values[1]))
                }
                with(filterVolumeSLD) {
                    filterViewModel.setVolumeFilter(
                        Pair(
                            this.values[0].toInt(),
                            this.values[1].toInt()
                        )
                    )
                }
                with(filterVoltageSLD) {
                    filterViewModel.setVoltageFilter(Pair(this.values[0], this.values[1]))
                }
                with(filterValueSLD) {
                    filterViewModel.setValueFilter(Pair(this.values[0], this.values[1]))
                }
                filterViewModel.update()
                findNavController().popBackStack()
            }
        }
    }
}