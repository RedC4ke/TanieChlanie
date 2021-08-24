package com.redc4ke.taniechlanie.ui.favourite

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.menu.AlcoListAdapter
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.FragmentFavouriteBinding
import com.redc4ke.taniechlanie.ui.AlcoListFragment
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.base.BaseListFragment

class FavouriteFragment: BaseListFragment<FragmentFavouriteBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFavouriteBinding
        get() = FragmentFavouriteBinding::inflate
    override val alcoObjectList: MutableLiveData<List<AlcoObject>> = MutableLiveData()
    private lateinit var userViewModel: UserViewModel
    private lateinit var alcoObjectViewModel: AlcoObjectViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        alcoObjectViewModel = ViewModelProvider(requireActivity())[AlcoObjectViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.getFavourites().observe(viewLifecycleOwner, {
            val tempList = mutableListOf<AlcoObject>()
            it.forEach { id ->
                val obj = alcoObjectViewModel.get(id.toInt())
                if (obj != null) {
                    tempList.add(obj)
                }
            }
            alcoObjectList.value = tempList
        })
    }


}