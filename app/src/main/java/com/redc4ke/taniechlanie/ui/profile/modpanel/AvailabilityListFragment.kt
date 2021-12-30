package com.redc4ke.taniechlanie.ui.profile.modpanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.data.profile.modpanel.AvailabilityListAdapter
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.ModpanelViewModel
import com.redc4ke.taniechlanie.databinding.FragmentRequestListBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class AvailabilityListFragment : BaseFragment<FragmentRequestListBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRequestListBinding
        get() = FragmentRequestListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val provider = ViewModelProvider(requireActivity())
        val categoryViewModel = provider[CategoryViewModel::class.java]
        val alcoObjectViewModel = provider[AlcoObjectViewModel::class.java]
        val modpanelViewModel = provider[ModpanelViewModel::class.java]

        val adapter = AvailabilityListAdapter(this, alcoObjectViewModel, categoryViewModel)

        with (binding) {
            requestListRV.layoutManager = LinearLayoutManager(requireContext())
            requestListRV.adapter = adapter
        }

        modpanelViewModel.getAvailability().observe(viewLifecycleOwner, {
            adapter.update(it)
        })
    }

}