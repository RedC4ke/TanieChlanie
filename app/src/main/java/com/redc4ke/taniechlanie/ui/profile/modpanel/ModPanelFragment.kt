package com.redc4ke.taniechlanie.ui.profile.modpanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.data.profile.modpanel.ModpanelActionsAdapter
import com.redc4ke.taniechlanie.data.viewmodels.ModpanelViewModel
import com.redc4ke.taniechlanie.databinding.FragmentModPanelBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class ModPanelFragment : BaseFragment<FragmentModPanelBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentModPanelBinding
        get() = FragmentModPanelBinding::inflate
    private lateinit var modpanelViewModel: ModpanelViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        modpanelViewModel =
            ViewModelProvider(requireActivity() as MainActivity)[ModpanelViewModel::class.java]
        modpanelViewModel.fetch()
    }

    override fun onStart() {
        super.onStart()

        val recyclerView = binding.modActionListRV
        val adapter = ModpanelActionsAdapter(this)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        modpanelViewModel.getNewBooze().observe(viewLifecycleOwner, {
            adapter.updateSources(it, 0)
        })
        modpanelViewModel.getChangedBooze().observe(viewLifecycleOwner, {
            adapter.updateSources(it, 1)
        })
        modpanelViewModel.getAvailability().observe(viewLifecycleOwner, {
            adapter.updateSources(it, 2)
        })
        modpanelViewModel.getReports().observe(viewLifecycleOwner, {
            adapter.updateSources(it, 3)
        })
    }

}