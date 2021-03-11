package com.redc4ke.taniechlanie.ui.favourite

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.menu.AlcoListAdapter
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.FragmentFavouriteBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class FavouriteFragment: BaseFragment<FragmentFavouriteBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFavouriteBinding
        get() = FragmentFavouriteBinding::inflate
    private lateinit var userViewModel: UserViewModel
    private lateinit var alcoObjectViewModel: AlcoObjectViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        alcoObjectViewModel = ViewModelProvider(requireActivity())[AlcoObjectViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()

        //userViewModel.getFavourites().observe(viewLifecycleOwner, {
        //    val list = mutableListOf<AlcoObject>()
        //    it.forEach {id ->
        //        alcoObjectViewModel.get(id.toInt())?.let { alcoObject ->
        //            list.add(alcoObject)
        //        }
        //    }
        //    binding.favouriteRV.run {
        //        layoutManager = LinearLayoutManager(requireContext())
        //        adapter = AlcoListAdapter(list, activity as MainActivity, )
        //    }
//
        //})
    }


}