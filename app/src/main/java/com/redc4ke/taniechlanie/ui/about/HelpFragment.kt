package com.redc4ke.taniechlanie.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.about.HelpRecyclerViewAdapter
import com.redc4ke.taniechlanie.data.viewmodels.FaqViewModel
import com.redc4ke.taniechlanie.databinding.FragmentHelpBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class HelpFragment : BaseFragment<FragmentHelpBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHelpBinding
        get() = FragmentHelpBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitions(R.transition.slide_up_menu, R.transition.slide_down_menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = super.onCreateView(inflater, container, savedInstanceState)

        val recyclerView = binding.helpRV
        val adapter = HelpRecyclerViewAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val faqViewModel =
            ViewModelProvider(requireActivity() as MainActivity)[FaqViewModel::class.java]
        faqViewModel.get().observe(viewLifecycleOwner, {
            adapter.update(it)
        })

        return root
    }

}