package com.redc4ke.taniechlanie.ui.menu

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.databinding.FragmentMenuBinding
import com.redc4ke.taniechlanie.ui.base.BaseListFragment

class MenuFragment : BaseListFragment<FragmentMenuBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMenuBinding
        get() = FragmentMenuBinding::inflate
    override val alcoObjectList = MutableLiveData<List<AlcoObject>>()
    private lateinit var alcoObjectViewModel: AlcoObjectViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)

        alcoObjectViewModel = ViewModelProvider(requireActivity())[AlcoObjectViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alcoObjectViewModel.getAll().observe(viewLifecycleOwner, {list ->
            alcoObjectList.value = list.sortedBy { it.name }
        })
    }

}