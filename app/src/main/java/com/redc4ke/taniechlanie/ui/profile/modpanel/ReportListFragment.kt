package com.redc4ke.taniechlanie.ui.profile.modpanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.data.profile.modpanel.ReportListAdapter
import com.redc4ke.taniechlanie.data.viewmodels.ModpanelViewModel
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.FragmentRequestListBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class ReportListFragment : BaseFragment<FragmentRequestListBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRequestListBinding
        get() = FragmentRequestListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val provider = ViewModelProvider(requireActivity())
        val modpanelViewModel = provider[ModpanelViewModel::class.java]
        val userViewModel = provider[UserViewModel::class.java]

        val adapter = ReportListAdapter(this, userViewModel)

        with (binding) {
            requestListRV.layoutManager = LinearLayoutManager(requireContext())
            requestListRV.adapter = adapter
        }

        modpanelViewModel.getReports().observe(viewLifecycleOwner, {
            adapter.update(it)
        })
    }
}