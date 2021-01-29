package com.redc4ke.taniechlanie.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import com.redc4ke.taniechlanie.databinding.FragmentProfileBinding
import com.redc4ke.taniechlanie.ui.BaseFragment

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate


}