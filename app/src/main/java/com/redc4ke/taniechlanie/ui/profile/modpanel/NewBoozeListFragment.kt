package com.redc4ke.taniechlanie.ui.profile.modpanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.data.profile.modpanel.NewBoozeListAdapter
import com.redc4ke.taniechlanie.data.viewmodels.NewBoozeRequest
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.ModpanelViewModel
import com.redc4ke.taniechlanie.databinding.FragmentRequestListBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class NewBoozeListFragment : BaseFragment<FragmentRequestListBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRequestListBinding
        get() = FragmentRequestListBinding::inflate
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var modpanelViewModel: ModpanelViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryViewModel =
            ViewModelProvider(requireActivity())[CategoryViewModel::class.java]
        modpanelViewModel =
            ViewModelProvider(requireActivity())[ModpanelViewModel::class.java]

        val adapter = NewBoozeListAdapter(this, categoryViewModel)
        binding.requestListRV.layoutManager = LinearLayoutManager(requireContext())
        binding.requestListRV.adapter = adapter

        modpanelViewModel.getNewBooze().observe(viewLifecycleOwner, {
            adapter.update(it)
        })
    }

    fun onItemClick(request: NewBoozeRequest) {
        val directions = NewBoozeListFragmentDirections.openRequestDetails(request)
        findNavController().navigate(directions)
    }

}