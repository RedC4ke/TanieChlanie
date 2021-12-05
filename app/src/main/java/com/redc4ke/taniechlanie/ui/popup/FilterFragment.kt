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
        val alcoObjectViewModel = provider[AlcoObjectViewModel::class.java]
        val filterViewModel = provider[FilterViewModel::class.java]

        alcoObjectViewModel.getAll().observe(viewLifecycleOwner, { list ->
            list.forEach { alcoObject ->
                if (alcoObject.volume > maxVolume) maxVolume = alcoObject.volume
                val price = alcoObject.shopToPrice.values.sortedBy { it }[0] ?: BigDecimal.ZERO
                if (price > maxPrice) maxPrice = price
            }
        })

        with(binding) {
            filterPriceSLD.valueTo = maxPrice.toFloat()
            filterVolumeSLD.valueTo = maxVolume.toFloat()

            filterViewModel.getPriceFilter().observe(viewLifecycleOwner, {
                filterPriceSLD.setValues(it?.first, it?.second)
            })
            filterViewModel.getVolumeFilter().observe(viewLifecycleOwner, {
                filterVolumeSLD.setValues(it?.first?.toFloat(), it?.second?.toFloat())
            })
            filterViewModel.getVoltageFilter().observe(viewLifecycleOwner, {
                filterVoltageSLD.setValues(it?.first, it?.second)
            })

            filterPriceSLD.addOnChangeListener { slider, _, _ ->
                filterViewModel.setPriceFilter(Pair(slider.valueFrom, slider.valueTo))
            }

            filterVolumeSLD.addOnChangeListener { slider, _, _ ->
                filterViewModel.setVolumeFilter(
                    Pair(
                        slider.valueFrom.toInt(),
                        slider.valueTo.toInt()
                    )
                )
            }

            filterVoltageSLD.addOnChangeListener { slider, _, _ ->
                filterViewModel.setVoltageFilter(Pair(slider.valueFrom, slider.valueTo))
            }
        }
    }
}