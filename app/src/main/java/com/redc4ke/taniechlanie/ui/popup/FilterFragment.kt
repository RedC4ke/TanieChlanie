package com.redc4ke.taniechlanie.ui.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.data.viewmodels.FilterViewModel
import com.redc4ke.taniechlanie.databinding.FragmentFilterBinding
import com.redc4ke.taniechlanie.ui.AlcoListFragment
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment
import java.math.BigDecimal

class FilterFragment() :
    BaseDialogFragment<FragmentFilterBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFilterBinding
        get() = FragmentFilterBinding::inflate
    private var maxPrice = BigDecimal.ONE
    private var maxVolume = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )

        val provider = ViewModelProvider(requireActivity())
        val filterViewModel = provider[FilterViewModel::class.java]

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

            val priceFilter = filterViewModel.getPriceFilter().value
            filterPriceSLD.setValues(priceFilter?.first, priceFilter?.second)

            val volumeFilter = filterViewModel.getVolumeFilter().value
            filterVolumeSLD.setValues(volumeFilter?.first?.toFloat(), volumeFilter?.second?.toFloat())

            val voltageFilter = filterViewModel.getVoltageFilter().value
            filterVoltageSLD.setValues(voltageFilter?.first, voltageFilter?.second)

            filterPriceSLD.addOnChangeListener { slider, _, _ ->
                filterViewModel.setPriceFilter(Pair(slider.values[0], slider.values[1]))
            }

            filterVolumeSLD.addOnChangeListener { slider, _, _ ->
                filterViewModel.setVolumeFilter(
                    Pair(
                        slider.values[0].toInt(), slider.values[1].toInt()
                    )
                )
            }

            filterVoltageSLD.addOnChangeListener { slider, _, _ ->
                filterViewModel.setVoltageFilter(Pair(slider.values[0], slider.values[1]))
            }
        }
    }
}