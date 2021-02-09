package com.redc4ke.taniechlanie.ui.profile.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseUser
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.FragmentProfileDisplaynameBinding
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment

class DisplayNameFragment(private val user: FirebaseUser?,
                          private val userViewModel: UserViewModel):
    BaseDialogFragment<FragmentProfileDisplaynameBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) ->
    FragmentProfileDisplaynameBinding
        get() = FragmentProfileDisplaynameBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.PMDisplaynameET.setText(user?.displayName)
        binding.cancelBT.setOnClickListener {
            this.dismiss()
        }
        binding.saveBT.setOnClickListener {
            userViewModel.setDisplayName(binding.PMDisplaynameET.text.toString())
            this.dismiss()
        }
    }

}