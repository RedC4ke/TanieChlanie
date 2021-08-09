package com.redc4ke.taniechlanie.ui.profile.modpanel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.profile.modpanel.NewBoozeListAdapter
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.ModpanelViewModel
import com.redc4ke.taniechlanie.data.viewmodels.Request
import com.redc4ke.taniechlanie.databinding.FragmentNewBoozeListBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.menu.AlcoListFragmentDirections

class NewBoozeListFragment : BaseFragment<FragmentNewBoozeListBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNewBoozeListBinding
        get() = FragmentNewBoozeListBinding::inflate
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var modpanelViewModel: ModpanelViewModel

    override fun onStart() {
        super.onStart()

        categoryViewModel =
            ViewModelProvider(requireActivity() as MainActivity)[CategoryViewModel::class.java]
        modpanelViewModel =
            ViewModelProvider(requireActivity() as MainActivity)[ModpanelViewModel::class.java]

        val adapter = NewBoozeListAdapter(this, categoryViewModel)
        binding.newBoozeRV.layoutManager = LinearLayoutManager(requireContext())
        binding.newBoozeRV.adapter = adapter

        modpanelViewModel.getNewBooze().observe(viewLifecycleOwner, {
            adapter.update(it)
        })
    }

    fun onItemClick(request: Request) {
        val directions = NewBoozeListFragmentDirections.openRequestDetails(request)
        findNavController().navigate(directions)
    }

}