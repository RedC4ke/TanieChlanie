package com.redc4ke.taniechlanie.ui.profile.modpanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.data.profile.modpanel.ModpanelActionsAdapter
import com.redc4ke.taniechlanie.data.viewmodels.ModpanelViewModel
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.FragmentModPanelBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class ModPanelFragment : BaseFragment<FragmentModPanelBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentModPanelBinding
        get() = FragmentModPanelBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val provider = ViewModelProvider(requireActivity() as MainActivity)
        val modpanelViewModel = provider[ModpanelViewModel::class.java]
        val userViewModel = provider[UserViewModel::class.java]
        modpanelViewModel.fetch()

        val recyclerView = binding.modActionListRV
        val adapter = ModpanelActionsAdapter(
            this,
            userViewModel.getPermissionLevel()
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        modpanelViewModel.getNewBooze().observe(viewLifecycleOwner, {
            adapter.updateSources(it, 0)
        })
        modpanelViewModel.getAvailability().observe(viewLifecycleOwner, {
            adapter.updateSources(it, 1)
        })
        modpanelViewModel.getReports().observe(viewLifecycleOwner, {
            adapter.updateSources(it, 2)
        })

    }
}