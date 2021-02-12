package com.redc4ke.taniechlanie.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.profile.ProfileMenuAdapter
import com.redc4ke.taniechlanie.databinding.FragmentProfileMenuBinding
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class ProfileMenuFragment: BaseFragment<FragmentProfileMenuBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileMenuBinding
        get() = FragmentProfileMenuBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitions(R.transition.slide_from_left, R.transition.slide_to_left)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding) {
            profileMenuRV.layoutManager = LinearLayoutManager(requireContext())
            profileMenuRV.adapter = ProfileMenuAdapter(this@ProfileMenuFragment)
        }
    }
}