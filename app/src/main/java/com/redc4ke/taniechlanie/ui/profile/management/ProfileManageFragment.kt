package com.redc4ke.taniechlanie.ui.profile.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.profile.ProfileManageAdapter
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.FragmentProfileManageBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class ProfileManageFragment: BaseFragment<FragmentProfileManageBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) ->
    FragmentProfileManageBinding
        get() = FragmentProfileManageBinding::inflate
    lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitions(R.transition.slide_from_right, R.transition.slide_to_right)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileManageReturnBT.setOnClickListener {
            findNavController().navigate(R.id.to_profileMenu_dest)
        }
        binding.profileManageRV.layoutManager = LinearLayoutManager(requireContext())
        binding.profileManageRV.adapter = ProfileManageAdapter(
            this, userViewModel, viewLifecycleOwner)
    }


}